package com.titanium.clause.domain.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.common.exception.ClauseOperationNotAllowedException;
import com.titanium.clause.domain.command.ActivateClauseCommand;
import com.titanium.clause.domain.command.AddCoverageCommand;
import com.titanium.clause.domain.command.AddExclusionCommand;
import com.titanium.clause.domain.command.AddNotificationCommand;
import com.titanium.clause.domain.command.ApproveClauseCommand;
import com.titanium.clause.domain.command.ArchiveClauseCommand;
import com.titanium.clause.domain.command.ChangeClauseStatusCommand;
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
import com.titanium.clause.domain.command.UpdateCoverageCommand;
import com.titanium.clause.domain.entity.ApprovalRecord;
import com.titanium.clause.domain.entity.ClaimEvent;
import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.entity.ClauseNotification;
import com.titanium.clause.domain.entity.ClauseSignTemplate;
import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.enums.ApprovalStatus;
import com.titanium.clause.domain.event.ClaimRuleSetEvent;
import com.titanium.clause.domain.event.ClauseApprovedEvent;
import com.titanium.clause.domain.event.ClauseArchivedEvent;
import com.titanium.clause.domain.event.ClauseCreatedEvent;
import com.titanium.clause.domain.event.ClauseRejectedEvent;
import com.titanium.clause.domain.event.ClauseRevisedEvent;
import com.titanium.clause.domain.event.ClauseStatusChangedEvent;
import com.titanium.clause.domain.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.domain.event.ClauseUpdatedEvent;
import com.titanium.clause.domain.event.ContractChangeRuleSetEvent;
import com.titanium.clause.domain.event.CoverageAddedEvent;
import com.titanium.clause.domain.event.CoverageRemovedEvent;
import com.titanium.clause.domain.event.ExclusionAddedEvent;
import com.titanium.clause.domain.event.ExclusionRemovedEvent;
import com.titanium.clause.domain.event.NotificationAddedEvent;
import com.titanium.clause.domain.event.PremiumRuleSetEvent;
import com.titanium.clause.domain.event.SignTemplateSetEvent;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.CoverageId;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.clause.domain.valueobject.TimeRange;
import com.titanium.clause.domain.valueobject.Version;
import com.titanium.common.domain.BaseAggregate;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.errorcode.ClauseErrorCode;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 条款聚合根
 * <p>
 * 条款域的核心聚合根，是保险合同的法律文本与规则载体。 负责管理条款的生命周期（创建→审批→生效→修订→归档），
 * 以及包含的规则组件（保险责任、责任免除、缴费规则、理赔规则、合同变更规则）。
 * </p>
 */
@Aggregate
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Clause extends BaseAggregate {

    @AggregateIdentifier
    private ClauseId                    clauseId;
    private ClauseCode                  clauseCode;
    private ClauseName                  clauseName;
    private ClauseEnum.ClauseType       clauseType;
    private String                      content;
    private ClauseEnum.ClauseStatus     status;
    private String                      description;
    private Version                     version;
    private ClauseId                    parentClauseId;
    private InsuranceType               insuranceType;
    private LocalDateTime               effectiveDate;
    private LocalDateTime               expiryDate;

    // ===== 规则组件（聚合内实体）=====
    private Map<CoverageId, Coverage>   coverages       = new HashMap<>();
    private Map<ExclusionId, Exclusion> exclusions      = new HashMap<>();
    private PremiumRule                 premiumRule;
    private ClaimRule                   claimRule;
    private ContractChangeRule          contractChangeRule;

    // ===== 通知与签署 =====
    private List<ClauseNotification>    notifications   = new ArrayList<>();
    private ClauseSignTemplate          signTemplate;

    // ===== 审批 =====
    private List<ApprovalRecord>        approvalRecords = new ArrayList<>();

    // ===== 审计字段 =====
    private String                      createdBy;
    private String                      updatedBy;

    // ========================== 命令处理器 ==========================

    /**
     * 创建条款命令处理器
     */
    @CommandHandler
    public Clause(CreateClauseCommand command) {
        AggregateLifecycle.apply(new ClauseCreatedEvent(command.clauseId(), command.clauseCode(), command.clauseName(),
                command.clauseType(), command.content(), ClauseEnum.ClauseStatus.DRAFT, command.description(),
                command.insuranceType(), command.version() != null ? command.version() : Version.of("V1.0"), null,
                command.effectiveDate(), command.expiryDate(), Map.of(), Map.of(), null, null, null, command.tenantId(),
                command.createdBy(), LocalDateTime.now(), command.createdBy(), LocalDateTime.now()));
    }

    /**
     * 更新条款命令处理器
     */
    @CommandHandler
    public void handle(UpdateClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseInvalidStatusException(
                    ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED.getMessage() + "：仅草稿状态允许更新");
        }

        AggregateLifecycle.apply(new ClauseUpdatedEvent(command.clauseId(), command.clauseCode(), command.clauseName(),
                command.clauseType(), command.content(), this.status, command.description(),
                command.insuranceType(), command.effectiveDate(), command.expiryDate(), this.coverages, this.exclusions,
                this.premiumRule, this.claimRule, this.contractChangeRule, command.tenantId(), command.updatedBy(),
                LocalDateTime.now()));
    }

    /**
     * 变更条款状态命令处理器
     */
    @CommandHandler
    public void handle(ChangeClauseStatusCommand command) {
        ClauseEnum.ClauseStatus newStatus = command.newStatus();
        if (this.status == newStatus) {
            return;
        }

        boolean isValid = switch (this.status) {
            case DRAFT -> newStatus == ClauseEnum.ClauseStatus.ACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.INACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.PENDING_APPROVAL;
            case PENDING_APPROVAL -> newStatus == ClauseEnum.ClauseStatus.ACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.DRAFT;
            case ACTIVE -> newStatus == ClauseEnum.ClauseStatus.INACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.EXPIRED
                    || newStatus == ClauseEnum.ClauseStatus.ARCHIVED;
            case INACTIVE -> newStatus == ClauseEnum.ClauseStatus.ACTIVE;
            default -> false;
        };

        if (!isValid) {
            throw new ClauseInvalidStatusException(
                    "条款状态从 " + this.status.getName() + " 不允许变更为 " + newStatus.getName());
        }

        AggregateLifecycle.apply(new ClauseStatusChangedEvent(command.clauseId(), command.newStatus(),
                command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 激活条款命令处理器
     */
    @CommandHandler
    public void handle(ActivateClauseCommand command) {
        handle(new ChangeClauseStatusCommand(command.clauseId(), ClauseEnum.ClauseStatus.ACTIVE,
                command.updatedBy()));
    }

    /**
     * 停用条款命令处理器
     */
    @CommandHandler
    public void handle(InactivateClauseCommand command) {
        handle(new ChangeClauseStatusCommand(command.clauseId(), ClauseEnum.ClauseStatus.INACTIVE,
                command.updatedBy()));
    }

    // ========================== 审批职责（待迁移）==========================
    // 注意：审批职责正迁移至独立聚合 ClauseApprovalProcess，
    // 以解决审批记录无界增长、与条款主体生命周期不一致的问题。
    // 以下 Submit/Approve/Reject 命令处理器及对应事件溯源处理器待迁移完成后移除，
    // 当前阶段保留以保证可编译与现有调用方不受影响（渐进式迁移）。

    /**
     * 提交条款审批命令处理器
     */
    @CommandHandler
    public void handle(SubmitClauseForApprovalCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseInvalidStatusException("仅草稿状态的条款可提交审批");
        }
        // 校验：至少有1个Coverage
        if (this.coverages == null || this.coverages.isEmpty()) {
            throw new ClauseOperationNotAllowedException("提交审批前至少需要配置1项保险责任");
        }

        AggregateLifecycle.apply(
                new ClauseSubmittedForApprovalEvent(command.clauseId(), command.submittedBy(), LocalDateTime.now()));
    }

    /**
     * 审批通过命令处理器
     */
    @CommandHandler
    public void handle(ApproveClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.PENDING_APPROVAL) {
            throw new ClauseInvalidStatusException("仅待审批状态的条款可执行审批操作");
        }

        ApprovalRecord record = new ApprovalRecord();
        record.setRecordId(java.util.UUID.randomUUID().toString().replace("-", ""));
        record.setApprovalType(command.approvalType());
        record.setApproverId(command.approverId());
        record.setApproverName(command.approverName());
        record.setApprovalStatus(ApprovalStatus.APPROVED);
        record.setComment(command.comment());
        record.setApprovalTime(LocalDateTime.now());

        AggregateLifecycle
                .apply(new ClauseApprovedEvent(command.clauseId(), record, command.approverId(), LocalDateTime.now()));
    }

    /**
     * 审批驳回命令处理器
     */
    @CommandHandler
    public void handle(RejectClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.PENDING_APPROVAL) {
            throw new ClauseInvalidStatusException("仅待审批状态的条款可执行驳回操作");
        }

        ApprovalRecord record = new ApprovalRecord();
        record.setRecordId(java.util.UUID.randomUUID().toString().replace("-", ""));
        record.setApprovalType(command.approvalType());
        record.setApproverId(command.approverId());
        record.setApproverName(command.approverName());
        record.setApprovalStatus(ApprovalStatus.REJECTED);
        record.setComment(command.comment());
        record.setApprovalTime(LocalDateTime.now());

        AggregateLifecycle
                .apply(new ClauseRejectedEvent(command.clauseId(), record, command.approverId(), LocalDateTime.now()));
    }

    /**
     * 条款修订命令处理器（创建新版本）
     */
    @CommandHandler
    public void handle(ReviseClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.ACTIVE) {
            throw new ClauseInvalidStatusException("仅生效状态的条款可发起修订");
        }

        Version newVersion = this.version.nextVersion();
        AggregateLifecycle.apply(new ClauseRevisedEvent(this.clauseId, command.newClauseId(), this.clauseCode,
                this.clauseName, this.clauseType, this.content, this.description, this.insuranceType, newVersion,
                this.effectiveDate, this.expiryDate, this.coverages, this.exclusions, this.premiumRule, this.claimRule,
                this.contractChangeRule, this.notifications, this.signTemplate, this.tenantId, command.revisedBy(),
                LocalDateTime.now()));
    }

    /**
     * 归档命令处理器
     */
    @CommandHandler
    public void handle(ArchiveClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.ACTIVE && this.status != ClauseEnum.ClauseStatus.INACTIVE) {
            throw new ClauseInvalidStatusException("仅生效或停用状态的条款可归档");
        }
        AggregateLifecycle
                .apply(new ClauseArchivedEvent(command.clauseId(), command.archivedBy(), LocalDateTime.now()));
    }

    /**
     * 添加保险责任命令处理器
     */
    @CommandHandler
    public void handle(AddCoverageCommand command) {
        validateDraftStatus("添加保险责任");
        AggregateLifecycle.apply(new CoverageAddedEvent(command.clauseId(), command.coverage(), command.updatedBy(),
                LocalDateTime.now()));
    }

    /**
     * 移除保险责任命令处理器
     */
    @CommandHandler
    public void handle(RemoveCoverageCommand command) {
        validateDraftStatus("移除保险责任");
        AggregateLifecycle.apply(new CoverageRemovedEvent(command.clauseId(), command.coverageId(), command.updatedBy(),
                LocalDateTime.now()));
    }

    /**
     * 更新保险责任命令处理器
     */
    @CommandHandler
    public void handle(UpdateCoverageCommand command) {
        validateDraftStatus("更新保险责任");
        if (!this.coverages.containsKey(command.coverage().getId())) {
            throw new ClauseOperationNotAllowedException("保险责任不存在: " + command.coverage().getId());
        }
        AggregateLifecycle.apply(new CoverageAddedEvent(command.clauseId(), command.coverage(), command.updatedBy(),
                LocalDateTime.now()));
    }

    /**
     * 添加责任免除命令处理器
     */
    @CommandHandler
    public void handle(AddExclusionCommand command) {
        validateDraftStatus("添加责任免除");
        AggregateLifecycle.apply(new ExclusionAddedEvent(command.clauseId(), command.exclusion(), command.updatedBy(),
                LocalDateTime.now()));
    }

    /**
     * 移除责任免除命令处理器
     */
    @CommandHandler
    public void handle(RemoveExclusionCommand command) {
        validateDraftStatus("移除责任免除");
        AggregateLifecycle.apply(new ExclusionRemovedEvent(command.clauseId(), command.exclusionId(),
                command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 设置缴费规则命令处理器
     */
    @CommandHandler
    public void handle(SetPremiumRuleCommand command) {
        validateDraftStatus("设置缴费规则");
        AggregateLifecycle.apply(new PremiumRuleSetEvent(command.clauseId(), command.premiumRule(), command.updatedBy(),
                LocalDateTime.now()));
    }

    /**
     * 设置理赔规则命令处理器
     */
    @CommandHandler
    public void handle(SetClaimRuleCommand command) {
        validateDraftStatus("设置理赔规则");
        AggregateLifecycle.apply(new ClaimRuleSetEvent(command.clauseId(), command.claimRule(), command.updatedBy(),
                LocalDateTime.now()));
    }

    /**
     * 设置合同变更规则命令处理器
     */
    @CommandHandler
    public void handle(SetContractChangeRuleCommand command) {
        validateDraftStatus("设置合同变更规则");
        AggregateLifecycle.apply(new ContractChangeRuleSetEvent(command.clauseId(), command.contractChangeRule(),
                command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 添加条款告知命令处理器
     */
    @CommandHandler
    public void handle(AddNotificationCommand command) {
        validateDraftStatus("添加条款告知");
        AggregateLifecycle.apply(new NotificationAddedEvent(command.clauseId(), command.notification(),
                command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 设置签署模板命令处理器
     */
    @CommandHandler
    public void handle(SetSignTemplateCommand command) {
        validateDraftStatus("设置签署模板");
        AggregateLifecycle.apply(new SignTemplateSetEvent(command.clauseId(), command.signTemplate(),
                command.updatedBy(), LocalDateTime.now()));
    }

    // ========================== 事件溯源处理器 ==========================

    @EventSourcingHandler
    public void on(ClauseCreatedEvent event) {
        this.clauseId = event.clauseId();
        this.clauseCode = event.clauseCode();
        this.clauseName = event.clauseName();
        this.clauseType = event.clauseType();
        this.content = event.content();
        this.status = ClauseEnum.ClauseStatus.DRAFT;
        this.description = event.description();
        this.insuranceType = event.insuranceType();
        this.version = event.version();
        this.parentClauseId = event.parentClauseId();
        this.effectiveDate = event.effectiveDate();
        this.expiryDate = event.expiryDate();
        this.coverages = event.coverages() != null ? new HashMap<>(event.coverages()) : new HashMap<>();
        this.exclusions = event.exclusions() != null ? new HashMap<>(event.exclusions()) : new HashMap<>();
        this.premiumRule = event.premiumRule();
        this.claimRule = event.claimRule();
        this.contractChangeRule = event.contractChangeRule();
        this.tenantId = event.tenantId();
        this.createdBy = event.createdBy();
        this.createTime = event.createdAt();
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ClauseUpdatedEvent event) {
        this.clauseCode = event.clauseCode();
        this.clauseName = event.clauseName();
        this.clauseType = event.clauseType();
        this.content = event.content();
        this.description = event.description();
        this.insuranceType = event.insuranceType();
        this.effectiveDate = event.effectiveDate();
        this.expiryDate = event.expiryDate();
        this.coverages = event.coverages() != null ? new HashMap<>(event.coverages()) : this.coverages;
        this.exclusions = event.exclusions() != null ? new HashMap<>(event.exclusions()) : this.exclusions;
        this.premiumRule = event.premiumRule() != null ? event.premiumRule() : this.premiumRule;
        this.claimRule = event.claimRule() != null ? event.claimRule() : this.claimRule;
        this.contractChangeRule = event.contractChangeRule() != null ? event.contractChangeRule()
                : this.contractChangeRule;
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ClauseStatusChangedEvent event) {
        this.status = event.newStatus();
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ClauseSubmittedForApprovalEvent event) {
        this.status = ClauseEnum.ClauseStatus.PENDING_APPROVAL;
        this.updatedBy = event.submittedBy();
        this.updateTime = event.submittedAt();
    }

    @EventSourcingHandler
    public void on(ClauseApprovedEvent event) {
        if (this.approvalRecords == null) {
            this.approvalRecords = new ArrayList<>();
        }
        this.approvalRecords.add(event.approvalRecord());
        // 审批通过 → ACTIVE
        this.status = ClauseEnum.ClauseStatus.ACTIVE;
        this.updatedBy = event.approverId();
        this.updateTime = event.approvedAt();
    }

    @EventSourcingHandler
    public void on(ClauseRejectedEvent event) {
        if (this.approvalRecords == null) {
            this.approvalRecords = new ArrayList<>();
        }
        this.approvalRecords.add(event.approvalRecord());
        // 驳回 → 回到DRAFT
        this.status = ClauseEnum.ClauseStatus.DRAFT;
        this.updatedBy = event.rejectedBy();
        this.updateTime = event.rejectedAt();
    }

    @EventSourcingHandler
    public void on(ClauseRevisedEvent event) {
        // 修订事件不改变当前聚合根状态
        // 新版本的创建在application层处理（创建新的Clause聚合实例）
    }

    @EventSourcingHandler
    public void on(ClauseArchivedEvent event) {
        this.status = ClauseEnum.ClauseStatus.ARCHIVED;
        this.updatedBy = event.archivedBy();
        this.updateTime = event.archivedAt();
    }

    @EventSourcingHandler
    public void on(CoverageAddedEvent event) {
        if (this.coverages == null) {
            this.coverages = new HashMap<>();
        }
        this.coverages.put(event.coverage().getId(), event.coverage());
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(CoverageRemovedEvent event) {
        if (this.coverages != null) {
            this.coverages.remove(event.coverageId());
        }
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ExclusionAddedEvent event) {
        if (this.exclusions == null) {
            this.exclusions = new HashMap<>();
        }
        this.exclusions.put(event.exclusion().getId(), event.exclusion());
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ExclusionRemovedEvent event) {
        if (this.exclusions != null) {
            this.exclusions.remove(event.exclusionId());
        }
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(PremiumRuleSetEvent event) {
        this.premiumRule = event.premiumRule();
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ClaimRuleSetEvent event) {
        this.claimRule = event.claimRule();
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ContractChangeRuleSetEvent event) {
        this.contractChangeRule = event.contractChangeRule();
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(NotificationAddedEvent event) {
        if (this.notifications == null) {
            this.notifications = new ArrayList<>();
        }
        this.notifications.add(event.notification());
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(SignTemplateSetEvent event) {
        this.signTemplate = event.signTemplate();
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    // ========================== 业务方法 ==========================

    /**
     * 校验理赔事件是否符合条款规则
     *
     * @param claimEvent 理赔事件
     * @return 是否符合条款规则
     */
    public boolean validateClaim(ClaimEvent claimEvent) {
        if (this.status != ClauseEnum.ClauseStatus.ACTIVE) {
            return false;
        }

        TimeRange validityRange = TimeRange.of(effectiveDate, expiryDate);
        if (!validityRange.isInRange(claimEvent.getClaimTime())) {
            return false;
        }

        for (Exclusion exclusion : exclusions.values()) {
            if (exclusion.isHitExclusion(claimEvent)) {
                return false;
            }
        }

        for (Coverage coverage : coverages.values()) {
            if (coverage.checkTriggerCondition(claimEvent)) {
                return true;
            }
        }

        return false;
    }

    // ========================== 私有辅助方法 ==========================

    /**
     * 校验条款当前是否为草稿状态，仅草稿状态允许修改规则组件
     */
    private void validateDraftStatus(String operation) {
        if (this.status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseOperationNotAllowedException("仅草稿状态允许" + operation + "，当前状态: " + this.status.getName());
        }
    }
}
