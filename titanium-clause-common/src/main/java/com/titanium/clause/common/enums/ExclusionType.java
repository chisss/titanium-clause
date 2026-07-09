package com.titanium.clause.common.enums;

import com.titanium.metadata.enums.BaseEnum;

import lombok.Getter;

/**
 * 免责类型枚举
 * <p>
 * 表示责任免除（Exclusion）的分类，条款域内部使用，不跨微服务复用。
 * 取值来源于领域实体注释“免责类型（故意行为/既往症/违法行为）”。
 */
@Getter
public enum ExclusionType implements BaseEnum {
    INTENTIONAL(1, "INTENTIONAL", "故意行为", "被保险人故意行为导致的损失"),
    PRE_EXISTING(2, "PRE_EXISTING", "既往症", "投保前已存在的疾病或状况"),
    ILLEGAL(3, "ILLEGAL", "违法行为", "违反法律法规的行为导致的损失");

    private final Integer enumCode;
    private final String  code;
    private final String  name;
    private final String  desc;

    ExclusionType(Integer enumCode, String code, String name, String desc) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null（统一范式入口，委托 {@link BaseEnum}）
     */
    public static ExclusionType fromCode(String code) {
        return BaseEnum.fromCode(ExclusionType.class, code);
    }
}
