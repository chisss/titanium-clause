package com.titanium.clause.domain.command;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 创建条款命令
 */
public record CreateClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClauseCode clauseCode,
        ClauseName clauseName,
        String clauseType,
        String content,
        String description,
        LocalDateTime effectiveDate,
        LocalDateTime expiryDate,
        Set<String> productIds,
        String tenantId,
        String createdBy
) {
}