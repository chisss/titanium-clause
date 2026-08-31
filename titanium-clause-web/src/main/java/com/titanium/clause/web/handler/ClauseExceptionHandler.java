package com.titanium.clause.web.handler;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.titanium.clause.common.exception.ClauseException;
import com.titanium.metadata.errorcode.ClauseErrorCode;
import com.titanium.metadata.errorcode.SystemErrorCode;
import com.titanium.metadata.exception.DomainException;
import com.titanium.metadata.response.ApiResponse;

/**
 * 条款域 Web 统一异常处理器（全局兜底）
 * <p>
 * 承接 controller 与 api provider 抛出的业务/领域异常，统一转为 {@link ApiResponse} 响应体。
 * HTTP 状态码在传输层表达（404/409/400/500），不进 {@code ApiResponse.code}（业务错误码 ≠ HTTP 状态码，
 * 见全局规约 8.2）；失败工厂只接受 {@code BaseErrorCode} 强类型。
 * </p>
 */
@RestControllerAdvice(basePackages = "com.titanium.clause.web")
public class ClauseExceptionHandler {

    /**
     * 处理条款域业务异常（ClauseNotFound/InvalidStatus/Expired/Duplicate 等，均携带 ClauseErrorCode）
     */
    @ExceptionHandler(ClauseException.class)
    public ResponseEntity<ApiResponse<Void>> handleClauseException(ClauseException exception) {
        return clauseError(exception.getErrorCode(), exception.getMessage())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED, exception.getMessage())));
    }

    /**
     * 处理领域规则异常（值对象校验等，携带 ClauseErrorCode/SystemErrorCode）
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException exception) {
        return clauseError(exception.getErrorCode(), exception.getMessage())
                .orElseGet(() -> systemError(exception.getErrorCode(), exception.getMessage()));
    }

    /**
     * 处理非法参数异常（存量裸抛的兜底，统一 400 + PARAM_INVALID）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(SystemErrorCode.PARAM_INVALID, exception.getMessage()));
    }

    /**
     * 兜底处理未预期异常（500 + SYSTEM_ERROR，不泄漏堆栈细节）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(SystemErrorCode.SYSTEM_ERROR, exception.getMessage()));
    }

    /**
     * 按 ClauseErrorCode 查找错误码并映射 HTTP 状态：不存在→404、重复→409、其余业务错误→400
     */
    private Optional<ResponseEntity<ApiResponse<Void>>> clauseError(String code, String message) {
        return Arrays.stream(ClauseErrorCode.values())
                .filter(errorCode -> errorCode.getCode().equals(code))
                .findFirst()
                .map(errorCode -> ResponseEntity.status(statusFor(errorCode))
                        .body(ApiResponse.<Void>error(errorCode, message)));
    }

    /**
     * 按 SystemErrorCode 查找错误码并映射 HTTP 状态：参数/租户头缺失→400、资源不存在→404、其余→500
     */
    private ResponseEntity<ApiResponse<Void>> systemError(String code, String message) {
        return Arrays.stream(SystemErrorCode.values())
                .filter(errorCode -> errorCode.getCode().equals(code))
                .findFirst()
                .map(errorCode -> ResponseEntity.status(statusFor(errorCode))
                        .body(ApiResponse.<Void>error(errorCode, message)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(SystemErrorCode.SYSTEM_ERROR, message)));
    }

    private HttpStatus statusFor(ClauseErrorCode errorCode) {
        return switch (errorCode) {
            case CLAUSE_NOT_EXIST -> HttpStatus.NOT_FOUND;
            case CLAUSE_DUPLICATE -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private HttpStatus statusFor(SystemErrorCode errorCode) {
        return switch (errorCode) {
            case PARAM_INVALID, TENANT_HEADER_MISSING, TENANT_CONTEXT_ERROR -> HttpStatus.BAD_REQUEST;
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
