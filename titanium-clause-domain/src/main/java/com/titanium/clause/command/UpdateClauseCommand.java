package com.titanium.clause.command;

import java.time.LocalDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

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
        InsuranceProductType insuranceType,
        LocalDateTime effectiveDate,
        LocalDateTime expiryDate,
        String tenantId,
        String updatedBy
) {
}
