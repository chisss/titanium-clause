package com.titanium.clause.query.result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.titanium.clause.valueobject.AgeGenderRate;

import lombok.Data;

/**
 * 缴费规则查询结果（CQRS 读侧稳定返回契约）
 * <p>
 * 由 {@link com.titanium.clause.query.service.ClauseRuleQueryService} 从读模型 {@code PremiumRuleView}
 * 组装，四维年龄性别费率表与职业系数表由 JSON 列反序列化还原，禁止直接返回读模型实体。这是 billing
 * 域费率表查询的数据契约来源。
 * </p>
 */
@Data
public class PremiumRuleQueryResult {
    /** 所属条款ID */
    private String                  clauseId;
    /** 保费计算方式（固定金额/费率计算） */
    private String                  calculationMethod;
    /** 基础保费（固定金额时使用） */
    private BigDecimal              basePremium;
    /** 单一费率标量（历史字段，向后兼容） */
    private BigDecimal              premiumRate;
    /** 缴费方式（趸交/年缴/月缴） */
    private String                  paymentMethod;
    /** 缴费年限 */
    private Integer                 paymentTerm;
    /** 宽限期天数 */
    private Integer                 gracePeriodDays;
    /** 基础费率（结构化模型基准） */
    private BigDecimal              baseRate;
    /** 无赔款优待系数(NCD，车险) */
    private BigDecimal              ncdCoefficient;
    /** 规则引擎规则集编码 */
    private String                  ruleSetCode;
    /** 费率表编码（支持多版本费率表管理，BILL-2） */
    private String                  tableCode;
    /** 费率表版本（支持按版本精确查询，BILL-2） */
    private String                  tableVersion;
    /** 四维年龄性别费率表（年龄×性别×缴费期×保障期，JSON 列反序列化还原） */
    private List<AgeGenderRate>     ageGenderRates;
    /** 职业系数表（key=职业类别码，JSON 列反序列化还原） */
    private Map<String, BigDecimal> occupationCoefficients;
    /** 租户ID */
    private String                  tenantId;
}
