package com.titanium.clause.domain.valueobject;

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
 * @param minAge 最小年龄（含）
 * @param maxAge 最大年龄（含）
 * @param gender 性别：M/F/ALL
 * @param rate   该年龄性别段对应费率
 */
public record AgeGenderRate(
        Integer minAge,
        Integer maxAge,
        String gender,
        BigDecimal rate
) {

    /**
     * 判断给定年龄性别是否落入本费率条目区间
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
