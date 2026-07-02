package com.titanium.clause.infrastructure.mapper;

import com.titanium.clause.domain.aggregate.Clause;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.Version;
import com.titanium.clause.infrastructure.entity.ClauseEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-30T12:33:07+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ClauseEntityMapperImpl implements ClauseEntityMapper {

    @Override
    public ClauseEntity toClauseEntity(Clause clause) {
        if ( clause == null ) {
            return null;
        }

        ClauseEntity clauseEntity = new ClauseEntity();

        clauseEntity.setId( clauseClauseIdValue( clause ) );
        clauseEntity.setClauseCode( clauseClauseCodeValue( clause ) );
        clauseEntity.setClauseName( clauseClauseNameValue( clause ) );
        clauseEntity.setClauseType( clause.getClauseType() );
        clauseEntity.setInsuranceType( clause.getInsuranceType() );
        clauseEntity.setVersion( clauseVersionValue( clause ) );
        clauseEntity.setParentClauseId( clauseParentClauseIdValue( clause ) );
        clauseEntity.setStatus( clause.getStatus() );
        clauseEntity.setTenantId( clause.getTenantId() );
        clauseEntity.setCreateTime( clause.getCreateTime() );
        clauseEntity.setUpdateTime( clause.getUpdateTime() );
        clauseEntity.setCreatedBy( clause.getCreatedBy() );
        clauseEntity.setUpdatedBy( clause.getUpdatedBy() );
        clauseEntity.setContent( clause.getContent() );
        clauseEntity.setEffectiveDate( clause.getEffectiveDate() );
        clauseEntity.setDescription( clause.getDescription() );

        return clauseEntity;
    }

    @Override
    public Clause toClause(ClauseEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Clause.ClauseBuilder<?, ?> clause = Clause.builder();

        clause.clauseId( toClauseId( entity.getId() ) );
        clause.clauseCode( toClauseCode( entity.getClauseCode() ) );
        clause.clauseName( toClauseName( entity.getClauseName() ) );
        clause.clauseType( entity.getClauseType() );
        clause.insuranceType( entity.getInsuranceType() );
        clause.version( toVersion( entity.getVersion() ) );
        clause.parentClauseId( toClauseId( entity.getParentClauseId() ) );
        clause.status( entity.getStatus() );
        clause.tenantId( entity.getTenantId() );
        clause.createTime( entity.getCreateTime() );
        clause.updateTime( entity.getUpdateTime() );
        clause.content( entity.getContent() );
        clause.description( entity.getDescription() );
        clause.effectiveDate( entity.getEffectiveDate() );
        clause.createdBy( entity.getCreatedBy() );
        clause.updatedBy( entity.getUpdatedBy() );

        return clause.build();
    }

    private String clauseClauseIdValue(Clause clause) {
        ClauseId clauseId = clause.getClauseId();
        if ( clauseId == null ) {
            return null;
        }
        return clauseId.getValue();
    }

    private String clauseClauseCodeValue(Clause clause) {
        ClauseCode clauseCode = clause.getClauseCode();
        if ( clauseCode == null ) {
            return null;
        }
        return clauseCode.getValue();
    }

    private String clauseClauseNameValue(Clause clause) {
        ClauseName clauseName = clause.getClauseName();
        if ( clauseName == null ) {
            return null;
        }
        return clauseName.getValue();
    }

    private String clauseVersionValue(Clause clause) {
        Version version = clause.getVersion();
        if ( version == null ) {
            return null;
        }
        return version.getValue();
    }

    private String clauseParentClauseIdValue(Clause clause) {
        ClauseId parentClauseId = clause.getParentClauseId();
        if ( parentClauseId == null ) {
            return null;
        }
        return parentClauseId.getValue();
    }
}
