package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.valueobject.ClauseId;

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
