package com.titanium.clause.domain.command;

import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 条款归档命令
 */
public record ArchiveClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String archivedBy
) {
}
