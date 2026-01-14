package com.titanium.clause.exception;

import com.titanium.metadata.errorcode.ClauseErrorCode;

/**
 * 条款不存在异常
 */
public class ClauseNotFoundException extends ClauseException {
    public ClauseNotFoundException(String message) {
        super(ClauseErrorCode.CLAUSE_NOT_EXIST.getCode(), message);
    }

    public ClauseNotFoundException(String message, Object... args) {
        super(ClauseErrorCode.CLAUSE_NOT_EXIST.getCode(), message, args);
    }

    public ClauseNotFoundException(String message, Throwable cause) {
        super(ClauseErrorCode.CLAUSE_NOT_EXIST.getCode(), message, cause);
    }

    public ClauseNotFoundException(String message, Throwable cause, Object... args) {
        super(ClauseErrorCode.CLAUSE_NOT_EXIST.getCode(), message, cause, args);
    }
}
