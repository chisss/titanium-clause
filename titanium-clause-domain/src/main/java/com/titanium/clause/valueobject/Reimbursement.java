package com.titanium.clause.valueobject;

import java.math.BigDecimal;

import com.titanium.metadata.enums.clause.DeductibleType;

/**
 * 报销规则值对象
 * <p>
 * 结构化表达理赔规则中的报销限额与报销比例，替代原先扁平的 {@code payoutRatio} 字符串。
 * 主要服务于医疗险等报销型责任。
 * </p>
 *
 * @param annualLimit        年度报销限额（封顶，可选）
 * @param perTimeLimit       单次报销限额（可选）
 * @param reimbursementRatio 报销比例 0-1（如社保内 90%、社保外 60%）
 * @param deductible         报销起付线/免赔额结构（可选，复用 {@link Deductible}）
 */
public record Reimbursement(
        BigDecimal annualLimit,
        BigDecimal perTimeLimit,
        BigDecimal reimbursementRatio,
        Deductible deductible
) {

    /**
     * 构造基础报销规则（年度限额 + 报销比例 + 起付线）
     *
     * @param annualLimit        年度报销限额
     * @param reimbursementRatio 报销比例 0-1
     * @param deductibleAmount   起付线金额
     * @return 报销规则值对象
     */
    public static Reimbursement of(BigDecimal annualLimit, BigDecimal reimbursementRatio, BigDecimal deductibleAmount) {
        Deductible deductible = deductibleAmount == null
                ? new Deductible(DeductibleType.NONE, BigDecimal.ZERO, null)
                : Deductible.fixedAmount(deductibleAmount);
        return new Reimbursement(annualLimit, null, reimbursementRatio, deductible);
    }
}
