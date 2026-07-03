package com.titanium.clause.query.query;

/**
 * 根据条款代码查询条款（CQRS 读侧查询入参）
 *
 * @param clauseCode 条款代码
 * @param tenantId 租户ID
 */
public record FindClauseByCodeQuery(String clauseCode, String tenantId) {
}
