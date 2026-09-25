package com.titanium.clause.web.dto.approval;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审批通过条款请求
 */
@Data
public class ApproveClauseDTO {
    /**
     * 审批类型码: LEGAL(法务)/ACTUARIAL(精算)/MANAGEMENT(管理层)
     */
    @NotBlank
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
    @NotBlank
    @Size(max = 128)
    private String approverName;

    /**
     * 审批意见
     */
    @Size(max = 1024)
    private String comment;
}
