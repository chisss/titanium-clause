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
     * 条款状态常量
     */
    public static final String CLAUSE_STATUS_DRAFT = "DRAFT";
    public static final String CLAUSE_STATUS_ACTIVE = "ACTIVE";
    public static final String CLAUSE_STATUS_INACTIVE = "INACTIVE";
    public static final String CLAUSE_STATUS_EXPIRED = "EXPIRED";

    /**
     * 条款类型常量
     */
    public static final String CLAUSE_TYPE_MAIN = "MAIN";
    public static final String CLAUSE_TYPE_ADDITIONAL = "ADDITIONAL";
    public static final String CLAUSE_TYPE_EXCLUSION = "EXCLUSION";

    /**
     * 请求头常量
     */
    public static final String HEADER_TENANT_ID = "X-Tenant-ID";

    /**
     * 错误码常量
     */
    public static final String ERROR_CLAUSE_NOT_FOUND = "CLAUSE-001";
    public static final String ERROR_CLAUSE_DUPLICATE = "CLAUSE-002";
    public static final String ERROR_CLAUSE_INVALID_STATUS = "CLAUSE-003";
    public static final String ERROR_CLAUSE_EXPIRED = "CLAUSE-004";
    public static final String ERROR_CLAUSE_OPERATION_NOT_ALLOWED = "CLAUSE-005";
}
