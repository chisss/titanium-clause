package com.titanium.clause.api.request;

import com.titanium.metadata.enums.insurance.InsuranceProductType;
import java.time.LocalDateTime;
import com.titanium.metadata.enums.clause.ClauseEnum;
import lombok.Data;
/**
 * 更新条款远程入参 DTO（跨服务 Feign 契约）
 */
@Data
public class UpdateClauseRequest {
    /** 条款名称 */
    private String                clauseName;
    /** 条款代码 */
    private String                clauseCode;
    /** 条款类型 */
    private ClauseEnum.ClauseType clauseType;
    /** 条款内容 */
    private String                content;
    /** 条款描述 */
    private String                description;
    /** 险种类型 */
    private InsuranceProductType         insuranceType;
    /** 生效日期 */
    private LocalDateTime         effectiveDate;
    /** 失效日期 */
    private LocalDateTime         expiryDate;
    /** 更新人 */
    private String                updatedBy;
}
