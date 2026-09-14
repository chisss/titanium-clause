package com.titanium.clause.infrastructure.event;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

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
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款出站发布器用例
 * <p>
 * 锁死三件事：① 两条出口（状态变更 / 审批通过）共用 {@code clause-status-changed} 主题，
 * 且分区键取条款ID（D15：须与消费端回写维度一致）；② 载荷是 JSON **对象**且携带 {@code tenantId}
 * （D16：消费线程无租户上下文，载荷是消费端判定租户归属的唯一依据）；
 * ③ 🔴 线格式——生产端 value serializer 对发布器产出的 JSON 字符串**逐字节透传**，不得二次编码。
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
