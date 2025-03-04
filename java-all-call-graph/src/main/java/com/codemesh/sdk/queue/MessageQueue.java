package com.codemesh.sdk.queue;

import com.codemesh.sdk.CodeMeshQueue;
import com.codemesh.sdk.request.CodeMeshRequest;

/**
 * 消息队列接口
 *
 * @author Fio
 * @date 2025/3/5
 */
public interface MessageQueue {
    /**
     * 启动队列处理
     */
    void start();

    /**
     * 结束队列处理
     */
    void finish();

    /**
     * 将消息放入队列
     *
     * @param event 事件类型
     * @param request 请求对象
     */
    void put(CodeMeshQueue.Event event, CodeMeshRequest request);

    /**
     * 获取队列当前大小
     *
     * @return 队列大小
     */
    int getQueueSize();

    /**
     * 获取队列是否已启动
     *
     * @return 是否已启动
     */
    boolean isStarted();
}
