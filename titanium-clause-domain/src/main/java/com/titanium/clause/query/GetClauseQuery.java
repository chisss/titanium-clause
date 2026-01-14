package com.titanium.clause.query;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 获取条款查询
 */
public record GetClauseQuery(ClauseId clauseId, String tenantId) {
}
