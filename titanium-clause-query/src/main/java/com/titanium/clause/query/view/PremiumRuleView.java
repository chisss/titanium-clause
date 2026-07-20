package com.titanium.clause.query.view;

import java.math.BigDecimal;

import com.titanium.common.jpa.BaseView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 缴费规则读模型实体（CQRS Projection）
 * <p>
 * 对应读模型表 {@code t_premium_rule_view}，与写侧事件存储物理隔离。由
 * {@link com.titanium.clause.query.handler.projection.ClauseProjectionEventHandler} 订阅
 * {@code PremiumRuleSetEvent} 投影而来。一条款仅一份缴费规则，故直接以 {@code clause_id} 作主键，
 * 天然满足 upsert（重复设置覆盖）语义。
 * </p>
 * <p>
 * <b>设计说明</b>：可展示的标量字段（计算方式/基础保费/基础费率/缴费方式/缴费年限/宽限期/NCD系数/
 * 规则集编码）独立建列；四维年龄性别费率表 {@code List<AgeGenderRate>}（年龄×性别×缴费期×保障期）
 * 与职业系数表以 JSON 列整体存储，查询时反序列化还原，与 {@code ProductView} 的 JSON 列模式一致。
 * </p>
 * <p>
 * 继承 {@link BaseView}，复用租户ID、创建/更新时间（投影时间）、乐观锁版本等读模型公共字段。
 * </p>
 */
@Entity
@Table(name = "t_premium_rule_view")
@Getter
@Setter
public class PremiumRuleView extends BaseView {

    /** 所属条款ID（一条款一费率规则，直接作读模型主键） */
    @Id
    @Column(name = "clause_id", nullable = false, length = 36)
    private String     clauseId;

    /** 保费计算方式（固定金额/费率计算） */
    @Column(name = "calculation_method", length = 50)
    private String     calculationMethod;

    /** 基础保费（固定金额时使用） */
    @Column(name = "base_premium", precision = 18, scale = 2)
    private BigDecimal basePremium;

    /** 单一费率标量（历史字段，向后兼容） */
    @Column(name = "premium_rate", precision = 12, scale = 6)
    private BigDecimal premiumRate;

    /** 缴费方式（趸交/年缴/月缴） */
    @Column(name = "payment_method", length = 50)
    private String     paymentMethod;

    /** 缴费年限 */
    @Column(name = "payment_term")
    private Integer    paymentTerm;

    /** 宽限期天数 */
    @Column(name = "grace_period_days")
    private Integer    gracePeriodDays;

    /** 基础费率（结构化模型基准） */
    @Column(name = "base_rate", precision = 12, scale = 6)
    private BigDecimal baseRate;

    /** 无赔款优待系数(NCD，车险) */
    @Column(name = "ncd_coefficient", precision = 12, scale = 6)
    private BigDecimal ncdCoefficient;

    /** 规则引擎规则集编码（复杂费率委托规则引擎） */
    @Column(name = "rule_set_code", length = 64)
    private String     ruleSetCode;

    /** 费率表编码（支持多版本费率表管理，BILL-2） */
    @Column(name = "table_code", length = 64)
    private String     tableCode;

    /** 费率表版本（支持按版本精确查询，BILL-2） */
    @Column(name = "table_version", length = 32)
    private String     tableVersion;

    /** 年龄性别费率表（List&lt;AgeGenderRate&gt; 四维费率整体序列化 JSON） */
    @Lob
    @Column(name = "age_gender_rates_json", columnDefinition = "TEXT")
    private String     ageGenderRatesJson;

    /** 职业系数表（Map&lt;String,BigDecimal&gt; 整体序列化 JSON） */
    @Lob
    @Column(name = "occupation_coefficients_json", columnDefinition = "TEXT")
    private String     occupationCoefficientsJson;
}
