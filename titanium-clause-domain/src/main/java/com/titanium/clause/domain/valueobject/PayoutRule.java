package com.titanium.clause.domain.valueobject;

import java.math.BigDecimal;

import com.titanium.metadata.enums.clause.PayoutType;

/**
 * 赔付规则值对象
 * <p>
 * 结构化表达保险责任的赔付计算方式，替代原先扁平的 {@code payoutRule} 字符串。
 * 通过 {@link PayoutType} 区分不同险种的赔付语义，并承载对应参数：
 * <ul>
 *   <li>{@code FIXED} 定额给付：使用 {@code fixedAmount}（寿险身故、重疾确诊即赔保额）</li>
 *   <li>{@code PROPORTIONAL} 比例赔付：使用 {@code proportion}（意外伤残按等级比例）</li>
 *   <li>{@code ACTUAL_LOSS} 按损赔付：使用 {@code deductible} + {@code maxPayout}（车险按损定损）</li>
 *   <li>{@code REIMBURSEMENT} 报销：使用 {@code reimbursementRatio} + {@code maxPayout}（医疗险报销）</li>
 * </ul>
 * </p>
 * <p>
 * <b>常规 vs 复杂：</b>常规赔付由 {@code payoutType} + 对应金额/比例字段承载；
 * 阶梯赔付、多次赔付递减等复杂规则通过 {@code ruleSetCode} 委托规则引擎（SpEL）执行。
 * </p>
 *
 * @param payoutType         赔付类型（不可为空）
 * @param fixedAmount        定额给付金额（FIXED 时使用）
 * @param proportion         赔付比例 0-1（PROPORTIONAL 时使用）
 * @param deductible         免赔额结构（ACTUAL_LOSS / REIMBURSEMENT 可选）
 * @param reimbursementRatio 报销比例 0-1（REIMBURSEMENT 时使用）
 * @param maxPayout          单次赔付上限（按损/报销封顶，可选）
 * @param ruleSetCode        规则引擎规则集编码（可选）；非空时复杂赔付计算委托规则引擎
 */
public record PayoutRule(
        PayoutType payoutType,
        BigDecimal fixedAmount,
        BigDecimal proportion,
        Deductible deductible,
        BigDecimal reimbursementRatio,
        BigDecimal maxPayout,
        String ruleSetCode
) {

    /**
     * 构造定额给付规则（寿险身故、重疾确诊即赔）
     *
     * @param fixedAmount 给付金额
     * @return 赔付规则
     */
    public static PayoutRule fixed(BigDecimal fixedAmount) {
        return new PayoutRule(PayoutType.FIXED, fixedAmount, null, null, null, null, null);
    }

    /**
     * 构造比例赔付规则（意外伤残按等级比例）
     *
     * @param proportion 赔付比例 0-1
     * @return 赔付规则
     */
    public static PayoutRule proportional(BigDecimal proportion) {
        return new PayoutRule(PayoutType.PROPORTIONAL, null, proportion, null, null, null, null);
    }

    /**
     * 构造按损赔付规则（车险按损定损）
     *
     * @param deductible 免赔额结构
     * @param maxPayout  赔付上限
     * @return 赔付规则
     */
    public static PayoutRule actualLoss(Deductible deductible, BigDecimal maxPayout) {
        return new PayoutRule(PayoutType.ACTUAL_LOSS, null, null, deductible, null, maxPayout, null);
    }

    /**
     * 构造报销赔付规则（医疗险报销）
     *
     * @param reimbursementRatio 报销比例 0-1
     * @param deductible         免赔额结构
     * @param maxPayout          报销上限
     * @return 赔付规则
     */
    public static PayoutRule reimbursement(BigDecimal reimbursementRatio, Deductible deductible, BigDecimal maxPayout) {
        return new PayoutRule(PayoutType.REIMBURSEMENT, null, null, deductible, reimbursementRatio, maxPayout, null);
    }

    /**
     * 是否委托规则引擎执行复杂赔付计算
     *
     * @return true 表示配置了规则集编码
     */
    public boolean usesRuleEngine() {
        return ruleSetCode != null && !ruleSetCode.isBlank();
    }
}
