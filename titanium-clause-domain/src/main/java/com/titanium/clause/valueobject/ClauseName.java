package com.titanium.clause.valueobject;

import java.io.Serializable;

/**
 * 条款名称值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record），
 * 事件溯源 payload {@code {"value":"..."}} 可正确回读。</p>
 */
public record ClauseName(String value) implements Serializable {

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
