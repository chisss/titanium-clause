package com.titanium.clause.domain.query;

import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 按状态查询条款
 *
 * @param status   条款状态（强类型枚举，REST 边界由 Controller 用 fromCode 转换）
 * @param tenantId 租户ID
 */
public record GetClausesByStatusQuery(ClauseEnum.ClauseStatus status, String tenantId) {
}
