package com.titanium.clause.valueobject;

/**
 * 版本值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record）。
 * 保留 {@code of}/{@code fromString} 静态工厂以兼容既有调用点。</p>
 */
public record Version(String value) {

    public static Version of(String value) {
        return new Version(value);
    }

    public static Version fromString(String value) {
        return new Version(value);
    }

    /**
     * 获取下一个版本号
     * @return 下一个版本号
     */
    public Version nextVersion() {
        // 简单的版本号递增逻辑，实际项目中可能需要更复杂的规则
        String[] parts = value.split("\\.");
        if (parts.length == 2) {
            int minorVersion = Integer.parseInt(parts[1]);
            return of(parts[0] + "." + (minorVersion + 1));
        }
        return of(value + ".1");
    }
}
