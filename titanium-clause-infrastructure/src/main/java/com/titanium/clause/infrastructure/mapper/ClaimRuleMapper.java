package com.titanium.clause.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.infrastructure.entity.ClaimRuleEntity;

/**
 * 理赔规则映射器
 */
@Mapper(componentModel = "spring")
public interface ClaimRuleMapper {

    /**
     * 将领域对象转换为数据库实体
     *
     * @param claimRule 领域对象
     * @return 数据库实体
     */
    @Mapping(source = "reportDeadlineDays", target = "reportDeadlineDays")
    @Mapping(source = "requiredMaterials", target = "requiredMaterials")
    @Mapping(source = "settlementPeriodDays", target = "settlementPeriodDays")
    @Mapping(source = "payoutRatio", target = "payoutRatio")
    @Mapping(source = "deductibleAmount", target = "deductibleAmount")
    @Mapping(source = "createdAt", target = "createTime")
    @Mapping(source = "updatedAt", target = "updateTime")
    ClaimRuleEntity toClaimRuleEntity(ClaimRule claimRule);

    /**
     * 将数据库实体转换为领域对象
     *
     * @param entity 数据库实体
     * @return 领域对象
     */
    @Mapping(source = "reportDeadlineDays", target = "reportDeadlineDays")
    @Mapping(source = "requiredMaterials", target = "requiredMaterials")
    @Mapping(source = "settlementPeriodDays", target = "settlementPeriodDays")
    @Mapping(source = "payoutRatio", target = "payoutRatio")
    @Mapping(source = "deductibleAmount", target = "deductibleAmount")
    @Mapping(source = "createTime", target = "createdAt")
    @Mapping(source = "updateTime", target = "updatedAt")
    ClaimRule toClaimRule(ClaimRuleEntity entity);
}
