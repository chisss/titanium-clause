package com.titanium.clause.valueobject;

import java.math.BigDecimal;

/**
 * 年龄性别费率值对象
 * <p>
 * 用于表达寿险、重疾险等险种按"年龄段 + 性别"差异定价的费率表条目。
 * 一份保险责任的费率表由多个本值对象组成（{@code List<AgeGenderRate>}）。
 * </p>
 * <p>
 * 性别取值约定：{@code "M"} 男、{@code "F"} 女、{@code "ALL"} 不分性别。
 * 年龄区间为闭区间 [minAge, maxAge]。
 * </p>
 *
 * <p>
 * 寿险费率表为四维定价：年龄 × 性别 × 缴费期 × 保障期。{@code paymentTerm}/{@code coverageTerm}
 * 为 {@code null} 表示该维度不限（通配），用于兼容不区分缴费期/保障期的简单费率表。
 * </p>
 *
 * @param minAge       最小年龄（含）
 * @param maxAge       最大年龄（含）
 * @param gender       性别：M/F/ALL
 * @param paymentTerm  缴费期（年数，null 表示不限）
 * @param coverageTerm 保障期（年数，null 表示不限）
 * @param rate         该维度组合对应费率
 */
public record AgeGenderRate(
        Integer minAge,
        Integer maxAge,
        String gender,
        Integer paymentTerm,
        Integer coverageTerm,
        BigDecimal rate
) {

    /**
     * 四维匹配：判断给定年龄/性别/缴费期/保障期是否落入本费率条目。
     * 缴费期/保障期为 null 的维度视为通配（不参与筛选）。
     *
     * @param age          投保年龄
     * @param gender       性别 M/F
     * @param paymentTerm  缴费期（年数）
     * @param coverageTerm 保障期（年数）
     * @return 是否匹配
     */
    public boolean matches(int age, String gender, Integer paymentTerm, Integer coverageTerm) {
        boolean ageMatch = (minAge == null || age >= minAge) && (maxAge == null || age <= maxAge);
        boolean genderMatch = this.gender == null || "ALL".equals(this.gender) || this.gender.equals(gender);
        boolean paymentMatch = this.paymentTerm == null || this.paymentTerm.equals(paymentTerm);
        boolean coverageMatch = this.coverageTerm == null || this.coverageTerm.equals(coverageTerm);
        return ageMatch && genderMatch && paymentMatch && coverageMatch;
    }

    /**
     * 二维匹配（仅年龄性别），兼容不区分缴费期/保障期的调用：
     * 只筛选年龄与性别，不对缴费期/保障期维度做限制（无论条目是否设定该维度均放行）。
     *
     * @param age    投保年龄
     * @param gender 性别 M/F
     * @return 是否匹配
     */
    public boolean matches(int age, String gender) {
        boolean ageMatch = (minAge == null || age >= minAge) && (maxAge == null || age <= maxAge);
        boolean genderMatch = this.gender == null || "ALL".equals(this.gender) || this.gender.equals(gender);
        return ageMatch && genderMatch;
    }
}
