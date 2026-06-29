package com.titanium.clause.archunit;

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
}
