package com.titanium.clause.infrastructure.projection;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseUpdatedEvent;
import com.titanium.clause.repository.ClauseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 条款投影处理器
 */
@Component
@RequiredArgsConstructor
public class ClauseProjection {
    private static final Logger                 logger = LoggerFactory.getLogger(ClauseProjection.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ClauseRepository              clauseRepository;

    /**
     * 处理条款创建事件
     * 
     * @param event 条款创建事件
     */
    @EventHandler
    public void handle(ClauseCreatedEvent event) {
        logger.info("处理条款创建事件: {}", event.clauseId());

        // 使用Mapper转换，将领域对象转换为JPA实体

        Clause clause = new Clause();
        clause.setClauseId(event.clauseId());
        clause.setClauseCode(event.clauseCode());
        clause.setClauseName(event.clauseName());
        clause.setClauseType(event.clauseType());
        clause.setContent(event.content());
        clause.setDescription(event.description());
        clause.setEffectiveDate(event.effectiveDate());
        clause.setExpiryDate(event.expiryDate());
        clause.setStatus(event.status());
        clause.setCreatedBy(event.createdBy());
        clause.setCreatedAt(event.createdAt());
        clause.setTenantId(event.tenantId());

        // 保存到数据库
        clauseRepository.save(clause);
    }

    /**
     * 处理条款更新事件
     * 
     * @param event 条款更新事件
     */
    @EventHandler
    public void handle(ClauseUpdatedEvent event) {
        logger.info("处理条款更新事件: {}", event.clauseCode());

        // 查找条款
        var clauseOptional = clauseRepository.findById(event.clauseId(), event.tenantId());
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();

            // 更新条款信息
            clause.setClauseName(event.clauseName());
            clause.setClauseType(event.clauseType());
            clause.setContent(event.content());
            clause.setDescription(event.description());
            clause.setEffectiveDate(event.effectiveDate());
            clause.setExpiryDate(event.expiryDate());
            clause.setUpdatedBy(event.updatedBy());
            clause.setUpdatedAt(event.updatedAt());

            // 保存到数据库
            clauseRepository.save(clause);
        }
    }

    /**
     * 处理条款状态变更事件
     * 
     * @param event 条款状态变更事件
     */
    @EventHandler
    public void handle(ClauseStatusChangedEvent event) {
        logger.info("处理条款状态变更事件: {}, 从 {} 变更为 {}", event.clauseId(), null, event.newStatus());

        // 查找条款
        var clauseOptional = clauseRepository.findById(event.clauseId(), null);
        if (clauseOptional.isPresent()) {
            Clause clause = clauseOptional.get();

            // 更新条款状态
            clause.setStatus(event.newStatus());
            clause.setUpdatedBy(event.updatedBy());
            clause.setUpdatedAt(event.updatedAt());

            // 保存到数据库
            clauseRepository.save(clause);

        }
    }
}
