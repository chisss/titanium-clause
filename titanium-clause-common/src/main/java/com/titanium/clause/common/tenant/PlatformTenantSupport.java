package com.titanium.clause.common.tenant;

import java.util.LinkedHashSet;
import java.util.Set;

import com.titanium.clause.common.constant.ClauseConstants;

/**
 * 平台默认租户回退支持。
 * <p>
 * 读侧查询在返回「当前租户自有数据」的同时，一并返回平台公共租户
 * ({@link ClauseConstants#PLATFORM_TENANT}) 的共享数据，使各业务租户可选用平台预置的公共条款/责任模板。
 * 仅用于读路径；写路径（新增/修改/删除、聚合存在性校验）不得使用此回退。
 * </p>
 */
public final class PlatformTenantSupport {

    private PlatformTenantSupport() {
    }

    /**
     * 构造查询租户集合：当前租户 + 平台公共租户（去重、保序）。
     * <p>
     * 当前租户为空时仅返回平台公共租户；当前租户即平台租户时集合仅含其自身（不重复）。
     * </p>
     *
     * @param tenantId 当前请求租户
     * @return 用于 {@code tenantId IN (...)} 下推的租户集合
     */
    public static Set<String> scope(String tenantId) {
        Set<String> tenants = new LinkedHashSet<>();
        if (tenantId != null && !tenantId.trim().isEmpty()) {
            tenants.add(tenantId);
        }
        tenants.add(ClauseConstants.PLATFORM_TENANT);
        return tenants;
    }
}
