package com.titanium.clause.web.response;

import java.time.LocalDateTime;

import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.Data;

/**
 * 条款响应 VO（后台/端上展示契约）
 */
@Data
public class ClauseVO {
    /** 条款ID */
    private String                  clauseId;

    /** 条款代码 */
    private String                  clauseCode;

    /** 条款名称 */
    private String                  clauseName;

    /** 条款类型 */
    private ClauseEnum.ClauseType   clauseType;

    /** 条款内容 */
    private String                  content;

    /** 条款描述 */
    private String                  description;

    /** 状态 */
    private ClauseEnum.ClauseStatus status;

    /** 版本号 */
    private String                  version;

    /** 险种类型 */
    private InsuranceType           insuranceType;

    /** 父条款ID */
    private String                  parentClauseId;

    /** 生效日期 */
    private LocalDateTime           effectiveDate;

    /** 失效日期 */
    private LocalDateTime           expiryDate;

    /** 创建时间 */
    private LocalDateTime           createdAt;

    /** 更新时间 */
    private LocalDateTime           updatedAt;

    /** 创建人 */
    private String                  createdBy;

    /** 更新人 */
    private String                  updatedBy;

    /** 租户ID */
    private String                  tenantId;
}
