package com.titanium.clause.infrastructure.config;

import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.titanium.clause.aggregate.Clause;

/**
 * Axon Framework配置类
 */
@Configuration
public class AxonConfig {
    /**
     * 配置命令总线
     *
     * @return 命令总线实例
     */
    // 🔴 m21-09（审查报告 ORC-05）：原手写 commandBus() bean 已删除。
    //    它是 SimpleCommandBus.builder().build()——与 Axon 自动配置的默认命令总线**完全等价**，
    //    手写的唯一实际效果是**让本域绕过 axon-spring-boot-autoconfigure 的总线装配**
    //    （在接入 AxonServer 的形态下会形成「局部单体」：本域命令不经 AxonServer 分发），
    //    且会连带绕过 common 在 CommandGateway 上装配的 RetryScheduler（m21-08）。
    //    删除后本域与其他域一致，由自动配置提供 CommandBus。

    /**
     * 配置条款聚合的事件溯源仓储
     *
     * @param eventStore 事件存储
     * @return 事件溯源仓储实例
     */
    @Bean
    public EventSourcingRepository<Clause> clauseEventSourcingRepository(
            EventStore eventStore) {
        return EventSourcingRepository.builder(Clause.class)
                .eventStore(eventStore)
                .build();
    }
}
