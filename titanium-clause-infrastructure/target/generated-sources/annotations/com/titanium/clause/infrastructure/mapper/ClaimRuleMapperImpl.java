package com.titanium.clause.infrastructure.mapper;

import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.infrastructure.entity.ClaimRuleEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-30T12:33:07+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ClaimRuleMapperImpl implements ClaimRuleMapper {

    @Override
    public ClaimRuleEntity toClaimRuleEntity(ClaimRule claimRule) {
        if ( claimRule == null ) {
            return null;
        }

        ClaimRuleEntity claimRuleEntity = new ClaimRuleEntity();

        claimRuleEntity.setReportDeadlineDays( claimRule.getReportDeadlineDays() );
        claimRuleEntity.setRequiredMaterials( claimRule.getRequiredMaterials() );
        claimRuleEntity.setSettlementPeriodDays( claimRule.getSettlementPeriodDays() );
        claimRuleEntity.setPayoutRatio( claimRule.getPayoutRatio() );
        claimRuleEntity.setDeductibleAmount( claimRule.getDeductibleAmount() );
        claimRuleEntity.setCreateTime( claimRule.getCreatedAt() );
        claimRuleEntity.setUpdateTime( claimRule.getUpdatedAt() );

        return claimRuleEntity;
    }

    @Override
    public ClaimRule toClaimRule(ClaimRuleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClaimRule claimRule = new ClaimRule();

        claimRule.setReportDeadlineDays( entity.getReportDeadlineDays() );
        claimRule.setRequiredMaterials( entity.getRequiredMaterials() );
        claimRule.setSettlementPeriodDays( entity.getSettlementPeriodDays() );
        claimRule.setPayoutRatio( entity.getPayoutRatio() );
        claimRule.setDeductibleAmount( entity.getDeductibleAmount() );
        claimRule.setCreatedAt( entity.getCreateTime() );
        claimRule.setUpdatedAt( entity.getUpdateTime() );

        return claimRule;
    }
}
