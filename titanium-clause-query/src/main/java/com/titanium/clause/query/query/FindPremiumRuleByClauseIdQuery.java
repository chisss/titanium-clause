package com.titanium.clause.query.query;

/**
 * 根据条款ID查询其缴费规则（CQRS 读侧查询入参）
 *
 * @param clauseId 条款ID
 * @param tenantId 租户ID
 */
public record FindPremiumRuleByClauseIdQuery(String clauseId, String tenantId) {
}
