package com.titanium.clause.domain.valueobject;

import java.io.Serializable;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 条款ID值对象
 */
@EqualsAndHashCode
@Getter
public class ClauseId implements Serializable {
    private final String value;

    /**
     * 构造函数
     * @param value 条款ID值
     */
    public ClauseId(String value) {
        this.value = value;
    }

    /**
     * 创建条款ID
     * @return 条款ID实例
     */
    public static ClauseId create() {
        return new ClauseId(UUID.randomUUID().toString());
    }

    /**
     * 根据字符串创建条款ID
     * @param value 字符串值
     * @return 条款ID实例
     */
    public static ClauseId fromString(String value) {
        return new ClauseId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
