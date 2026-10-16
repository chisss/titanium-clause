package com.titanium.clause.event;

import java.time.LocalDateTime;

import org.axonframework.serialization.Revision;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款已提交审批事件
 */
@Revision("1")
public record ClauseSubmittedForApprovalEvent(
        ClauseId clauseId,
        String submittedBy,
        LocalDateTime submittedAt
) {
}
