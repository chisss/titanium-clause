package com.titanium.clause.query.query;

/**
 * 根据条款ID查询其全部保险责任（CQRS 读侧查询入参）
 *
 * @param clauseId 条款ID
 * @param tenantId 租户ID
 */
public record FindCoveragesByClauseIdQuery(String clauseId, String tenantId) {
}
