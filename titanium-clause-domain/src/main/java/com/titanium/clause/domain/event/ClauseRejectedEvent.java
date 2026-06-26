package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.ApprovalRecord;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

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
