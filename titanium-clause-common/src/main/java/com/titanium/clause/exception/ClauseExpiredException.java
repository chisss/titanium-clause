package com.titanium.clause.exception;

import com.titanium.metadata.errorcode.ClauseErrorCode;

/**
 * 条款已过期异常
 */
public class ClauseExpiredException extends ClauseException {
    public ClauseExpiredException(String message) {
        super(ClauseErrorCode.CLAUSE_EXPIRED.getCode(), message);
    }

    public ClauseExpiredException(String message, Object... args) {
        super(ClauseErrorCode.CLAUSE_EXPIRED.getCode(), message, args);
    }

    public ClauseExpiredException(String message, Throwable cause) {
        super(ClauseErrorCode.CLAUSE_EXPIRED.getCode(), message, cause);
    }

    public ClauseExpiredException(String message, Throwable cause, Object... args) {
        super(ClauseErrorCode.CLAUSE_EXPIRED.getCode(), message, cause, args);
    }
}
