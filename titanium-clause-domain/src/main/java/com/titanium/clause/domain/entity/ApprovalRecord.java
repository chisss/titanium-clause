package com.titanium.clause.domain.entity;

import java.time.LocalDateTime;

import com.titanium.clause.domain.enums.ApprovalStatus;
import com.titanium.clause.domain.enums.ApprovalType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审批记录实体
 */
@Data
@NoArgsConstructor
public class ApprovalRecord {
    /** 审批记录ID */
    private String         recordId;
    /** 审批类型: LEGAL(法务)/ACTUARIAL(精算)/MANAGEMENT(管理层) */
    private ApprovalType   approvalType;
    /** 审批人ID */
    private String         approverId;
    /** 审批人姓名 */
    private String         approverName;
    /** 审批状态: PENDING/APPROVED/REJECTED */
    private ApprovalStatus approvalStatus;
    /** 审批意见 */
    private String        comment;
    /** 审批时间 */
    private LocalDateTime approvalTime;
}
