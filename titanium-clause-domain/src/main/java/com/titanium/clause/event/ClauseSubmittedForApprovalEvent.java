package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款已提交审批事件
 */
public record ClauseSubmittedForApprovalEvent(
        ClauseId clauseId,
        String submittedBy,
        LocalDateTime submittedAt
) {
}
