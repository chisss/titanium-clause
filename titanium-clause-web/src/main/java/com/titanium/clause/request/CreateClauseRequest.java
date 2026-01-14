package com.titanium.clause.request;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 创建条款请求
 */
@Data
public class CreateClauseRequest {
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
}