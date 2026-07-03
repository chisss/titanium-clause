package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.valueobject.ClauseId;

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
