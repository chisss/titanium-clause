package com.titanium.clause.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.titanium.clause.domain.valueobject.CoverageId;
import com.titanium.clause.domain.valueobject.CoverageTrigger;
import com.titanium.clause.domain.valueobject.PayoutRule;
import com.titanium.clause.domain.enums.CoverageType;
import com.titanium.metadata.enums.CommonStatus;
import com.titanium.metadata.enums.clause.CoverageTriggerType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保险责任实体类
 * <p>
 * 责任的赔付触发与赔付计算已结构化：
 * <ul>
 *   <li>{@link #trigger}（{@link CoverageTrigger}）替代原 {@link #triggerCondition} 字符串，
 *       区分身故/确诊重疾/意外伤残/车辆定损/医疗费用等险种差异；</li>
 *   <li>{@link #structuredPayoutRule}（{@link PayoutRule}）替代原 {@link #payoutRule} 字符串，
 *       区分定额/比例/按损/报销等赔付方式。</li>
 * </ul>
 * <b>常规走结构化字段，复杂走 ruleSetCode + 规则引擎。</b>
 * 原 {@code triggerCondition}、{@code payoutRule} 字符串字段保留以向后兼容历史数据与调用方，
 * 新逻辑应优先使用结构化字段。
 * </p>
 */
@Data
@NoArgsConstructor
public class Coverage {
    // 责任ID（聚合内唯一）
    private CoverageId      id;

    private String          code;

    private String          name;

    private String          premiumRate;

    private String          description;

    private CommonStatus    status;

    // 责任类型（重疾/医疗/意外/身故）
    private CoverageType    type;
    // 责任保额
    private BigDecimal       coverageAmount;
    // 【已结构化-向后兼容】赔付触发条件文字描述（如"确诊重疾""住院满3天"），新逻辑请用 trigger
    private String          triggerCondition;
    // 【已结构化-向后兼容】赔付规则文字描述（单次/多次/比例赔付），新逻辑请用 structuredPayoutRule
    private String          payoutRule;
    // 是否附加责任
    private Boolean         isAdditional;
    // 关联的主险责任ID
    private CoverageId      mainCoverageId;

    // ===== 结构化规则字段 =====
    // 结构化赔付触发条件（区分险种差异；复杂判定走 trigger.ruleSetCode + 规则引擎）
    private CoverageTrigger trigger;
    // 结构化赔付规则（定额/比例/按损/报销；复杂计算走 payoutRule.ruleSetCode + 规则引擎）
    private PayoutRule      structuredPayoutRule;

    private LocalDateTime   createdAt;

    private LocalDateTime   updatedAt;

    /**
     * 校验是否满足赔付触发条件
     * <p>
     * 优先使用结构化 {@link #trigger} 判定：
     * <ul>
     *   <li>配置了 ruleSetCode 的复杂触发，交由应用层调用规则引擎执行，此处不做本地短路判定，返回 true 放行至规则引擎；</li>
     *   <li>重疾类按疾病清单匹配理赔事件触发条件；</li>
     *   <li>其余类型按触发类型 code 与理赔事件触发条件比对。</li>
     * </ul>
     * 未配置结构化 trigger 时，回退到原 {@link #triggerCondition} 字符串比对（向后兼容）。
     * </p>
     *
     * @param claimEvent 理赔事件
     * @return 是否满足触发条件
     */
    public boolean checkTriggerCondition(ClaimEvent claimEvent) {
        if (trigger != null && trigger.triggerType() != null) {
            // 复杂触发：委托规则引擎，由应用层执行，领域内不做本地否决
            if (trigger.usesRuleEngine()) {
                return true;
            }
            String eventCondition = claimEvent.getTriggerCondition();
            if (eventCondition == null) {
                return false;
            }
            // 重疾：理赔事件触发条件需命中约定疾病清单之一
            if (trigger.triggerType() == CoverageTriggerType.CRITICAL_ILLNESS && !trigger.diseaseList().isEmpty()) {
                return trigger.diseaseList().contains(eventCondition);
            }
            // 其余：按触发类型 code 比对
            return trigger.triggerType().getCode().equals(eventCondition);
        }
        // 向后兼容：回退到原字符串比对
        return triggerCondition != null && triggerCondition.equals(claimEvent.getTriggerCondition());
    }
}
