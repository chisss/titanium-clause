package com.titanium.clause.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 条款服务基础异常类
 */
@Getter
public class ClauseException extends RuntimeException {
    private final String errorCode;
    private final String message;
    private final Object[] args;

    public ClauseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.args = null;
    }

    public ClauseException(String errorCode, String message, Object... args) {
        super(StringUtils.isNotBlank(message) ? message : errorCode);
        this.errorCode = errorCode;
        this.message = message;
        this.args = args;
    }

    public ClauseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.args = null;
    }

    public ClauseException(String errorCode, String message, Throwable cause, Object... args) {
        super(StringUtils.isNotBlank(message) ? message : errorCode, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.args = args;
    }
}
