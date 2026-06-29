package com.titanium.clause.application.mapper;

import com.titanium.clause.api.request.CreateClauseRequest;
import com.titanium.clause.api.request.UpdateClauseRequest;
import com.titanium.clause.domain.command.CreateClauseCommand;
import com.titanium.clause.domain.command.UpdateClauseCommand;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.Version;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T17:21:54+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ClauseMapperImpl implements ClauseMapper {

    @Override
    public CreateClauseCommand toCreateClauseCommand(CreateClauseRequest request, ClauseId clauseId, String tenantId) {
        if ( request == null && clauseId == null && tenantId == null ) {
            return null;
        }

        ClauseCode clauseCode = null;
        ClauseName clauseName = null;
        ClauseEnum.ClauseType clauseType = null;
        String content = null;
        String description = null;
        InsuranceType insuranceType = null;
        LocalDateTime effectiveDate = null;
        LocalDateTime expiryDate = null;
        String createdBy = null;
        if ( request != null ) {
            clauseCode = toClauseCode( request.getClauseCode() );
            clauseName = toClauseName( request.getClauseName() );
            clauseType = request.getClauseType();
            content = request.getContent();
            description = request.getDescription();
            insuranceType = request.getInsuranceType();
            effectiveDate = request.getEffectiveDate();
            expiryDate = request.getExpiryDate();
            createdBy = request.getCreatedBy();
        }
        ClauseId clauseId1 = null;
        clauseId1 = clauseId;
        String tenantId1 = null;
        tenantId1 = tenantId;

        Version version = null;

        CreateClauseCommand createClauseCommand = new CreateClauseCommand( clauseId1, clauseCode, clauseName, clauseType, content, description, insuranceType, version, effectiveDate, expiryDate, tenantId1, createdBy );

        return createClauseCommand;
    }

    @Override
    public UpdateClauseCommand toUpdateClauseCommand(UpdateClauseRequest request, ClauseId clauseId, String tenantId) {
        if ( request == null && clauseId == null && tenantId == null ) {
            return null;
        }

        ClauseCode clauseCode = null;
        ClauseName clauseName = null;
        ClauseEnum.ClauseType clauseType = null;
        String content = null;
        String description = null;
        InsuranceType insuranceType = null;
        LocalDateTime effectiveDate = null;
        LocalDateTime expiryDate = null;
        String updatedBy = null;
        if ( request != null ) {
            clauseCode = toClauseCode( request.getClauseCode() );
            clauseName = toClauseName( request.getClauseName() );
            clauseType = request.getClauseType();
            content = request.getContent();
            description = request.getDescription();
            insuranceType = request.getInsuranceType();
            effectiveDate = request.getEffectiveDate();
            expiryDate = request.getExpiryDate();
            updatedBy = request.getUpdatedBy();
        }
        ClauseId clauseId1 = null;
        clauseId1 = clauseId;
        String tenantId1 = null;
        tenantId1 = tenantId;

        UpdateClauseCommand updateClauseCommand = new UpdateClauseCommand( clauseId1, clauseCode, clauseName, clauseType, content, description, insuranceType, effectiveDate, expiryDate, tenantId1, updatedBy );

        return updateClauseCommand;
    }
}
