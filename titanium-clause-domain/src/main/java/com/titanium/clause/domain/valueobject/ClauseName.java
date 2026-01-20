package com.titanium.clause.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * 条款名称值对象
 */
@EqualsAndHashCode
@Getter
public class ClauseName implements Serializable {
    private final String value;

    /**
     * 构造函数
     * @param value 条款名称值
     */
    public ClauseName(String value) {
        this.value = value;
    }

    /**
     * 创建条款名称
     * @param value 条款名称值
     * @return 条款名称实例
     */
    public static ClauseName fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("条款名称不能为空");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("条款名称长度不能超过100个字符");
        }
        return new ClauseName(value.trim());
    }

    @Override
    public String toString() {
        return value;
    }
}
