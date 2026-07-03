package com.titanium.clause.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.infrastructure.entity.ContractChangeRuleEntity;

/**
 * 合同变更规则映射器
 */
@Mapper(componentModel = "spring")
public interface ContractChangeRuleMapper {

    /**
     * 将领域对象转换为数据库实体
     * @param contractChangeRule 领域对象
     * @return 数据库实体
     */
    @Mapping(source = "surrenderCashValueRule", target = "surrenderCashValueRule")
    @Mapping(source = "renewalType", target = "renewalType")
    @Mapping(source = "reinstatementCondition", target = "reinstatementCondition")
    @Mapping(source = "waitingPeriodDays", target = "waitingPeriodDays")
    @Mapping(source = "freeLookPeriodDays", target = "freeLookPeriodDays")
    @Mapping(source = "createdAt", target = "createTime")
    @Mapping(source = "updatedAt", target = "updateTime")
    ContractChangeRuleEntity toContractChangeRuleEntity(ContractChangeRule contractChangeRule);

    /**
     * 将数据库实体转换为领域对象
     * @param entity 数据库实体
     * @return 领域对象
     */
    @Mapping(source = "surrenderCashValueRule", target = "surrenderCashValueRule")
    @Mapping(source = "renewalType", target = "renewalType")
    @Mapping(source = "reinstatementCondition", target = "reinstatementCondition")
    @Mapping(source = "waitingPeriodDays", target = "waitingPeriodDays")
    @Mapping(source = "freeLookPeriodDays", target = "freeLookPeriodDays")
    @Mapping(source = "createTime", target = "createdAt")
    @Mapping(source = "updateTime", target = "updatedAt")
    ContractChangeRule toContractChangeRule(ContractChangeRuleEntity entity);
}
