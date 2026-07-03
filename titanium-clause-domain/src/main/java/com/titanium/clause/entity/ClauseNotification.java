package com.titanium.clause.entity;

import com.titanium.clause.common.enums.NoticeType;
import com.titanium.metadata.enums.CommonStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 条款告知实体
 */
@Data
@NoArgsConstructor
public class ClauseNotification {
    /** 告知ID */
    private String     notificationId;
    /** 告知书标题 */
    private String     title;
    /** 告知内容（投保须知/免责说明/健康告知等） */
    private String     content;
    /** 告知类型: INSURE_NOTICE/EXCLUSION_NOTICE/HEALTH_NOTICE */
    private NoticeType noticeType;
    /** 状态 */
    private CommonStatus status;
    /** 是否强制展示 */
    private Boolean    isMandatory;
}
