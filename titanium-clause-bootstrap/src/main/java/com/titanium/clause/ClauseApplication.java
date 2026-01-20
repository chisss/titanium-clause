package com.titanium.clause;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 条款服务启动类
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.titanium.clause.infrastructure.client")
@EntityScan(basePackages = "com.titanium.clause.infrastructure.entity")
@EnableJpaRepositories(basePackages = "com.titanium.clause.infrastructure.repository.jpa")
public class ClauseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClauseApplication.class, args);
    }
}
