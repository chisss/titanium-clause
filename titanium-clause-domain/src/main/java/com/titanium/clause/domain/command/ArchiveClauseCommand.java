package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 条款归档命令
 */
public record ArchiveClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String archivedBy
) {
}
