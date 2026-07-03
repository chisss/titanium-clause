package com.titanium.clause;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 条款服务启动类（组合根）
 * <p>
 * 写侧读模型（写侧业务查询）实体 {@code infrastructure.entity} + 读侧 CQRS 读模型 {@code query.view} 一并纳入
 * JPA 扫描；仓储同理扫描写侧 {@code infrastructure.repository.jpa} 与读侧 {@code query.repository}。
 * {@code @EnableScheduling} 驱动读侧 DLQ 重试。
 * </p>
 */
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.titanium.clause.api")
@EntityScan(basePackages = { "com.titanium.clause.infrastructure.entity", "com.titanium.clause.query.view" })
@EnableJpaRepositories(basePackages = { "com.titanium.clause.infrastructure.repository.jpa",
        "com.titanium.clause.query.repository" })
public class ClauseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClauseApplication.class, args);
    }
}
