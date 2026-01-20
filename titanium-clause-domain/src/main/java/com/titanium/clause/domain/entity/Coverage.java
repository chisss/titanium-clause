package com.titanium.clause.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.titanium.clause.domain.valueobject.CoverageId;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保险责任实体类
 */
@Data
@NoArgsConstructor
public class Coverage {
    // 责任ID（聚合内唯一）
    private CoverageId    id;

    private String        code;

    private String        name;

    private String        premiumRate;

    private String        description;

    private String        status;

    // 责任类型（重疾/医疗/意外/身故）
    private String        type;
    // 责任保额
    private BigDecimal    coverageAmount;
    // 赔付触发条件（如"确诊重疾""住院满3天"）
    private String        triggerCondition;
    // 赔付规则（单次赔付/多次赔付/比例赔付）
    private String        payoutRule;
    // 是否附加责任
    private Boolean       isAdditional;
    // 关联的主险责任ID
    private CoverageId    mainCoverageId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 校验是否满足赔付触发条件
     * 
     * @param claimEvent 理赔事件
     * @return 是否满足触发条件
     */
    public boolean checkTriggerCondition(ClaimEvent claimEvent) {
        // 这里简化实现，实际项目中可能需要更复杂的规则引擎
        return triggerCondition != null && triggerCondition.equals(claimEvent.getTriggerCondition());
    }
}
