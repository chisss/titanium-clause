package com.titanium.clause.query.view;

import java.time.LocalDateTime;

import com.titanium.clause.query.converter.InsuranceProductTypeConverter;
import com.titanium.common.jpa.BaseView;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 条款读模型实体（CQRS Projection）
 * <p>
 * 对应读模型表 {@code t_clause_view}，与写侧事件存储物理隔离。 由
 * {@link com.titanium.clause.query.handler.projection.ClauseProjectionEventHandler} 订阅领域事件投影而来。
 * </p>
 * <p>
 * 继承 {@link BaseView}，复用租户ID、创建/更新时间（投影时间）、乐观锁版本等读模型公共字段。
 * </p>
 */
@Entity
@Table(name = "t_clause_view")
@Getter
@Setter
public class ClauseView extends BaseView {

    /** 条款ID（聚合根ID，读模型主键） */
    @Id
    @Column(name = "clause_id", nullable = false, length = 36)
    private String                  clauseId;

    /** 条款代码 */
    @Column(name = "clause_code", length = 64)
    private String                  clauseCode;

    /** 条款名称 */
    @Column(name = "clause_name", length = 256)
    private String                  clauseName;

    /** 条款类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "clause_type", length = 50)
    private ClauseEnum.ClauseType   clauseType;

    /** 条款内容 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String                  content;

    /** 条款描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String                  description;

    /** 条款状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private ClauseEnum.ClauseStatus status;

    /** 版本号 */
    @Column(name = "clause_version", length = 20)
    private String                  clauseVersion;

    /** 险种类型 */
    @Convert(converter = InsuranceProductTypeConverter.class)
    @Column(name = "insurance_type", length = 50)
    private InsuranceProductType           insuranceType;

    /** 父条款ID */
    @Column(name = "parent_clause_id", length = 36)
    private String                  parentClauseId;

    /** 生效日期 */
    @Column(name = "effective_date")
    private LocalDateTime           effectiveDate;

    /** 失效日期 */
    @Column(name = "expiry_date")
    private LocalDateTime           expiryDate;

    /** 创建人 */
    @Column(name = "created_by", length = 32)
    private String                  createdBy;

    /** 更新人 */
    @Column(name = "updated_by", length = 32)
    private String                  updatedBy;
}
