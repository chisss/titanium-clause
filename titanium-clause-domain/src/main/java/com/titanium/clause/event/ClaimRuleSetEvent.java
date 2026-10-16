package com.titanium.clause.event;

import java.time.LocalDateTime;

import org.axonframework.serialization.Revision;

import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 理赔规则已设置事件
 */
@Revision("1")
public record ClaimRuleSetEvent(
        ClauseId clauseId,
        ClaimRule claimRule,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
