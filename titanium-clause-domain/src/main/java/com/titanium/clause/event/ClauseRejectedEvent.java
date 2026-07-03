package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.ApprovalRecord;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款审批驳回事件
 */
public record ClauseRejectedEvent(
        ClauseId clauseId,
        ApprovalRecord approvalRecord,
        String rejectedBy,
        LocalDateTime rejectedAt
) {
}
