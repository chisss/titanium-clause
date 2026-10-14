package com.titanium.clause.common.enums;

import com.titanium.metadata.enums.BaseEnum;
import com.titanium.metadata.enums.Reserved;

import lombok.Getter;

/**
 * 条款告知类型枚举
 * <p>
 * 表示条款告知书的类型（投保须知/免责说明/健康告知），条款域内部使用，不跨微服务复用。
 */
@Getter
public enum NoticeType implements BaseEnum {
    @Reserved("2026-10-14 g16-05 零引用登记（未见消费）：待业务核对——接入触发链路或删除；核毕摘标注+删清单行。见 docs/技术文档/枚举零引用清单.md")
    INSURE_NOTICE(1, "INSURE_NOTICE", "投保须知"),
    @Reserved("2026-10-14 g16-05 零引用登记（未见消费）：待业务核对——接入触发链路或删除；核毕摘标注+删清单行。见 docs/技术文档/枚举零引用清单.md")
    EXCLUSION_NOTICE(2, "EXCLUSION_NOTICE", "免责说明"),
    @Reserved("2026-10-14 g16-05 零引用登记（未见消费）：待业务核对——接入触发链路或删除；核毕摘标注+删清单行。见 docs/技术文档/枚举零引用清单.md")
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
     * 根据 code 反查枚举，未匹配返回 null（统一范式入口，委托 {@link BaseEnum}）
     */
    public static NoticeType fromCode(String code) {
        return BaseEnum.fromCode(NoticeType.class, code);
    }
}
