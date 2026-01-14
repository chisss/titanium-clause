package com.titanium.clause;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 条款服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.titanium.clause")
@EnableFeignClients(basePackages = "com.titanium.clause.client")
public class ClauseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClauseApplication.class, args);
    }
}