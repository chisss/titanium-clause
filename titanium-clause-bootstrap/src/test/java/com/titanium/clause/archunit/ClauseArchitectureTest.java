package com.titanium.clause.archunit;

import org.junit.jupiter.api.Test;

import com.titanium.buildtools.archunit.AbstractArchitectureGuardTest;

/**
 * 条款域架构守护测试：继承共享基类，仅提供本域根包。
 * 全部 DDD 分层/命名/依赖注入规则由 {@link AbstractArchitectureGuardTest} 提供，
 * 规则一处维护、各域复用。
 */
class ClauseArchitectureTest extends AbstractArchitectureGuardTest {

    @Override
    protected String basePackage() {
        return "com.titanium.clause";
    }

    /**
     * 启用「Web 层不得直接依赖领域命令/聚合根」严格隔离规则。
     * <p>
     * 基类默认 {@code @Disabled} 该规则。条款域 Controller 只依赖 api DTO/Web Request+VO 与应用层服务，不直接消费 domain
     * command/aggregate，故在本子类覆盖启用。
     * </p>
     */
    @Test
    @Override
    protected void webShouldNotDependOnDomainCommandsOrAggregates() {
        super.webShouldNotDependOnDomainCommandsOrAggregates();
    }
}
