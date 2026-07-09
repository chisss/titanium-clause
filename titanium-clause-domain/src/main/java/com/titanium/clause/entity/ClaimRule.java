package com.titanium.clause.entity;

import java.time.LocalDateTime;

import com.titanium.clause.valueobject.Deductible;
import com.titanium.clause.valueobject.Reimbursement;

/**
 * 理赔规则（聚合内实体，不可变值对象）
 * <p>
 * 免赔与报销已结构化：{@link #deductible} 区分无免赔/固定金额/比例免赔；{@link #reimbursement} 承载报销限额与比例（医疗险）。
 * <b>常规理赔走结构化字段，复杂理赔判定走 {@link #ruleSetCode} + 规则引擎。</b>原字符串字段保留向后兼容。
 * </p>
 *
 * @param reportDeadlineDays   报案时效天数
 * @param requiredMaterials    理赔所需材料
 * @param settlementPeriodDays 理赔结案时效天数
 * @param payoutRatio          【已结构化-向后兼容】赔付比例文字描述
 * @param deductibleAmount     【已结构化-向后兼容】免赔额文字描述
 * @param deductible           结构化免赔额（类型 + 金额/比例）
 * @param reimbursement        结构化报销规则（限额 + 比例，医疗险）
 * @param ruleSetCode          规则引擎规则集编码（可选）
 * @param createdAt            创建时间
 * @param updatedAt            更新时间
 */
public record ClaimRule(
        Integer reportDeadlineDays,
        String requiredMaterials,
        Integer settlementPeriodDays,
        String payoutRatio,
        String deductibleAmount,
        Deductible deductible,
        Reimbursement reimbursement,
        String ruleSetCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
