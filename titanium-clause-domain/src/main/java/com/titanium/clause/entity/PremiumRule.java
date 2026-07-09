package com.titanium.clause.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.titanium.clause.valueobject.AgeGenderRate;

/**
 * 缴费规则（聚合内实体，不可变值对象）
 * <p>
 * 费率已从单标量升级为结构化模型：{@link #baseRate} 基础费率、{@link #ageGenderRates} 年龄性别费率表（寿险/重疾）、
 * {@link #ncdCoefficient} 无赔款优待系数（车险）、{@link #occupationCoefficients} 职业系数表（意外险）。
 * <b>常规费率走结构化字段，复杂费率计算走 {@link #ruleSetCode} + 规则引擎。</b>原标量 {@link #premiumRate} 保留向后兼容。
 * </p>
 *
 * @param calculationMethod       保费计算方式（固定金额/费率计算）
 * @param basePremium             基础保费（固定金额时使用）
 * @param premiumRate             【已结构化-向后兼容】单一费率标量
 * @param paymentMethod           缴费方式（趸交/年缴/月缴）
 * @param paymentTerm             缴费年限
 * @param gracePeriodDays         宽限期天数
 * @param baseRate                基础费率（结构化模型基准）
 * @param ageGenderRates          年龄性别费率表
 * @param ncdCoefficient          无赔款优待系数(NCD)
 * @param occupationCoefficients  职业系数表（key=职业类别码）
 * @param ruleSetCode             规则引擎规则集编码（可选，复杂费率委托规则引擎）
 * @param createdAt               创建时间
 * @param updatedAt               更新时间
 */
public record PremiumRule(
        String calculationMethod,
        BigDecimal basePremium,
        BigDecimal premiumRate,
        String paymentMethod,
        Integer paymentTerm,
        Integer gracePeriodDays,
        BigDecimal baseRate,
        List<AgeGenderRate> ageGenderRates,
        BigDecimal ncdCoefficient,
        Map<String, BigDecimal> occupationCoefficients,
        String ruleSetCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * 紧凑构造器：年龄性别费率表空值归一化为空列表，保证不可变与遍历安全。
     */
    public PremiumRule {
        ageGenderRates = ageGenderRates == null ? new ArrayList<>() : ageGenderRates;
    }

    /**
     * 按年龄性别查找适用费率（充血：费率解析内聚于值对象）。
     * <p>
     * 优先从年龄性别费率表匹配；未命中回退基础费率，再回退历史标量费率。复杂费率（配置 ruleSetCode）由应用层委托规则引擎。
     * </p>
     *
     * @param age    投保年龄
     * @param gender 性别 M/F
     * @return 适用费率，无任何配置时返回 null
     */
    public BigDecimal resolveRate(int age, String gender) {
        for (AgeGenderRate ageGenderRate : ageGenderRates) {
            if (ageGenderRate.matches(age, gender)) {
                return ageGenderRate.rate();
            }
        }
        if (baseRate != null) {
            return baseRate;
        }
        return premiumRate;
    }
}
