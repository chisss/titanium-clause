package com.titanium.clause.domain.command;

import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 条款修订命令（基于当前版本创建新版本）
 */
public record ReviseClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClauseId newClauseId,
        String revisedBy
) {
}
