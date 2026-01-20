package com.titanium.clause.domain.entity;

import com.titanium.clause.domain.valueobject.ExclusionId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 责任免除实体类
 */
@Data
@NoArgsConstructor
public class Exclusion {
    // 免责ID（聚合内唯一）
    private ExclusionId   id;

    private String        description;

    private String        status;
    // 免责类型（故意行为/既往症/违法行为）
    private String        type;
    // 免责规则描述（关联标准化规则库）
    private String        exclusionRuleCode;
    // 是否法定免责（不可修改）
    private Boolean       isMandatory;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 校验理赔事件是否命中免责规则
     * 
     * @param claimEvent 理赔事件
     * @return 是否命中免责规则
     */
    public boolean isHitExclusion(ClaimEvent claimEvent) {
        // 这里简化实现，实际项目中可能需要更复杂的规则引擎
        // 调用规则引擎校验
        return exclusionRuleCode != null && claimEvent.getOtherInfo().contains(exclusionRuleCode);
    }
}
