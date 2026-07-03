package com.titanium.clause.query.query;

import com.titanium.metadata.enums.InsuranceType;

/**
 * 根据险种类型查询条款列表（CQRS 读侧查询入参）
 *
 * @param insuranceType 险种类型
 * @param tenantId 租户ID
 */
public record FindClausesByTypeQuery(InsuranceType insuranceType, String tenantId) {
}
