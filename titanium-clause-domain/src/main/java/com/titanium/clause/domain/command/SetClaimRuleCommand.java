package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.valueobject.ClauseId;

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
