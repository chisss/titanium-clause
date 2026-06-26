package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

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
