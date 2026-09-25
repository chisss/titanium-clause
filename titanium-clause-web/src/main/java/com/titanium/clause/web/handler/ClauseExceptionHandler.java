package com.titanium.clause.web.handler;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.titanium.clause.common.exception.ClauseException;
import com.titanium.common.boundary.ErrorCategoryHttpStatus;
import com.titanium.common.boundary.ExceptionResponseFactory;
import com.titanium.metadata.errorcode.ClauseErrorCode;
import com.titanium.metadata.errorcode.SystemErrorCode;
import com.titanium.metadata.exception.DomainException;
import com.titanium.metadata.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 条款域 Web 统一异常处理器（全局兜底）
 * <p>
 * 承接 controller 与 api provider 抛出的业务/领域异常，统一转为 {@link ApiResponse} 响应体。
 * HTTP 状态码在传输层表达（404/409/422/500），不进 {@code ApiResponse.code}（业务错误码 ≠ HTTP 状态码，
 * 见全局规约 8.2）；失败工厂只接受 {@code BaseErrorCode} 强类型。
 * </p>
 * <p>
 * 🔴 <b>状态码由错误码的语义类别决定，不由错误码的名字决定</b>：改造前本类持有一份手写的
 * 「常量 → 状态」switch，其中只有 2 个常量被显式列出（{@code CLAUSE_NOT_EXIST}→404、
 * {@code CLAUSE_DUPLICATE}→409），其余一律落 {@code default -> 400}；而同类语义的其它常量
 * （{@code CLAUSE_EXPIRED}、{@code CLAUSE_INVALID_STATUS}、{@code CLAUSE_OPERATION_NOT_ALLOWED}）
 * 按类别应为 409。手写表因此是**残缺的**，且新增常量不会触发任何构建期告警。
 * 现统一走 {@link ExceptionResponseFactory}——它内部用
 * {@link ErrorCategoryHttpStatus}（全系统唯一一份「类别 → 状态」表）定状态，
 * 并对 5xx 改用码表文案（不透传异常自带消息）。
 * </p>
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.titanium.clause.web")
public class ClauseExceptionHandler {

    /**
     * 处理条款域业务异常（ClauseNotFound/InvalidStatus/Expired/Duplicate 等，均携带 ClauseErrorCode）
     */
    @ExceptionHandler(ClauseException.class)
    public ResponseEntity<ApiResponse<Void>> handleClauseException(ClauseException exception) {
        return clauseError(exception)
                .orElseGet(() -> ExceptionResponseFactory.of(exception, ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED));
    }

    /**
     * 处理领域规则异常（值对象校验等，携带 ClauseErrorCode/SystemErrorCode）。
     * <p>
     * 状态与错误码均取自异常自身：{@code DomainException} 已强类型持有 {@code BaseErrorCode}，
     * 故无需在处理器里逐类型分支——原先那两段 {@code instanceof ClauseErrorCode/SystemErrorCode}
     * 只是为了喂给各自的 {@code statusFor} 手写表，表删了分支也就没了。
     * </p>
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException exception) {
        return ExceptionResponseFactory.of(exception);
    }

    /**
     * 兜底处理未预期异常（500 + SYSTEM_ERROR；不泄漏堆栈与原始消息）
     * <p>
     * 🔴 本处理器有意接管 {@code IllegalArgumentException}/{@code IllegalStateException}（审查报告 EXC-05）：
     * 二者语义是<b>编程错误</b>而非「调用方参数错误」，故原 IAE→400 兜底已删除——它会把「服务端自己写错了」
     * 误报成「客户端传错了」。入参类错误已在边界由 Bean Validation 拦为 400（API-03），领域内的业务条件
     * 一律抛携带错误码的领域异常，故该路径上不再应有正常业务流量；真命中即说明存在未收敛的裸抛点。
     * </p>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        // CWE-209：原始异常消息只进日志、不回显给调用方。原实现返回 exception.getMessage()，
        // 会把内部类名 / 包名 / SQL 片段 / 下游服务地址一并泄露，为攻击者提供侦察信息。
        log.error("[条款] 未处理异常，请求处理失败", exception);
        return ExceptionResponseFactory.of(SystemErrorCode.SYSTEM_ERROR);
    }

    /**
     * 按码串为本域 {@link ClauseException} 反查枚举并产出响应（状态取该码的语义类别）。
     * <p>
     * 仅服务于 {@link ClauseException} 分支——该异常族<b>不继承</b> {@code DomainException}，
     * 自带 {@code String errorCode}（独立的遗留异常族，归 EXC-01 的后续任务收敛），
     * 故此处仍需按码串反查。{@code DomainException} 分支已改为直接用强类型枚举，无反查。
     * </p>
     * <p>
     * 🔴 产出响应必须经 {@link ExceptionResponseFactory}，<b>不得</b>自行拼
     * {@code ResponseEntity.status(...).body(ApiResponse.error(errorCode, message))}：原实现如此，
     * 于是当错误码的类别落在 5xx 时，会把异常自带的内部文案（类名 / 字段路径 / 下游地址）
     * 当作 5xx 响应体回显出去（CWE-209）。工厂对 5xx 一律改用码表文案，细节只进日志；
     * 4xx 仍回显调用方可见的文案，行为与改前一致。
     * </p>
     */
    private Optional<ResponseEntity<ApiResponse<Void>>> clauseError(ClauseException exception) {
        return Arrays.stream(ClauseErrorCode.values())
                .filter(errorCode -> errorCode.getCode().equals(exception.getErrorCode()))
                .findFirst()
                .map(errorCode -> ExceptionResponseFactory.of(exception, errorCode));
    }
}
