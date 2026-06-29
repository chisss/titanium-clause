package com.titanium.clause.infrastructure.mapper;

import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.clause.infrastructure.entity.InsuranceExclusionEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T17:21:51+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ExclusionMapperImpl implements ExclusionMapper {

    @Override
    public InsuranceExclusionEntity toInsuranceExclusionEntity(Exclusion exclusion) {
        if ( exclusion == null ) {
            return null;
        }

        InsuranceExclusionEntity insuranceExclusionEntity = new InsuranceExclusionEntity();

        insuranceExclusionEntity.setId( exclusionIdValue( exclusion ) );
        insuranceExclusionEntity.setType( exclusion.getType() );
        insuranceExclusionEntity.setExclusionRuleCode( exclusion.getExclusionRuleCode() );
        insuranceExclusionEntity.setIsMandatory( exclusion.getIsMandatory() );
        insuranceExclusionEntity.setDescription( exclusion.getDescription() );
        insuranceExclusionEntity.setStatus( exclusion.getStatus() );
        insuranceExclusionEntity.setCreateTime( exclusion.getCreatedAt() );
        insuranceExclusionEntity.setUpdateTime( exclusion.getUpdatedAt() );

        return insuranceExclusionEntity;
    }

    @Override
    public Exclusion toExclusion(InsuranceExclusionEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Exclusion exclusion = new Exclusion();

        exclusion.setId( toExclusionId( entity.getId() ) );
        exclusion.setType( entity.getType() );
        exclusion.setExclusionRuleCode( entity.getExclusionRuleCode() );
        exclusion.setIsMandatory( entity.getIsMandatory() );
        exclusion.setDescription( entity.getDescription() );
        exclusion.setStatus( entity.getStatus() );
        exclusion.setCreatedAt( entity.getCreateTime() );
        exclusion.setUpdatedAt( entity.getUpdateTime() );

        return exclusion;
    }

    private String exclusionIdValue(Exclusion exclusion) {
        ExclusionId id = exclusion.getId();
        if ( id == null ) {
            return null;
        }
        return id.getValue();
    }
}
