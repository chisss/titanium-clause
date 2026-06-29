package com.titanium.clause.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.infrastructure.entity.PremiumRuleEntity;

/**
 * 缴费规则映射器
 */
@Mapper(componentModel = "spring")
public interface PremiumRuleMapper {

    /**
     * 将领域对象转换为数据库实体
     * @param premiumRule 领域对象
     * @return 数据库实体
     */
    @Mapping(source = "calculationMethod", target = "calculationMethod")
    @Mapping(source = "basePremium", target = "basePremium")
    @Mapping(source = "premiumRate", target = "premiumRate")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "paymentTerm", target = "paymentTerm")
    @Mapping(source = "gracePeriodDays", target = "gracePeriodDays")
    @Mapping(source = "createdAt", target = "createTime")
    @Mapping(source = "updatedAt", target = "updateTime")
    PremiumRuleEntity toPremiumRuleEntity(PremiumRule premiumRule);

    /**
     * 将数据库实体转换为领域对象
     * @param entity 数据库实体
     * @return 领域对象
     */
    @Mapping(source = "calculationMethod", target = "calculationMethod")
    @Mapping(source = "basePremium", target = "basePremium")
    @Mapping(source = "premiumRate", target = "premiumRate")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "paymentTerm", target = "paymentTerm")
    @Mapping(source = "gracePeriodDays", target = "gracePeriodDays")
    @Mapping(source = "createTime", target = "createdAt")
    @Mapping(source = "updateTime", target = "updatedAt")
    PremiumRule toPremiumRule(PremiumRuleEntity entity);
}
