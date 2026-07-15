--liquibase formatted sql
-- 说明: 寿险四大险种(定期寿/终身寿/两全/年金)责任模板开箱种子数据。
-- 每条责任挂在一个演示条款(clause_id)下，供产品配置时选用；租户 default 为平台级公共模板。
-- 触发类型 trigger_type 取 CoverageTriggerType(DEATH/SURVIVAL)，给付方式 payout_type 取 PayoutType
-- (FIXED 定额给付身故金/满期金，PERIODIC 年金周期给付)。幂等: 主键固定 + INSERT ... 依赖 changeset 一次性执行。
--changeset weisun:clause-life-seed-1
-- 定期寿险·身故责任: 保障期内身故定额给付基本保额
INSERT INTO t_coverage (id, clause_id, coverage_code, coverage_name, trigger_type, payout_type, payout_ratio,
                        waiting_days, description, tenant_id, created_by, updated_by)
VALUES ('seed_cov_term_life_death', 'seed_clause_term_life', 'TERM_LIFE_DEATH', '定期寿险身故保险金',
        'DEATH', 'FIXED', 1.000000, 180, '被保险人于保险期间内身故，按基本保额定额给付身故保险金，合同终止。',
        'default', 'system', 'system');

-- 终身寿险·身故责任: 终身保障，身故定额给付基本保额(含现金价值累积)
INSERT INTO t_coverage (id, clause_id, coverage_code, coverage_name, trigger_type, payout_type, payout_ratio,
                        waiting_days, description, tenant_id, created_by, updated_by)
VALUES ('seed_cov_whole_life_death', 'seed_clause_whole_life', 'WHOLE_LIFE_DEATH', '终身寿险身故保险金',
        'DEATH', 'FIXED', 1.000000, 180, '被保险人身故(终身保障)，按基本保额与现金价值孰高定额给付身故保险金，合同终止。',
        'default', 'system', 'system');

-- 两全保险·满期生存责任: 生存至保险期满定额给付满期金
INSERT INTO t_coverage (id, clause_id, coverage_code, coverage_name, trigger_type, payout_type, payout_ratio,
                        waiting_days, description, tenant_id, created_by, updated_by)
VALUES ('seed_cov_endowment_survival', 'seed_clause_endowment', 'ENDOWMENT_MATURITY', '两全保险满期生存保险金',
        'SURVIVAL', 'FIXED', 1.000000, 0, '被保险人生存至保险期间届满，按基本保额定额给付满期生存保险金，合同终止。',
        'default', 'system', 'system');

-- 两全保险·身故责任: 满期前身故定额给付身故金(两全=生死两全)
INSERT INTO t_coverage (id, clause_id, coverage_code, coverage_name, trigger_type, payout_type, payout_ratio,
                        waiting_days, description, tenant_id, created_by, updated_by)
VALUES ('seed_cov_endowment_death', 'seed_clause_endowment', 'ENDOWMENT_DEATH', '两全保险身故保险金',
        'DEATH', 'FIXED', 1.000000, 180, '被保险人于保险期间内身故，按基本保额定额给付身故保险金，合同终止。',
        'default', 'system', 'system');

-- 年金保险·生存给付责任: 给付期内按频率周期性给付生存年金(不终止保单，逐期推进)
INSERT INTO t_coverage (id, clause_id, coverage_code, coverage_name, trigger_type, payout_type, payout_ratio,
                        waiting_days, description, tenant_id, created_by, updated_by)
VALUES ('seed_cov_annuity_survival', 'seed_clause_annuity', 'ANNUITY_SURVIVAL', '年金保险生存年金',
        'SURVIVAL', 'PERIODIC', 1.000000, 0, '被保险人生存至年金给付日，按约定频率(月/季/半年/年)周期性给付生存年金，给付不终止合同。',
        'default', 'system', 'system');
--rollback DELETE FROM t_coverage WHERE id IN ('seed_cov_term_life_death','seed_cov_whole_life_death','seed_cov_endowment_survival','seed_cov_endowment_death','seed_cov_annuity_survival');
