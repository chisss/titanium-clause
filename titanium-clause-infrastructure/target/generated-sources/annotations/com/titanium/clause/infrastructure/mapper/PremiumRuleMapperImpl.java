package com.titanium.clause.infrastructure.mapper;

import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.infrastructure.entity.PremiumRuleEntity;
import com.titanium.metadata.enums.billing.BillingEnum;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-03T10:42:17+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class PremiumRuleMapperImpl implements PremiumRuleMapper {

    @Override
    public PremiumRuleEntity toPremiumRuleEntity(PremiumRule premiumRule) {
        if ( premiumRule == null ) {
            return null;
        }

        PremiumRuleEntity premiumRuleEntity = new PremiumRuleEntity();

        premiumRuleEntity.setCalculationMethod( premiumRule.getCalculationMethod() );
        premiumRuleEntity.setBasePremium( premiumRule.getBasePremium() );
        premiumRuleEntity.setPremiumRate( premiumRule.getPremiumRate() );
        if ( premiumRule.getPaymentMethod() != null ) {
            premiumRuleEntity.setPaymentMethod( Enum.valueOf( BillingEnum.PaymentMethod.class, premiumRule.getPaymentMethod() ) );
        }
        premiumRuleEntity.setPaymentTerm( premiumRule.getPaymentTerm() );
        premiumRuleEntity.setGracePeriodDays( premiumRule.getGracePeriodDays() );
        premiumRuleEntity.setCreateTime( premiumRule.getCreatedAt() );
        premiumRuleEntity.setUpdateTime( premiumRule.getUpdatedAt() );

        return premiumRuleEntity;
    }

    @Override
    public PremiumRule toPremiumRule(PremiumRuleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PremiumRule premiumRule = new PremiumRule();

        premiumRule.setCalculationMethod( entity.getCalculationMethod() );
        premiumRule.setBasePremium( entity.getBasePremium() );
        premiumRule.setPremiumRate( entity.getPremiumRate() );
        if ( entity.getPaymentMethod() != null ) {
            premiumRule.setPaymentMethod( entity.getPaymentMethod().name() );
        }
        premiumRule.setPaymentTerm( entity.getPaymentTerm() );
        premiumRule.setGracePeriodDays( entity.getGracePeriodDays() );
        premiumRule.setCreatedAt( entity.getCreateTime() );
        premiumRule.setUpdatedAt( entity.getUpdateTime() );

        return premiumRule;
    }
}
