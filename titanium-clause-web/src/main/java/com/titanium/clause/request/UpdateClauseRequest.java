package com.titanium.clause.request;

import java.time.LocalDateTime;

import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.Data;

/**
 * 更新条款请求
 */
@Data
public class UpdateClauseRequest {
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
    private ClauseEnum.ClauseType clauseType;

    /**
     * 条款内容
     */
    private String content;

    /**
     * 条款描述
     */
    private String description;

    /**
     * 生效日期
     */
    private LocalDateTime effectiveDate;

    /**
     * 失效日期
     */
    private LocalDateTime expiryDate;

    /**
     * 更新人
     */
    private String updatedBy;
}
