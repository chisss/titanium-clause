package com.titanium.clause.query.handler.projection;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.event.ClauseApprovedEvent;
import com.titanium.clause.event.ClauseArchivedEvent;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseRejectedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.event.ClauseUpdatedEvent;
import com.titanium.clause.query.repository.ClauseViewRepository;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ClauseProjectionEventHandler {

    private final ClauseViewRepository clauseViewRepository;

    /**
     * 投影条款创建事件：新建读模型记录
     */
    @EventHandler
    @Transactional
    public void on(ClauseCreatedEvent event) {
        log.info("[读模型投影] 条款创建: clauseId={}", event.clauseId());

        ClauseView view = clauseViewRepository
                .findByClauseIdAndTenantId(event.clauseId().getValue(), event.tenantId())
                .orElseGet(ClauseView::new);

        view.setClauseId(event.clauseId().getValue());
        view.setClauseCode(event.clauseCode() != null ? event.clauseCode().getValue() : null);
        view.setClauseName(event.clauseName() != null ? event.clauseName().getValue() : null);
        view.setClauseType(event.clauseType());
        view.setContent(event.content());
        view.setDescription(event.description());
        view.setInsuranceType(event.insuranceType());
        view.setClauseVersion(event.version() != null ? event.version().getValue() : null);
        view.setParentClauseId(event.parentClauseId() != null ? event.parentClauseId().getValue() : null);
        view.setEffectiveDate(event.effectiveDate());
        view.setExpiryDate(event.expiryDate());
        view.setStatus(event.status() != null ? event.status() : ClauseEnum.ClauseStatus.DRAFT);
        view.setCreatedBy(event.createdBy());
        view.setUpdatedBy(event.updatedBy());
        view.setTenantId(event.tenantId());
        if (view.getCreateTime() == null) {
            view.setCreateTime(event.createdAt());
        }
        view.setUpdateTime(event.createdAt());

        clauseViewRepository.save(view);
    }

    /**
     * 投影条款更新事件
     */
    @EventHandler
    @Transactional
    public void on(ClauseUpdatedEvent event) {
        log.info("[读模型投影] 条款更新: clauseId={}", event.clauseId());

        clauseViewRepository.findByClauseIdAndTenantId(event.clauseId().getValue(), event.tenantId())
                .ifPresentOrElse(view -> {
                    view.setClauseName(event.clauseName() != null ? event.clauseName().getValue() : null);
                    view.setClauseType(event.clauseType());
                    view.setContent(event.content());
                    view.setDescription(event.description());
                    view.setInsuranceType(event.insuranceType());
                    view.setEffectiveDate(event.effectiveDate());
                    view.setExpiryDate(event.expiryDate());
                    view.setUpdatedBy(event.updatedBy());
                    view.setUpdateTime(event.updatedAt());
                    clauseViewRepository.save(view);
                }, () -> log.warn("[读模型投影] 条款更新失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款状态变更事件
     */
    @EventHandler
    @Transactional
    public void on(ClauseStatusChangedEvent event) {
        log.info("[读模型投影] 条款状态变更: clauseId={}, 新状态={}", event.clauseId(), event.newStatus());

        clauseViewRepository.findById(event.clauseId().getValue())
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
    @Transactional
    public void on(ClauseSubmittedForApprovalEvent event) {
        log.info("[读模型投影] 条款提交审批: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().getValue()).ifPresentOrElse(view -> {
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
    @Transactional
    public void on(ClauseApprovedEvent event) {
        log.info("[读模型投影] 条款审批通过: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().getValue()).ifPresentOrElse(view -> {
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
    @Transactional
    public void on(ClauseRejectedEvent event) {
        log.info("[读模型投影] 条款审批驳回: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().getValue()).ifPresentOrElse(view -> {
            view.setStatus(ClauseEnum.ClauseStatus.DRAFT);
            view.setUpdatedBy(event.rejectedBy());
            view.setUpdateTime(event.rejectedAt());
            clauseViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 条款审批驳回失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }

    /**
     * 投影条款归档事件
     */
    @EventHandler
    @Transactional
    public void on(ClauseArchivedEvent event) {
        log.info("[读模型投影] 条款归档: clauseId={}", event.clauseId());

        clauseViewRepository.findById(event.clauseId().getValue()).ifPresentOrElse(view -> {
            view.setStatus(ClauseEnum.ClauseStatus.ARCHIVED);
            view.setUpdatedBy(event.archivedBy());
            view.setUpdateTime(event.archivedAt());
            clauseViewRepository.save(view);
        }, () -> log.warn("[读模型投影] 条款归档失败：未找到读模型记录 clauseId={}", event.clauseId()));
    }
}
