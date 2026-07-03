package com.titanium.clause.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.titanium.clause.valueobject.AgeGenderRate;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缴费规则实体类
 * <p>
 * 费率已从单标量 {@link #premiumRate} 升级为结构化费率模型，支持表达险种差异：
 * <ul>
 *   <li>{@link #baseRate} 基础费率：所有险种通用的基准费率；</li>
 *   <li>{@link #ageGenderRates} 年龄性别费率表：寿险/重疾按"年龄段+性别"差异定价；</li>
 *   <li>{@link #ncdCoefficient} 无赔款优待系数(NCD)：车险按历史出险记录浮动；</li>
 *   <li>{@link #occupationCoefficients} 职业系数表：意外险按职业风险等级浮动。</li>
 * </ul>
 * <b>常规费率走上述结构化字段，复杂费率计算走 {@link #ruleSetCode} + 规则引擎。</b>
 * 原标量 {@link #premiumRate} 字段保留以向后兼容历史数据与调用方。
 * </p>
 */
@Data
@NoArgsConstructor
public class PremiumRule {
    // 保费计算方式（如固定金额/费率计算）
    private String                calculationMethod;
    // 基础保费（固定金额时使用）
    private BigDecimal            basePremium;
    // 【已结构化-向后兼容】单一费率标量；新逻辑请用 baseRate / ageGenderRates 等结构化字段
    private BigDecimal            premiumRate;
    // 缴费方式（趸交/年缴/月缴）
    private String                paymentMethod;
    // 缴费年限
    private Integer               paymentTerm;
    // 宽限期天数
    private Integer               gracePeriodDays;

    // ===== 结构化费率因子 =====
    // 基础费率（结构化模型的基准费率）
    private BigDecimal            baseRate;
    // 年龄性别费率表（寿险/重疾按年龄段+性别定价）
    private List<AgeGenderRate>   ageGenderRates = new ArrayList<>();
    // 无赔款优待系数(NCD)（车险按出险记录浮动，如 0.85 表示 85%）
    private BigDecimal            ncdCoefficient;
    // 职业系数表（意外险按职业类别浮动，key=职业类别码，value=系数）
    private Map<String, BigDecimal> occupationCoefficients;
    // 规则引擎规则集编码（可选）：复杂费率计算委托规则引擎(SpEL)，常规走结构化字段
    private String                ruleSetCode;

    private LocalDateTime         createdAt;
    private LocalDateTime         updatedAt;

    /**
     * 按年龄性别查找适用费率
     * <p>
     * 优先从结构化年龄性别费率表匹配；未命中则回退到基础费率，再回退到历史标量费率。
     * 复杂费率（配置了 ruleSetCode）应由应用层委托规则引擎计算，本方法仅处理常规结构化场景。
     * </p>
     *
     * @param age    投保年龄
     * @param gender 性别 M/F
     * @return 适用费率，无任何配置时返回 null
     */
    public BigDecimal resolveRate(int age, String gender) {
        if (ageGenderRates != null) {
            for (AgeGenderRate ageGenderRate : ageGenderRates) {
                if (ageGenderRate.matches(age, gender)) {
                    return ageGenderRate.rate();
                }
            }
        }
        if (baseRate != null) {
            return baseRate;
        }
        return premiumRate;
    }
}
