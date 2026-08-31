package com.titanium.clause.entity;

import java.time.LocalDateTime;

import com.titanium.metadata.enums.claim.ClaimEnum;

/**
 * 理赔事件实体（聚合内实体，不可变值对象）
 *
 * @param id               理赔事件ID
 * @param triggerCondition 触发条件
 * @param claimAmount      理赔金额
 * @param claimType        理赔类型
 * @param otherInfo        其他理赔相关信息
 * @param claimTime        理赔时间
 */
public record ClaimEvent(
        String              id,
        String              triggerCondition,
        String              claimAmount,
        ClaimEnum.ClaimType claimType,
        String              otherInfo,
        LocalDateTime       claimTime) {
}
