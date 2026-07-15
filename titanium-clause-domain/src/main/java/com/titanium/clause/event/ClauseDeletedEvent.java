package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款已删除事件（草稿硬删除）
 * <p>
 * 表示一条 DRAFT（草稿）条款被彻底删除。读侧投影据此从读模型 {@code t_clause_view} 移除对应记录。
 * </p>
 */
public record ClauseDeletedEvent(
        ClauseId clauseId,
        String deletedBy,
        LocalDateTime deletedAt
) {
}
