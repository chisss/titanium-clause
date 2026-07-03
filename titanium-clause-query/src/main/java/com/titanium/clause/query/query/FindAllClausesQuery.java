package com.titanium.clause.query.query;

/**
 * 查询全部条款列表（CQRS 读侧查询入参）
 *
 * @param tenantId 租户ID
 */
public record FindAllClausesQuery(String tenantId) {
}
