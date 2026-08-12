package com.titanium.clause.valueobject;

import java.io.Serializable;

/**
 * 条款代码值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record），
 * 事件溯源 payload {@code {"value":"..."}} 可正确回读。</p>
 */
public record ClauseCode(String value) implements Serializable {

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
