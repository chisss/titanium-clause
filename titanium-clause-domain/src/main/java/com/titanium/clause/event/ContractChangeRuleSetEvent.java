package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.valueobject.ClauseId;

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
