package com.titanium.clause.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * 条款代码值对象
 */
@EqualsAndHashCode
@Getter
public class ClauseCode implements Serializable {
    private final String value;

    /**
     * 构造函数
     * @param value 条款代码值
     */
    public ClauseCode(String value) {
        this.value = value;
    }

    /**
     * 创建条款代码
     * @param value 条款代码值
     * @return 条款代码实例
     */
    public static ClauseCode fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("条款代码不能为空");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("条款代码长度不能超过50个字符");
        }
        return new ClauseCode(value.trim().toUpperCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
