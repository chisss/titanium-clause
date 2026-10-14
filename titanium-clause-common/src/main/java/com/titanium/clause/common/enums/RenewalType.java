package com.titanium.clause.common.enums;

import com.titanium.metadata.enums.BaseEnum;
import com.titanium.metadata.enums.Reserved;

import lombok.Getter;

/**
 * 续保类型枚举
 * <p>
 * 表示合同变更规则中的续保方式，条款域内部使用，不跨微服务复用。
 */
@Getter
public enum RenewalType implements BaseEnum {
    @Reserved("2026-10-14 g16-05 零引用登记（未见消费）：待业务核对——接入触发链路或删除；核毕摘标注+删清单行。见 docs/技术文档/枚举零引用清单.md")
    GUARANTEED(1, "GUARANTEED", "保证续保"),
    @Reserved("2026-10-14 g16-05 零引用登记（未见消费）：待业务核对——接入触发链路或删除；核毕摘标注+删清单行。见 docs/技术文档/枚举零引用清单.md")
    NON_GUARANTEED(2, "NON_GUARANTEED", "不保证续保");

    private final Integer enumCode;
    private final String  code;
    private final String  name;

    RenewalType(Integer enumCode, String code, String name) {
        this.enumCode = enumCode;
        this.code = code;
        this.name = name;
    }

    /**
     * 根据 code 反查枚举，未匹配返回 null（统一范式入口，委托 {@link BaseEnum}）
     */
    public static RenewalType fromCode(String code) {
        return BaseEnum.fromCode(RenewalType.class, code);
    }
}
