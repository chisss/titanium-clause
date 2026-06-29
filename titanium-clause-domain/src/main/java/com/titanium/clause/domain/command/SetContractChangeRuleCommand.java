package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.valueobject.ClauseId;

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
