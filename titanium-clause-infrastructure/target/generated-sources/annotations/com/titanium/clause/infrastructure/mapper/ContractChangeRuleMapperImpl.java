package com.titanium.clause.infrastructure.mapper;

import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.infrastructure.entity.ContractChangeRuleEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-30T12:33:07+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ContractChangeRuleMapperImpl implements ContractChangeRuleMapper {

    @Override
    public ContractChangeRuleEntity toContractChangeRuleEntity(ContractChangeRule contractChangeRule) {
        if ( contractChangeRule == null ) {
            return null;
        }

        ContractChangeRuleEntity contractChangeRuleEntity = new ContractChangeRuleEntity();

        contractChangeRuleEntity.setSurrenderCashValueRule( contractChangeRule.getSurrenderCashValueRule() );
        contractChangeRuleEntity.setRenewalType( contractChangeRule.getRenewalType() );
        contractChangeRuleEntity.setReinstatementCondition( contractChangeRule.getReinstatementCondition() );
        contractChangeRuleEntity.setWaitingPeriodDays( contractChangeRule.getWaitingPeriodDays() );
        contractChangeRuleEntity.setFreeLookPeriodDays( contractChangeRule.getFreeLookPeriodDays() );
        contractChangeRuleEntity.setCreateTime( contractChangeRule.getCreatedAt() );
        contractChangeRuleEntity.setUpdateTime( contractChangeRule.getUpdatedAt() );

        return contractChangeRuleEntity;
    }

    @Override
    public ContractChangeRule toContractChangeRule(ContractChangeRuleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ContractChangeRule contractChangeRule = new ContractChangeRule();

        contractChangeRule.setSurrenderCashValueRule( entity.getSurrenderCashValueRule() );
        contractChangeRule.setRenewalType( entity.getRenewalType() );
        contractChangeRule.setReinstatementCondition( entity.getReinstatementCondition() );
        contractChangeRule.setWaitingPeriodDays( entity.getWaitingPeriodDays() );
        contractChangeRule.setFreeLookPeriodDays( entity.getFreeLookPeriodDays() );
        contractChangeRule.setCreatedAt( entity.getCreateTime() );
        contractChangeRule.setUpdatedAt( entity.getUpdateTime() );

        return contractChangeRule;
    }
}
