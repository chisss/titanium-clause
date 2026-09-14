package com.titanium.clause.common.constant;

import com.titanium.metadata.topic.CrossDomainTopics;

/**
 * 条款服务常量类
 */
public class ClauseConstants {
    /**
     * Kafka主题常量
     * <p>
     * 仅保留条款状态变更主题——条款的创建/修改/废止在聚合内**统一收敛为状态流转**，
     * 外发只经「创建」与「状态变更」两条出口（见 {@code ClauseKafkaEventPublisher}），
     * 原 TOPIC_CLAUSE_CREATED/UPDATED/DELETED 三个常量自声明起从无发布点，已删除（m5-903）。
     * </p>
     */
    public static final String TOPIC_CLAUSE_STATUS_CHANGED = CrossDomainTopics.CLAUSE_STATUS_CHANGED;

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
