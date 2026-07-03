package com.titanium.clause.entity;

import java.time.LocalDateTime;

import com.titanium.clause.valueobject.Deductible;
import com.titanium.clause.valueobject.Reimbursement;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 理赔规则实体类
 * <p>
 * 免赔与报销已结构化，支持表达险种差异：
 * <ul>
 *   <li>{@link #deductible}（{@link Deductible}）替代原 {@link #deductibleAmount} 字符串，
 *       区分无免赔/固定金额免赔/比例免赔；</li>
 *   <li>{@link #reimbursement}（{@link Reimbursement}）替代原 {@link #payoutRatio} 字符串，
 *       承载报销限额与报销比例（医疗险）。</li>
 * </ul>
 * <b>常规理赔走结构化字段，复杂理赔判定走 {@link #ruleSetCode} + 规则引擎。</b>
 * 原 {@code payoutRatio}、{@code deductibleAmount} 字符串字段保留以向后兼容历史数据与调用方。
 * </p>
 */
@Data
@NoArgsConstructor
public class ClaimRule {
    // 报案时效天数
    private Integer       reportDeadlineDays;
    // 理赔所需材料
    private String        requiredMaterials;
    // 理赔结案时效天数
    private Integer       settlementPeriodDays;
    // 【已结构化-向后兼容】赔付比例文字描述；新逻辑请用 reimbursement
    private String        payoutRatio;
    // 【已结构化-向后兼容】免赔额文字描述；新逻辑请用 deductible
    private String        deductibleAmount;

    // ===== 结构化理赔字段 =====
    // 结构化免赔额（类型 + 金额/比例）
    private Deductible    deductible;
    // 结构化报销规则（限额 + 比例，医疗险）
    private Reimbursement reimbursement;
    // 规则引擎规则集编码（可选）：复杂理赔判定委托规则引擎(SpEL)，常规走结构化字段
    private String        ruleSetCode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
