package com.titanium.clause.common.enums;

import lombok.Getter;

/**
 * 条款告知类型枚举
 * <p>
 * 表示条款告知书的类型（投保须知/免责说明/健康告知），条款域内部使用，不跨微服务复用。
 */
@Getter
public enum NoticeType {
    INSURE_NOTICE(1, "INSURE_NOTICE", "投保须知"),
    EXCLUSION_NOTICE(2, "EXCLUSION_NOTICE", "免责说明"),
    HEALTH_NOTICE(3, "HEALTH_NOTICE", "健康告知");

    private final Integer enumCode;
    private final String  code;
    private final String  name;

    NoticeType(Integer enumCode, String code, String name) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null
     */
    public static NoticeType fromCode(String code) {
        for (NoticeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
