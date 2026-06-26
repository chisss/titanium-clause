package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

/**
 * 理赔规则已设置事件
 */
public record ClaimRuleSetEvent(
        ClauseId clauseId,
        ClaimRule claimRule,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
