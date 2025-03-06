package com.codemesh.sdk.util;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fio
 * @date 2025/3/5
 */
public class ThreadLocalUtil {

    private static final String GLOBAL_WORKSPACE = "WORKSPACE_ID";

    private static final String GLOBAL_PROJECT = "PROJECT_ID";

    private static final ThreadLocal<Map<String, Object>> threadLocal = new TransmittableThreadLocal<>();

    private static Map<String, Object> getMap() {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            synchronized (ThreadLocalUtil.class) {
                map = threadLocal.get();
                if (map != null) {
                    return map;
                }
                map = new HashMap<>();
                threadLocal.set(map);
            }

        }
        return map;
    }

    public static void setWorkspaceId(String workspaceId) {
        getMap().put(GLOBAL_WORKSPACE, workspaceId);
    }

    public static void setProjectId(String projectId) {
        getMap().put(GLOBAL_PROJECT, projectId);
    }

    public static String getWorkspaceId() {
        return (String) getMap().get(GLOBAL_WORKSPACE);
    }

    public static String getProjectId() {
        return (String) getMap().get(GLOBAL_PROJECT);
    }

    public static void clear() {
        threadLocal.remove();
    }

}
