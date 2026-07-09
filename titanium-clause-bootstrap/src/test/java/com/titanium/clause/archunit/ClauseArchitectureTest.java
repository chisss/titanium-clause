package com.titanium.clause.archunit;

import org.junit.jupiter.api.Test;

import com.titanium.buildtools.archunit.AbstractArchitectureGuardTest;

/**
 * 条款域架构守护测试：继承共享基类，仅提供本域根包。
 * 全部 DDD 分层/命名/依赖注入规则由 {@link AbstractArchitectureGuardTest} 提供，
 * 规则一处维护、各域复用，杜绝测试代码复制粘贴漂移。
 */
class ClauseArchitectureTest extends AbstractArchitectureGuardTest {

    @Override
    protected String basePackage() {
        return "com.titanium.clause";
    }

    /**
     * 启用「application 层不得依赖 api 的 DTO」。
     * <p>
     * 条款域 api/web 已按《API层与Web层职责边界及协作规范》整改：DTO→应用层入参的翻译在 web/provider 完成，
     * {@code ClauseApplicationService} 只依赖标量入参与领域命令，不依赖 {@code clause.api} 契约细节。
     * </p>
     */
    @Test
    @Override
    protected void applicationMustNotDependOnApiDto() {
        super.applicationMustNotDependOnApiDto();
    }

    /**
     * 启用「API 契约实现（Provider）须位于 web.provider 且以 Provider 结尾」。
     * <p>
     * 条款域契约实现为 {@code ClauseApiProvider}，统一落在 web/provider。
     * </p>
     */
    @Test
    @Override
    protected void apiContractImplMustResideInProviderPackage() {
        super.apiContractImplMustResideInProviderPackage();
    }

    /**
     * 启用「Controller 不得实现 api 契约接口」。
     * <p>
     * {@code ClauseController} 已去掉 {@code implements ClauseApi}，契约实现下沉 web/provider 的 Provider。
     * </p>
     */
    @Test
    @Override
    protected void controllerMustNotImplementApi() {
        super.controllerMustNotImplementApi();
    }

    /**
     * 启用「api 层 Feign 契约接口须以 Api 结尾（命名主键为聚合根）」。
     * <p>
     * 条款域契约统一为 {@code ClauseApi}，原 {@code ClauseClient}（Client 后缀）冗余已重命名。
     * </p>
     */
    @Test
    @Override
    protected void apiInterfacesMustBeNamedByAggregate() {
        super.apiInterfacesMustBeNamedByAggregate();
    }

    // 注：不启用严格隔离断言 webShouldNotDependOnDomainCommandsOrAggregates。
    // 现行 api/web 规范允许 web 依赖 command/query（但不碰 aggregate），故回退为基类默认 @Disabled。
}
