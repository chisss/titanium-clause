package com.titanium.clause.domain.aggregate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.titanium.clause.domain.command.CreateProductCommand;
import com.titanium.clause.domain.command.UpdateProductCommand;
import com.titanium.clause.domain.event.ProductCreatedEvent;
import com.titanium.clause.domain.event.ProductUpdatedEvent;
import com.titanium.clause.domain.valueobject.InsuranceProductId;
import com.titanium.clause.domain.valueobject.ProductCode;
import com.titanium.clause.domain.valueobject.ProductName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保险产品聚合根
 */
@Aggregate
@Data
@NoArgsConstructor
public class InsuranceProduct {
    @AggregateIdentifier
    private InsuranceProductId productId;
    private ProductCode        productCode;
    private ProductName        productName;
    private String             productType;
    private String             productClass;
    private String             description;
    private String             status;
    private Integer            mainProduct;                // 是否主险: 0-非主险, 1-主险
    private String             currency;                   // 币种
    private Integer            gracePeriod;                // 宽限期(天)
    private Integer            freeLookPeriod;             // 犹豫期(天)
    private Set<String>        clauseIds = new HashSet<>();
    private String             tenantId;
    private String             createdBy;
    private LocalDateTime      createdAt;
    private String             updatedBy;
    private LocalDateTime      updatedAt;

    /**
     * 创建产品命令处理器
     * 
     * @param command 创建产品命令
     */
    @CommandHandler
    public InsuranceProduct(CreateProductCommand command) {
        AggregateLifecycle.apply(new ProductCreatedEvent(command.productId(), command.productCode(),
                command.productName(), command.productType(), command.productClass(), command.description(),
                command.status(), command.mainProduct() != null ? command.mainProduct() : 0,
                command.currency() != null ? command.currency() : "CNY", command.gracePeriod(),
                command.freeLookPeriod(), command.clauseIds(), command.tenantId(), command.createdBy(),
                LocalDateTime.now(), command.createdBy(), LocalDateTime.now()));
    }

    /**
     * 更新产品命令处理器
     * 
     * @param command 更新产品命令
     */
    @CommandHandler
    public void handle(UpdateProductCommand command) {
        AggregateLifecycle
                .apply(new ProductUpdatedEvent(command.productId(), command.productCode(), command.productName(),
                        command.productType(), command.productClass(), command.description(), command.status(),
                        command.mainProduct(), command.currency(), command.gracePeriod(), command.freeLookPeriod(),
                        command.clauseIds(), command.tenantId(), command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 产品创建事件处理器
     * 
     * @param event 产品创建事件
     */
    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.productId();
        this.productCode = event.productCode();
        this.productName = event.productName();
        this.productType = event.productType();
        this.productClass = event.productClass();
        this.description = event.description();
        this.status = event.status();
        this.mainProduct = event.mainProduct() != null ? event.mainProduct() : 0;
        this.currency = event.currency() != null ? event.currency() : "CNY";
        this.gracePeriod = event.gracePeriod();
        this.freeLookPeriod = event.freeLookPeriod();
        if (event.clauseIds() != null) {
            this.clauseIds = new HashSet<>(event.clauseIds());
        }
        this.tenantId = event.tenantId();
        this.createdBy = event.createdBy();
        this.createdAt = event.createdAt();
        this.updatedBy = event.updatedBy();
        this.updatedAt = event.updatedAt();
    }

    /**
     * 产品更新事件处理器
     * 
     * @param event 产品更新事件
     */
    @EventSourcingHandler
    public void on(ProductUpdatedEvent event) {
        this.productCode = event.productCode();
        this.productName = event.productName();
        this.productType = event.productType();
        this.productClass = event.productClass();
        this.description = event.description();
        this.status = event.status();
        this.mainProduct = event.mainProduct() != null ? event.mainProduct() : this.mainProduct;
        this.currency = event.currency() != null ? event.currency() : this.currency;
        this.gracePeriod = event.gracePeriod() != null ? event.gracePeriod() : this.gracePeriod;
        this.freeLookPeriod = event.freeLookPeriod() != null ? event.freeLookPeriod() : this.freeLookPeriod;
        if (event.clauseIds() != null) {
            this.clauseIds = new HashSet<>(event.clauseIds());
        }
        this.updatedBy = event.updatedBy();
        this.updatedAt = event.updatedAt();
    }

    /**
     * 添加条款到产品
     * 
     * @param clauseId 条款ID
     */
    public void addClause(String clauseId) {
        if (clauseId != null && !clauseId.isEmpty()) {
            this.clauseIds.add(clauseId);
        }
    }

    /**
     * 从产品中移除条款
     * 
     * @param clauseId 条款ID
     */
    public void removeClause(String clauseId) {
        if (clauseId != null && !clauseId.isEmpty()) {
            this.clauseIds.remove(clauseId);
        }
    }
}
