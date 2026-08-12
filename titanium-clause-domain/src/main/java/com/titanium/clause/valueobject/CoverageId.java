package com.titanium.clause.valueobject;

/**
 * 保险责任ID值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record）。
 * 保留 {@code of}/{@code fromString} 静态工厂以兼容既有调用点。</p>
 */
public record CoverageId(String value) {

    public static CoverageId of(String value) {
        return new CoverageId(value);
    }

    public static CoverageId fromString(String value) {
        return new CoverageId(value);
    }
}
