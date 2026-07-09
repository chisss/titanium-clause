package com.titanium.clause.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.titanium.clause.common.enums.CoverageType;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.PayoutRule;
import com.titanium.metadata.enums.CommonStatus;
import com.titanium.metadata.enums.clause.CoverageTriggerType;

/**
 * 保险责任（聚合内实体，不可变值对象）
 * <p>
 * 责任的赔付触发与赔付计算已结构化：{@link #trigger} 区分身故/确诊重疾/意外伤残/车辆定损/医疗费用等险种差异；
 * {@link #structuredPayoutRule} 区分定额/比例/按损/报销等赔付方式。<b>常规走结构化字段，复杂走 ruleSetCode + 规则引擎。</b>
 * 原 {@code triggerCondition}/{@code payoutRule} 字符串字段保留以向后兼容历史数据与调用方。
 * </p>
 *
 * @param id                   责任ID（聚合内唯一）
 * @param code                 责任编码
 * @param name                 责任名称
 * @param premiumRate          费率（字符串，历史字段）
 * @param description          描述
 * @param status               状态
 * @param type                 责任类型（重疾/医疗/意外/身故）
 * @param coverageAmount       责任保额
 * @param triggerCondition     【已结构化-向后兼容】赔付触发条件文字描述，新逻辑用 trigger
 * @param payoutRule           【已结构化-向后兼容】赔付规则文字描述，新逻辑用 structuredPayoutRule
 * @param isAdditional         是否附加责任
 * @param mainCoverageId       关联的主险责任ID
 * @param trigger              结构化赔付触发条件（复杂判定走 trigger.ruleSetCode + 规则引擎）
 * @param structuredPayoutRule 结构化赔付规则（复杂计算走 payoutRule.ruleSetCode + 规则引擎）
 * @param createdAt            创建时间
 * @param updatedAt            更新时间
 */
public record Coverage(
        CoverageId id,
        String code,
        String name,
        String premiumRate,
        String description,
        CommonStatus status,
        CoverageType type,
        BigDecimal coverageAmount,
        String triggerCondition,
        String payoutRule,
        Boolean isAdditional,
        CoverageId mainCoverageId,
        CoverageTrigger trigger,
        PayoutRule structuredPayoutRule,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * 校验是否满足赔付触发条件（充血：规则内聚于值对象）。
     * <p>
     * 优先用结构化 {@link #trigger} 判定：配置了 ruleSetCode 的复杂触发放行至规则引擎（返回 true 不本地否决）；
     * 重疾类按疾病清单匹配；其余按触发类型 code 比对。未配置结构化 trigger 时回退到原字符串比对。
     * </p>
     *
     * @param claimEvent 理赔事件
     * @return 是否满足触发条件
     */
    public boolean checkTriggerCondition(ClaimEvent claimEvent) {
        if (trigger != null && trigger.triggerType() != null) {
            if (trigger.usesRuleEngine()) {
                return true;
            }
            String eventCondition = claimEvent.getTriggerCondition();
            if (eventCondition == null) {
                return false;
            }
            if (trigger.triggerType() == CoverageTriggerType.CRITICAL_ILLNESS && !trigger.diseaseList().isEmpty()) {
                return trigger.diseaseList().contains(eventCondition);
            }
            return trigger.triggerType().getCode().equals(eventCondition);
        }
        return triggerCondition != null && triggerCondition.equals(claimEvent.getTriggerCondition());
    }
}
