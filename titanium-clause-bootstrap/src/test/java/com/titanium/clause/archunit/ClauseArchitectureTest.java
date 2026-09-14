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

    /**
     * 启用「api 层使用 Request/Response 而非 DTO」（2026-07-19 命名新规）。
     * <p>
     * 条款域 api 层已弃用 DTO：写入参 {@code CreateClauseRequest}/{@code UpdateClauseRequest} 等落
     * {@code clause.api.request}，读出参 {@code ClauseResponse}/{@code PremiumRuleResponse}/{@code AgeGenderRateResponse}
     * 落 {@code clause.api.response}，api 层无 DTO 后缀类型。
     * </p>
     */
    @Test
    @Override
    protected void apiLayerUsesRequestResponseNotDto() {
        super.apiLayerUsesRequestResponseNotDto();
    }

    /**
     * 启用「web 层使用 DTO/VO 而非 Request/Response」（2026-07-19 命名新规）。
     * <p>
     * 条款域 web 层前端入参已改名 {@code CreateClauseDTO}/{@code UpdateClauseDTO} 等落 {@code clause.web.dto}，
     * 出参 {@code ClauseVO} 用 VO，web 层无 Request/Response 后缀类型。
     * </p>
     */
    @Test
    @Override
    protected void webLayerUsesDtoVoNotRequest() {
        super.webLayerUsesDtoVoNotRequest();
    }

    /**
     * 启用「web.dto 按业务主题拆子包、顶层清零」（分包规则·批次 2）。
     * <p>
     * 条款域 web.dto 已按业务主题三分：{@code dto.lifecycle}（创建 / 更新 / 激活 / 停用 / 修订
     * 五件套，条款内容与状态维护）、{@code dto.approval}（提交审批 / 审批通过 / 驳回 / 归档，
     * 审批闭环）、{@code dto.coverage}（保险责任配置，条款配置页「保险责任」编辑区），顶层零类。
     * </p>
     */
    @Test
    @Override
    protected void webDtoShouldNotContainFlatClasses() {
        super.webDtoShouldNotContainFlatClasses();
    }

    /**
     * 启用「api.response 按业务主题拆子包、顶层清零」（分包规则·批次 2）。
     * <p>
     * 条款域 api.response 已按业务主题二分：{@code response.clause}（条款主体 / 保险责任契约）
     * 与 {@code response.rate}（年龄性别费率 / 缴费规则契约），顶层零类。
     * </p>
     * <p>
     * 🔴 <b>跨域引用面</b>：{@code response.clause.ClauseResponse} 与
     * {@code response.clause.CoverageResponse} 被 claim（{@code ClauseServiceAdapter}）与
     * policy（{@code ClauseServiceAdapter}）import，须先 install 本域 api 再编译下游。
     * </p>
     */
    @Test
    @Override
    protected void apiResponseShouldNotContainFlatClasses() {
        super.apiResponseShouldNotContainFlatClasses();
    }

    // 注：域内其余契约/门面包（web.controller 1、web.response 2 类）顶层 ≤2 类，api.request 4 类
    // （创建 / 更新 / 激活 / 停用）为「条款生命周期维护」单一业务主题集中包，
    // 均按《包结构分包规范与执行方案-2026-09》§一判据表豁免，不启用对应断言。

    // 注：不启用严格隔离断言 webShouldNotDependOnDomainCommandsOrAggregates。
    // 现行 api/web 规范允许 web 依赖 command/query（但不碰 aggregate），故回退为基类默认 @Disabled。
}
