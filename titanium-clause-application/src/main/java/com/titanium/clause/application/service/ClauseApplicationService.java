package com.titanium.clause.application.service;

import java.time.LocalDateTime;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.command.ActivateClauseCommand;
import com.titanium.clause.command.AddCoverageCommand;
import com.titanium.clause.command.AddExclusionCommand;
import com.titanium.clause.command.AddNotificationCommand;
import com.titanium.clause.command.ApproveClauseCommand;
import com.titanium.clause.command.ArchiveClauseCommand;
import com.titanium.clause.command.CreateClauseCommand;
import com.titanium.clause.command.DeleteClauseCommand;
import com.titanium.clause.command.InactivateClauseCommand;
import com.titanium.clause.command.RejectClauseCommand;
import com.titanium.clause.command.RemoveCoverageCommand;
import com.titanium.clause.command.RemoveExclusionCommand;
import com.titanium.clause.command.ReviseClauseCommand;
import com.titanium.clause.command.SetClaimRuleCommand;
import com.titanium.clause.command.SetContractChangeRuleCommand;
import com.titanium.clause.command.SetPremiumRuleCommand;
import com.titanium.clause.command.SetSignTemplateCommand;
import com.titanium.clause.command.SubmitClauseForApprovalCommand;
import com.titanium.clause.command.UpdateClauseCommand;
import com.titanium.clause.common.enums.ApprovalType;
import com.titanium.clause.common.exception.ClauseDuplicateException;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.common.exception.ClauseNotFoundException;
import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.entity.ClauseNotification;
import com.titanium.clause.entity.ClauseSignTemplate;
import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.query.repository.ClauseViewRepository;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.clause.service.ClauseDomainService;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.ExclusionId;
import com.titanium.clause.valueobject.Version;
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
    private final CommandGateway        commandGateway;
    private final ClauseDomainService   clauseDomainService;
    private final ClauseViewRepository  clauseViewRepository;

    /**
     * 创建条款
     */
    @Transactional
    public ClauseId createClause(String clauseCode, String clauseName, ClauseEnum.ClauseType clauseType, String content,
                                 String description, InsuranceType insuranceType, LocalDateTime effectiveDate,
                                 LocalDateTime expiryDate, String createdBy, String tenantId) {
        // 唯一性校验：写侧收敛为纯事件溯源后，取数走 CQRS 读模型 t_clause_view（最终一致）
        if (clauseViewRepository.findByClauseCodeAndTenantId(clauseCode, tenantId).isPresent()) {
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
        ClauseView view = findClauseOrThrow(id, tenantId);

        clauseDomainService.validateClauseUpdateStatus(view.getStatus());
        clauseDomainService.validateClauseData(view.getClauseCode(), clauseName, clauseType, content,
                effectiveDate, expiryDate);

        UpdateClauseCommand command = new UpdateClauseCommand(
                id, ClauseCode.fromString(view.getClauseCode()), ClauseName.fromString(clauseName),
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
        ClauseView view = findClauseOrThrow(id, tenantId);

        if (!clauseDomainService.canActivateClause(view.getStatus())) {
            throw new ClauseInvalidStatusException("条款状态不允许激活: " + view.getStatus());
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
     * <p>
     * 写侧纯事件溯源，按当前状态分派删除语义：
     * <ul>
     * <li>DRAFT（草稿）→ {@link DeleteClauseCommand} 硬删除（恢复「删草稿」能力，读模型物理移除）；</li>
     * <li>ACTIVE/INACTIVE（生效/停用）→ {@link ArchiveClauseCommand} 归档软删（保留读模型、状态置 ARCHIVED）；</li>
     * <li>其它状态（待审批/已过期/已归档）→ 不允许删除。</li>
     * </ul>
     * 前置状态校验由聚合内聚裁决，应用层仅按状态选择对应命令。
     * </p>
     */
    @Transactional
    public void deleteClause(String clauseId, String tenantId) {
        ClauseId id = ClauseId.fromString(clauseId);
        ClauseView view = findClauseOrThrow(id, tenantId);

        ClauseEnum.ClauseStatus status = view.getStatus();
        if (status == ClauseEnum.ClauseStatus.DRAFT) {
            commandGateway.sendAndWait(new DeleteClauseCommand(id, "system"));
        } else if (status == ClauseEnum.ClauseStatus.ACTIVE || status == ClauseEnum.ClauseStatus.INACTIVE) {
            commandGateway.sendAndWait(new ArchiveClauseCommand(id, "system"));
        } else {
            throw new ClauseInvalidStatusException("条款状态不允许删除: " + status);
        }
    }

    // ===== 私有方法 =====

    /**
     * 存在性校验（编排职责）：查 CQRS 读模型 t_clause_view，不存在直接抛异常。
     */
    private ClauseView findClauseOrThrow(ClauseId clauseId, String tenantId) {
        return clauseViewRepository.findByClauseIdAndTenantId(clauseId.getValue(), tenantId)
                .orElseThrow(() -> new ClauseNotFoundException("条款不存在: " + clauseId.getValue()));
    }
}
