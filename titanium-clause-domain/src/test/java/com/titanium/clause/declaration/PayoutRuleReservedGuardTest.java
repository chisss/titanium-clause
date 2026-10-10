package com.titanium.clause.declaration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.titanium.clause.valueobject.PayoutRule;
import com.titanium.metadata.enums.Reserved;

/**
 * 「声明即登记」条款域守卫（G12/g12-07）：{@code PayoutRule.PeriodicPayoutTerms#guaranteedYears} 定性固化。
 * <p>
 * 守的是<b>结构面</b>——标注存在、边界未被越界：{@code guaranteedYears}（保证领取年期）
 * 全仓仅两处构造点且均显式传 {@code null}（clause-web {@code CoverageAssembler} PERIODIC 分支、
 * clause-application {@code LifeClauseSeedInitializer} 年金种子）、除声明外零读取、未进入任何给付计算，
 * 须标 {@link Reserved}；同 record 其余 5 个组件（frequency/startAge/startAfterYears/payoutYears/
 * amountPerPeriod）多标即误标。
 * </p>
 * <p>
 * 🔴 <b>「触达」那一面不在本测试</b>（零读取静态扫描）——归验收脚本
 * {@code ~/.g12-snapshots/validate-g12-07.sh}，两侧都要能判红。
 * </p>
 *
 * @author wei.sun
 * @since 2026/10/10
 */
class PayoutRuleReservedGuardTest {

    @Test
    @DisplayName("PeriodicPayoutTerms 恰 guaranteedYears 标 @Reserved（G12/g12-07；其余组件多标即误标）")
    void shouldReserveExactlyGuaranteedYears() {
        Class<?> type = PayoutRule.PeriodicPayoutTerms.class;

        List<String> reserved = new ArrayList<>();
        for (RecordComponent component : type.getRecordComponents()) {
            if (reservedOfField(type, component.getName()) != null) {
                reserved.add(component.getName());
            }
        }
        assertEquals(List.of("guaranteedYears"), reserved,
                "guaranteedYears 恒 null 构值且零读取，应标 @Reserved（g12-07 定性），多标即误标");

        Reserved annotation = reservedOfField(type, "guaranteedYears");
        assertNotNull(annotation);
        assertFalse(annotation.value().isBlank(),
                "guaranteedYears 的 @Reserved 须写明核对时点、口径与触发条件");
        assertTrue(annotation.value().contains("g12-07"),
                "value 须写明核对来源（g12-07），当前: " + annotation.value());
    }

    /** 读 record 组件上的 {@link Reserved}（组件注解随编译器落在同名实例字段，JLS 8.10.3） */
    private Reserved reservedOfField(Class<?> type, String fieldName) {
        try {
            Annotation annotation = type.getDeclaredField(fieldName).getAnnotation(Reserved.class);
            return annotation instanceof Reserved reserved ? reserved : null;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("record 组件在字节码里不是字段，判据口径失效: "
                    + type.getSimpleName() + "." + fieldName, e);
        }
    }
}
