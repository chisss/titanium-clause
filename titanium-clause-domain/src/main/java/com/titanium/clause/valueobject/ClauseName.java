package com.titanium.clause.valueobject;

import java.io.Serializable;

import com.titanium.metadata.errorcode.ClauseErrorCode;
import com.titanium.metadata.exception.DomainException;

/**
 * 条款名称值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record），
 * 事件溯源 payload {@code {"value":"..."}} 可正确回读。校验异常携带 {@link ClauseErrorCode}
 * 枚举码（红线16：异常必须携带 BaseErrorCode）。</p>
 */
public record ClauseName(String value) implements Serializable {

    /**
     * 创建条款名称
     * @param value 条款名称值
     * @return 条款名称实例
     */
    public static ClauseName fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException(ClauseErrorCode.CLAUSE_NAME_REQUIRED);
        }
        if (value.length() > 100) {
            throw new DomainException(ClauseErrorCode.CLAUSE_NAME_TOO_LONG);
        }
        return new ClauseName(value.trim());
    }

    @Override
    public String toString() {
        return value;
    }
}
