package com.titanium.clause.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.valueobject.CoverageId;
import com.titanium.clause.infrastructure.entity.InsuranceLiabilityEntity;

/**
 * 保险责任映射器
 */
@Mapper(componentModel = "spring")
public interface CoverageMapper {

    /**
     * 将领域对象转换为数据库实体
     *
     * @param coverage 领域对象
     * @return 数据库实体
     */
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "coverageAmount", target = "coverage")
    @Mapping(source = "premiumRate", target = "premiumRate")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createTime")
    @Mapping(source = "updatedAt", target = "updateTime")
    InsuranceLiabilityEntity toInsuranceLiabilityEntity(Coverage coverage);

    /**
     * 将数据库实体转换为领域对象
     *
     * @param entity 数据库实体
     * @return 领域对象
     */
    @Mapping(source = "id", target = "id", qualifiedByName = "toCoverageId")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "coverage", target = "coverageAmount")
    @Mapping(source = "premiumRate", target = "premiumRate")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createTime", target = "createdAt")
    @Mapping(source = "updateTime", target = "updatedAt")
    Coverage toCoverage(InsuranceLiabilityEntity entity);

    /**
     * 将字符串转换为CoverageId
     *
     * @param id 保险责任ID字符串
     * @return CoverageId对象
     */
    @Named("toCoverageId")
    default CoverageId toCoverageId(String id) {
        return id != null ? CoverageId.fromString(id) : null;
    }
}
