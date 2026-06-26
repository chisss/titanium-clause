package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

/**
 * 缴费规则已设置事件
 */
public record PremiumRuleSetEvent(
        ClauseId clauseId,
        PremiumRule premiumRule,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
