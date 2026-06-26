package com.titanium.clause.domain.enums;

import lombok.Getter;

/**
 * 条款快照业务类型枚举
 * <p>
 * 表示触发条款快照冻结的业务单据类型（保单/批单），条款域内部使用。
 * 语义为"业务单据类型"，与 metadata 的业务域类型 BusinessDomainType 不同，故独立定义。
 */
@Getter
public enum SnapshotBusinessType {
    POLICY(1, "POLICY", "保单"),
    ENDORSEMENT(2, "ENDORSEMENT", "批单");

    private final Integer enumCode;
    private final String  code;
    private final String  name;

    SnapshotBusinessType(Integer enumCode, String code, String name) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null
     */
    public static SnapshotBusinessType fromCode(String code) {
        for (SnapshotBusinessType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
