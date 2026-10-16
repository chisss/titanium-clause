package com.titanium.clause.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.titanium.clause.command.ActivateClauseCommand;
import com.titanium.clause.command.AddNotificationCommand;
import com.titanium.clause.command.ApproveClauseCommand;
import com.titanium.clause.command.ArchiveClauseCommand;
import com.titanium.clause.command.ChangeClauseStatusCommand;
import com.titanium.clause.command.CreateClauseCommand;
import com.titanium.clause.command.DeleteClauseCommand;
import com.titanium.clause.command.InactivateClauseCommand;
import com.titanium.clause.command.RejectClauseCommand;
import com.titanium.clause.command.ReviseClauseCommand;
import com.titanium.clause.command.SetSignTemplateCommand;
import com.titanium.clause.command.SubmitClauseForApprovalCommand;
import com.titanium.clause.command.UpdateClauseCommand;
import com.titanium.clause.common.enums.ApprovalStatus;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.common.exception.ClauseOperationNotAllowedException;
import com.titanium.clause.entity.ApprovalRecord;
import com.titanium.clause.entity.ClaimEvent;
import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.entity.ClauseNotification;
import com.titanium.clause.entity.ClauseSignTemplate;
import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.event.ClaimRuleSetEvent;
import com.titanium.clause.event.ClauseApprovedEvent;
import com.titanium.clause.event.ClauseArchivedEvent;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseDeletedEvent;
import com.titanium.clause.event.ClauseRejectedEvent;
import com.titanium.clause.event.ClauseRevisedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.event.ClauseUpdatedEvent;
import com.titanium.clause.event.ContractChangeRuleSetEvent;
import com.titanium.clause.event.CoverageAddedEvent;
import com.titanium.clause.event.CoverageRemovedEvent;
import com.titanium.clause.event.ExclusionAddedEvent;
import com.titanium.clause.event.ExclusionRemovedEvent;
import com.titanium.clause.event.NotificationAddedEvent;
import com.titanium.clause.event.PremiumRuleSetEvent;
import com.titanium.clause.event.SignTemplateSetEvent;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.ExclusionId;
import com.titanium.clause.valueobject.TimeRange;
import com.titanium.clause.valueobject.Version;
import com.titanium.common.domain.BaseAggregate;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;
import com.titanium.metadata.errorcode.ClauseErrorCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 条款聚合根
 * <p>
 * 条款域的核心聚合根，是保险合同的法律文本与规则载体。 负责管理条款的生命周期（创建→审批→生效→修订→归档），
 * 以及包含的规则组件（保险责任、责任免除、缴费规则、理赔规则、合同变更规则）。
 * </p>
 * <p>
 * 🔴 规则组件的业务状态与命令处理器已随 AXN-02 成员下沉迁至 {@link ClauseRuleComponents}
 * （{@code @AggregateMember}，Axon 自动路由）；本根保留条款生命周期与告知/签署族处理器，
 * 并以双监听方式维护规则组件事件的审计字段。
 * </p>
 */
@Aggregate
@Getter
/** 仅供 Axon 回放使用（Lombok 生成 protected 无参构造器），业务代码禁止调用 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private InsuranceProductType               insuranceType;
    private LocalDateTime               effectiveDate;
    private LocalDateTime               expiryDate;

    // ===== 规则组件（聚合内成员实体，AXN-02 成员下沉）=====
    // 状态与命令处理器在成员侧（ClauseRuleComponents）；根侧不重建同名处理器，否则 Axon 报多处理器冲突。
    @AggregateMember
    private ClauseRuleComponents        ruleComponents;

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
     * 修订新版本聚合的工厂构造器（非命令处理器）
     * <p>
     * 由原 ACTIVE 条款在 {@link #handle(ReviseClauseCommand)} 中经 {@link AggregateLifecycle#createNew}
     * 调用，为 {@code newClauseId} 建立独立事件流。此处 apply {@link ClauseRevisedEvent} 作为新聚合的首个事件，
     * 状态初始化由 {@link #on(ClauseRevisedEvent)} 承接。
     * </p>
     */
    public Clause(ClauseRevisedEvent event) {
        AggregateLifecycle.apply(event);
    }

    /**
     * 更新条款命令处理器
     */
    @CommandHandler
    public void handle(UpdateClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseInvalidStatusException(
                    ClauseErrorCode.CLAUSE_INVALID_STATUS.getMessage() + "：仅草稿状态允许更新");
        }

        AggregateLifecycle.apply(new ClauseUpdatedEvent(command.clauseId(), command.clauseCode(), command.clauseName(),
                command.clauseType(), command.content(), this.status, command.description(), command.insuranceType(),
                command.effectiveDate(), command.expiryDate(), this.ruleComponents.getCoverages(),
                this.ruleComponents.getExclusions(), this.ruleComponents.getPremiumRule(),
                this.ruleComponents.getClaimRule(), this.ruleComponents.getContractChangeRule(), command.tenantId(),
                command.updatedBy(), LocalDateTime.now()));
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
            // 草稿只能提交审批
            case DRAFT -> newStatus == ClauseEnum.ClauseStatus.PENDING_APPROVAL;
            // 待审批只能撤销提交回到草稿。🔴 生效（ACTIVE）的唯一入口是审批通过（ClauseApprovedEvent）：
            // 一旦放行此处的 PENDING_APPROVAL → ACTIVE，ActivateClauseCommand 转发本命令即可让待审批条款
            // 无审批结论、无审批记录地直接生效，绕过审批。
            case PENDING_APPROVAL -> newStatus == ClauseEnum.ClauseStatus.DRAFT;
            // 生效条款可停用 / 过期 / 归档
            case ACTIVE -> newStatus == ClauseEnum.ClauseStatus.INACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.EXPIRED
                    || newStatus == ClauseEnum.ClauseStatus.ARCHIVED;
            // 停用条款可重新启用（重新启用不是首次生效，无需审批）
            case INACTIVE -> newStatus == ClauseEnum.ClauseStatus.ACTIVE;
            default -> false;
        };

        if (!isValid) {
            throw new ClauseInvalidStatusException(
                    "条款状态从 " + this.status.getName() + " 不允许变更为 " + newStatus.getName());
        }

        AggregateLifecycle.apply(new ClauseStatusChangedEvent(command.clauseId(), command.newStatus(),
                command.updatedBy(), LocalDateTime.now(), this.tenantId));
    }

    /**
     * 激活条款命令处理器
     * <p>
     * 转发为 {@code ACTIVE} 的状态迁移，合法性由 {@link #handle(ChangeClauseStatusCommand)} 的迁移表裁定：
     * 仅停用（INACTIVE）条款可经此重新启用；草稿、待审批条款均被拒绝（后者必须走审批通过）。
     * </p>
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
        // 校验：至少有1个Coverage（规则组件状态由成员持有，经成员包级判定方法读取）
        if (!this.ruleComponents.hasCoverages()) {
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

        ApprovalRecord record = new ApprovalRecord(UUID.randomUUID().toString().replace("-", ""),
                command.approvalType(), command.approverId(), command.approverName(), ApprovalStatus.APPROVED,
                command.comment(), LocalDateTime.now());

        AggregateLifecycle.apply(new ClauseApprovedEvent(command.clauseId(), record, command.approverId(),
                LocalDateTime.now(), this.tenantId));
    }

    /**
     * 审批驳回命令处理器
     */
    @CommandHandler
    public void handle(RejectClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.PENDING_APPROVAL) {
            throw new ClauseInvalidStatusException("仅待审批状态的条款可执行驳回操作");
        }

        ApprovalRecord record = new ApprovalRecord(UUID.randomUUID().toString().replace("-", ""),
                command.approvalType(), command.approverId(), command.approverName(), ApprovalStatus.REJECTED,
                command.comment(), LocalDateTime.now());

        AggregateLifecycle
                .apply(new ClauseRejectedEvent(command.clauseId(), record, command.approverId(), LocalDateTime.now()));
    }

    /**
     * 条款修订命令处理器（创建新版本）
     * <p>
     * 修订不改写当前 ACTIVE 版本，而是以全新的 {@code newClauseId} 创建一个独立的 DRAFT 聚合实例，
     * 使新版本拥有自己的事件流。通过 {@link AggregateLifecycle#createNew} 在同一工作单元内构造新聚合，
     * 由新聚合的构造器 {@link #Clause(ClauseRevisedEvent)} 应用 {@link ClauseRevisedEvent} 初始化状态。
     * 原聚合状态保持 ACTIVE 不变（本命令不对原聚合 apply 任何事件）。
     * </p>
     */
    @CommandHandler
    public void handle(ReviseClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.ACTIVE) {
            throw new ClauseInvalidStatusException("仅生效状态的条款可发起修订");
        }

        Version newVersion = this.version.nextVersion();
        ClauseRevisedEvent revisedEvent = new ClauseRevisedEvent(this.clauseId, command.newClauseId(), this.clauseCode,
                this.clauseName, this.clauseType, this.content, this.description, this.insuranceType, newVersion,
                this.effectiveDate, this.expiryDate, this.ruleComponents.getCoverages(),
                this.ruleComponents.getExclusions(), this.ruleComponents.getPremiumRule(),
                this.ruleComponents.getClaimRule(), this.ruleComponents.getContractChangeRule(), this.notifications,
                this.signTemplate, this.tenantId, command.revisedBy(), LocalDateTime.now());
        try {
            AggregateLifecycle.createNew(Clause.class, () -> new Clause(revisedEvent));
        } catch (Exception e) {
            throw new ClauseOperationNotAllowedException("条款修订失败：创建新版本聚合异常", e);
        }
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
     * 删除条款命令处理器
     * <p>
     * 仅允许硬删除草稿（DRAFT）状态的条款，恢复「删草稿」能力。 已生效/停用条款应走归档（{@link ArchiveClauseCommand}）软删，不可硬删。
     * </p>
     */
    @CommandHandler
    public void handle(DeleteClauseCommand command) {
        if (this.status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseInvalidStatusException(
                    "仅草稿状态的条款可删除，当前状态: " + this.status.getName() + "，已生效条款请使用归档");
        }
        AggregateLifecycle.apply(new ClauseDeletedEvent(command.clauseId(), command.deletedBy(), LocalDateTime.now()));
    }

    // 规则组件命令处理器（添加/更新/移除保险责任、添加/移除责任免除、设置缴费/理赔/合同变更规则）
    // 已随 AXN-02 成员下沉迁至 ClauseRuleComponents（@AggregateMember，Axon 按命令类型自动路由）。
    // 🔴 不得在本根重建同名处理器——下沉即搬迁，不是复制；双份处理器会被 Axon 判为多处理器冲突。

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
        // 规则组件成员在此初始化（null 集合按空表规范化、状态镜像置 DRAFT），语义与下沉前逐字一致
        this.ruleComponents = new ClauseRuleComponents(event.coverages(), event.exclusions(), event.premiumRule(),
                event.claimRule(), event.contractChangeRule(), ClauseEnum.ClauseStatus.DRAFT);
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
        // null 字段表示该组件本次未变更、保留现值（语义与下沉前逐字一致，由成员承接）
        this.ruleComponents.applyUpdate(event.coverages(), event.exclusions(), event.premiumRule(),
                event.claimRule(), event.contractChangeRule());
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ClauseStatusChangedEvent event) {
        this.status = event.newStatus();
        this.ruleComponents.markStatus(event.newStatus());
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ClauseSubmittedForApprovalEvent event) {
        this.status = ClauseEnum.ClauseStatus.PENDING_APPROVAL;
        this.ruleComponents.markStatus(ClauseEnum.ClauseStatus.PENDING_APPROVAL);
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
        this.ruleComponents.markStatus(ClauseEnum.ClauseStatus.ACTIVE);
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
        this.ruleComponents.markStatus(ClauseEnum.ClauseStatus.DRAFT);
        this.updatedBy = event.rejectedBy();
        this.updateTime = event.rejectedAt();
    }

    /**
     * 修订事件溯源处理器：初始化「新版本」聚合的状态。
     * <p>
     * 该事件是经 {@link AggregateLifecycle#createNew} 创建的新聚合的首个事件，运行在新聚合
     * （{@code newClauseId}）自己的事件流上，故此处对 {@code clauseId} 的赋值是<b>初始化</b>而非改写既有标识。
     * 新版本落为 DRAFT 状态，完整继承原条款的规则组件（责任/免除/费率/理赔/合同变更规则/告知/签署模板），
     * 并通过 {@code parentClauseId} 溯源到原条款。
     * </p>
     */
    @EventSourcingHandler
    public void on(ClauseRevisedEvent event) {
        this.clauseId = event.newClauseId();
        this.parentClauseId = event.originalClauseId();
        this.clauseCode = event.clauseCode();
        this.clauseName = event.clauseName();
        this.clauseType = event.clauseType();
        this.content = event.content();
        this.status = ClauseEnum.ClauseStatus.DRAFT;
        this.description = event.description();
        this.insuranceType = event.insuranceType();
        this.version = event.newVersion();
        this.effectiveDate = event.effectiveDate();
        this.expiryDate = event.expiryDate();
        // 规则组件成员随修订重建（继承原条款规则组件、状态镜像置 DRAFT），语义与下沉前逐字一致
        this.ruleComponents = new ClauseRuleComponents(event.coverages(), event.exclusions(), event.premiumRule(),
                event.claimRule(), event.contractChangeRule(), ClauseEnum.ClauseStatus.DRAFT);
        this.notifications = event.notifications() != null ? new ArrayList<>(event.notifications()) : new ArrayList<>();
        this.signTemplate = event.signTemplate();
        this.approvalRecords = new ArrayList<>();
        this.tenantId = event.tenantId();
        this.createdBy = event.revisedBy();
        this.createTime = event.revisedAt();
        this.updatedBy = event.revisedBy();
        this.updateTime = event.revisedAt();
    }

    @EventSourcingHandler
    public void on(ClauseArchivedEvent event) {
        this.status = ClauseEnum.ClauseStatus.ARCHIVED;
        this.ruleComponents.markStatus(ClauseEnum.ClauseStatus.ARCHIVED);
        this.updatedBy = event.archivedBy();
        this.updateTime = event.archivedAt();
    }

    @EventSourcingHandler
    public void on(ClauseDeletedEvent event) {
        // 草稿硬删除：标记聚合已删除，后续命令不可再路由到本聚合
        this.updatedBy = event.deletedBy();
        this.updateTime = event.deletedAt();
        AggregateLifecycle.markDeleted();
    }

    /** 保险责任添加回放：审计字段维护，业务状态由成员 ClauseRuleComponents 重建（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(CoverageAddedEvent event) {
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    /** 保险责任移除回放：审计字段维护，业务状态由成员 ClauseRuleComponents 重建（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(CoverageRemovedEvent event) {
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    /** 责任免除添加回放：审计字段维护，业务状态由成员 ClauseRuleComponents 重建（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ExclusionAddedEvent event) {
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    /** 责任免除移除回放：审计字段维护，业务状态由成员 ClauseRuleComponents 重建（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ExclusionRemovedEvent event) {
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    /** 缴费规则设置回放：审计字段维护，业务状态由成员 ClauseRuleComponents 重建（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(PremiumRuleSetEvent event) {
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    /** 理赔规则设置回放：审计字段维护，业务状态由成员 ClauseRuleComponents 重建（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ClaimRuleSetEvent event) {
        this.updatedBy = event.updatedBy();
        this.updateTime = event.updatedAt();
    }

    /** 合同变更规则设置回放：审计字段维护，业务状态由成员 ClauseRuleComponents 重建（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ContractChangeRuleSetEvent event) {
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
        if (!validityRange.isInRange(claimEvent.claimTime())) {
            return false;
        }

        for (Exclusion exclusion : this.ruleComponents.getExclusions().values()) {
            if (exclusion.isHitExclusion(claimEvent)) {
                return false;
            }
        }

        for (Coverage coverage : this.ruleComponents.getCoverages().values()) {
            if (coverage.checkTriggerCondition(claimEvent)) {
                return true;
            }
        }

        return false;
    }

    // ========================== 私有辅助方法 ==========================

    /**
     * 校验条款当前是否为草稿状态，仅草稿状态允许修改（告知/签署族留驻命令使用）。
     * <p>
     * 规则组件成员的草稿态门禁在 {@link ClauseRuleComponents#validateDraftStatus} 内（读状态镜像），
     * 两者规则逐字同源、状态来源不同。
     * </p>
     */
    private void validateDraftStatus(String operation) {
        if (this.status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseOperationNotAllowedException("仅草稿状态允许" + operation + "，当前状态: " + this.status.getName());
        }
    }

    // 🔴 @JsonIgnore 不可删（D-501-19）：以下规则组件转发 getter 均无对应根字段，Jackson 会把它们
    //    写进载荷（快照），反序列化时无写入面 ⇒ UnrecognizedPropertyException → 聚合无法加载。
    //    组件状态持有者是成员 ClauseRuleComponents（随 ruleComponents 字段成对序列化往返）。

    /** coverages 不可变视图（DSN-02：杜绝经 getter 外泄内部集合引用，变更须经命令与事件） */
    @JsonIgnore
    public Map<CoverageId, Coverage> getCoverages() {
        return ruleComponents == null ? null : ruleComponents.getCoverages();
    }

    /** exclusions 不可变视图（DSN-02：杜绝经 getter 外泄内部集合引用，变更须经命令与事件） */
    @JsonIgnore
    public Map<ExclusionId, Exclusion> getExclusions() {
        return ruleComponents == null ? null : ruleComponents.getExclusions();
    }

    /** 缴费规则转发 getter（AXN-02 后由规则组件成员持有） */
    @JsonIgnore
    public PremiumRule getPremiumRule() {
        return ruleComponents == null ? null : ruleComponents.getPremiumRule();
    }

    /** 理赔规则转发 getter（AXN-02 后由规则组件成员持有） */
    @JsonIgnore
    public ClaimRule getClaimRule() {
        return ruleComponents == null ? null : ruleComponents.getClaimRule();
    }

    /** 合同变更规则转发 getter（AXN-02 后由规则组件成员持有） */
    @JsonIgnore
    public ContractChangeRule getContractChangeRule() {
        return ruleComponents == null ? null : ruleComponents.getContractChangeRule();
    }

    /** notifications 不可变视图（DSN-02：杜绝经 getter 外泄内部集合引用，变更须经命令与事件） */
    public List<ClauseNotification> getNotifications() {
        return notifications == null ? null : List.copyOf(notifications);
    }

    /** approvalRecords 不可变视图（DSN-02：杜绝经 getter 外泄内部集合引用，变更须经命令与事件） */
    public List<ApprovalRecord> getApprovalRecords() {
        return approvalRecords == null ? null : List.copyOf(approvalRecords);
    }
}
