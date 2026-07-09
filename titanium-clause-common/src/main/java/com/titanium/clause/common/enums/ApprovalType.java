package com.titanium.clause.common.enums;

import com.titanium.metadata.enums.BaseEnum;

import lombok.Getter;

/**
 * 条款审批类型枚举
 * <p>
 * 表示条款审批环节的审批职责类型，条款域内部使用，不跨微服务复用。
 */
@Getter
public enum ApprovalType implements BaseEnum {
    LEGAL(1, "LEGAL", "法务审批"),
    ACTUARIAL(2, "ACTUARIAL", "精算审批"),
    MANAGEMENT(3, "MANAGEMENT", "管理层审批");

    private final Integer enumCode;
    private final String  code;
    private final String  name;

    ApprovalType(Integer enumCode, String code, String name) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null（统一范式入口，委托 {@link BaseEnum}）
     */
    public static ApprovalType fromCode(String code) {
        return BaseEnum.fromCode(ApprovalType.class, code);
    }
}
