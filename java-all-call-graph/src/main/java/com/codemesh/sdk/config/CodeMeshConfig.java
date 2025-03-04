package com.codemesh.sdk.config;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * CodeMesh SDK 配置类
 *
 * @author Fio
 * @date 2025/3/5
 */
@Data
@Builder
@Slf4j
public class CodeMeshConfig {
    /**
     * API 基础 URL
     */
    @Builder.Default
    private String apiBaseUrl = "http://127.0.0.1:7071/openApi";

    /**
     * API 版本
     */
    @Builder.Default
    private String apiVersion = "0.0.1";

    /**
     * 队列容量
     */
    @Builder.Default
    private int queueCapacity = 100000;

    /**
     * 最大错误次数
     */
    @Builder.Default
    private int maxErrorCount = 10;

    /**
     * 最大重试次数
     */
    @Builder.Default
    private int maxRetryCount = 3;

    /**
     * 消费者线程数
     */
    @Builder.Default
    private int consumerThreads = 5;

    /**
     * 重试间隔基础毫秒数
     */
    @Builder.Default
    private int retryIntervalMs = 100;

}
