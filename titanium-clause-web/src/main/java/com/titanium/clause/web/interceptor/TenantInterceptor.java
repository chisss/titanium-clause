package com.titanium.clause.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.titanium.clause.common.constant.ClauseConstants;
import com.titanium.common.context.RequestContextHolder;
import com.titanium.metadata.errorcode.SystemErrorCode;
import com.titanium.metadata.exception.DomainException;

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
            throw new DomainException(SystemErrorCode.TENANT_HEADER_MISSING);
        }
        // 写入租户ID到请求上下文
        RequestContextHolder.setTenantId(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 只清租户维度（整包上下文由公共 RequestContextInterceptor 统一清理）
        RequestContextHolder.setTenantId(null);
    }
}
