package com.titanium.clause.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.titanium.clause.web.interceptor.TenantInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * Web配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册租户拦截器，拦截后台/端上(/web)与远程契约(/api)两类入口
        // 说明：ClauseController(/web/**) 依赖 TenantContext 取租户，若不拦截则租户为 null，
        //      读模型查询会退化为 where tenant_id is null，命中不到任何数据
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/web/**", "/api/**");
    }
}
