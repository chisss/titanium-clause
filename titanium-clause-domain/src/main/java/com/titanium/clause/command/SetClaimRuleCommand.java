package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 设置理赔规则命令
 */
public record SetClaimRuleCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClaimRule claimRule,
        String updatedBy
) {
}
