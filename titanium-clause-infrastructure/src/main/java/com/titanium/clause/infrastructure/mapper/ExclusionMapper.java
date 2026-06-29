package com.titanium.clause.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.clause.infrastructure.entity.InsuranceExclusionEntity;

/**
 * 责任免除映射器
 */
@Mapper(componentModel = "spring")
public interface ExclusionMapper {

    /**
     * 将领域对象转换为数据库实体
     * @param exclusion 领域对象
     * @return 数据库实体
     */
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "exclusionRuleCode", target = "exclusionRuleCode")
    @Mapping(source = "isMandatory", target = "isMandatory")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createTime")
    @Mapping(source = "updatedAt", target = "updateTime")
    InsuranceExclusionEntity toInsuranceExclusionEntity(Exclusion exclusion);

    /**
     * 将数据库实体转换为领域对象
     * @param entity 数据库实体
     * @return 领域对象
     */
    @Mapping(source = "id", target = "id", qualifiedByName = "toExclusionId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "exclusionRuleCode", target = "exclusionRuleCode")
    @Mapping(source = "isMandatory", target = "isMandatory")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createTime", target = "createdAt")
    @Mapping(source = "updateTime", target = "updatedAt")
    Exclusion toExclusion(InsuranceExclusionEntity entity);

    /**
     * 将字符串转换为ExclusionId
     * @param id 责任免除ID字符串
     * @return ExclusionId对象
     */
    @Named("toExclusionId")
    default ExclusionId toExclusionId(String id) {
        return id != null ? ExclusionId.fromString(id) : null;
    }
}
