package com.titanium.clause.domain.command;

import java.time.LocalDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 更新条款命令
 */
public record UpdateClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClauseCode clauseCode,
        ClauseName clauseName,
        ClauseEnum.ClauseType clauseType,
        String content,
        String description,
        InsuranceType insuranceType,
        LocalDateTime effectiveDate,
        LocalDateTime expiryDate,
        String tenantId,
        String updatedBy
) {
}
