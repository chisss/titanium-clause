package com.titanium.clause.aggregate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateLifecycle;

import com.titanium.clause.command.AddCoverageCommand;
import com.titanium.clause.command.AddExclusionCommand;
import com.titanium.clause.command.RemoveCoverageCommand;
import com.titanium.clause.command.RemoveExclusionCommand;
import com.titanium.clause.command.SetClaimRuleCommand;
import com.titanium.clause.command.SetContractChangeRuleCommand;
import com.titanium.clause.command.SetPremiumRuleCommand;
import com.titanium.clause.command.UpdateCoverageCommand;
import com.titanium.clause.common.exception.ClauseOperationNotAllowedException;
import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.event.ClaimRuleSetEvent;
import com.titanium.clause.event.ContractChangeRuleSetEvent;
import com.titanium.clause.event.CoverageAddedEvent;
import com.titanium.clause.event.CoverageRemovedEvent;
import com.titanium.clause.event.ExclusionAddedEvent;
import com.titanium.clause.event.ExclusionRemovedEvent;
import com.titanium.clause.event.PremiumRuleSetEvent;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.ExclusionId;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Clause 聚合的「规则组件（Rule Components）」成员实体（AXN-02 自聚合根下沉，m26-03 批次）。
 * <p>
 * <b>职责边界</b>：承载条款规则组件的全部状态——保险责任（{@link #coverages}）、责任免除
 * （{@link #exclusions}）与三类规则（{@link #premiumRule} / {@link #claimRule} /
 * {@link #contractChangeRule}）。八类组件命令（增删保险责任、增删责任免除、设置缴费/理赔/合同变更规则）
 * 由本实体的 {@code @CommandHandler} 处理、状态由本实体的 {@code @EventSourcingHandler} 回放重建。
 * {@link #status} 是聚合根状态的<b>镜像</b>，仅服务于规则组件命令的「草稿态」门禁——Axon 经
 * {@code @AggregateMember} 把命令直接路由到本实体，不经过聚合根处理器，故草稿态须由根侧各状态事件
 * 回放处理器经 {@link #markStatus} 同步一份到本实体。
 * </p>
 * <p>
 * <b>成员不互引（🔴 勿破坏）</b>：本实体只持有自身主题的状态，不引用聚合内其它成员；快照序列化
 * （Axon JacksonSerializer）会把成员内联进聚合 JSON，成员互引会导致同一对象被序列化两次、反序列化出
 * 相互独立的副本 ⇒ 状态不同步。依赖其它主题的判定留在聚合根。
 * </p>
 * <p>
 * <b>下沉边界（🔴 为何只下沉八个处理器）</b>：只有完全操作本成员自身状态的处理器随之下沉；以下处理器
 * <b>留驻聚合根</b>：① 条款生命周期族（创建/更新/修订/状态迁移/审批/归档/删除——操作根字段并 apply
 * 根事件）；② 告知与签署族（{@code notifications} / {@code signTemplate} 不属规则组件）；③
 * {@code validateClaim} 理赔校验（读根状态与时间范围，规则组件数据经本实体 getter 取）。
 * 🔴 聚合根<b>不得</b>重建与本实体同名的命令处理器——Axon 会报多处理器冲突；下沉即搬迁，不是复制。
 * </p>
 * <p>
 * <b>双监听为设计意图（🔴 勿合并或迁移）</b>：七类组件事件的 {@code updatedBy} / {@code updateTime}
 * 审计字段由聚合根侧同名 {@code @EventSourcingHandler} 维护，本实体只重建业务状态。
 * </p>
 */
@Getter
@NoArgsConstructor
public class ClauseRuleComponents {

    /** 保险责任（键为责任标识；添加/更新事件按标识 put，移除事件按标识 remove） */
    private Map<CoverageId, Coverage>   coverages   = new HashMap<>();

    /** 责任免除（键为免除标识；语义同 coverages） */
    private Map<ExclusionId, Exclusion> exclusions  = new HashMap<>();

    /** 缴费规则（未设置时为 null） */
    private PremiumRule                 premiumRule;

    /** 理赔规则（未设置时为 null） */
    private ClaimRule                   claimRule;

    /** 合同变更规则（未设置时为 null） */
    private ContractChangeRule          contractChangeRule;

    /** 聚合根状态的镜像（根侧各状态事件回放处理器经 {@link #markStatus} 同步），仅服务于草稿态门禁 */
    private ClauseEnum.ClauseStatus     status;

    /**
     * 包级构造器（根侧创建/修订事件回放时调用）。
     * <p>
     * null 集合按「空表」规范化、null 规则按「未设置」保留，与下沉前聚合根在
     * {@code ClauseCreatedEvent} / {@code ClauseRevisedEvent} 回放中的初始化语义逐字一致。
     * </p>
     */
    ClauseRuleComponents(Map<CoverageId, Coverage> coverages, Map<ExclusionId, Exclusion> exclusions,
            PremiumRule premiumRule, ClaimRule claimRule, ContractChangeRule contractChangeRule,
            ClauseEnum.ClauseStatus status) {
        this.coverages = coverages != null ? new HashMap<>(coverages) : new HashMap<>();
        this.exclusions = exclusions != null ? new HashMap<>(exclusions) : new HashMap<>();
        this.premiumRule = premiumRule;
        this.claimRule = claimRule;
        this.contractChangeRule = contractChangeRule;
        this.status = status;
    }

    // ==================== 命令处理器（完全操作本成员自身状态者随之下沉） ====================

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
        if (!this.coverages.containsKey(command.coverage().id())) {
            throw new ClauseOperationNotAllowedException("保险责任不存在: " + command.coverage().id());
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

    // ==================== 事件溯源处理器（业务状态重建；审计字段由聚合根侧同名处理器维护） ====================

    /** 保险责任添加回放：业务状态重建。审计字段由聚合根侧同名处理器维护（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(CoverageAddedEvent event) {
        if (this.coverages == null) {
            this.coverages = new HashMap<>();
        }
        this.coverages.put(event.coverage().id(), event.coverage());
    }

    /** 保险责任移除回放：业务状态重建。审计字段由聚合根侧同名处理器维护（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(CoverageRemovedEvent event) {
        if (this.coverages != null) {
            this.coverages.remove(event.coverageId());
        }
    }

    /** 责任免除添加回放：业务状态重建。审计字段由聚合根侧同名处理器维护（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ExclusionAddedEvent event) {
        if (this.exclusions == null) {
            this.exclusions = new HashMap<>();
        }
        this.exclusions.put(event.exclusion().id(), event.exclusion());
    }

    /** 责任免除移除回放：业务状态重建。审计字段由聚合根侧同名处理器维护（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ExclusionRemovedEvent event) {
        if (this.exclusions != null) {
            this.exclusions.remove(event.exclusionId());
        }
    }

    /** 缴费规则设置回放：业务状态重建。审计字段由聚合根侧同名处理器维护（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(PremiumRuleSetEvent event) {
        this.premiumRule = event.premiumRule();
    }

    /** 理赔规则设置回放：业务状态重建。审计字段由聚合根侧同名处理器维护（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ClaimRuleSetEvent event) {
        this.claimRule = event.claimRule();
    }

    /** 合同变更规则设置回放：业务状态重建。审计字段由聚合根侧同名处理器维护（双监听为设计意图）。 */
    @EventSourcingHandler
    public void on(ContractChangeRuleSetEvent event) {
        this.contractChangeRule = event.contractChangeRule();
    }

    // ==================== 供聚合根调用的包级方法（状态同步 / 更新应用 / 读取判定） ====================

    /**
     * 同步聚合根状态镜像（根侧各状态事件回放处理器调用）。
     * <p>
     * 规则组件命令的草稿态门禁以本镜像为准；漏同步会让非草稿状态的组件变更被误放行（静默失效，
     * 无异常可察），故根侧每次状态迁移都必须调用本方法。
     * </p>
     */
    void markStatus(ClauseEnum.ClauseStatus status) {
        this.status = status;
    }

    /**
     * 应用条款更新载荷（根侧 {@code ClauseUpdatedEvent} 回放处理器调用）。
     * <p>
     * null 表示该组件本次未变更、保留现值；语义与下沉前聚合根在 {@code ClauseUpdatedEvent}
     * 回放中的逐字段 null 判断逐字一致。
     * </p>
     */
    void applyUpdate(Map<CoverageId, Coverage> coverages, Map<ExclusionId, Exclusion> exclusions,
            PremiumRule premiumRule, ClaimRule claimRule, ContractChangeRule contractChangeRule) {
        this.coverages = coverages != null ? new HashMap<>(coverages) : this.coverages;
        this.exclusions = exclusions != null ? new HashMap<>(exclusions) : this.exclusions;
        this.premiumRule = premiumRule != null ? premiumRule : this.premiumRule;
        this.claimRule = claimRule != null ? claimRule : this.claimRule;
        this.contractChangeRule = contractChangeRule != null ? contractChangeRule : this.contractChangeRule;
    }

    /** 是否已配置至少一项保险责任（根侧提交审批校验调用）。 */
    boolean hasCoverages() {
        return coverages != null && !coverages.isEmpty();
    }

    // ==================== 只读视图（DSN-02 同源纪律：内部集合不得经 getter 外泄） ====================

    /** coverages 不可变视图（DSN-02：杜绝经 getter 外泄内部集合引用，变更须经命令与事件） */
    public Map<CoverageId, Coverage> getCoverages() {
        return coverages == null ? null : Map.copyOf(coverages);
    }

    /** exclusions 不可变视图（DSN-02：杜绝经 getter 外泄内部集合引用，变更须经命令与事件） */
    public Map<ExclusionId, Exclusion> getExclusions() {
        return exclusions == null ? null : Map.copyOf(exclusions);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 校验条款当前是否为草稿状态，仅草稿状态允许修改规则组件。
     * <p>
     * 与聚合根侧同名方法（告知/签署族留驻命令使用）规则逐字同源，状态来源不同：根侧读根状态字段、
     * 本实体读 {@link #status} 镜像。
     * </p>
     */
    private void validateDraftStatus(String operation) {
        if (this.status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseOperationNotAllowedException("仅草稿状态允许" + operation + "，当前状态: " + this.status.getName());
        }
    }
}
