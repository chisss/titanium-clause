package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 设置合同变更规则命令
 */
public record SetContractChangeRuleCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ContractChangeRule contractChangeRule,
        String updatedBy
) {
}
