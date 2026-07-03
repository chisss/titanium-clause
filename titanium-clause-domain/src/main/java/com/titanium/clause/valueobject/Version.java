package com.titanium.clause.valueobject;

import lombok.Value;

/**
 * 版本值对象
 */
@Value(staticConstructor = "of")
public class Version {
    private String value;

    public static Version fromString(String value) {
        return of(value);
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
