package com.titanium.clause.query.view;

import java.math.BigDecimal;

import com.titanium.clause.common.enums.CoverageType;
import com.titanium.common.jpa.BaseView;
import com.titanium.metadata.enums.clause.CoverageTriggerType;
import com.titanium.metadata.enums.clause.PayoutType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 保险责任读模型实体（CQRS Projection）
 * <p>
 * 对应读模型表 {@code t_coverage_view}，与写侧事件存储物理隔离。由
 * {@link com.titanium.clause.query.handler.projection.ClauseProjectionEventHandler} 订阅
 * {@code CoverageAddedEvent}/{@code CoverageRemovedEvent} 投影而来。一个条款可包含多个责任，
 * 故以 {@code coverage_id} 为主键、{@code clause_id} 建索引支持按条款批量查询。
 * </p>
 * <p>
 * <b>设计说明</b>：可检索/展示的标量字段（编码/名称/类型/保额/触发类型/赔付类型）独立建列；
 * 结构化的赔付触发条件 {@code CoverageTrigger} 与赔付规则 {@code PayoutRule}（含疾病清单、
 * 免赔额、周期给付等复杂嵌套）整体以 JSON 列存储，查询时反序列化还原值对象，与 {@code ProductView}
 * 的 JSON 列模式保持一致。
 * </p>
 * <p>
 * 继承 {@link BaseView}，复用租户ID、创建/更新时间（投影时间）、乐观锁版本等读模型公共字段。
 * </p>
 */
@Entity
@Table(name = "t_coverage_view")
@Getter
@Setter
public class CoverageView extends BaseView {

    /** 责任ID（聚合内唯一，读模型主键） */
    @Id
    @Column(name = "coverage_id", nullable = false, length = 36)
    private String              coverageId;

    /** 所属条款ID（一条款多责任，建索引） */
    @Column(name = "clause_id", nullable = false, length = 36)
    private String              clauseId;

    /** 责任编码 */
    @Column(name = "coverage_code", length = 64)
    private String              coverageCode;

    /** 责任名称 */
    @Column(name = "coverage_name", length = 256)
    private String              coverageName;

    /** 责任类型（重疾/医疗/意外/身故） */
    @Enumerated(EnumType.STRING)
    @Column(name = "coverage_type", length = 50)
    private CoverageType        coverageType;

    /** 责任保额 */
    @Column(name = "coverage_amount", precision = 18, scale = 2)
    private BigDecimal          coverageAmount;

    /** 责任描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String              description;

    /** 赔付触发类型（身故/确诊重疾/意外伤残/车辆定损/医疗费用） */
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", length = 50)
    private CoverageTriggerType triggerType;

    /** 赔付类型（定额/比例/按损/报销/周期给付） */
    @Enumerated(EnumType.STRING)
    @Column(name = "payout_type", length = 50)
    private PayoutType          payoutType;

    /** 是否附加责任 */
    @Column(name = "is_additional")
    private Boolean             isAdditional;

    /** 关联的主险责任ID（附加责任时指向主险） */
    @Column(name = "main_coverage_id", length = 36)
    private String              mainCoverageId;

    /** 结构化赔付触发条件（CoverageTrigger 整体序列化 JSON） */
    @Lob
    @Column(name = "trigger_json", columnDefinition = "TEXT")
    private String              triggerJson;

    /** 结构化赔付规则（PayoutRule 整体序列化 JSON） */
    @Lob
    @Column(name = "payout_rule_json", columnDefinition = "TEXT")
    private String              payoutRuleJson;
}
