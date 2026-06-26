package com.titanium.clause.domain.event;

import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

/**
 * 条款已提交审批事件
 */
public record ClauseSubmittedForApprovalEvent(
        ClauseId clauseId,
        String submittedBy,
        LocalDateTime submittedAt
) {
}
