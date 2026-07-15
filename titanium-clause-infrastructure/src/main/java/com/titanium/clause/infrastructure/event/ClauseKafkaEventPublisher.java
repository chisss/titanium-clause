package com.titanium.clause.infrastructure.event;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import com.titanium.clause.common.constant.ClauseConstants;
import com.titanium.clause.event.ClauseApprovedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 条款 Kafka 事件发布者
 * <p>
 * 监听条款域领域事件，序列化为 JSON 后发布到 Kafka 消息总线供其它域消费。 写侧聚合已纯事件溯源，读模型投影由 CQRS 读侧
 * {@code query.handler.projection.ClauseProjectionEventHandler} 负责；本组件只做跨域外发，
 * 不再回写任何 JPA 写表（原 {@code ClauseProjection} 的双写职责已移除）。
 * </p>
 */
@Slf4j
@Component
@ProcessingGroup(ClauseKafkaEventPublisher.PROCESSING_GROUP)
@RequiredArgsConstructor
public class ClauseKafkaEventPublisher {

    /** 跨域外发处理组，与写侧、读侧 clause-query-group 隔离 */
    public static final String PROCESSING_GROUP = "clause-kafka-group";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 条款状态变更外发：供其它域感知条款状态流转
     */
    @EventHandler
    public void on(ClauseStatusChangedEvent event) {
        publish(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED, event.clauseId().getValue(), event);
    }

    /**
     * 条款审批通过外发：条款生效供其它域感知
     */
    @EventHandler
    public void on(ClauseApprovedEvent event) {
        publish(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED, event.clauseId().getValue(), event);
    }

    /**
     * 统一外发：以聚合ID为分区键保证同一条款事件有序，事件体经 fastjson2 序列化。
     *
     * @param topic 目标 Kafka topic
     * @param clauseId 聚合ID，作为消息 key
     * @param event 领域事件对象
     */
    private void publish(String topic, String clauseId, Object event) {
        log.info("发布条款事件到 Kafka, topic: {}, clauseId: {}", topic, clauseId);
        kafkaTemplate.send(topic, clauseId, JSON.toJSONString(event));
    }
}
