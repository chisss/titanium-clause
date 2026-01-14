package com.titanium.clause.exception;

import com.titanium.metadata.errorcode.ClauseErrorCode;

/**
 * 条款状态无效异常
 */
public class ClauseInvalidStatusException extends ClauseException {
    public ClauseInvalidStatusException(String message) {
        super(ClauseErrorCode.CLAUSE_INVALID_STATUS.getCode(), message);
    }

    public ClauseInvalidStatusException(String message, Object... args) {
        super(ClauseErrorCode.CLAUSE_INVALID_STATUS.getCode(), message, args);
    }

    public ClauseInvalidStatusException(String message, Throwable cause) {
        super(ClauseErrorCode.CLAUSE_INVALID_STATUS.getCode(), message, cause);
    }

    public ClauseInvalidStatusException(String message, Throwable cause, Object... args) {
        super(ClauseErrorCode.CLAUSE_INVALID_STATUS.getCode(), message, cause, args);
    }
}
