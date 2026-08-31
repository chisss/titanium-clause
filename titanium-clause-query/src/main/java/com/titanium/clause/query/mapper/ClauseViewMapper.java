package com.titanium.clause.query.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseRevisedEvent;
import com.titanium.clause.event.ClauseUpdatedEvent;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.Version;

/**
 * 条款读模型投影映射器（MapStruct，事件 → 读模型字段拷贝）
 * <p>
 * 承担"新建型"条款创建投影的事件 record → View 结构字段映射，取代投影处理器中逐字段 set。采用
 * {@link MappingTarget} 就地更新既有/新建 View 实例，保留投影的 upsert 语义；
 * {@link NullValuePropertyMappingStrategy#IGNORE} 确保事件缺省字段不覆盖 View 既有值。
 * </p>
 * <p>
 * <b>职责边界</b>：仅做纯字段/值对象结构翻译（{@link ClauseId}/{@link ClauseCode}/{@link ClauseName}/
 * {@link Version} 拆解为字符串）。以下三类含默认值或运行时副作用的赋值仍留在投影处理器，不下沉映射器：
 * <ul>
 *   <li>{@code status}：空值回落 {@code DRAFT} 默认值，IGNORE 策略下空源会被跳过而非回落，故由处理器判定；</li>
 *   <li>{@code createTime}（仅首次）/{@code updateTime}：审计时间戳取自事件时间，含"仅首次"语义，由处理器控制；</li>
 *   <li>{@code version}：读模型乐观锁版本（{@code BaseView} 的 {@code Long}），与事件的 {@link Version} 值对象
 *       同名但语义无关，显式 {@code ignore} 阻断错误的名称匹配，交由 JPA 维护。</li>
 * </ul>
 * 故此处对应目标字段均 {@code ignore}。
 * </p>
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClauseViewMapper {

    /**
     * 条款创建事件 → 条款读模型（就地 upsert；值对象拆解为字符串）。
     * <p>
     * status（DRAFT 默认）、createTime/updateTime（审计时间戳）、version（乐观锁）均 ignore，由处理器承接。
     * </p>
     */
    @Mapping(target = "clauseId", source = "clauseId", qualifiedByName = "clauseIdValue")
    @Mapping(target = "clauseCode", source = "clauseCode", qualifiedByName = "clauseCodeValue")
    @Mapping(target = "clauseName", source = "clauseName", qualifiedByName = "clauseNameValue")
    @Mapping(target = "clauseVersion", source = "version", qualifiedByName = "versionValue")
    @Mapping(target = "parentClauseId", source = "parentClauseId", qualifiedByName = "clauseIdValue")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void applyCreated(@MappingTarget ClauseView view, ClauseCreatedEvent event);

    /**
     * 条款修订事件 → 条款读模型（就地新建；生成新条款ID的独立读模型记录）。
     * <p>
     * 修订产生全新版本聚合，此处为 {@code newClauseId} 建立独立读模型记录，{@code parentClauseId} 溯源原条款；
     * 修订态 status 固定 DRAFT、createTime/updateTime（审计时间戳）、version（乐观锁）均 ignore，由处理器承接。
     * </p>
     */
    @Mapping(target = "clauseId", source = "newClauseId", qualifiedByName = "clauseIdValue")
    @Mapping(target = "clauseCode", source = "clauseCode", qualifiedByName = "clauseCodeValue")
    @Mapping(target = "clauseName", source = "clauseName", qualifiedByName = "clauseNameValue")
    @Mapping(target = "clauseVersion", source = "newVersion", qualifiedByName = "versionValue")
    @Mapping(target = "parentClauseId", source = "originalClauseId", qualifiedByName = "clauseIdValue")
    @Mapping(target = "createdBy", source = "revisedBy")
    @Mapping(target = "updatedBy", source = "revisedBy")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void applyRevised(@MappingTarget ClauseView view, ClauseRevisedEvent event);

    /**
     * 条款更新事件 → 条款读模型（就地更新）。
     * <p>
     * 与投影语义一致仅更新事件携带的字段（clauseName 值对象拆解为字符串，clauseCode 维持原值不回写）；
     * status、createTime/updateTime（审计时间戳）、version（乐观锁）均 ignore，由处理器承接。
     * </p>
     */
    @Mapping(target = "clauseName", source = "clauseName", qualifiedByName = "clauseNameValue")
    @Mapping(target = "clauseId", ignore = true)
    @Mapping(target = "clauseCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void applyUpdated(@MappingTarget ClauseView view, ClauseUpdatedEvent event);

    /** 条款ID值对象 → 字符串（空安全；复用于 clauseId 与 parentClauseId） */
    @Named("clauseIdValue")
    default String clauseIdValue(ClauseId clauseId) {
        return clauseId != null ? clauseId.value() : null;
    }

    /** 条款代码值对象 → 字符串（空安全） */
    @Named("clauseCodeValue")
    default String clauseCodeValue(ClauseCode clauseCode) {
        return clauseCode != null ? clauseCode.value() : null;
    }

    /** 条款名称值对象 → 字符串（空安全） */
    @Named("clauseNameValue")
    default String clauseNameValue(ClauseName clauseName) {
        return clauseName != null ? clauseName.value() : null;
    }

    /** 版本值对象 → 字符串（空安全） */
    @Named("versionValue")
    default String versionValue(Version version) {
        return version != null ? version.value() : null;
    }
}
