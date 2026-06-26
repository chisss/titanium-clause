package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.Version;

/**
 * 提交条款审批命令（面向独立审批聚合 ClauseApprovalProcess）
 * <p>
 * 开启一条针对某条款指定版本的审批流程实例。
 * </p>
 *
 * @param approvalId    审批流程ID（聚合标识）
 * @param clauseId      关联的条款ID
 * @param clauseVersion 关联的条款版本
 * @param submittedBy   提交人
 * @param tenantId      租户ID
 */
public record SubmitClauseApprovalCommand(
        @TargetAggregateIdentifier
        String approvalId,
        ClauseId clauseId,
        Version clauseVersion,
        String submittedBy,
        String tenantId
) {
}
