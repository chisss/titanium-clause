package com.titanium.clause.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 条款数据传输对象
 */
@Data
public class ClauseDTO {
    /**
     * 条款ID
     */
    private String clauseId;

    /**
     * 条款代码
     */
    private String clauseCode;

    /**
     * 条款名称
     */
    private String clauseName;

    /**
     * 条款类型
     */
    private String clauseType;

    /**
     * 条款内容
     */
    private String content;

    /**
     * 条款描述
     */
    private String description;

    /**
     * 条款状态
     */
    private String status;

    /**
     * 生效日期
     */
    private LocalDateTime effectiveDate;

    /**
     * 失效日期
     */
    private LocalDateTime expiryDate;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 租户ID
     */
    private String tenantId;
}