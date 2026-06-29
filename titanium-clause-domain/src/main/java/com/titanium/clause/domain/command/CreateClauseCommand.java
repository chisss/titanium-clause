package com.titanium.clause.domain.command;

import java.time.LocalDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.Version;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 创建条款命令
 */
public record CreateClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClauseCode clauseCode,
        ClauseName clauseName,
        ClauseEnum.ClauseType clauseType,
        String content,
        String description,
        InsuranceType insuranceType,
        Version version,
        LocalDateTime effectiveDate,
        LocalDateTime expiryDate,
        String tenantId,
        String createdBy
) {
}
