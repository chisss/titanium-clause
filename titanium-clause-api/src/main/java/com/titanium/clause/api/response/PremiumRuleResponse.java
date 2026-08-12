package com.titanium.clause.api.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Data;
/**
 * 缴费规则数据传输对象（对外契约）
 * <p>
 * 条款缴费规则的跨服务传输契约，承载 billing 域保费计算所需的费率数据：基础费率、四维年龄性别费率表
 * （{@link AgeGenderRateResponse}：年龄×性别×缴费期×保障期）、NCD 系数、职业系数表、规则集编码等。
 * 由 {@code ClauseApi#getPremiumRuleByClauseId} 返回，是 billing 费率表查询模式的数据入口。
 * </p>
 */
@Data
public class PremiumRuleResponse {
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
    /** 四维年龄性别费率表（年龄×性别×缴费期×保障期） */
    private List<AgeGenderRateResponse>  ageGenderRates;
    /** 职业系数表（key=职业类别码） */
    private Map<String, BigDecimal> occupationCoefficients;
    /** 租户ID */
    private String                  tenantId;
}
