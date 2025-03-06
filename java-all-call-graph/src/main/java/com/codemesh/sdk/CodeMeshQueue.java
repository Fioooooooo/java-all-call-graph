package com.codemesh.sdk;

import com.codemesh.sdk.api.ApiClient;
import com.codemesh.sdk.api.CodeMeshApi;
import com.codemesh.sdk.config.CodeMeshConfig;
import com.codemesh.sdk.entity.CleanCallChainsRequest;
import com.codemesh.sdk.metrics.CodeMeshMetrics;
import com.codemesh.sdk.queue.MessageQueue;
import com.codemesh.sdk.entity.CodeMeshRequest;
import com.codemesh.sdk.entity.TaskStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Fio
 * @date 2025/3/5
 */
@Slf4j
public class CodeMeshQueue implements MessageQueue {

    private static volatile CodeMeshQueue INSTANCE;

    private final CodeMeshConfig config;
    private final ApiClient apiClient;
    private final ArrayBlockingQueue<QueueMessage> messageQueue;
    private final ExecutorService messagePool;
    private final AtomicInteger countErrorApi;
    private final Thread consumerThread;

    private volatile boolean start = false;

    private CodeMeshQueue(CodeMeshConfig config, ApiClient apiClient) {
        this.config = config;
        this.apiClient = apiClient;
        this.messageQueue = new ArrayBlockingQueue<>(config.getQueueCapacity());
        this.countErrorApi = new AtomicInteger(0);

        // 创建线程池
        this.messagePool = Executors.newFixedThreadPool(
                config.getConsumerThreads(),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "codemesh-worker-" + threadNumber.getAndIncrement());
                        t.setDaemon(true);
                        return t;
                    }
                }
        );

        // 创建消费者线程
        this.consumerThread = new Thread(this::consume, "codemesh-consumer");
        this.consumerThread.setDaemon(true);

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(this::finish, "codemesh-shutdown-hook"));
    }

    /**
     * 获取单例实例
     *
     * @return 队列实例
     */
    public static CodeMeshQueue getInstance() {
        return getInstance(CodeMeshConfig.builder().build());
    }

    /**
     * 获取单例实例
     *
     * @param config 配置
     * @return 队列实例
     */
    private static synchronized CodeMeshQueue getInstance(CodeMeshConfig config) {
        if (INSTANCE == null) {
            synchronized (CodeMeshQueue.class) {
                if (INSTANCE == null) {
                    ApiClient apiClient = CodeMeshApi.getInstance(config);
                    INSTANCE = new CodeMeshQueue(config, apiClient);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        if (this.start) {
            log.info("CodeMeshQueue already started");
            return;
        }

        log.info("Starting CodeMeshQueue with config: {}", config);
        this.start = true;
        updateTaskStatus(true);
        cleanCallChains();

        // 启动消费者线程
        this.consumerThread.start();

        log.info("CodeMeshQueue started");
    }

    @Override
    public void finish() {
        if (!this.start) {
            log.info("CodeMeshQueue already stopped");
            return;
        }

        log.info("Stopping CodeMeshQueue");
        this.start = false;

        // 等待队列中的消息处理完成
        try {
            log.info("Waiting for queue to drain, current size: {}", messageQueue.size());
            while (!messageQueue.isEmpty() && !Thread.currentThread().isInterrupted()) {
                Thread.sleep(100);
            }

            // 关闭线程池
            messagePool.shutdown();
            if (!messagePool.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("MessagePool did not terminate in time, forcing shutdown");
                messagePool.shutdownNow();
            }

            log.info("MessagePool shutdown complete");
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for queue to drain", e);
            messagePool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        updateTaskStatus(false);
        log.info("CodeMeshQueue stopped");
    }

    @Override
    public void put(Event event, CodeMeshRequest request) {
        if (!start) {
            log.warn("Attempting to put message when queue is not started, event: {}", event);
            return;
        }

        QueueMessage message = new QueueMessage(event, request);
        try {
            boolean offered = messageQueue.offer(message, 1, TimeUnit.SECONDS);
            if (!offered) {
                log.warn("Failed to put message in queue, queue might be full, event: {}", event);
            } else {
                // 更新队列大小指标
                CodeMeshMetrics.INSTANCE.updateQueueSize(messageQueue.size());
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while putting message in queue", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int getQueueSize() {
        return messageQueue.size();
    }

    @Override
    public boolean isStarted() {
        return start;
    }

    private void consume() {
        log.info("Consumer thread started");
        while (start || !messageQueue.isEmpty()) {
            try {
                QueueMessage message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                if (message != null) {
                    CodeMeshMetrics.INSTANCE.updateQueueSize(messageQueue.size());
                    messagePool.execute(() -> consumeMessage(message));
                }
            } catch (InterruptedException e) {
                log.warn("Consumer thread interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Exception when consuming message", e);
                if (start) {
                    finish();
                    throw new RuntimeException("Exit when queue message consume exception", e);
                }
            }
        }
        log.info("Consumer thread stopped");
    }

    private void consumeMessage(QueueMessage message) {
        if (message == null) {
            return;
        }

        message.request.setRequestEvent(message.event);

        // 重试
        int retryCount = 0;
        boolean success = false;
        Exception lastException = null;

        while (retryCount <= config.getMaxRetryCount() && !success) {
            try {
                if (retryCount > 0) {
                    CodeMeshMetrics.INSTANCE.recordRetry();
                    log.info("Retrying request for event {}, attempt {}/{}",
                            message.event, retryCount, config.getMaxRetryCount());

                    // 指数退避
                    sleep(config.getRetryIntervalMs() * (1 << (retryCount - 1)));
                }

                success = apiClient.doRequest(message.request);
                if (success) {
                    // 重置错误计数
                    countErrorApi.set(0);
                    break;
                }

                retryCount++;
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                log.warn("Exception during API request for event {}, attempt {}/{}",
                        message.event, retryCount, config.getMaxRetryCount());
                log.warn("Exception is", e);
            }
        }

        if (success) {
            return;
        }

        int errorCount = countErrorApi.incrementAndGet();
        log.warn("API request failed after {} retries, total error count: {}/{}",
                retryCount - 1, errorCount, config.getMaxErrorCount());

        if (errorCount >= config.getMaxErrorCount()) {
            String errorMsg = "CodeMesh request has failed for MAX_ERROR: " + config.getMaxErrorCount();
            if (lastException != null) {
                throw new RuntimeException(errorMsg, lastException);
            } else {
                throw new RuntimeException(errorMsg);
            }
        }
    }

    private void updateTaskStatus(boolean start) {
        TaskStatusRequest.StatusEnum status = start
                ? TaskStatusRequest.StatusEnum.START
                : TaskStatusRequest.StatusEnum.FINISH;

        TaskStatusRequest request = TaskStatusRequest.builder()
                .status(status).requestEvent(Event.UPDATE_TASK_STATUS).build();

        try {
            boolean success = apiClient.doRequest(request);
            if (!success) {
                log.warn("Failed to update task status to {}", status);
            }
        } catch (Exception e) {
            log.error("Exception when updating task status to {}", status, e);
        }
    }

    private void cleanCallChains() {
        CleanCallChainsRequest request = CleanCallChainsRequest.builder()
                .requestEvent(Event.CLEAN_CALL_CHAINS).build();

        boolean success = apiClient.doRequest(request);
        if (success) {
            log.info("Clean all existed call chains successfully");
        }

        throw new RuntimeException("Clean existed call chains failed.");
    }

    private void sleep(int mills) {
        try {
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (Exception ignore) {
        }
    }

    @RequiredArgsConstructor
    private static class QueueMessage {
        private final Event event;
        private final CodeMeshRequest request;
    }

    public enum Event {
        UPDATE_TASK_STATUS, LOG_REPORT, CLEAN_CALL_CHAINS, ADD_CALL_CHAIN;
    }
}
