package com.codemesh.resolver.util;

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

    public static void setWorkspaceId(Long workspaceId) {
        getMap().put(GLOBAL_WORKSPACE, workspaceId);
    }

    public static void setProjectId(Long projectId) {
        getMap().put(GLOBAL_PROJECT, projectId);
    }

    public static Long getWorkspaceId() {
        return (Long) getMap().get(GLOBAL_WORKSPACE);
    }

    public static Long getProjectId() {
        return (Long) getMap().get(GLOBAL_PROJECT);
    }

    public static void clear() {
        threadLocal.remove();
    }

}
