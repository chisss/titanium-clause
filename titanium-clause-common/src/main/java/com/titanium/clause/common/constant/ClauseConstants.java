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
     * 错误码常量
     */
    public static final String ERROR_CLAUSE_NOT_FOUND = "CLAUSE-001";
    public static final String ERROR_CLAUSE_DUPLICATE = "CLAUSE-002";
    public static final String ERROR_CLAUSE_INVALID_STATUS = "CLAUSE-003";
    public static final String ERROR_CLAUSE_EXPIRED = "CLAUSE-004";
    public static final String ERROR_CLAUSE_OPERATION_NOT_ALLOWED = "CLAUSE-005";
}
