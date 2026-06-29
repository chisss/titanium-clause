package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 设置缴费规则命令
 */
public record SetPremiumRuleCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        PremiumRule premiumRule,
        String updatedBy
) {
}
