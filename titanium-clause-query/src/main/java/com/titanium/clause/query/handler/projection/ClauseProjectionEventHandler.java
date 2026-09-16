package com.titanium.clause.query.handler.projection;

import java.time.LocalDateTime;
import java.util.Optional;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.event.ClauseApprovedEvent;
import com.titanium.clause.event.ClauseArchivedEvent;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseDeletedEvent;
import com.titanium.clause.event.ClauseRejectedEvent;
import com.titanium.clause.event.ClauseRevisedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.event.ClauseUpdatedEvent;
import com.titanium.clause.event.CoverageAddedEvent;
import com.titanium.clause.event.CoverageRemovedEvent;
import com.titanium.clause.event.PremiumRuleSetEvent;
import com.titanium.clause.query.mapper.ClauseRuleViewMapper;
import com.titanium.clause.query.mapper.ClauseViewMapper;
import com.titanium.clause.query.repository.ClauseViewRepository;
import com.titanium.clause.query.repository.CoverageViewRepository;
import com.titanium.clause.query.repository.PremiumRuleViewRepository;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.clause.query.view.CoverageView;
import com.titanium.clause.query.view.PremiumRuleView;
import com.titanium.common.jpa.BaseView;
import com.titanium.common.number.BusinessNumberGenerator;
import com.titanium.common.number.BusinessNumberType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 条款域读模型投影事件处理器（CQRS 读侧核心）
 * <p>
 * 订阅 clause 域领域事件，投影到读模型表 {@code t_clause_view}，实现读写分离。 只做「事件 → 读模型」写入，
 * 不发命令、不持有 CommandGateway（读侧编排越界禁止）。
 * </p>
 * <p>
 * <b>处理组</b>：{@code clause-query-group}，读侧投影 + 查询处理器 + DLQ 三处一致。
 * </p>
 */
@Slf4j
@Component
@ProcessingGroup("clause-query-group")
public class ClauseProjectionEventHandler {

    private final ClauseViewRepository      clauseViewRepository;
    private final ClauseViewMapper          clauseViewMapper;
    private final CoverageViewRepository    coverageViewRepository;
    private final PremiumRuleViewRepository premiumRuleViewRepository;
    private final ClauseRuleViewMapper      clauseRuleViewMapper;
    private final BusinessNumberGenerator   businessNumberGenerator;

    public ClauseProjectionEventHandler(ClauseViewRepository repository, ClauseViewMapper mapper, CoverageViewRepository coverageRepository, PremiumRuleViewRepository premiumRepository, ClauseRuleViewMapper ruleMapper, BusinessNumberGenerator generator) {
        this.clauseViewRepository = repository;
        this.clauseViewMapper = mapper;
        this.coverageViewRepository = coverageRepository;
        this.premiumRuleViewRepository = premiumRepository;
        this.clauseRuleViewMapper = ruleMapper;
        this.businessNumberGenerator = generator;
    }

    /**
     * 投影条款创建事件：新建读模型记录
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseCreatedEvent event) {
        log.info("[读模型投影] 条款创建: clauseId={}", event.clauseId());

        ClauseView view = clauseViewRepository
                .findByClauseIdAndTenantId(event.clauseId().value(), event.tenantId())
                .orElseGet(ClauseView::new);

        // 事件字段 → 读模型的结构映射收敛到 MapStruct（含值对象拆解），消除逐字段 set
        clauseViewMapper.applyCreated(view, event);
        if (view.getClauseNo() == null && businessNumberGenerator != null) {
            view.setClauseNo(businessNumberGenerator.next(event.tenantId(), BusinessNumberType.CLAUSE));
        }
        // status 含 DRAFT 默认回落语义，IGNORE 策略下空源会被跳过而非回落，故仍由处理器判定
        view.setStatus(event.status() != null ? event.status() : ClauseEnum.ClauseStatus.DRAFT);
        // 审计时间戳取自事件时间，含"仅首次"语义，留处理器（不下沉映射器）
        stampAuditTime(view, event.createdAt());

        clauseViewRepository.save(view);
    }

    /**
     * 投影条款更新事件
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseUpdatedEvent event) {
        log.info("[读模型投影] 条款更新: clauseId={}", event.clauseId());

        clauseViewRepository.findByClauseIdAndTenantId(event.clauseId().value(), event.tenantId())
                .ifPresentOrElse(view -> {
                    // 事件字段 → 读模型的结构映射收敛到 MapStruct（含值对象拆解），消除逐字段 set
                    clauseViewMapper.applyUpdated(view, event);
                    view.setUpdateTime(event.updatedAt());
                    clauseViewRepository.save(view);
                }, () -> log.warn("[读模型投影] 条款更新失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款状态变更事件
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseStatusChangedEvent event) {
        log.info("[读模型投影] 条款状态变更: clauseId={}, 新状态={}", event.clauseId(), event.newStatus());

        clauseViewRepository.findById(event.clauseId().value())
                .ifPresentOrElse(view -> {
                    view.setStatus(event.newStatus());
                    view.setUpdatedBy(event.updatedBy());
                    view.setUpdateTime(event.updatedAt());
                    clauseViewRepository.save(view);
                }, () -> log.warn("[读模型投影] 条款状态变更失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款提交审批事件
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseSubmittedForApprovalEvent event) {
        log.info("[读模型投影] 条款提交审批: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().value()).ifPresentOrElse(view -> {
            view.setStatus(ClauseEnum.ClauseStatus.PENDING_APPROVAL);
            view.setUpdatedBy(event.submittedBy());
            view.setUpdateTime(event.submittedAt());
            clauseViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 条款提交审批失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款审批通过事件
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseApprovedEvent event) {
        log.info("[读模型投影] 条款审批通过: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().value()).ifPresentOrElse(view -> {
            view.setStatus(ClauseEnum.ClauseStatus.ACTIVE);
            view.setUpdatedBy(event.approverId());
            view.setUpdateTime(event.approvedAt());
            clauseViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 条款审批通过失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款审批驳回事件
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseRejectedEvent event) {
        log.info("[读模型投影] 条款审批驳回: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().value()).ifPresentOrElse(view -> {
            view.setStatus(ClauseEnum.ClauseStatus.DRAFT);
            view.setUpdatedBy(event.rejectedBy());
            view.setUpdateTime(event.rejectedAt());
            clauseViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 条款审批驳回失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款修订事件：为修订生成的「新版本」条款建立独立读模型记录
     * <p>
     * 修订经写侧 {@code AggregateLifecycle.createNew} 创建了以 {@code newClauseId} 为标识的新聚合，
     * 此处对应建立一条新的 {@link ClauseView}（DRAFT 状态、{@code parentClauseId} 溯源原条款），
     * 使新版本可被查询。事件字段 → 读模型的结构映射收敛到 MapStruct（含值对象拆解），消除逐字段 set。
     * </p>
     * <p>
     * 规则组件读模型随版本复制（{@link #copyInheritedRuleViews}）：新版本聚合继承原条款责任/费率
     * （ID 复用），责任读模型以 {@code (coverageId, clauseId)} 复合主键按条款版本隔离、费率读模型以
     * clauseId 为主键天然按版本隔离，复制后新版本读模型与聚合状态一致（责任随版本变更可见、可独立增删改）。
     * </p>
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseRevisedEvent event) {
        log.info("[读模型投影] 条款修订: newClauseId={}, originalClauseId={}", event.newClauseId(),
                event.originalClauseId());

        ClauseView view = clauseViewRepository
                .findByClauseIdAndTenantId(event.newClauseId().value(), event.tenantId())
                .orElseGet(ClauseView::new);

        clauseViewMapper.applyRevised(view, event);
        // 修订态固定 DRAFT；租户显式承接（事件已携带 tenantId）
        view.setStatus(ClauseEnum.ClauseStatus.DRAFT);
        view.setTenantId(event.tenantId());
        stampAuditTime(view, event.revisedAt());

        clauseViewRepository.save(view);

        // 责任/费率读模型随版本复制：新版本读模型与写侧聚合状态一致
        copyInheritedRuleViews(event);
    }

    /**
     * 投影条款归档事件
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseArchivedEvent event) {
        log.info("[读模型投影] 条款归档: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().value()).ifPresentOrElse(view -> {
            view.setStatus(ClauseEnum.ClauseStatus.ARCHIVED);
            view.setUpdatedBy(event.archivedBy());
            view.setUpdateTime(event.archivedAt());
            clauseViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 条款归档失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款删除事件：物理移除读模型记录
     * <p>
     * 草稿条款硬删除，写侧聚合已 {@code markDeleted}，读模型同步物理删除，保持读写一致。
     * </p>
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ClauseDeletedEvent event) {
        log.info("[读模型投影] 条款删除: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().value())
                .ifPresentOrElse(clauseViewRepository::delete,
                        () -> log.warn("[读模型投影] 条款删除失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    // ==================== 规则组件投影：保险责任 / 缴费规则 ====================

    /**
     * 投影保险责任添加事件：新建/更新责任读模型记录（addCoverage 与 updateCoverage 均发此事件，故 upsert）
     * <p>
     * 责任事件未携带租户ID，从父条款读模型 {@code ClauseView} 继承，保持责任读模型的租户隔离与条款一致。
     * 父条款读模型缺失时告警跳过，由 DLQ 重试（事件乱序保障）。
     * </p>
     * <p>
     * <b>复合主键定位</b>：按 {@code (coverageId, clauseId)} 定位所属版本的责任行——修订版本继承的责任
     * （ID 复用）在此 upsert 的是新版本自己的行，不会改写原条款版本的责任读模型。
     * </p>
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(CoverageAddedEvent event) {
        String clauseId = event.clauseId().value();
        String coverageId = event.coverage().id() != null ? event.coverage().id().value() : null;
        log.info("[读模型投影] 保险责任添加: clauseId={}, coverageId={}", clauseId, coverageId);

        resolveTenantId(clauseId).ifPresentOrElse(tenantId -> {
            CoverageView view = coverageViewRepository
                    .findByCoverageIdAndClauseIdAndTenantId(coverageId, clauseId, tenantId)
                    .orElseGet(CoverageView::new);
            // 责任实体 → 读模型的结构映射（含 JSON 序列化）收敛到 MapStruct，消除逐字段 set
            clauseRuleViewMapper.applyCoverage(view, event.coverage());
            view.setClauseId(clauseId);
            view.setTenantId(tenantId);
            stampAuditTime(view, event.updatedAt());
            coverageViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 保险责任添加失败：未找到父条款读模型 clauseId={}（可能事件乱序，将由DLQ重试）", clauseId));
    }

    /**
     * 投影保险责任移除事件：物理删除责任读模型记录（按复合主键仅删该条款版本下的责任行）
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(CoverageRemovedEvent event) {
        String clauseId = event.clauseId().value();
        String coverageId = event.coverageId().value();
        log.info("[读模型投影] 保险责任移除: clauseId={}, coverageId={}", clauseId, coverageId);

        resolveTenantId(clauseId).ifPresentOrElse(tenantId -> {
            long deleted = coverageViewRepository.deleteByCoverageIdAndClauseIdAndTenantId(coverageId, clauseId,
                    tenantId);
            if (deleted == 0) {
                log.warn("[读模型投影] 保险责任移除未命中读模型记录: clauseId={}, coverageId={}", clauseId,
                        coverageId);
            }
        }, () -> log.warn("[读模型投影] 保险责任移除失败：未找到父条款读模型 clauseId={}（可能事件乱序，将由DLQ重试）", clauseId));
    }

    /**
     * 投影缴费规则设置事件：upsert 费率读模型记录（一条款一费率规则，主键即 clauseId）
     * <p>
     * 费率事件未携带租户ID，从父条款读模型继承。父条款读模型缺失时告警跳过，由 DLQ 重试。
     * </p>
     */
    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(PremiumRuleSetEvent event) {
        String clauseId = event.clauseId().value();
        log.info("[读模型投影] 缴费规则设置: clauseId={}", clauseId);

        resolveTenantId(clauseId).ifPresentOrElse(tenantId -> {
            PremiumRuleView view = premiumRuleViewRepository.findByClauseIdAndTenantId(clauseId, tenantId)
                    .orElseGet(PremiumRuleView::new);
            // 费率实体 → 读模型的结构映射（含四维费率表 JSON 序列化）收敛到 MapStruct，消除逐字段 set
            clauseRuleViewMapper.applyPremiumRule(view, event.premiumRule());
            view.setClauseId(clauseId);
            view.setTenantId(tenantId);
            stampAuditTime(view, event.updatedAt());
            premiumRuleViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 缴费规则设置失败：未找到父条款读模型 clauseId={}（可能事件乱序，将由DLQ重试）", clauseId));
    }

    /**
     * 从父条款读模型解析租户ID（规则组件事件未携带 tenantId，继承自所属条款）
     *
     * @param clauseId 条款ID
     * @return 租户ID，父条款读模型缺失时为空
     */
    private Optional<String> resolveTenantId(String clauseId) {
        return clauseViewRepository.findById(clauseId).map(ClauseView::getTenantId);
    }

    /**
     * 修订版本规则组件读模型复制：责任/费率随版本复制为新版本自己的读模型行
     * <p>
     * 修订事件携带完整规则组件快照（继承自原条款，ID 复用）与 tenantId，无需父条款解析。
     * 责任读模型按 {@code (coverageId, clauseId)} 复合主键定位新版本行（与原条款行互不冲突）；
     * 费率读模型以 clauseId 为主键，天然按条款版本隔离。审计时间戳统一取修订事件时间。
     * </p>
     *
     * @param event 条款修订事件（含继承的规则组件快照）
     */
    private void copyInheritedRuleViews(ClauseRevisedEvent event) {
        String newClauseId = event.newClauseId().value();
        String tenantId = event.tenantId();

        if (event.coverages() != null) {
            event.coverages().values().forEach(coverage -> {
                String coverageId = coverage.id() != null ? coverage.id().value() : null;
                if (coverageId == null) {
                    log.warn("[读模型投影] 条款修订复制责任跳过：责任ID缺失 newClauseId={}", newClauseId);
                    return;
                }
                CoverageView coverageView = coverageViewRepository
                        .findByCoverageIdAndClauseIdAndTenantId(coverageId, newClauseId, tenantId)
                        .orElseGet(CoverageView::new);
                clauseRuleViewMapper.applyCoverage(coverageView, coverage);
                coverageView.setClauseId(newClauseId);
                coverageView.setTenantId(tenantId);
                stampAuditTime(coverageView, event.revisedAt());
                coverageViewRepository.save(coverageView);
            });
        }

        if (event.premiumRule() != null) {
            PremiumRuleView premiumView = premiumRuleViewRepository.findByClauseIdAndTenantId(newClauseId, tenantId)
                    .orElseGet(PremiumRuleView::new);
            clauseRuleViewMapper.applyPremiumRule(premiumView, event.premiumRule());
            premiumView.setClauseId(newClauseId);
            premiumView.setTenantId(tenantId);
            stampAuditTime(premiumView, event.revisedAt());
            premiumRuleViewRepository.save(premiumView);
        }
    }

    // ==================== 读模型审计时间戳（含"仅首次"语义，不下沉映射器） ====================

    /**
     * 统一填充读模型审计时间戳：createTime 仅首次创建时写入、updateTime 每次投影刷新。
     * <p>
     * 该逻辑含"仅首次设置"语义，属投影处理器职责，不下沉 MapStruct 映射器。时间取自事件时间（业务时间），
     * 由调用方传入，而非 {@code now()}，保持与条款域既有投影语义一致。
     * </p>
     *
     * @param view 目标读模型
     * @param eventTime 事件时间（作为创建/更新时间戳）
     */
    private void stampAuditTime(BaseView view, LocalDateTime eventTime) {
        if (view.getCreateTime() == null) {
            view.setCreateTime(eventTime);
        }
        view.setUpdateTime(eventTime);
    }
}
