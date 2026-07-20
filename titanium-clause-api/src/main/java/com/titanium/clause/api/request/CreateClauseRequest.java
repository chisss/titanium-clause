package com.titanium.clause.api.request;

import java.time.LocalDateTime;

import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.Data;

/**
 * 创建条款远程入参 DTO（跨服务 Feign 契约）
 */
@Data
public class CreateClauseRequest {
    /** 条款代码 */
    private String                clauseCode;

    /** 条款名称 */
    private String                clauseName;

    /** 条款类型 */
    private ClauseEnum.ClauseType clauseType;

    /** 条款内容 */
    private String                content;

    /** 条款描述 */
    private String                description;

    /** 险种类型 */
    private InsuranceType         insuranceType;

    /** 生效日期 */
    private LocalDateTime         effectiveDate;

    /** 失效日期 */
    private LocalDateTime         expiryDate;

    /** 创建人 */
    private String                createdBy;
}
