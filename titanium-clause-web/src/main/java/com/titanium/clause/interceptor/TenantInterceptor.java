package com.titanium.clause.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.titanium.clause.common.constant.ClauseConstants;
import com.titanium.clause.infrastructure.config.TenantContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 租户拦截器
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取租户ID
        String tenantId = request.getHeader(ClauseConstants.HEADER_TENANT_ID);
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        // 设置租户ID到ThreadLocal
        TenantContext.setCurrentTenant(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除ThreadLocal中的租户ID
        TenantContext.clear();
    }
}
