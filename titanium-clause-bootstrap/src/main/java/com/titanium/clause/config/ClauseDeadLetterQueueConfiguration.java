package com.titanium.clause.config;

import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.deadletter.jpa.JpaSequencedDeadLetterQueue;
import org.axonframework.serialization.Serializer;
import org.axonframework.springboot.EventProcessorProperties;
import org.axonframework.springboot.util.DeadLetterQueueProviderConfigurerModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 条款域事件处理器死信队列配置。
 *
 * <p>Axon 4.10 在 Spring Boot 4 环境中不会自动提供 JPA DLQ provider，因此由条款服务组合根
 * 使用同一 EntityManager 与事务管理器装配 JPA 死信队列。</p>
 */
@Configuration(proxyBeanMethods = false)
public class ClauseDeadLetterQueueConfiguration {

    /**
     * 为配置中启用 DLQ 的处理组提供 JPA 持久化死信队列。
     */
    @Bean
    @ConditionalOnMissingBean(DeadLetterQueueProviderConfigurerModule.class)
    public DeadLetterQueueProviderConfigurerModule clauseDeadLetterQueueProviderConfigurerModule(
            EventProcessorProperties eventProcessorProperties,
            EntityManagerProvider entityManagerProvider,
            TransactionManager transactionManager,
            @Qualifier("serializer") Serializer genericSerializer,
            @Qualifier("eventSerializer") Serializer eventSerializer) {
        return new DeadLetterQueueProviderConfigurerModule(
                eventProcessorProperties,
                processingGroup -> configuration -> JpaSequencedDeadLetterQueue.<EventMessage<?>>builder()
                        .processingGroup(processingGroup)
                        .entityManagerProvider(entityManagerProvider)
                        .transactionManager(transactionManager)
                        .genericSerializer(genericSerializer)
                        .eventSerializer(eventSerializer)
                        .build());
    }
}
