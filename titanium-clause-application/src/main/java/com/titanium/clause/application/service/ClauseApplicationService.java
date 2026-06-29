package com.titanium.clause.application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.common.exception.ClauseDuplicateException;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.common.exception.ClauseNotFoundException;
import com.titanium.clause.domain.aggregate.Clause;
import com.titanium.clause.domain.command.ActivateClauseCommand;
import com.titanium.clause.domain.command.AddCoverageCommand;
import com.titanium.clause.domain.command.AddExclusionCommand;
import com.titanium.clause.domain.command.AddNotificationCommand;
import com.titanium.clause.domain.command.ApproveClauseCommand;
import com.titanium.clause.domain.command.ArchiveClauseCommand;
import com.titanium.clause.domain.command.CreateClauseCommand;
import com.titanium.clause.domain.command.InactivateClauseCommand;
import com.titanium.clause.domain.command.RejectClauseCommand;
import com.titanium.clause.domain.command.RemoveCoverageCommand;
import com.titanium.clause.domain.command.RemoveExclusionCommand;
import com.titanium.clause.domain.command.ReviseClauseCommand;
import com.titanium.clause.domain.command.SetClaimRuleCommand;
import com.titanium.clause.domain.command.SetContractChangeRuleCommand;
import com.titanium.clause.domain.command.SetPremiumRuleCommand;
import com.titanium.clause.domain.command.SetSignTemplateCommand;
import com.titanium.clause.domain.command.SubmitClauseForApprovalCommand;
import com.titanium.clause.domain.command.UpdateClauseCommand;
import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.entity.ClauseNotification;
import com.titanium.clause.domain.entity.ClauseSignTemplate;
import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.enums.ApprovalType;
import com.titanium.clause.domain.repository.ClauseRepository;
import com.titanium.clause.domain.service.ClauseDomainService;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.CoverageId;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.clause.domain.valueobject.Version;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款应用服务
 * <p>
 * 负责编排条款相关的命令操作，协调领域服务和命令网关。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ClauseApplicationService {
    private final CommandGateway      commandGateway;
    private final ClauseDomainService clauseDomainService;
    private final ClauseRepository    clauseRepository;

    /**
     * 创建条款
     */
    @Transactional
    public ClauseId createClause(String clauseCode, String clauseName, ClauseEnum.ClauseType clauseType, String content,
                                 String description, InsuranceType insuranceType, LocalDateTime effectiveDate,
                                 LocalDateTime expiryDate, String createdBy, String tenantId) {
        Optional<Clause> existingClause = clauseRepository.findByCode(ClauseCode.fromString(clauseCode), tenantId);
        if (existingClause.isPresent()) {
            throw new ClauseDuplicateException("条款代码已存在: " + clauseCode);
        }

        clauseDomainService.validateClauseData(clauseCode, clauseName, clauseType, content, effectiveDate, expiryDate);

        ClauseId clauseId = ClauseId.create();

        CreateClauseCommand command = new CreateClauseCommand(
                clauseId, ClauseCode.fromString(clauseCode), ClauseName.fromString(clauseName),
                clauseType, content, description, insuranceType,
                Version.of("V1.0"), effectiveDate, expiryDate, tenantId, createdBy
        );

        commandGateway.sendAndWait(command);
        return clauseId;
    }

    /**
     * 更新条款
     */
    @Transactional
    public void updateClause(String clauseId, String clauseName, ClauseEnum.ClauseType clauseType, String content,
                             String description, InsuranceType insuranceType, LocalDateTime effectiveDate,
                             LocalDateTime expiryDate, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        Clause clause = findClauseOrThrow(id, tenantId);

        clauseDomainService.validateClauseUpdateStatus(clause.getStatus());
        clauseDomainService.validateClauseData(clause.getClauseCode().getValue(), clauseName, clauseType, content,
                effectiveDate, expiryDate);

        UpdateClauseCommand command = new UpdateClauseCommand(
                id, clause.getClauseCode(), ClauseName.fromString(clauseName),
                clauseType, content, description, insuranceType,
                effectiveDate, expiryDate, tenantId, updatedBy
        );

        commandGateway.sendAndWait(command);
    }

    /**
     * 激活条款
     */
    @Transactional
    public void activateClause(String clauseId, String activatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        Clause clause = findClauseOrThrow(id, tenantId);

        if (!clauseDomainService.canActivateClause(clause)) {
            throw new ClauseInvalidStatusException("条款状态不允许激活: " + clause.getStatus());
        }

        commandGateway.sendAndWait(new ActivateClauseCommand(id, activatedBy));
    }

    /**
     * 停用条款
     */
    @Transactional
    public void inactivateClause(String clauseId, String inactivatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new InactivateClauseCommand(id, inactivatedBy));
    }

    /**
     * 提交条款审批
     */
    @Transactional
    public void submitForApproval(String clauseId, String submittedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new SubmitClauseForApprovalCommand(id, submittedBy));
    }

    /**
     * 审批通过条款
     */
    @Transactional
    public void approveClause(String clauseId, ApprovalType approvalType, String approverId,
                              String approverName, String comment, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new ApproveClauseCommand(id, approvalType, approverId, approverName, comment));
    }

    /**
     * 审批驳回条款
     */
    @Transactional
    public void rejectClause(String clauseId, ApprovalType approvalType, String approverId,
                             String approverName, String comment, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new RejectClauseCommand(id, approvalType, approverId, approverName, comment));
    }

    /**
     * 条款修订（基于当前ACTIVE版本创建新的DRAFT版本）
     */
    @Transactional
    public ClauseId reviseClause(String clauseId, String revisedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        ClauseId newClauseId = ClauseId.create();
        commandGateway.sendAndWait(new ReviseClauseCommand(id, newClauseId, revisedBy));

        return newClauseId;
    }

    /**
     * 条款归档
     */
    @Transactional
    public void archiveClause(String clauseId, String archivedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new ArchiveClauseCommand(id, archivedBy));
    }

    /**
     * 添加保险责任
     */
    @Transactional
    public void addCoverage(String clauseId, Coverage coverage, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new AddCoverageCommand(id, coverage, updatedBy));
    }

    /**
     * 移除保险责任
     */
    @Transactional
    public void removeCoverage(String clauseId, String coverageId, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new RemoveCoverageCommand(id, CoverageId.fromString(coverageId), updatedBy));
    }

    /**
     * 添加责任免除
     */
    @Transactional
    public void addExclusion(String clauseId, Exclusion exclusion, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new AddExclusionCommand(id, exclusion, updatedBy));
    }

    /**
     * 移除责任免除
     */
    @Transactional
    public void removeExclusion(String clauseId, String exclusionId, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new RemoveExclusionCommand(id, ExclusionId.fromString(exclusionId), updatedBy));
    }

    /**
     * 设置缴费规则
     */
    @Transactional
    public void setPremiumRule(String clauseId, PremiumRule premiumRule, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new SetPremiumRuleCommand(id, premiumRule, updatedBy));
    }

    /**
     * 设置理赔规则
     */
    @Transactional
    public void setClaimRule(String clauseId, ClaimRule claimRule, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new SetClaimRuleCommand(id, claimRule, updatedBy));
    }

    /**
     * 设置合同变更规则
     */
    @Transactional
    public void setContractChangeRule(String clauseId, ContractChangeRule contractChangeRule,
                                      String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new SetContractChangeRuleCommand(id, contractChangeRule, updatedBy));
    }

    /**
     * 添加条款告知
     */
    @Transactional
    public void addNotification(String clauseId, ClauseNotification notification, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new AddNotificationCommand(id, notification, updatedBy));
    }

    /**
     * 设置签署模板
     */
    @Transactional
    public void setSignTemplate(String clauseId, ClauseSignTemplate signTemplate, String updatedBy, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        findClauseOrThrow(id, tenantId);

        commandGateway.sendAndWait(new SetSignTemplateCommand(id, signTemplate, updatedBy));
    }

    /**
     * 删除条款
     */
    @Transactional
    public void deleteClause(String clauseId, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        Clause clause = findClauseOrThrow(id, tenantId);

        if (!clauseDomainService.canDeleteClause(clause)) {
            throw new ClauseInvalidStatusException("条款状态不允许删除: " + clause.getStatus());
        }

        clauseRepository.deleteById(id, tenantId);
    }

    // ===== 私有方法 =====

    private Clause findClauseOrThrow(ClauseId clauseId, String tenantId) {
        return clauseRepository.findById(clauseId, tenantId)
                .orElseThrow(() -> new ClauseNotFoundException("条款不存在: " + clauseId.getValue()));
    }
}
