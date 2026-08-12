package com.titanium.clause.valueobject;

/**
 * 责任免除ID值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record）。
 * 保留 {@code of}/{@code fromString} 静态工厂以兼容既有调用点。</p>
 */
public record ExclusionId(String value) {

    public static ExclusionId of(String value) {
        return new ExclusionId(value);
    }

    public static ExclusionId fromString(String value) {
        return new ExclusionId(value);
    }
}
