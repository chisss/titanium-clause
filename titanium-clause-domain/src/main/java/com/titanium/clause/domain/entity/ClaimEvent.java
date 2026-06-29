package com.titanium.clause.domain.entity;

import java.time.LocalDateTime;

import com.titanium.metadata.enums.claim.ClaimEnum;

import lombok.Data;

/**
 * 理赔事件实体类
 */
@Data
public class ClaimEvent {
    // 理赔事件ID
    private String              id;
    // 触发条件
    private String              triggerCondition;
    // 理赔金额
    private String              claimAmount;
    // 理赔类型
    private ClaimEnum.ClaimType claimType;
    // 其他理赔相关信息
    private String              otherInfo;
    // 理赔时间
    private LocalDateTime       claimTime;

}
