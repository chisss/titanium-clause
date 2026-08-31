package com.titanium.clause.valueobject;

import java.io.Serializable;

import com.titanium.metadata.errorcode.ClauseErrorCode;
import com.titanium.metadata.exception.DomainException;

/**
 * 条款代码值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record），
 * 事件溯源 payload {@code {"value":"..."}} 可正确回读。校验异常携带 {@link ClauseErrorCode}
 * 枚举码（红线16：异常必须携带 BaseErrorCode）。</p>
 */
public record ClauseCode(String value) implements Serializable {

    /**
     * 创建条款代码
     * @param value 条款代码值
     * @return 条款代码实例
     */
    public static ClauseCode fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException(ClauseErrorCode.CLAUSE_CODE_REQUIRED);
        }
        if (value.length() > 50) {
            throw new DomainException(ClauseErrorCode.CLAUSE_CODE_TOO_LONG);
        }
        return new ClauseCode(value.trim().toUpperCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
