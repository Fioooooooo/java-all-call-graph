package com.codemesh.sdk.example;

import com.codemesh.sdk.CodeMeshQueue;
import com.codemesh.sdk.config.CodeMeshConfig;
import com.codemesh.sdk.metrics.CodeMeshMetrics;
import com.codemesh.sdk.request.AddCallChainRequest;
import com.codemesh.sdk.request.CleanCallChainsRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * CodeMesh SDK 使用示例
 *
 * @author Fio
 * @date 2025/3/5
 */
@Slf4j
public class CodeMeshExample {

    public static void main(String[] args) {
        try {
            // 1. 创建配置
            CodeMeshConfig config = CodeMeshConfig.builder()
                    .apiBaseUrl("http://127.0.0.1:7071/openApi")
                    .apiVersion("0.0.1")
                    .queueCapacity(10000)
                    .maxErrorCount(5)
                    .maxRetryCount(3)
                    .consumerThreads(2)
                    .build();
            
            // 2. 获取队列实例
            CodeMeshQueue queue = CodeMeshQueue.getInstance(config);
            
            // 3. 启动队列
            queue.start();
            
            // 4. 清理调用链
            CleanCallChainsRequest cleanRequest = CleanCallChainsRequest.builder()
                    .workspaceId(1L)
                    .projectId(2L)
                    .build();
            queue.put(CodeMeshQueue.Event.CLEAN_CALL_CHAINS, cleanRequest);
            
            // 5. 添加调用链
            for (int i = 0; i < 10; i++) {
                AddCallChainRequest addRequest = AddCallChainRequest.builder()
                        .workspaceId(1L)
                        .projectId(2L)
                        .callerFilePath("com/example/service/UserService.java")
                        .callerFileName("UserService.java")
                        .callerModuleName("com.example.service.UserService")
                        .callerMethodName("getUserById")
                        .callerMethodSignature("getUserById(Long)")
                        .callerStartLine(100)
                        .callerEndLine(120)
                        .calleeFilePath("com/example/repository/UserRepository.java")
                        .calleeFileName("UserRepository.java")
                        .calleeModuleName("com.example.repository.UserRepository")
                        .calleeMethodName("findById")
                        .calleeMethodSignature("findById(Long)")
                        .calleeStartLine(50)
                        .calleeEndLine(60)
                        .language("java")
                        .callType("METHOD_CALL")
                        .context("Service calling repository")
                        .build();
                queue.put(CodeMeshQueue.Event.ADD_CALL_CHAIN, addRequest);
            }
            
            // 6. 等待一段时间让消息处理完成
            Thread.sleep(5000);
            
            // 7. 打印指标
            CodeMeshMetrics.INSTANCE.logMetrics();
            
            // 8. 关闭队列
            queue.finish();
            
            log.info("Example completed successfully");
        } catch (Exception e) {
            log.error("Example failed", e);
        }
    }
}
