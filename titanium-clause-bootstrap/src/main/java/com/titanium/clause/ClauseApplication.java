package com.titanium.clause;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 条款服务启动类（组合根）
 * <p>
 * 写侧聚合已纯事件溯源（Axon 持久化事件流），JPA 仅承载 CQRS 读侧读模型 {@code query.view} /
 * 仓储 {@code query.repository}。{@code @EnableScheduling} 驱动读侧 DLQ 重试。
 * {@code @ComponentScan} 额外扫 {@code com.titanium.common}：读侧投影发号依赖共享内核的
 * {@code JdbcBusinessNumberGenerator}（对齐 policy 域样板）。
 * </p>
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "com.titanium.clause", "com.titanium.common" })
@EnableFeignClients(basePackages = { "com.titanium.clause.api", "com.titanium.ruleengine.api" })
@EntityScan(basePackages = { "com.titanium.clause.query.view" })
@EnableJpaRepositories(basePackages = { "com.titanium.clause.query.repository" })
public class ClauseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClauseApplication.class, args);
    }
}
