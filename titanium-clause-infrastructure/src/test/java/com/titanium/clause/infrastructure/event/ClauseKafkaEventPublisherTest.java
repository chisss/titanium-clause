package com.titanium.clause.infrastructure.event;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.axonframework.config.ProcessingGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.titanium.clause.common.constant.ClauseConstants;
import com.titanium.clause.common.enums.ApprovalStatus;
import com.titanium.clause.common.enums.ApprovalType;
import com.titanium.clause.entity.ApprovalRecord;
import com.titanium.clause.event.ClauseApprovedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.infrastructure.config.KafkaConfig;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.common.kafka.KafkaPublishException;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款出站发布器用例
 * <p>
 * 锁死四件事：① 两条出口（状态变更 / 审批通过）共用 {@code clause-status-changed} 主题，
 * 且分区键取条款ID（D15：须与消费端回写维度一致）；② 载荷是 JSON **对象**且携带 {@code tenantId}
 * （D16：消费线程无租户上下文，载荷是消费端判定租户归属的唯一依据）；
 * ③ 🔴 线格式——生产端 value serializer 对发布器产出的 JSON 字符串**逐字节透传**，不得二次编码；
 * ④ 🔴 <b>发布失败必须抛出</b>——出站处理组为 tracking + DLQ 形态，Axon 只对「处理器抛出的异常」
 * 入队死信；若失败仅记日志，事件既不重投也不留痕，等于静默丢失。
 * </p>
 */
class ClauseKafkaEventPublisherTest {

    private static final String CLAUSE_ID = "CL-001";
    private static final String TENANT_ID = "TENANT-001";
    private static final String UPDATED_BY = "tester";
    private static final LocalDateTime OCCURRED_AT = LocalDateTime.of(2026, 9, 14, 10, 0, 0);

    private KafkaTemplate<String, String> kafkaTemplate;

    private ClauseKafkaEventPublisher publisher;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        // 默认发布成功：发布器现在会等待 broker 确认，未 stub 时 send 返回 null 会直接 NPE
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
        publisher = new ClauseKafkaEventPublisher(kafkaTemplate);
    }

    @Test
    @DisplayName("条款状态变更外发：clauseId 为分区键，载荷为 JSON 对象且含租户ID")
    void shouldPublishStatusChangedAsJsonObject() {
        publisher.on(statusChangedEvent());

        JSONObject payload = capturedPayload();

        assertEquals(CLAUSE_ID, payload.getJSONObject("clauseId").getString("value"));
        assertEquals("ACTIVE", payload.getString("newStatus"));
        assertEquals(TENANT_ID, payload.getString("tenantId"));
        // 时间字段的线格式是「空格分隔字符串」而非 Jackson 默认的时间戳数组 —— 改发 POJO 会让此处变成数组，
        // 与 billing 域 LapseNotificationMessage 踩过的坑同源，故逐字锁死。
        assertEquals("2026-09-14 10:00:00", payload.getString("updatedAt"));
    }

    @Test
    @DisplayName("条款审批通过复用同一主题，载荷含租户ID")
    void shouldPublishApprovedEventWithTenantId() {
        publisher.on(approvedEvent());

        JSONObject payload = capturedPayload();

        assertEquals(CLAUSE_ID, payload.getJSONObject("clauseId").getString("value"));
        assertEquals(TENANT_ID, payload.getString("tenantId"));
    }

    @Test
    @DisplayName("🔴 发布失败必须抛出（不再静默丢弃），否则事件不会进死信队列、无从重投")
    void shouldThrowWhenSendFails() {
        CompletableFuture<SendResult<String, String>> failed =
                CompletableFuture.failedFuture(new RuntimeException("broker unreachable"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(failed);

        ClauseStatusChangedEvent event = statusChangedEvent();

        KafkaPublishException exception = assertThrows(KafkaPublishException.class, () -> publisher.on(event));

        assertTrue(exception.getMessage().contains(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED), "异常须携带主题以便定位");
        assertNotNull(exception.getCause(), "须保留底层失败原因");
    }

    @Test
    @DisplayName("出站处理组名与 @ProcessingGroup 一致（漂移会使 DLQ 与首启位点配置同时落空）")
    void shouldDeclareProcessingGroupConsistently() {
        ProcessingGroup annotation = ClauseKafkaEventPublisher.class.getAnnotation(ProcessingGroup.class);

        assertNotNull(annotation, "出站发布器必须声明 @ProcessingGroup");
        assertEquals(ClauseKafkaEventPublisher.PROCESSING_GROUP, annotation.value());
        assertEquals("clause-kafka-group", ClauseKafkaEventPublisher.PROCESSING_GROUP,
                "组名须与 application.yml 的 axon.eventhandling.processors 键一致");
    }

    @Test
    @DisplayName("🔴 回归锁定：生产端序列化器对发布载荷逐字节透传，不再二次编码")
    void shouldTransmitPayloadByteForByteOnWire() throws ReflectiveOperationException {
        publisher.on(statusChangedEvent());
        String payload = capturedPayloadString();

        Serializer<String> valueSerializer = configuredValueSerializer();

        assertEquals(StringSerializer.class, valueSerializer.getClass(),
                "生产端 value serializer 必须是 StringSerializer —— JSON 序列化器会把已是 JSON 字符串的载荷再编码一次");
        byte[] wire = valueSerializer.serialize(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED, payload);
        assertEquals('{', (char) wire[0],
                "线上载荷首字节必须是 {；是 \" 则说明被二次编码成了 JSON 字符串字面量，消费端按对象解析即刻失败");
        assertArrayEquals(payload.getBytes(StandardCharsets.UTF_8), wire, "载荷须逐字节透传，不能在传输层被改写");
    }

    /**
     * 捕获发布器交给 KafkaTemplate 的载荷原文（第三实参）并断言其是 JSON 对象。
     */
    private String capturedPayloadString() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED), eq(CLAUSE_ID), captor.capture());
        String payload = captor.getValue();
        assertEquals('{', payload.charAt(0), "发布器交给 KafkaTemplate 的必须是 JSON 对象文本，不是 POJO 也不是转义字面量");
        return payload;
    }

    private JSONObject capturedPayload() {
        return JSON.parseObject(capturedPayloadString());
    }

    /**
     * 取生产端实际配置的 value serializer 实例。
     * <p>
     * spring-kafka 4.0 起 {@code DefaultKafkaProducerFactory#getValueSerializer()} 改为惰性字段（未创建过生产者时
     * 返回 null），故从配置属性读序列化器**类**再按其规则实例化 —— KafkaProducer 自身正按此方式取材。
     * </p>
     */
    @SuppressWarnings("unchecked")
    private Serializer<String> configuredValueSerializer() throws ReflectiveOperationException {
        Object configured = producerFactory().getConfigurationProperties()
                .get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG);
        assertInstanceOf(Class.class, configured, "生产端必须配置 value serializer");
        Serializer<String> serializer = ((Class<Serializer<String>>) configured).getDeclaredConstructor().newInstance();
        serializer.configure(Map.of(), false);
        return serializer;
    }

    /**
     * 构造未经 Spring 装配的生产端工厂（{@code @Value} 字段以反射注入），断言其确为默认实现。
     */
    private DefaultKafkaProducerFactory<String, String> producerFactory() throws ReflectiveOperationException {        KafkaConfig kafkaConfig = new KafkaConfig();
        setField(kafkaConfig, "bootstrapServers", "localhost:9092");
        setField(kafkaConfig, "consumerGroupId", "clause-test-group");
        ProducerFactory<String, String> producerFactory = kafkaConfig.producerFactory();
        assertInstanceOf(DefaultKafkaProducerFactory.class, producerFactory);
        return (DefaultKafkaProducerFactory<String, String>) producerFactory;
    }

    private void setField(Object target, String name, Object value) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private ClauseStatusChangedEvent statusChangedEvent() {
        return new ClauseStatusChangedEvent(
                ClauseId.fromString(CLAUSE_ID), ClauseEnum.ClauseStatus.ACTIVE, UPDATED_BY, OCCURRED_AT, TENANT_ID);
    }

    private ClauseApprovedEvent approvedEvent() {
        return new ClauseApprovedEvent(ClauseId.fromString(CLAUSE_ID), approvalRecord(), UPDATED_BY, OCCURRED_AT,
                TENANT_ID);
    }

    private ApprovalRecord approvalRecord() {
        return new ApprovalRecord("AR-001", ApprovalType.LEGAL, "AP-001", "审批人", ApprovalStatus.APPROVED, "同意",
                OCCURRED_AT);
    }
}
