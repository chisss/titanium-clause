package com.titanium.clause.aggregate;

import java.time.LocalDateTime;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.titanium.clause.command.CreateLiabilityCommand;
import com.titanium.clause.command.UpdateLiabilityCommand;
import com.titanium.clause.event.LiabilityCreatedEvent;
import com.titanium.clause.event.LiabilityUpdatedEvent;
import com.titanium.clause.valueobject.InsuranceLiabilityId;
import com.titanium.clause.valueobject.LiabilityCode;
import com.titanium.clause.valueobject.LiabilityName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保险责任聚合根
 */
@Aggregate
@Data
@NoArgsConstructor
public class InsuranceLiability {
    @AggregateIdentifier
    private InsuranceLiabilityId liabilityId;
    private LiabilityCode        code;
    private LiabilityName        name;
    private Double               coverage;
    private Double               premiumRate;
    private String               description;
    private String               status;
    private String               tenantId;
    private String               createdBy;
    private LocalDateTime        createdAt;
    private String               updatedBy;
    private LocalDateTime        updatedAt;

    /**
     * 创建责任命令处理器
     * 
     * @param command 创建责任命令
     */
    @CommandHandler
    public InsuranceLiability(CreateLiabilityCommand command) {
        AggregateLifecycle.apply(new LiabilityCreatedEvent(command.liabilityId(), command.code(), command.name(),
                command.coverage(), command.premiumRate(), command.description(), command.status(), command.tenantId(),
                command.createdBy(), LocalDateTime.now(), command.createdBy(), LocalDateTime.now()));
    }

    /**
     * 更新责任命令处理器
     * 
     * @param command 更新责任命令
     */
    @CommandHandler
    public void handle(UpdateLiabilityCommand command) {
        AggregateLifecycle.apply(new LiabilityUpdatedEvent(command.liabilityId(), command.code(), command.name(),
                command.coverage(), command.premiumRate(), command.description(), command.status(), command.tenantId(),
                command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 责任创建事件处理器
     * 
     * @param event 责任创建事件
     */
    @EventSourcingHandler
    public void on(LiabilityCreatedEvent event) {
        this.liabilityId = event.liabilityId();
        this.code = event.code();
        this.name = event.name();
        this.coverage = event.coverage();
        this.premiumRate = event.premiumRate();
        this.description = event.description();
        this.status = event.status();
        this.tenantId = event.tenantId();
        this.createdBy = event.createdBy();
        this.createdAt = event.createdAt();
        this.updatedBy = event.updatedBy();
        this.updatedAt = event.updatedAt();
    }

    /**
     * 责任更新事件处理器
     * 
     * @param event 责任更新事件
     */
    @EventSourcingHandler
    public void on(LiabilityUpdatedEvent event) {
        this.code = event.code();
        this.name = event.name();
        this.coverage = event.coverage();
        this.premiumRate = event.premiumRate();
        this.description = event.description();
        this.status = event.status();
        this.updatedBy = event.updatedBy();
        this.updatedAt = event.updatedAt();
    }
}
