package com.titanium.clause.domain.query;

import com.titanium.metadata.enums.InsuranceType;

/**
 * 按险种类型查询条款（仓储 findByType 实为按险种类型查询）
 *
 * @param clauseType 险种类型（强类型枚举，REST 边界由 Controller 用 fromCode 转换）
 * @param tenantId   租户ID
 */
public record GetClausesByTypeQuery(InsuranceType clauseType, String tenantId) {
}
