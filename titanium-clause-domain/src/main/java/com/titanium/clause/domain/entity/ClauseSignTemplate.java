package com.titanium.clause.domain.entity;

import com.titanium.clause.domain.enums.SignTemplateType;
import com.titanium.metadata.enums.CommonStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 签署模板实体
 */
@Data
@NoArgsConstructor
public class ClauseSignTemplate {
    /** 模板ID */
    private String           templateId;
    /** 模板名称 */
    private String           templateName;
    /** 模板类型: E_SIGN(电子签名)/PAPER_SIGN(纸质签署) */
    private SignTemplateType templateType;
    /** 模板内容 */
    private String           templateContent;
    /** 签署位置（JSON） */
    private String           signPositions;
    /** 状态 */
    private CommonStatus     status;
}
