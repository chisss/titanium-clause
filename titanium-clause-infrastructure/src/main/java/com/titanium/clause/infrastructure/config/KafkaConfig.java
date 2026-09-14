package com.titanium.clause.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.titanium.clause.common.constant.ClauseConstants;

/**
 * Kafka配置类
 */
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    /**
     * 创建Kafka生产者配置
     * <p>
     * 🔴 value serializer 必须是 {@link StringSerializer}，不能是 JSON 序列化器：发布方
     * {@code ClauseKafkaEventPublisher} 已用 fastjson2 生成 JSON **字符串**，JSON 序列化器会把它当作
     * 一个普通 String 值再编码一次，线上载荷遂成为「外层带引号、内层转义」的 JSON 字符串字面量
     * （实测：39 字节的 {@code {"clauseId":...}} 变为 49 字节的 {@code "{\"clauseId\":...}"}），
     * 首字节是 {@code "} 而非 {@code {}——消费端按 JSON 对象解析即刻失败。
     * </p>
     *
     * @return 生产者配置
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * 创建Kafka模板
     * <p>
     * 泛型 {@code <String, String>} 与发布器注入类型一致（发布器发的是 fastjson2 JSON 字符串），
     * 亦是全域统一形态（policy/payment 等域同此）。
     * </p>
     *
     * @return Kafka模板
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 创建Kafka消费者配置
     *
     * @return 消费者配置
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        configProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.titanium.clause.event");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * 创建Kafka监听器容器工厂
     *
     * @return 监听器容器工厂
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    /**
     * 创建条款状态变更事件主题（本域唯一外发主题）
     * <p>
     * 🔴 条款的创建/修改/废止在聚合内**统一收敛为状态流转**，外发只经「创建」与「状态变更」两条出口
     * （见 {@code ClauseKafkaEventPublisher}）。原 {@code clauseCreatedTopic}/{@code clauseUpdatedTopic}/
     * {@code clauseDeletedTopic} 三个 Bean 建的主题自建起无任何发布点、亦无消费者，属死主题，
     * 已随其同名常量一并删除（m5-903）——留着只会让 broker 上多出三个永不产生消息的主题。
     * </p>
     *
     * @return 主题对象
     */
    @Bean
    public NewTopic clauseStatusChangedTopic() {
        return TopicBuilder.name(ClauseConstants.TOPIC_CLAUSE_STATUS_CHANGED).partitions(3).replicas(2).build();
    }
}
