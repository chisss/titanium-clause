package com.titanium.clause.domain.enums;

import lombok.Getter;

/**
 * 签署模板类型枚举
 * <p>
 * 表示条款签署模板的签署方式，条款域内部使用，不跨微服务复用。
 */
@Getter
public enum SignTemplateType {
    E_SIGN(1, "E_SIGN", "电子签名"),
    PAPER_SIGN(2, "PAPER_SIGN", "纸质签署");

    private final Integer enumCode;
    private final String  code;
    private final String  name;

    SignTemplateType(Integer enumCode, String code, String name) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null
     */
    public static SignTemplateType fromCode(String code) {
        for (SignTemplateType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
