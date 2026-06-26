package com.titanium.clause.domain.valueobject;

import java.util.List;
import java.util.Map;

import com.titanium.metadata.enums.clause.CoverageTriggerType;

/**
 * 赔付触发条件值对象
 * <p>
 * 结构化表达保险责任的赔付触发条件，替代原先扁平的 {@code triggerCondition} 字符串。
 * 通过 {@link CoverageTriggerType} 区分不同险种的触发语义：
 * <ul>
 *   <li>寿险身故：{@code DEATH}</li>
 *   <li>重疾确诊：{@code CRITICAL_ILLNESS} + {@code diseaseList}（确诊28种重疾清单）</li>
 *   <li>意外伤残：{@code ACCIDENT_INJURY} + {@code params}（伤残等级表）</li>
 *   <li>车辆定损：{@code VEHICLE_LOSS}</li>
 *   <li>医疗费用：{@code MEDICAL_EXPENSE}</li>
 * </ul>
 * </p>
 * <p>
 * <b>常规 vs 复杂：</b>常规触发由 {@code triggerType} + 结构化字段（{@code diseaseList}/{@code params}）承载；
 * 复杂或租户可配置的触发逻辑通过 {@code ruleSetCode} 委托规则引擎（SpEL）执行。
 * 两者非互斥：可先用结构化字段做粗筛，再交规则引擎做精判。
 * </p>
 *
 * @param triggerType   触发类型（不可为空，决定触发语义）
 * @param description   触发条件的可读描述（如"确诊约定28种重大疾病之一"）
 * @param diseaseList   疾病/伤残项清单，重疾险承载约定疾病名称，意外险可承载伤残项
 * @param params        结构化扩展参数（如等待期天数、住院天数门槛、伤残等级->比例映射等）
 * @param ruleSetCode   规则引擎规则集编码（可选）；非空时复杂触发判定委托规则引擎执行
 */
public record CoverageTrigger(
        CoverageTriggerType triggerType,
        String description,
        List<String> diseaseList,
        Map<String, Object> params,
        String ruleSetCode
) {

    /**
     * 紧凑构造器：规范化集合字段，避免 null 导致的下游 NPE
     */
    public CoverageTrigger {
        diseaseList = diseaseList == null ? List.of() : List.copyOf(diseaseList);
        params = params == null ? Map.of() : Map.copyOf(params);
    }

    /**
     * 构造简单触发条件（无疾病清单、无规则引擎）
     *
     * @param triggerType 触发类型
     * @param description 描述
     * @return 触发条件值对象
     */
    public static CoverageTrigger of(CoverageTriggerType triggerType, String description) {
        return new CoverageTrigger(triggerType, description, List.of(), Map.of(), null);
    }

    /**
     * 构造重疾确诊触发条件（含约定疾病清单）
     *
     * @param description 描述
     * @param diseaseList 约定疾病清单
     * @return 触发条件值对象
     */
    public static CoverageTrigger criticalIllness(String description, List<String> diseaseList) {
        return new CoverageTrigger(CoverageTriggerType.CRITICAL_ILLNESS, description, diseaseList, Map.of(), null);
    }

    /**
     * 是否委托规则引擎执行复杂触发判定
     *
     * @return true 表示配置了规则集编码
     */
    public boolean usesRuleEngine() {
        return ruleSetCode != null && !ruleSetCode.isBlank();
    }
}
