package com.titanium.clause.entity;

import java.time.LocalDateTime;

import com.titanium.clause.common.enums.ExclusionType;
import com.titanium.clause.valueobject.ExclusionId;
import com.titanium.metadata.enums.CommonStatus;

/**
 * 责任免除（聚合内实体，不可变值对象）
 *
 * @param id                免责ID（聚合内唯一）
 * @param description       描述
 * @param status            状态
 * @param type              免责类型（故意行为/既往症/违法行为）
 * @param exclusionRuleCode 免责规则描述（关联标准化规则库）
 * @param ruleSetCode       规则引擎规则集编码（可选，复杂免责判定委托规则引擎）
 * @param isMandatory       是否法定免责（不可修改）
 * @param createdAt         创建时间
 * @param updatedAt         更新时间
 */
public record Exclusion(
        ExclusionId id,
        String description,
        CommonStatus status,
        ExclusionType type,
        String exclusionRuleCode,
        String ruleSetCode,
        Boolean isMandatory,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * 校验理赔事件是否命中免责规则（充血：规则内聚于值对象）。
     * <p>
     * 简化实现：按免责规则编码在理赔事件附加信息中做文本匹配；复杂免责判定应由应用层委托规则引擎。
     * </p>
     *
     * @param claimEvent 理赔事件
     * @return 是否命中免责规则
     */
    public boolean isHitExclusion(ClaimEvent claimEvent) {
        return exclusionRuleCode != null && claimEvent.otherInfo().contains(exclusionRuleCode);
    }
}
