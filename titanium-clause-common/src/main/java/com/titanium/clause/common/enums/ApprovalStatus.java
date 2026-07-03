package com.titanium.clause.common.enums;

import lombok.Getter;

/**
 * 条款审批状态枚举
 * <p>
 * 表示一条审批记录的处理结果状态，条款域内部使用，不跨微服务复用。
 */
@Getter
public enum ApprovalStatus {
    PENDING(1, "PENDING", "待审批"),
    APPROVED(2, "APPROVED", "审批通过"),
    REJECTED(3, "REJECTED", "审批驳回");

    private final Integer enumCode;
    private final String  code;
    private final String  name;

    ApprovalStatus(Integer enumCode, String code, String name) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null
     */
    public static ApprovalStatus fromCode(String code) {
        for (ApprovalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
