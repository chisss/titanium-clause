package com.titanium.clause.response;

import com.titanium.metadata.enums.clause.ClauseEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 条款响应
 */
@Data
public class ClauseResponse {
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
     * 状态
     */
    private ClauseEnum.ClauseStatus status;

    /**
     * 生效日期
     */
    private LocalDateTime effectiveDate;

    /**
     * 失效日期
     */
    private LocalDateTime expiryDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;
}