package com.titanium.clause.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 理赔事件实体类
 */
@Data
public class ClaimEvent {
    // 理赔事件ID
    private String        id;
    // 触发条件
    private String        triggerCondition;
    // 理赔金额
    private String        claimAmount;
    // 理赔类型
    private String        claimType;
    // 其他理赔相关信息
    private String        otherInfo;
    // 理赔时间
    private LocalDateTime claimTime;

}
