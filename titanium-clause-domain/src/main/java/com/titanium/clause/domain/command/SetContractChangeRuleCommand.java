package com.titanium.clause.domain.command;

import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
