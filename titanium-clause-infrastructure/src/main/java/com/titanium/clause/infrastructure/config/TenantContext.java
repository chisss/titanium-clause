package com.titanium.clause.infrastructure.config;

/**
 * 租户上下文，用于线程本地存储租户ID
 */
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * 设置当前租户ID
     * @param tenantId 租户ID
     */
    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * 获取当前租户ID
     * @return 租户ID
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * 清除当前租户ID
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
