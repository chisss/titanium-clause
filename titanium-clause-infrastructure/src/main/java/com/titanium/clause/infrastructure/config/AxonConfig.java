package com.titanium.clause.infrastructure.config;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
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
     * 快照触发阈值：聚合累计事件数达到该值即生成快照。
     * <p>
     * m26-04 真机实测（2026-10-16）：本域单聚合累计事件数最多 6，远低于阈值。取 50 与全域统一口径一致（全仓实测最大为
     * regulatory 的 RegulatoryReport 54，其余均 ≤37）——存量聚合零快照开销，同时为事件流持续增长兜底：越过阈值后聚合加载只需「最新快照
     * + 增量事件」。
     * </p>
     */
    private static final int SNAPSHOT_TRIGGER_THRESHOLD = 50;

    /**
     * 条款域快照触发器（经 {@link #clauseEventSourcingRepository} 显式挂载）。
     */
    @Bean
    public SnapshotTriggerDefinition clauseSnapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, SNAPSHOT_TRIGGER_THRESHOLD);
    }

    /**
     * 配置条款聚合的事件溯源仓储（挂载快照触发器）
     * <p>
     * 🔴 触发器必须挂在 builder 上。本域以显式 {@code EventSourcingRepository<Clause>} Bean 装配仓库，Axon 的 Spring
     * 集成直接复用该 Bean，{@code @Aggregate} 注解上的 {@code snapshotTriggerDefinition} 属性在这条装配路径上不生效（m25-01 实证）。
     * 若将来移除本 Bean 改由注解装配，须同步将注解属性指向本 Bean 名，否则快照将静默失效（无报错、无日志、无快照）。
     * </p>
     *
     * @param eventStore 事件存储
     * @param clauseSnapshotTriggerDefinition 快照触发器
     * @return 事件溯源仓储实例
     */
    @Bean
    public EventSourcingRepository<Clause> clauseEventSourcingRepository(EventStore eventStore,
            SnapshotTriggerDefinition clauseSnapshotTriggerDefinition) {
        return EventSourcingRepository.builder(Clause.class)
                .eventStore(eventStore)
                .snapshotTriggerDefinition(clauseSnapshotTriggerDefinition)
                .build();
    }
}
