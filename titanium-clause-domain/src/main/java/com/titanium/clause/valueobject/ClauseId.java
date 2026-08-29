package com.titanium.clause.valueobject;

import java.io.Serializable;
import com.titanium.common.util.SnowflakeIdGenerator;

/**
 * 条款ID值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record），
 * 事件溯源 payload {@code {"value":"..."}} 可正确回读，规避 class 型单参构造无 {@code @JsonCreator}
 * 导致的反序列化失败。</p>
 */
public record ClauseId(String value) implements Serializable {

    /**
     * 创建条款ID
     * @return 条款ID实例
     */
    public static ClauseId create() {
        return new ClauseId(SnowflakeIdGenerator.generate());
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
