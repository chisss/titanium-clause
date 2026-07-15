package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 删除条款命令（硬删除草稿）
 * <p>
 * 仅用于删除 DRAFT（草稿）状态的条款：草稿尚未生效、无业务凭证依赖，可从事件溯源与读模型彻底移除。
 * 已生效/停用条款的下线走 {@link ArchiveClauseCommand}（归档软删），二者语义不同不可混用。
 * </p>
 */
public record DeleteClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String deletedBy
) {
}
