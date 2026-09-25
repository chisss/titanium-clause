package com.titanium.clause.web.dto.approval;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审批驳回条款请求
 */
@Data
public class RejectClauseDTO {
    /**
     * 审批类型码: LEGAL(法务)/ACTUARIAL(精算)/MANAGEMENT(管理层)
     */
    @Size(max = 64)
    private String approvalType;

    /**
     * 审批人ID
     */
    @Size(max = 64)
    private String approverId;

    /**
     * 审批人姓名
     */
    @Size(max = 128)
    private String approverName;

    /**
     * 驳回意见
     */
    @Size(max = 1024)
    private String comment;
}
