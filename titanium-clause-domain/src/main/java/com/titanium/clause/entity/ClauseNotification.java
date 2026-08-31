package com.titanium.clause.entity;

import com.titanium.clause.common.enums.NoticeType;
import com.titanium.metadata.enums.CommonStatus;

/**
 * 条款告知实体（聚合内实体，不可变值对象）
 *
 * @param notificationId 告知ID
 * @param title          告知书标题
 * @param content        告知内容（投保须知/免责说明/健康告知等）
 * @param noticeType     告知类型: INSURE_NOTICE/EXCLUSION_NOTICE/HEALTH_NOTICE
 * @param status         状态
 * @param isMandatory    是否强制展示
 */
public record ClauseNotification(
        String       notificationId,
        String       title,
        String       content,
        NoticeType   noticeType,
        CommonStatus status,
        Boolean      isMandatory) {
}
