package com.titanium.clause.query.query;

import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 根据状态查询条款列表（CQRS 读侧查询入参）
 *
 * @param status 条款状态
 * @param tenantId 租户ID
 */
public record FindClausesByStatusQuery(ClauseEnum.ClauseStatus status, String tenantId) {
}
