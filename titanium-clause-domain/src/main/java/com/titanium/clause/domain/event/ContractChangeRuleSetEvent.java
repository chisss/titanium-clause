package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 合同变更规则已设置事件
 */
public record ContractChangeRuleSetEvent(
        ClauseId clauseId,
        ContractChangeRule contractChangeRule,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
