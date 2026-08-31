package com.titanium.clause.entity;

import com.titanium.clause.common.enums.SignTemplateType;
import com.titanium.metadata.enums.CommonStatus;

/**
 * 签署模板实体（聚合内实体，不可变值对象）
 *
 * @param templateId      模板ID
 * @param templateName    模板名称
 * @param templateType    模板类型: E_SIGN(电子签名)/PAPER_SIGN(纸质签署)
 * @param templateContent 模板内容
 * @param signPositions   签署位置（JSON）
 * @param status          状态
 */
public record ClauseSignTemplate(
        String           templateId,
        String           templateName,
        SignTemplateType templateType,
        String           templateContent,
        String           signPositions,
        CommonStatus     status) {
}
