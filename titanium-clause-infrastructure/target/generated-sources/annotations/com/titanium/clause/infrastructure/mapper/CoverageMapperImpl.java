package com.titanium.clause.infrastructure.mapper;

import com.titanium.clause.entity.Coverage;
import com.titanium.clause.infrastructure.entity.InsuranceLiabilityEntity;
import com.titanium.clause.valueobject.CoverageId;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-03T10:42:17+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class CoverageMapperImpl implements CoverageMapper {

    @Override
    public InsuranceLiabilityEntity toInsuranceLiabilityEntity(Coverage coverage) {
        if ( coverage == null ) {
            return null;
        }

        InsuranceLiabilityEntity insuranceLiabilityEntity = new InsuranceLiabilityEntity();

        insuranceLiabilityEntity.setId( coverageIdValue( coverage ) );
        insuranceLiabilityEntity.setCode( coverage.getCode() );
        insuranceLiabilityEntity.setName( coverage.getName() );
        if ( coverage.getCoverageAmount() != null ) {
            insuranceLiabilityEntity.setCoverage( coverage.getCoverageAmount().doubleValue() );
        }
        if ( coverage.getPremiumRate() != null ) {
            insuranceLiabilityEntity.setPremiumRate( Double.parseDouble( coverage.getPremiumRate() ) );
        }
        insuranceLiabilityEntity.setDescription( coverage.getDescription() );
        insuranceLiabilityEntity.setStatus( coverage.getStatus() );
        insuranceLiabilityEntity.setCreateTime( coverage.getCreatedAt() );
        insuranceLiabilityEntity.setUpdateTime( coverage.getUpdatedAt() );

        return insuranceLiabilityEntity;
    }

    @Override
    public Coverage toCoverage(InsuranceLiabilityEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Coverage coverage = new Coverage();

        coverage.setId( toCoverageId( entity.getId() ) );
        coverage.setCode( entity.getCode() );
        coverage.setName( entity.getName() );
        if ( entity.getCoverage() != null ) {
            coverage.setCoverageAmount( BigDecimal.valueOf( entity.getCoverage() ) );
        }
        if ( entity.getPremiumRate() != null ) {
            coverage.setPremiumRate( String.valueOf( entity.getPremiumRate() ) );
        }
        coverage.setDescription( entity.getDescription() );
        coverage.setStatus( entity.getStatus() );
        coverage.setCreatedAt( entity.getCreateTime() );
        coverage.setUpdatedAt( entity.getUpdateTime() );

        return coverage;
    }

    private String coverageIdValue(Coverage coverage) {
        CoverageId id = coverage.getId();
        if ( id == null ) {
            return null;
        }
        return id.getValue();
    }
}
