package com.titanium.clause.domain.aggregate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.titanium.clause.domain.command.ActivateClauseCommand;
import com.titanium.clause.domain.command.ChangeClauseStatusCommand;
import com.titanium.clause.domain.command.CreateClauseCommand;
import com.titanium.clause.domain.command.InactivateClauseCommand;
import com.titanium.clause.domain.command.UpdateClauseCommand;
import com.titanium.clause.common.constant.ClauseConstants;
import com.titanium.clause.domain.entity.ClaimEvent;
import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.event.ClauseCreatedEvent;
import com.titanium.clause.domain.event.ClauseStatusChangedEvent;
import com.titanium.clause.domain.event.ClauseUpdatedEvent;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.CoverageId;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.clause.domain.valueobject.TimeRange;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.errorcode.ClauseErrorCode;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 条款聚合根
 * <p>
 * 条款聚合根，负责处理条款相关的命令和事件。包含创建、更新、激活、停用条款等命令。
 * <p>
 */
@Aggregate
@Data
@NoArgsConstructor
public class Clause {
    // 手动添加getter方法，解决Lombok注解处理问题
    @AggregateIdentifier
    private ClauseId                    clauseId;
    private ClauseCode                  clauseCode;
    private ClauseName                  clauseName;
    private String                      clauseType;
    private String                      content;
    private String                      status;
    private String                      description;
    private LocalDateTime               effectiveDate;
    private LocalDateTime               expiryDate;
    private Set<String>                 productIds = new HashSet<>();
    private Map<CoverageId, Coverage>   coverages  = new HashMap<>(); // 保险责任列表（key: CoverageId, value: Coverage）
    private Map<ExclusionId, Exclusion> exclusions = new HashMap<>(); // 责任免除列表（key: ExclusionId, value: Exclusion）
    private PremiumRule                 premiumRule;                  // 缴费规则
    private ClaimRule                   claimRule;                    // 理赔规则
    private ContractChangeRule          contractChangeRule;           // 合同变更规则
    private String                      tenantId;
    private String                      createdBy;
    private LocalDateTime               createdAt;
    private String                      updatedBy;
    private LocalDateTime               updatedAt;

    /**
     * 创建条款命令处理器
     *
     * @param command 创建条款命令
     */
    @CommandHandler
    public Clause(CreateClauseCommand command) {
        AggregateLifecycle.apply(new ClauseCreatedEvent(command.clauseId(), command.clauseCode(), command.clauseName(),
                command.clauseType(), command.content(), ClauseConstants.CLAUSE_STATUS_DRAFT, command.description(),
                command.effectiveDate(), command.expiryDate(),
                command.productIds() != null ? command.productIds() : Set.of(), Map.of(), // 初始化空的保险责任
                Map.of(), // 初始化空的责任免除
                null, // 初始化为空的缴费规则
                null, // 初始化为空的理赔规则
                null, // 初始化为空的合同变更规则
                command.tenantId(), command.createdBy(), LocalDateTime.now(), command.createdBy(),
                LocalDateTime.now()));
    }

    /**
     * 更新条款命令处理器
     *
     * @param command 更新条款命令
     */
    @CommandHandler
    public void handle(UpdateClauseCommand command) {
        // 检查条款状态是否允许更新
        if (!ClauseEnum.ClauseStatus.DRAFT.getCode().equals(this.status)
                && !ClauseEnum.ClauseStatus.ACTIVE.getCode().equals(this.status)) {
            throw new ClauseInvalidStatusException(ClauseErrorCode.CLAUSE_OPERATION_NOT_ALLOWED.getMessage());
        }

        AggregateLifecycle.apply(new ClauseUpdatedEvent(command.clauseId(), command.clauseCode(), command.clauseName(),
                command.clauseType(), command.content(), this.status, command.description(), command.effectiveDate(),
                command.expiryDate(), command.productIds() != null ? command.productIds() : this.productIds,
                this.coverages, // 使用现有保险责任
                this.exclusions, // 使用现有责任免除
                this.premiumRule, // 使用现有缴费规则
                this.claimRule, // 使用现有理赔规则
                this.contractChangeRule, // 使用现有合同变更规则
                command.tenantId(), command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 变更条款状态命令处理器
     *
     * @param command 变更条款状态命令
     */
    @CommandHandler
    public void handle(ChangeClauseStatusCommand command) {
        // 检查状态变更是否合法
        String newStatus = command.newStatus();
        if (this.status.equals(newStatus)) {
            return;
        }

        boolean isValid = switch (this.status) {
            case ClauseConstants.CLAUSE_STATUS_DRAFT -> ClauseEnum.ClauseStatus.ACTIVE.getCode().equals(newStatus)
                    || ClauseEnum.ClauseStatus.INACTIVE.getCode().equals(newStatus);
            case ClauseConstants.CLAUSE_STATUS_ACTIVE -> ClauseEnum.ClauseStatus.INACTIVE.getCode().equals(newStatus)
                    || ClauseEnum.ClauseStatus.EXPIRED.getCode().equals(newStatus);
            case ClauseConstants.CLAUSE_STATUS_INACTIVE -> ClauseEnum.ClauseStatus.ACTIVE.getCode().equals(newStatus);
            default -> false;
        };

        String errorMessage = switch (this.status) {
            case ClauseConstants.CLAUSE_STATUS_DRAFT -> "草稿状态的条款只能变更为激活或停用状态";
            case ClauseConstants.CLAUSE_STATUS_ACTIVE -> "激活状态的条款只能变更为停用或过期状态";
            case ClauseConstants.CLAUSE_STATUS_INACTIVE -> "停用状态的条款只能变更为激活状态";
            case ClauseConstants.CLAUSE_STATUS_EXPIRED -> "过期状态的条款不能变更状态";
            default -> "无效的条款状态: " + this.status;
        };
        if (!isValid) {
            throw new ClauseInvalidStatusException(errorMessage);
        }

        AggregateLifecycle.apply(new ClauseStatusChangedEvent(command.clauseId(), command.newStatus(),
                command.updatedBy(), LocalDateTime.now()));
    }

    /**
     * 激活条款命令处理器
     *
     * @param command 激活条款命令
     */
    @CommandHandler
    public void handle(ActivateClauseCommand command) {
        handle(new ChangeClauseStatusCommand(command.clauseId(), ClauseConstants.CLAUSE_STATUS_ACTIVE,
                command.updatedBy()));
    }

    /**
     * 停用条款命令处理器
     *
     * @param command 停用条款命令
     */
    @CommandHandler
    public void handle(InactivateClauseCommand command) {
        handle(new ChangeClauseStatusCommand(command.clauseId(), ClauseConstants.CLAUSE_STATUS_INACTIVE,
                command.updatedBy()));
    }

    /**
     * 条款创建事件处理器
     *
     * @param event 条款创建事件
     */
    @EventSourcingHandler
    public void on(ClauseCreatedEvent event) {
        this.clauseId = event.clauseId();
        this.clauseCode = event.clauseCode();
        this.clauseName = event.clauseName();
        this.clauseType = event.clauseType();
        this.content = event.content();
        this.status = event.status();
        this.description = event.description();
        this.effectiveDate = event.effectiveDate();
        this.expiryDate = event.expiryDate();
        this.productIds = event.productIds() != null ? event.productIds() : new HashSet<>();
        this.coverages = event.coverages() != null ? event.coverages() : new HashMap<>();
        this.exclusions = event.exclusions() != null ? event.exclusions() : new HashMap<>();
        this.premiumRule = event.premiumRule();
        this.claimRule = event.claimRule();
        this.contractChangeRule = event.contractChangeRule();
        this.tenantId = event.tenantId();
        this.createdBy = event.createdBy();
        this.createdAt = event.createdAt();
        this.updatedBy = event.updatedBy();
        this.updatedAt = event.updatedAt();
    }

    /**
     * 条款更新事件处理器
     *
     * @param event 条款更新事件
     */
    @EventSourcingHandler
    public void on(ClauseUpdatedEvent event) {
        this.clauseCode = event.clauseCode();
        this.clauseName = event.clauseName();
        this.clauseType = event.clauseType();
        this.content = event.content();
        this.description = event.description();
        this.effectiveDate = event.effectiveDate();
        this.expiryDate = event.expiryDate();
        this.productIds = event.productIds() != null ? event.productIds() : this.productIds;
        this.coverages = event.coverages() != null ? event.coverages() : this.coverages;
        this.exclusions = event.exclusions() != null ? event.exclusions() : this.exclusions;
        this.premiumRule = event.premiumRule() != null ? event.premiumRule() : this.premiumRule;
        this.claimRule = event.claimRule() != null ? event.claimRule() : this.claimRule;
        this.contractChangeRule = event.contractChangeRule() != null ? event.contractChangeRule()
                : this.contractChangeRule;
        this.updatedBy = event.updatedBy();
        this.updatedAt = event.updatedAt();
    }

    /**
     * 条款状态变更事件处理器
     *
     * @param event 条款状态变更事件
     */
    @EventSourcingHandler
    public void on(ClauseStatusChangedEvent event) {
        this.status = event.newStatus();
        this.updatedBy = event.updatedBy();
        this.updatedAt = event.updatedAt();
    }

    /**
     * 添加保险责任
     *
     * @param coverage 保险责任
     */
    public void addCoverage(Coverage coverage) {
        if (coverage != null && coverage.getId() != null) {
            coverages.put(coverage.getId(), coverage);
        }
    }

    /**
     * 移除保险责任
     *
     * @param coverageId 保险责任ID
     */
    public void removeCoverage(CoverageId coverageId) {
        coverages.remove(coverageId);
    }

    /**
     * 添加责任免除
     *
     * @param exclusion 责任免除
     */
    public void addExclusion(Exclusion exclusion) {
        if (exclusion != null && exclusion.getId() != null) {
            exclusions.put(exclusion.getId(), exclusion);
        }
    }

    /**
     * 移除责任免除
     *
     * @param exclusionId 责任免除ID
     */
    public void removeExclusion(ExclusionId exclusionId) {
        exclusions.remove(exclusionId);
    }

    /**
     * 校验理赔事件是否符合条款规则
     *
     * @param claimEvent 理赔事件
     * @return 是否符合条款规则
     */
    public boolean validateClaim(ClaimEvent claimEvent) {
        // 1. 检查条款是否有效
        if (!ClauseConstants.CLAUSE_STATUS_ACTIVE.equals(status)) {
            return false;
        }

        // 2. 检查条款有效期
        TimeRange validityRange = TimeRange.of(effectiveDate, expiryDate);
        if (!validityRange.isInRange(claimEvent.getClaimTime())) {
            return false;
        }

        // 3. 检查是否命中责任免除

        for (Exclusion exclusion : exclusions.values()) {
            if (exclusion.isHitExclusion(claimEvent)) {
                return false;
            }
        }

        // 4. 检查是否命中保险责任
        for (Coverage coverage : coverages.values()) {
            if (coverage.checkTriggerCondition(claimEvent)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 添加产品关联
     *
     * @param productId 产品ID
     */
    public void addProduct(String productId) {
        if (productId != null) {
            productIds.add(productId);
        }
    }

    /**
     * 移除产品关联
     *
     * @param productId 产品ID
     */
    public void removeProduct(String productId) {
        productIds.remove(productId);
    }
}
