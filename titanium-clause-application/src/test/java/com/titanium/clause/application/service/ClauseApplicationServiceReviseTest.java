package com.titanium.clause.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.titanium.clause.command.ReviseClauseCommand;
import com.titanium.clause.common.exception.ClauseNotFoundException;
import com.titanium.clause.query.repository.ClauseViewRepository;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.clause.service.ClauseDomainService;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.common.number.IdGenerator;

/**
 * 条款修订应用服务测试（缺口 G9：修订→新版本编排）
 * <p>
 * 校验应用层修订编排：读模型存在性校验（不存在即抛条款不存在）、为修订生成独立新条款ID、
 * 经命令网关发送 {@link ReviseClauseCommand} 并返回新版本条款ID（供调用方对新版本继续编辑责任）。
 * </p>
 */
class ClauseApplicationServiceReviseTest {

    private CommandGateway        commandGateway;
    private ClauseDomainService   clauseDomainService;
    private ClauseViewRepository  clauseViewRepository;
    private IdGenerator           idGenerator;
    private ClauseApplicationService service;

    private static final String CLAUSE_ID = "clause-001";
    private static final String NEW_CLAUSE_ID = "clause-revised-002";
    private static final String TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        commandGateway = mock(CommandGateway.class);
        clauseDomainService = mock(ClauseDomainService.class);
        clauseViewRepository = mock(ClauseViewRepository.class);
        idGenerator = mock(IdGenerator.class);
        service = new ClauseApplicationService(commandGateway, clauseDomainService, clauseViewRepository, idGenerator);
    }

    @Test
    void reviseClauseSendsCommandAndReturnsNewClauseId() {
        when(clauseViewRepository.findByClauseIdAndTenantId(CLAUSE_ID, TENANT_ID))
                .thenReturn(Optional.of(new ClauseView()));
        when(idGenerator.nextIdString()).thenReturn(NEW_CLAUSE_ID);
        when(commandGateway.sendAndWait(any(ReviseClauseCommand.class))).thenReturn(null);

        ClauseId newClauseId = service.reviseClause(CLAUSE_ID, "reviser", TENANT_ID);

        // 新版本条款ID须独立生成并返回（调用方以此继续编辑责任）；DSN-05：ID 取自注入的 IdGenerator
        assertThat(newClauseId).isNotNull();
        assertThat(newClauseId.value()).isEqualTo(NEW_CLAUSE_ID);
        assertThat(newClauseId.value()).isNotEqualTo(CLAUSE_ID);
        verify(commandGateway).sendAndWait(any(ReviseClauseCommand.class));
    }

    @Test
    void reviseClauseThrowsWhenClauseMissing() {
        when(clauseViewRepository.findByClauseIdAndTenantId(CLAUSE_ID, TENANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reviseClause(CLAUSE_ID, "reviser", TENANT_ID))
                .isInstanceOf(ClauseNotFoundException.class);
    }
}
