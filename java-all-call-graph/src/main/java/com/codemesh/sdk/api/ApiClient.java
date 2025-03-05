package com.codemesh.sdk.api;

import com.codemesh.sdk.CodeMeshQueue;
import com.codemesh.sdk.entity.CodeMeshRequest;

/**
 * API 客户端接口
 *
 * @author Fio
 * @date 2025/3/5
 */
public interface ApiClient {
    /**
     * 执行请求
     *
     * @param request 请求对象
     * @return 是否成功
     */
    boolean doRequest(CodeMeshRequest request);

    /**
     * 获取 API URL
     *
     * @param event 事件类型
     * @return API URL
     */
    String getApiUrl(CodeMeshQueue.Event event);
}
