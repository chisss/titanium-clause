package com.titanium.clause.domain.query;

import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 获取条款查询
 */
public record GetClauseByIdQuery(ClauseId clauseId, String tenantId) {
}
