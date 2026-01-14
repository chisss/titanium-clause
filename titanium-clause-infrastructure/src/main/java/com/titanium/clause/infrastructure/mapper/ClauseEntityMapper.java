package com.titanium.clause.infrastructure.mapper;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.entity.ClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
    @Mapping(source = "clauseType", target = "insuranceType")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createTime")
    @Mapping(source = "updatedAt", target = "updateTime")
    ClauseEntity toClauseEntity(Clause clause);

    /**
     * 将数据库实体转换为领域对象
     * @param entity 数据库实体
     * @return 领域对象
     */
    @Mapping(source = "id", target = "clauseId", qualifiedByName = "toClauseId")
    @Mapping(source = "clauseCode", target = "clauseCode", qualifiedByName = "toClauseCode")
    @Mapping(source = "clauseName", target = "clauseName", qualifiedByName = "toClauseName")
    @Mapping(source = "insuranceType", target = "clauseType")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createTime", target = "createdAt")
    @Mapping(source = "updateTime", target = "updatedAt")
    Clause toClause(ClauseEntity entity);

    /**
     * 将字符串转换为ClauseId
     * @param id 条款ID字符串
     * @return ClauseId对象
     */
    @Named("toClauseId")
    default com.titanium.clause.valueobject.ClauseId toClauseId(String id) {
        return id != null ? com.titanium.clause.valueobject.ClauseId.fromString(id) : null;
    }

    /**
     * 将字符串转换为ClauseCode
     * @param code 条款代码字符串
     * @return ClauseCode对象
     */
    @Named("toClauseCode")
    default com.titanium.clause.valueobject.ClauseCode toClauseCode(String code) {
        return code != null ? com.titanium.clause.valueobject.ClauseCode.fromString(code) : null;
    }

    /**
     * 将字符串转换为ClauseName
     * @param name 条款名称字符串
     * @return ClauseName对象
     */
    @Named("toClauseName")
    default com.titanium.clause.valueobject.ClauseName toClauseName(String name) {
        return name != null ? com.titanium.clause.valueobject.ClauseName.fromString(name) : null;
    }
}