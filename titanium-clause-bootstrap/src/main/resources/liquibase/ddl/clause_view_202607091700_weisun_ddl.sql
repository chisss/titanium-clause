--liquibase formatted sql
--changeset weisun:clause-1
CREATE TABLE IF NOT EXISTS t_clause_view (
    clause_id        VARCHAR(36)  NOT NULL COMMENT '条款ID(聚合根ID,读模型主键)',
    clause_code      VARCHAR(64)  COMMENT '条款代码',
    clause_name      VARCHAR(256) COMMENT '条款名称',
    clause_type      VARCHAR(50)  COMMENT '条款类型(ClauseType)',
    content          TEXT         COMMENT '条款内容',
    description      TEXT         COMMENT '条款描述',
    status           VARCHAR(30)  COMMENT '条款状态(ClauseStatus)',
    clause_version   VARCHAR(20)  COMMENT '版本号',
    insurance_type   VARCHAR(50)  COMMENT '险种类型',
    parent_clause_id VARCHAR(36)  COMMENT '父条款ID',
    effective_date   DATETIME     COMMENT '生效日期',
    expiry_date      DATETIME     COMMENT '失效日期',
    created_by       VARCHAR(32)  COMMENT '创建人',
    updated_by       VARCHAR(32)  COMMENT '更新人',
    tenant_id        VARCHAR(32)  NOT NULL COMMENT '租户ID',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投影创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '投影更新时间',
    version          BIGINT       COMMENT '乐观锁版本(防并发投影覆盖)',
    PRIMARY KEY (clause_id),
    KEY idx_clause_view_tenant (tenant_id),
    KEY idx_clause_view_code (clause_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='条款读模型(CQRS Projection)';
--rollback DROP TABLE IF EXISTS t_clause_view;

--changeset weisun:clause-2
CREATE TABLE IF NOT EXISTS t_coverage_view (
    coverage_id      VARCHAR(36)  NOT NULL COMMENT '责任ID(聚合内唯一,读模型主键)',
    clause_id        VARCHAR(36)  NOT NULL COMMENT '所属条款ID',
    coverage_code    VARCHAR(64)  COMMENT '责任编码',
    coverage_name    VARCHAR(256) COMMENT '责任名称',
    coverage_type    VARCHAR(50)  COMMENT '责任类型(CoverageType:重疾/医疗/意外/身故)',
    coverage_amount  DECIMAL(18,2) COMMENT '责任保额',
    description      TEXT         COMMENT '责任描述',
    trigger_type     VARCHAR(50)  COMMENT '赔付触发类型(CoverageTriggerType)',
    payout_type      VARCHAR(50)  COMMENT '赔付类型(PayoutType)',
    is_additional    TINYINT(1)   COMMENT '是否附加责任(0:否,1:是)',
    main_coverage_id VARCHAR(36)  COMMENT '关联的主险责任ID',
    trigger_json     TEXT         COMMENT '结构化赔付触发条件(CoverageTrigger JSON)',
    payout_rule_json TEXT         COMMENT '结构化赔付规则(PayoutRule JSON)',
    tenant_id        VARCHAR(32)  NOT NULL COMMENT '租户ID',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投影创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '投影更新时间',
    version          BIGINT       COMMENT '乐观锁版本(防并发投影覆盖)',
    PRIMARY KEY (coverage_id),
    KEY idx_coverage_view_tenant (tenant_id),
    KEY idx_coverage_view_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保险责任读模型(CQRS Projection)';
--rollback DROP TABLE IF EXISTS t_coverage_view;

--changeset weisun:clause-3
CREATE TABLE IF NOT EXISTS t_premium_rule_view (
    clause_id                    VARCHAR(36)  NOT NULL COMMENT '所属条款ID(一条款一费率规则,读模型主键)',
    calculation_method           VARCHAR(50)  COMMENT '保费计算方式(固定金额/费率计算)',
    base_premium                 DECIMAL(18,2) COMMENT '基础保费(固定金额时使用)',
    premium_rate                 DECIMAL(12,6) COMMENT '单一费率标量(历史字段,向后兼容)',
    payment_method               VARCHAR(50)  COMMENT '缴费方式(趸交/年缴/月缴)',
    payment_term                 INT          COMMENT '缴费年限',
    grace_period_days            INT          COMMENT '宽限期天数',
    base_rate                    DECIMAL(12,6) COMMENT '基础费率(结构化模型基准)',
    ncd_coefficient              DECIMAL(12,6) COMMENT '无赔款优待系数(NCD,车险)',
    rule_set_code                VARCHAR(64)  COMMENT '规则引擎规则集编码',
    age_gender_rates_json        TEXT         COMMENT '四维年龄性别费率表(List<AgeGenderRate> JSON:年龄×性别×缴费期×保障期)',
    occupation_coefficients_json TEXT         COMMENT '职业系数表(Map<String,BigDecimal> JSON)',
    tenant_id                    VARCHAR(32)  NOT NULL COMMENT '租户ID',
    create_time                  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投影创建时间',
    update_time                  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '投影更新时间',
    version                      BIGINT       COMMENT '乐观锁版本(防并发投影覆盖)',
    PRIMARY KEY (clause_id),
    KEY idx_premium_rule_view_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费规则读模型(CQRS Projection)';
--rollback DROP TABLE IF EXISTS t_premium_rule_view;
