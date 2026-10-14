package com.titanium.clause.common.enums;

import com.titanium.metadata.enums.BaseEnum;
import com.titanium.metadata.enums.Reserved;

import lombok.Getter;

/**
 * 条款快照业务类型枚举
 * <p>
 * 表示触发条款快照冻结的业务单据类型（保单/批单），条款域内部使用。
 * 语义为"业务单据类型"，与 metadata 的业务域类型 BusinessDomainType 不同，故独立定义。
 */
@Getter
public enum SnapshotBusinessType implements BaseEnum {
    @Reserved("2026-10-14 g16-05 零引用登记（未见消费）：待业务核对——接入触发链路或删除；核毕摘标注+删清单行。见 docs/技术文档/枚举零引用清单.md")
    POLICY(1, "POLICY", "保单"),
    @Reserved("2026-10-14 g16-05 零引用登记（未见消费）：待业务核对——接入触发链路或删除；核毕摘标注+删清单行。见 docs/技术文档/枚举零引用清单.md")
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
     * 根据 code 反查枚举，未匹配返回 null（统一范式入口，委托 {@link BaseEnum}）
     */
    public static SnapshotBusinessType fromCode(String code) {
        return BaseEnum.fromCode(SnapshotBusinessType.class, code);
    }
}
