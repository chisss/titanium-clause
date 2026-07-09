package com.titanium.clause.common.enums;

import com.titanium.metadata.enums.BaseEnum;

import lombok.Getter;

/**
 * 责任类型枚举
 * <p>
 * 表示保险责任（Coverage）的分类，条款域内部使用，不跨微服务复用。
 * 取值来源于领域实体注释“责任类型（重疾/医疗/意外/身故）”。
 */
@Getter
public enum CoverageType implements BaseEnum {
    CRITICAL_ILLNESS(1, "CRITICAL_ILLNESS", "重疾", "重大疾病保障责任"),
    MEDICAL(2, "MEDICAL", "医疗", "医疗费用保障责任"),
    ACCIDENT(3, "ACCIDENT", "意外", "意外伤害保障责任"),
    DEATH(4, "DEATH", "身故", "身故保障责任");

    private final Integer enumCode;
    private final String  code;
    private final String  name;
    private final String  desc;

    CoverageType(Integer enumCode, String code, String name, String desc) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null（统一范式入口，委托 {@link BaseEnum}）
     */
    public static CoverageType fromCode(String code) {
        return BaseEnum.fromCode(CoverageType.class, code);
    }
}
