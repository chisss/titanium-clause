package com.titanium.clause.domain.valueobject;

import java.math.BigDecimal;

import com.titanium.metadata.enums.clause.DeductibleType;

/**
 * 免赔额值对象
 * <p>
 * 结构化表达理赔/赔付中的免赔额，替代原先扁平的 {@code deductibleAmount} 字符串。
 * 通过 {@link DeductibleType} 区分免赔计算方式：
 * <ul>
 *   <li>{@code NONE} 无免赔</li>
 *   <li>{@code FIXED_AMOUNT} 固定金额免赔：使用 {@code amount}（车险绝对免赔额）</li>
 *   <li>{@code PROPORTIONAL} 比例免赔：使用 {@code ratio}（按损失比例免赔）</li>
 * </ul>
 * </p>
 *
 * @param type   免赔类型（不可为空）
 * @param amount 固定免赔金额（FIXED_AMOUNT 时使用）
 * @param ratio  免赔比例 0-1（PROPORTIONAL 时使用）
 */
public record Deductible(
        DeductibleType type,
        BigDecimal amount,
        BigDecimal ratio
) {

    /**
     * 无免赔
     *
     * @return 免赔额值对象
     */
    public static Deductible none() {
        return new Deductible(DeductibleType.NONE, BigDecimal.ZERO, null);
    }

    /**
     * 固定金额免赔
     *
     * @param amount 免赔金额
     * @return 免赔额值对象
     */
    public static Deductible fixedAmount(BigDecimal amount) {
        return new Deductible(DeductibleType.FIXED_AMOUNT, amount, null);
    }

    /**
     * 比例免赔
     *
     * @param ratio 免赔比例 0-1
     * @return 免赔额值对象
     */
    public static Deductible proportional(BigDecimal ratio) {
        return new Deductible(DeductibleType.PROPORTIONAL, null, ratio);
    }
}
