package com.titanium.clause.query.query;

/**
 * 根据ID查询条款（CQRS 读侧查询入参）
 *
 * @param clauseId 条款ID
 * @param tenantId 租户ID
 */
public record FindClauseByIdQuery(String clauseId, String tenantId) {
}
