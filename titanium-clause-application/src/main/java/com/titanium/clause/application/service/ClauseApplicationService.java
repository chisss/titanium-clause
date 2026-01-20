package com.titanium.clause.application.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.common.exception.ClauseDuplicateException;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.common.exception.ClauseNotFoundException;
import com.titanium.clause.domain.aggregate.Clause;
import com.titanium.clause.domain.command.ActivateClauseCommand;
import com.titanium.clause.domain.command.CreateClauseCommand;
import com.titanium.clause.domain.command.InactivateClauseCommand;
import com.titanium.clause.domain.command.UpdateClauseCommand;
import com.titanium.clause.domain.repository.ClauseRepository;
import com.titanium.clause.domain.service.ClauseDomainService;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;

import lombok.RequiredArgsConstructor;

/**
 * 条款应用服务
 */
@Component
@RequiredArgsConstructor
public class ClauseApplicationService {
    private final CommandGateway      commandGateway;
    private final ClauseDomainService clauseDomainService;
    private final ClauseRepository    clauseRepository;

    /**
     * 创建条款
     *
     * @param clauseCode 条款代码
     * @param clauseName 条款名称
     * @param clauseType 条款类型
     * @param content 条款内容
     * @param description 条款描述
     * @param effectiveDate 生效日期
     * @param expiryDate 失效日期
     * @param createdBy 创建人
     * @param tenantId 租户ID
     * @return 条款ID
     */
    @Transactional
    public ClauseId createClause(String clauseCode, String clauseName, String clauseType, String content,
                                 String description, LocalDateTime effectiveDate, LocalDateTime expiryDate,
                                 String createdBy, String tenantId) {
        // 检查条款代码是否已存在
        Optional<Clause> existingClause = clauseRepository.findByCode(ClauseCode.fromString(clauseCode), tenantId);
        if (existingClause.isPresent()) {
            throw new ClauseDuplicateException("条款代码已存在: " + clauseCode);
        }

        // 验证条款数据
        clauseDomainService.validateClauseData(clauseCode, clauseName, clauseType, content, effectiveDate, expiryDate);

        // 创建条款ID
        ClauseId clauseId = ClauseId.create();

        // 发布创建条款命令
        CreateClauseCommand command = new CreateClauseCommand(clauseId, ClauseCode.fromString(clauseCode),
                ClauseName.fromString(clauseName), clauseType, content, description, effectiveDate, expiryDate,
                Set.of(), tenantId, createdBy);

        commandGateway.sendAndWait(command);

        return clauseId;
    }

    /**
     * 更新条款
     *
     * @param clauseId 条款ID
     * @param clauseName 条款名称
     * @param clauseType 条款类型
     * @param content 条款内容
     * @param description 条款描述
     * @param effectiveDate 生效日期
     * @param expiryDate 失效日期
     * @param updatedBy 更新人
     * @param tenantId 租户ID
     */
    @Transactional
    public void updateClause(String clauseId, String clauseName, String clauseType, String content, String description,
                             LocalDateTime effectiveDate, LocalDateTime expiryDate, String updatedBy, String tenantId) {
        // 验证条款是否存在
        ClauseId id = ClauseId.fromString(clauseId);
        Optional<Clause> clauseOptional = clauseRepository.findById(id, tenantId);
        if (clauseOptional.isEmpty()) {
            throw new ClauseNotFoundException("条款不存在: " + clauseId);
        }

        Clause clause = clauseOptional.get();

        // 验证条款状态是否允许更新
        clauseDomainService.validateClauseUpdateStatus(clause.getStatus());

        // 验证条款数据
        clauseDomainService.validateClauseData(clause.getClauseCode().getValue(), clauseName, clauseType, content,
                effectiveDate, expiryDate);

        // 发布更新条款命令
        UpdateClauseCommand command = new UpdateClauseCommand(id, clause.getClauseCode(),
                ClauseName.fromString(clauseName), clauseType, content, description, effectiveDate, expiryDate,
                Set.of(), tenantId, updatedBy);

        commandGateway.sendAndWait(command);
    }

    /**
     * 激活条款
     *
     * @param clauseId 条款ID
     * @param activatedBy 激活人
     * @param tenantId 租户ID
     */
    @Transactional
    public void activateClause(String clauseId, String activatedBy, String tenantId) {
        // 验证条款是否存在
        ClauseId id = ClauseId.fromString(clauseId);
        Optional<Clause> clauseOptional = clauseRepository.findById(id, tenantId);
        if (clauseOptional.isEmpty()) {
            throw new ClauseNotFoundException("条款不存在: " + clauseId);
        }

        Clause clause = clauseOptional.get();

        // 验证条款是否可以激活
        if (!clauseDomainService.canActivateClause(clause)) {
            throw new ClauseInvalidStatusException("条款状态不允许激活: " + clause.getStatus());
        }

        // 发布激活条款命令
        ActivateClauseCommand command = new ActivateClauseCommand(id, activatedBy);
        commandGateway.sendAndWait(command);
    }

    /**
     * 停用条款
     *
     * @param clauseId 条款ID
     * @param inactivatedBy 停用人人
     * @param tenantId 租户ID
     */
    @Transactional
    public void inactivateClause(String clauseId, String inactivatedBy, String tenantId) {
        // 验证条款是否存在
        ClauseId id = ClauseId.fromString(clauseId);
        Optional<Clause> clauseOptional = clauseRepository.findById(id, tenantId);
        if (clauseOptional.isEmpty()) {
            throw new ClauseNotFoundException("条款不存在: " + clauseId);
        }

        // 发布停用条款命令
        InactivateClauseCommand command = new InactivateClauseCommand(id, inactivatedBy);
        commandGateway.sendAndWait(command);
    }

    /**
     * 删除条款
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     */
    @Transactional
    public void deleteClause(String clauseId, String tenantId) {
        // 验证条款是否存在
        ClauseId id = ClauseId.fromString(clauseId);
        Optional<Clause> clauseOptional = clauseRepository.findById(id, tenantId);
        if (clauseOptional.isEmpty()) {
            throw new ClauseNotFoundException("条款不存在: " + clauseId);
        }

        Clause clause = clauseOptional.get();

        // 验证条款是否可以删除
        if (!clauseDomainService.canDeleteClause(clause)) {
            throw new ClauseInvalidStatusException("条款状态不允许删除: " + clause.getStatus());
        }

        // 执行删除操作
        clauseRepository.deleteById(id, tenantId);
    }
}
