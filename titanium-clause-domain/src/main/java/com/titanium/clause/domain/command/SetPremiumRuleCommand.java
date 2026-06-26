package com.titanium.clause.domain.command;

import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
