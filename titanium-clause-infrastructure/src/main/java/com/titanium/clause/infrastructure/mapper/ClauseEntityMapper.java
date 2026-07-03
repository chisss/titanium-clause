package com.titanium.clause.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.infrastructure.entity.ClauseEntity;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.Version;

/**
 * 条款实体映射器
 */
@Mapper(componentModel = "spring")
public interface ClauseEntityMapper {

    /**
     * 将领域对象转换为数据库实体
     * @param clause 领域对象
     * @return 数据库实体
     */
    @Mapping(source = "clauseId.value", target = "id")
    @Mapping(source = "clauseCode.value", target = "clauseCode")
    @Mapping(source = "clauseName.value", target = "clauseName")
    @Mapping(source = "clauseType", target = "clauseType")
    @Mapping(source = "insuranceType", target = "insuranceType")
    @Mapping(source = "version.value", target = "version")
    @Mapping(source = "parentClauseId.value", target = "parentClauseId")
    @Mapping(source = "status", target = "status")
    ClauseEntity toClauseEntity(Clause clause);

    /**
     * 将数据库实体转换为领域对象
     * @param entity 数据库实体
     * @return 领域对象
     */
    @Mapping(source = "id", target = "clauseId", qualifiedByName = "toClauseId")
    @Mapping(source = "clauseCode", target = "clauseCode", qualifiedByName = "toClauseCode")
    @Mapping(source = "clauseName", target = "clauseName", qualifiedByName = "toClauseName")
    @Mapping(source = "clauseType", target = "clauseType")
    @Mapping(source = "insuranceType", target = "insuranceType")
    @Mapping(source = "version", target = "version", qualifiedByName = "toVersion")
    @Mapping(source = "parentClauseId", target = "parentClauseId", qualifiedByName = "toClauseId")
    @Mapping(source = "status", target = "status")
    Clause toClause(ClauseEntity entity);

    /**
     * 将字符串转换为ClauseId
     * @param id 条款ID字符串
     * @return ClauseId对象
     */
    @Named("toClauseId")
    default ClauseId toClauseId(String id) {
        return id != null ? ClauseId.fromString(id) : null;
    }

    /**
     * 将字符串转换为ClauseCode
     * @param code 条款代码字符串
     * @return ClauseCode对象
     */
    @Named("toClauseCode")
    default ClauseCode toClauseCode(String code) {
        return code != null ? ClauseCode.fromString(code) : null;
    }

    /**
     * 将字符串转换为ClauseName
     * @param name 条款名称字符串
     * @return ClauseName对象
     */
    @Named("toClauseName")
    default ClauseName toClauseName(String name) {
        return name != null ? ClauseName.fromString(name) : null;
    }

    /**
     * 将字符串转换为Version
     * @param version 版本字符串
     * @return Version对象
     */
    @Named("toVersion")
    default Version toVersion(String version) {
        return version != null ? Version.fromString(version) : null;
    }
}
