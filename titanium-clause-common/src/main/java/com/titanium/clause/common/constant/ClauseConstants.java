package com.titanium.clause.common.constant;

/**
 * 条款服务常量类
 */
public class ClauseConstants {
    /**
     * Kafka主题常量
     */
    public static final String TOPIC_CLAUSE_CREATED = "clause-created";
    public static final String TOPIC_CLAUSE_UPDATED = "clause-updated";
    public static final String TOPIC_CLAUSE_DELETED = "clause-deleted";
    public static final String TOPIC_CLAUSE_STATUS_CHANGED = "clause-status-changed";

    /**
     * 请求头常量
     */
    public static final String HEADER_TENANT_ID = "X-Tenant-ID";

    /**
     * 平台级公共租户标识。
     * <p>
     * 平台预置的公共条款/责任模板（如四大传统寿险条款）以此租户落库，供所有业务租户在产品配置时选用。
     * 读侧查询在返回「当前租户自有数据」的同时，一并返回该公共租户的共享数据（平台默认回退）。
     * 写侧（新增/修改/删除、聚合存在性校验）不适用此回退，业务租户不得篡改平台公共模板。
     * </p>
     */
    public static final String PLATFORM_TENANT = "default";
}
