package com.codemesh.sdk.metrics;

import com.codemesh.sdk.CodeMeshQueue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CodeMesh 指标收集类
 *
 * @author Fio
 * @date 2025/3/5
 */
@Slf4j
@Data
public class CodeMeshMetrics {
    /**
     * 单例实例
     */
    public static final CodeMeshMetrics INSTANCE = new CodeMeshMetrics();

    private CodeMeshMetrics() {
    }

    /**
     * 总请求数
     */
    private final AtomicLong totalRequests = new AtomicLong(0);

    /**
     * 成功请求数
     */
    private final AtomicLong successRequests = new AtomicLong(0);

    /**
     * 失败请求数
     */
    private final AtomicLong failedRequests = new AtomicLong(0);

    /**
     * 重试请求数
     */
    private final AtomicLong retryRequests = new AtomicLong(0);

    /**
     * 当前队列大小
     */
    private final AtomicLong queueSize = new AtomicLong(0);

    /**
     * 事件计数
     */
    private final Map<CodeMeshQueue.Event, AtomicLong> eventCounts = new ConcurrentHashMap<>();

    /**
     * 记录请求
     *
     * @param event 事件类型
     * @param success 是否成功
     */
    public void recordRequest(CodeMeshQueue.Event event, boolean success) {
        totalRequests.incrementAndGet();
        if (success) {
            successRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }

        eventCounts.computeIfAbsent(event, e -> new AtomicLong()).incrementAndGet();
    }

    /**
     * 记录重试
     */
    public void recordRetry() {
        retryRequests.incrementAndGet();
    }

    /**
     * 更新队列大小
     *
     * @param size 队列大小
     */
    public void updateQueueSize(int size) {
        queueSize.set(size);
    }

    /**
     * 获取事件计数
     *
     * @param event 事件类型
     * @return 计数
     */
    public long getEventCount(CodeMeshQueue.Event event) {
        AtomicLong count = eventCounts.get(event);
        return count != null ? count.get() : 0;
    }

    /**
     * 获取成功率
     *
     * @return 成功率
     */
    public double getSuccessRate() {
        long total = totalRequests.get();
        if (total == 0) {
            return 1.0;
        }
        return (double) successRequests.get() / total;
    }

    /**
     * 重置所有指标
     */
    public void reset() {
        totalRequests.set(0);
        successRequests.set(0);
        failedRequests.set(0);
        retryRequests.set(0);
        queueSize.set(0);
        eventCounts.clear();
    }

    /**
     * 打印当前指标
     */
    public void logMetrics() {
        log.info("CodeMesh Metrics: totalRequests={}, successRequests={}, failedRequests={}, retryRequests={}, queueSize={}, successRate={}%",
                totalRequests.get(), successRequests.get(), failedRequests.get(), retryRequests.get(), queueSize.get(), 
                String.format("%.2f", getSuccessRate() * 100));

        for (Map.Entry<CodeMeshQueue.Event, AtomicLong> entry : eventCounts.entrySet()) {
            log.info("Event {}: count={}", entry.getKey(), entry.getValue().get());
        }
    }
}
