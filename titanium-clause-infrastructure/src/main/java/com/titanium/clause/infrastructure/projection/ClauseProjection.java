package com.titanium.clause.infrastructure.projection;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.common.constant.ClauseConstants;
import com.titanium.clause.event.ClauseApprovedEvent;
import com.titanium.clause.event.ClauseArchivedEvent;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseRejectedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.event.ClauseUpdatedEvent;
import com.titanium.clause.repository.ClauseRepository;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款投影处理器
 * <p>
 * 负责将领域事件投影到读模型（数据库），同时发布Kafka事件供其他域消费。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ClauseProjection {
    private static final Logger                 LOGGER = LoggerFactory.getLogger(ClauseProjection.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ClauseRepository              clauseRepository;

    /**
     * 处理条款创建事件
     */
    @EventHandler
    public void handle(ClauseCreatedEvent event) {
        LOGGER.info("处理条款创建事件: {}", event.clauseId());

        Clause clause = Clause.builder()
                .tenantId(event.tenantId())
                .createTime(event.createdAt())
                .updateTime(event.createdAt())
                .build();
        clause.setClauseId(event.clauseId());
        clause.setClauseCode(event.clauseCode());
        clause.setClauseName(event.clauseName());
        clause.setClauseType(event.clauseType());
        clause.setContent(event.content());
        clause.setDescription(event.description());
        clause.setInsuranceType(event.insuranceType());
        clause.setVersion(event.version());
        clause.setParentClauseId(event.parentClauseId());
        clause.setEffectiveDate(event.effectiveDate());
        clause.setExpiryDate(event.expiryDate());
        clause.setStatus(ClauseEnum.ClauseStatus.DRAFT);
        clause.setCreatedBy(event.createdBy());

        clauseRepository.save(clause);
    }

    /**
     * 处理条款更新事件
     */
    @EventHandler
    public void handle(ClauseUpdatedEvent event) {
        LOGGER.info("处理条款更新事件: {}", event.clauseCode());

        var clauseOptional = clauseRepository.findById(event.clauseId(), event.tenantId());
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();
            clause.setClauseName(event.clauseName());
            clause.setClauseType(event.clauseType());
            clause.setContent(event.content());
            clause.setDescription(event.description());
            clause.setInsuranceType(event.insuranceType());
            clause.setEffectiveDate(event.effectiveDate());
            clause.setExpiryDate(event.expiryDate());
            clause.setUpdatedBy(event.updatedBy());
            clause = clause.toBuilder().updateTime(event.updatedAt()).build();

            clauseRepository.save(clause);
        }
    }

    /**
     * 处理条款状态变更事件
     */
    @EventHandler
    public void handle(ClauseStatusChangedEvent event) {
        LOGGER.info("处理条款状态变更事件: {}, 新状态: {}", event.clauseId(), event.newStatus());

        var clauseOptional = clauseRepository.findById(event.clauseId(), null);
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();
            clause.setStatus(event.newStatus());
            clause.setUpdatedBy(event.updatedBy());
            clause = clause.toBuilder().updateTime(event.updatedAt()).build();

            clauseRepository.save(clause);

            // 发布Kafka事件供其他域消费
            kafkaTemplate.send(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED, event);
        }
    }

    /**
     * 处理条款提交审批事件
     */
    @EventHandler
    public void handle(ClauseSubmittedForApprovalEvent event) {
        LOGGER.info("处理条款提交审批事件: {}", event.clauseId());

        var clauseOptional = clauseRepository.findById(event.clauseId(), null);
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();
            clause.setStatus(ClauseEnum.ClauseStatus.PENDING_APPROVAL);
            clause.setUpdatedBy(event.submittedBy());
            clause = clause.toBuilder().updateTime(event.submittedAt()).build();

            clauseRepository.save(clause);
        }
    }

    /**
     * 处理条款审批通过事件
     */
    @EventHandler
    public void handle(ClauseApprovedEvent event) {
        LOGGER.info("处理条款审批通过事件: {}", event.clauseId());

        var clauseOptional = clauseRepository.findById(event.clauseId(), null);
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();
            clause.setStatus(ClauseEnum.ClauseStatus.ACTIVE);
            clause.setUpdatedBy(event.approverId());
            clause = clause.toBuilder().updateTime(event.approvedAt()).build();

            clauseRepository.save(clause);

            // 发布条款激活Kafka事件
            kafkaTemplate.send(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED, event);
        }
    }

    /**
     * 处理条款审批驳回事件
     */
    @EventHandler
    public void handle(ClauseRejectedEvent event) {
        LOGGER.info("处理条款审批驳回事件: {}", event.clauseId());

        var clauseOptional = clauseRepository.findById(event.clauseId(), null);
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();
            clause.setStatus(ClauseEnum.ClauseStatus.DRAFT);
            clause.setUpdatedBy(event.rejectedBy());
            clause = clause.toBuilder().updateTime(event.rejectedAt()).build();

            clauseRepository.save(clause);
        }
    }

    /**
     * 处理条款归档事件
     */
    @EventHandler
    public void handle(ClauseArchivedEvent event) {
        LOGGER.info("处理条款归档事件: {}", event.clauseId());

        var clauseOptional = clauseRepository.findById(event.clauseId(), null);
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();
            clause.setStatus(ClauseEnum.ClauseStatus.ARCHIVED);
            clause.setUpdatedBy(event.archivedBy());
            clause = clause.toBuilder().updateTime(event.archivedAt()).build();

            clauseRepository.save(clause);
        }
    }
}
