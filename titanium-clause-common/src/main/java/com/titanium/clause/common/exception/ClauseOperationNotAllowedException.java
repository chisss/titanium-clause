package com.titanium.clause.common.exception;

import com.titanium.metadata.errorcode.ClauseErrorCode;

/**
 * 条款操作不允许异常
 */
public class ClauseOperationNotAllowedException extends ClauseException {
    public ClauseOperationNotAllowedException(String message) {
        super(ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED.getCode(), message);
    }

    public ClauseOperationNotAllowedException(String message, Object... args) {
        super(ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED.getCode(), message, args);
    }

    public ClauseOperationNotAllowedException(String message, Throwable cause) {
        super(ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED.getCode(), message, cause);
    }

    public ClauseOperationNotAllowedException(String message, Throwable cause, Object... args) {
        super(ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED.getCode(), message, cause, args);
    }
}
