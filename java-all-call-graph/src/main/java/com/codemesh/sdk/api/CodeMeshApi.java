package com.codemesh.sdk.api;

import com.codemesh.sdk.CodeMeshQueue;
import com.codemesh.sdk.config.CodeMeshConfig;
import com.codemesh.sdk.metrics.CodeMeshMetrics;
import com.codemesh.sdk.entity.CodeMeshRequest;
import com.codemesh.sdk.entity.CodeMeshResponse;
import com.codemesh.sdk.util.HttpClientUtil;
import com.codemesh.sdk.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fio
 * @date 2025/3/4
 */
@Slf4j
public class CodeMeshApi implements ApiClient {

    private static volatile CodeMeshApi INSTANCE;

    private final CodeMeshConfig config;
    private final CloseableHttpClient httpClient;
    private final Map<CodeMeshQueue.Event, String> apiMap;

    private CodeMeshApi(CodeMeshConfig config) {
        this.config = config;

        this.apiMap = initApiMap();

        this.httpClient = HttpClientUtil.createHttpClient(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Shutting down HTTP client...");
                this.httpClient.close();
            } catch (IOException e) {
                log.error("Error closing HTTP client", e);
            }
        }));
    }

    private Map<CodeMeshQueue.Event, String> initApiMap() {
        ApiUrl.setApiPrefix(config.getApiBaseUrl());

        Map<CodeMeshQueue.Event, String> apiMap = new HashMap<>();
        apiMap.put(CodeMeshQueue.Event.UPDATE_TASK_STATUS, ApiUrl.getFullUrl(ApiUrl.UPDATE_TASK_STATUS));
        apiMap.put(CodeMeshQueue.Event.ADD_CALL_CHAIN, ApiUrl.getFullUrl(ApiUrl.ADD_CALL_CHAIN));
        apiMap.put(CodeMeshQueue.Event.CLEAN_CALL_CHAINS, ApiUrl.getFullUrl(ApiUrl.CLEAN_CALL_CHAINS));
        apiMap.put(CodeMeshQueue.Event.LOG_REPORT, ApiUrl.getFullUrl(ApiUrl.LOG_REPORT));
        return apiMap;
    }

    public static synchronized CodeMeshApi getInstance(CodeMeshConfig config) {
        if (INSTANCE == null) {
            synchronized (CodeMeshApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CodeMeshApi(config);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public boolean doRequest(CodeMeshRequest request) {
        CodeMeshQueue.Event event = request.getRequestEvent();
        String apiUrl = getApiUrl(event);

        if (StringUtils.isEmpty(apiUrl)) {
            log.warn("Unknown request event: {}", event);
            return false;
        }

        try {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setEntity(new StringEntity(JsonUtil.toJsonString(request), ContentType.APPLICATION_JSON));

            String responseStr = httpClient.execute(httpPost, new BasicHttpClientResponseHandler());

            CodeMeshResponse response = JsonUtil.toObject(responseStr, CodeMeshResponse.class);
            if (response == null) {
                log.warn("CodeMesh request for {} got null response.", apiUrl);
                CodeMeshMetrics.INSTANCE.recordRequest(event, false);
                return false;
            }

            boolean success = BooleanUtils.isTrue(response.getSuccess());
            if (!success) {
                log.warn("CodeMesh request for {} response false. Response message is: {}", apiUrl, response.getMessage());
            }

            // 记录指标
            CodeMeshMetrics.INSTANCE.recordRequest(event, success);
            return success;
        } catch (Exception e) {
            log.error("Exception when request " + request + " to " + apiUrl, e);
            CodeMeshMetrics.INSTANCE.recordRequest(event, false);
            return false;
        }
    }

    @Override
    public String getApiUrl(CodeMeshQueue.Event event) {
        return apiMap.get(event);
    }
}
