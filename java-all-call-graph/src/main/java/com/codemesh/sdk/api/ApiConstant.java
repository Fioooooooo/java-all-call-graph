package com.codemesh.sdk.api;

/**
 * @author Fio
 * @date 2025/3/4
 */
public class ApiConstant {

    public static final String API_VERSION = "0.0.1";

    private static String API_PREFIX = "http://127.0.0.1:7071/openApi";

    /**
     * 更新任务状态，start / finish
     */
    public static final String UPDATE_TASK_STATUS = "/updateTaskStatus";

    /**
     * 任务日志上报
     */
    public static final String LOG_REPORT = "/logReport";

    /**
     * 清理工程所有的 call chain
     */
    public static final String CLEAN_CALL_CHAINS = "/cleanCallChain";

    /**
     * 添加新的 call chain
     */
    public static final String ADD_CALL_CHAIN = "/addCallChain";

    /**
     * 设置 API 前缀
     *
     * @param apiPrefix API 前缀
     */
    public static void setApiPrefix(String apiPrefix) {
        API_PREFIX = apiPrefix;
    }

    /**
     * 获取 API 前缀
     *
     * @return API 前缀
     */
    public static String getApiPrefix() {
        return API_PREFIX;
    }

    /**
     * 获取完整 API URL
     *
     * @param path API 路径
     * @return 完整 URL
     */
    public static String getFullUrl(String path) {
        return API_PREFIX + path;
    }
}
