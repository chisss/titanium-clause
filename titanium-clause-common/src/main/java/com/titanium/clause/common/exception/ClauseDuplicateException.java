package com.titanium.clause.common.exception;

import com.titanium.metadata.errorcode.ClauseErrorCode;

/**
 * 条款重复异常
 */
public class ClauseDuplicateException extends ClauseException {
    public ClauseDuplicateException(String message) {
        super(ClauseErrorCode.CLAUSE_DUPLICATE.getCode(), message);
    }

    public ClauseDuplicateException(String message, Object... args) {
        super(ClauseErrorCode.CLAUSE_DUPLICATE.getCode(), message, args);
    }

    public ClauseDuplicateException(String message, Throwable cause) {
        super(ClauseErrorCode.CLAUSE_DUPLICATE.getCode(), message, cause);
    }

    public ClauseDuplicateException(String message, Throwable cause, Object... args) {
        super(ClauseErrorCode.CLAUSE_DUPLICATE.getCode(), message, cause, args);
    }
}
