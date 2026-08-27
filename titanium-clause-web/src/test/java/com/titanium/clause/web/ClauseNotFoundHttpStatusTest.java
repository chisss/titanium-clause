package com.titanium.clause.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.titanium.clause.application.query.ClauseAppQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.common.context.TenantContext;
import com.titanium.clause.web.assembler.CoverageAssembler;
import com.titanium.clause.web.assembler.CoverageResponseAssembler;
import com.titanium.clause.web.controller.ClauseController;
import com.titanium.clause.web.mapper.ClauseWebMapper;
import com.titanium.clause.web.provider.ClauseApiProvider;

class ClauseNotFoundHttpStatusTest {

    private static final String TENANT_ID = "TEST-TENANT-001";

    private ClauseAppQueryService clauseAppQueryService;
    private MockMvc               webMockMvc;
    private MockMvc               apiMockMvc;

    @BeforeEach
    void setUp() {
        clauseAppQueryService = mock(ClauseAppQueryService.class);
        ClauseApplicationService applicationService = mock(ClauseApplicationService.class);
        ClauseWebMapper webMapper = mock(ClauseWebMapper.class);
        ClauseController clauseController = new ClauseController(applicationService, clauseAppQueryService, webMapper,
                mock(CoverageAssembler.class));
        ClauseApiProvider clauseApiProvider = new ClauseApiProvider(applicationService, clauseAppQueryService, webMapper,
                mock(CoverageResponseAssembler.class));
        webMockMvc = MockMvcBuilders.standaloneSetup(clauseController).build();
        apiMockMvc = MockMvcBuilders.standaloneSetup(clauseApiProvider).build();
        TenantContext.setCurrentTenant(TENANT_ID);
    }

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    @Test
    void webQueryByIdReturnsNotFoundWhenClauseDoesNotExist() throws Exception {
        when(clauseAppQueryService.findById("missing-clause", TENANT_ID)).thenReturn(Optional.empty());

        webMockMvc.perform(get("/web/v1/clauses/missing-clause"))
                .andExpect(status().isNotFound());
    }

    @Test
    void webQueryByCodeReturnsNotFoundWhenClauseDoesNotExist() throws Exception {
        when(clauseAppQueryService.findByCode("MISSING-CODE", TENANT_ID)).thenReturn(Optional.empty());

        webMockMvc.perform(get("/web/v1/clauses/code/MISSING-CODE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiProviderQueryByIdReturnsNotFoundWhenClauseDoesNotExist() throws Exception {
        when(clauseAppQueryService.findById("missing-clause", TENANT_ID)).thenReturn(Optional.empty());

        apiMockMvc.perform(get("/api/v1/clauses/missing-clause")
                .header("X-Tenant-Id", TENANT_ID))
                .andExpect(status().isNotFound());
    }
}
