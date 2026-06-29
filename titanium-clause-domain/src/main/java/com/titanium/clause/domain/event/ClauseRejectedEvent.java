package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.entity.ApprovalRecord;
import com.titanium.clause.domain.valueobject.ClauseId;

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
