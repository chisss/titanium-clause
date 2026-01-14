package com.titanium.clause.mapper;

import com.titanium.clause.command.CreateClauseCommand;
import com.titanium.clause.command.UpdateClauseCommand;

import com.titanium.clause.dto.CreateClauseRequest;
import com.titanium.clause.dto.UpdateClauseRequest;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 条款映射器
 */
@Mapper(componentModel = "spring")
public interface ClauseMapper {

    /**
     * 将API请求转换为创建条款命令
     * @param request 创建条款请求
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 创建条款命令
     */
    @Mapping(target = "clauseId", source = "clauseId")
    @Mapping(target = "clauseCode", source = "request.clauseCode", qualifiedByName = "toClauseCode")
    @Mapping(target = "clauseName", source = "request.clauseName", qualifiedByName = "toClauseName")
    @Mapping(target = "tenantId", source = "tenantId")
    CreateClauseCommand toCreateClauseCommand(CreateClauseRequest request, ClauseId clauseId, String tenantId);

    /**
     * 将API请求转换为更新条款命令
     * @param request 更新条款请求
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 更新条款命令
     */
    @Mapping(target = "clauseId", source = "clauseId")
    @Mapping(target = "clauseCode", source = "request.clauseCode", qualifiedByName = "toClauseCode")
    @Mapping(target = "clauseName", source = "request.clauseName", qualifiedByName = "toClauseName")
    @Mapping(target = "tenantId", source = "tenantId")
    UpdateClauseCommand toUpdateClauseCommand(UpdateClauseRequest request, ClauseId clauseId, String tenantId);

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
}