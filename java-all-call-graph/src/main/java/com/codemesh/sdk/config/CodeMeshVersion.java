package com.codemesh.sdk.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 版本信息工具类
 * 用于获取项目版本号
 *
 * @author Fio
 * @date 2025/3/8
 */
@Slf4j
public final class CodeMeshVersion {
    private static final String VERSION_PROPERTIES = "version.properties";
    private static final String VERSION_KEY = "app.version";
    private static final String BUILD_TIME_KEY = "build.timestamp";
    private static final String DEFAULT_VERSION = "";
    private static final String DEFAULT_BUILD_TIME = "";
    
    private static final String VERSION;
    private static final String BUILD_TIME;
    
    static {
        String version = DEFAULT_VERSION;
        String buildTime = DEFAULT_BUILD_TIME;
        try {
            Properties props = new Properties();
            InputStream is = CodeMeshVersion.class.getClassLoader().getResourceAsStream(VERSION_PROPERTIES);
            if (is != null) {
                props.load(is);
                version = props.getProperty(VERSION_KEY, DEFAULT_VERSION);
                buildTime = props.getProperty(BUILD_TIME_KEY, DEFAULT_BUILD_TIME);
                is.close();
            } else {
                log.warn("无法加载版本信息文件: {}", VERSION_PROPERTIES);
            }
        } catch (IOException e) {
            log.error("读取版本信息时发生错误", e);
        }
        VERSION = version;
        BUILD_TIME = buildTime;
    }
    
    /**
     * 获取应用版本号
     *
     * @return 应用版本号
     */
    public static String getVersion() {
        return VERSION;
    }
    
    /**
     * 获取构建时间
     *
     * @return 构建时间
     */
    public static String getBuildTime() {
        return BUILD_TIME;
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private CodeMeshVersion() {
        // 防止实例化
    }
}
