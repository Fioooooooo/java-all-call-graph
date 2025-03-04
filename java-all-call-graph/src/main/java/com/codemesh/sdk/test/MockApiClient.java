package com.codemesh.sdk.test;

import com.codemesh.sdk.CodeMeshQueue;
import com.codemesh.sdk.api.ApiClient;
import com.codemesh.sdk.api.ApiConstant;
import com.codemesh.sdk.request.CodeMeshRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模拟 API 客户端，用于测试
 *
 * @author Fio
 * @date 2025/3/5
 */
public class MockApiClient implements ApiClient {

    @Getter
    private final List<CodeMeshRequest> requests = new ArrayList<>();
    
    @Getter
    private final Map<CodeMeshQueue.Event, AtomicInteger> eventCounts = new ConcurrentHashMap<>();
    
    @Getter
    private final Map<CodeMeshQueue.Event, List<CodeMeshRequest>> eventRequests = new ConcurrentHashMap<>();
    
    @Setter
    private boolean shouldSucceed = true;
    
    @Setter
    private int failureCount = 0;
    
    private int requestCount = 0;
    
    private final Map<CodeMeshQueue.Event, String> apiMap;
    
    public MockApiClient() {
        this.apiMap = new HashMap<>();
        apiMap.put(CodeMeshQueue.Event.UPDATE_TASK_STATUS, ApiConstant.getFullUrl(ApiConstant.UPDATE_TASK_STATUS));
        apiMap.put(CodeMeshQueue.Event.ADD_CALL_CHAIN, ApiConstant.getFullUrl(ApiConstant.ADD_CALL_CHAIN));
        apiMap.put(CodeMeshQueue.Event.CLEAN_CALL_CHAINS, ApiConstant.getFullUrl(ApiConstant.CLEAN_CALL_CHAINS));
        apiMap.put(CodeMeshQueue.Event.LOG_REPORT, ApiConstant.getFullUrl(ApiConstant.LOG_REPORT));
    }
    
    @Override
    public boolean doRequest(CodeMeshRequest request) {
        requests.add(request);
        requestCount++;
        
        CodeMeshQueue.Event event = request.getRequestEvent();
        eventCounts.computeIfAbsent(event, e -> new AtomicInteger()).incrementAndGet();
        
        eventRequests.computeIfAbsent(event, e -> new ArrayList<>()).add(request);
        
        if (failureCount > 0 && requestCount <= failureCount) {
            return false;
        }
        
        return shouldSucceed;
    }
    
    @Override
    public String getApiUrl(CodeMeshQueue.Event event) {
        return apiMap.get(event);
    }
    
    /**
     * 获取特定事件的请求次数
     *
     * @param event 事件类型
     * @return 请求次数
     */
    public int getEventCount(CodeMeshQueue.Event event) {
        AtomicInteger count = eventCounts.get(event);
        return count != null ? count.get() : 0;
    }
    
    /**
     * 获取特定事件的请求列表
     *
     * @param event 事件类型
     * @return 请求列表
     */
    public List<CodeMeshRequest> getEventRequests(CodeMeshQueue.Event event) {
        return eventRequests.getOrDefault(event, new ArrayList<>());
    }
    
    /**
     * 重置所有计数和请求
     */
    public void reset() {
        requests.clear();
        eventCounts.clear();
        eventRequests.clear();
        requestCount = 0;
    }
}
