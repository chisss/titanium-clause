package com.titanium.clause.query.query;

/**
 * 根据条款ID、费率表编码、版本查询缴费规则（CQRS 读侧查询入参，BILL-2）
 * <p>
 * 支持按 tableCode+version 精确匹配特定版本费率表。tableCode 和 version 均为 null 时
 * 返回默认规则（向后兼容）；tableCode 非空但 version 为 null 时返回该 tableCode 最新版；
 * 均非空时精确匹配。
 * </p>
 *
 * @param clauseId  条款ID
 * @param tableCode 费率表编码（可选）
 * @param version   费率表版本（可选）
 * @param tenantId  租户ID
 */
public record FindPremiumRuleByClauseIdAndVersionQuery(String clauseId, String tableCode, String version,
                                                        String tenantId) {
}
