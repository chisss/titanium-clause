-- liquibase formatted sql

-- changeset sunwei:20260113_create_insurance_product_tables

-- 创建保险产品表
CREATE TABLE IF NOT EXISTS t_insurance_product (
    id VARCHAR(32) PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_type VARCHAR(32) NOT NULL,
    product_class VARCHAR(32) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL,
    main_product INT NOT NULL DEFAULT 0 COMMENT '是否主险: 0-非主险, 1-主险',
    currency VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    grace_period INT COMMENT '宽限期(天)',
    free_look_period INT COMMENT '犹豫期(天)',
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    created_by VARCHAR(32) NOT NULL,
    updated_by VARCHAR(32) NOT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

-- 创建保险产品与条款的关联表
CREATE TABLE IF NOT EXISTS t_insurance_product_clause (
    product_id VARCHAR(32) NOT NULL,
    clause_id VARCHAR(32) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES t_insurance_product(id) ON DELETE CASCADE,
    FOREIGN KEY (clause_id) REFERENCES t_clause(id) ON DELETE CASCADE
);

-- 创建责任表
CREATE TABLE IF NOT EXISTS t_insurance_liability (
    id VARCHAR(32) PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    coverage DECIMAL(18,2) COMMENT '保额',
    premium_rate DECIMAL(18,6) COMMENT '费率',
    description TEXT,
    status VARCHAR(32) NOT NULL,
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    created_by VARCHAR(32) NOT NULL,
    updated_by VARCHAR(32) NOT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

-- 创建保险产品与责任的关联表
CREATE TABLE IF NOT EXISTS t_insurance_product_liability (
    product_id VARCHAR(32) NOT NULL,
    liability_id VARCHAR(32) NOT NULL,
    coverage DECIMAL(18,2) COMMENT '保额',
    premium_rate DECIMAL(18,6) COMMENT '费率',
    FOREIGN KEY (product_id) REFERENCES t_insurance_product(id) ON DELETE CASCADE,
    FOREIGN KEY (liability_id) REFERENCES t_insurance_liability(id) ON DELETE CASCADE
);

-- 创建责任免除表
CREATE TABLE IF NOT EXISTS t_insurance_exclusion (
    id VARCHAR(32) PRIMARY KEY,
    type VARCHAR(32) NOT NULL COMMENT '免责类型',
    exclusion_rule_code VARCHAR(50) NOT NULL COMMENT '免责规则代码',
    is_mandatory BOOLEAN NOT NULL DEFAULT false COMMENT '是否法定免责',
    description TEXT,
    status VARCHAR(32) NOT NULL,
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    created_by VARCHAR(32) NOT NULL,
    updated_by VARCHAR(32) NOT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

-- 创建条款与责任关联表
CREATE TABLE IF NOT EXISTS t_clause_liability (
    clause_id VARCHAR(32) NOT NULL,
    liability_id VARCHAR(32) NOT NULL,
    FOREIGN KEY (clause_id) REFERENCES t_clause(id) ON DELETE CASCADE,
    FOREIGN KEY (liability_id) REFERENCES t_insurance_liability(id) ON DELETE CASCADE
);

-- 创建条款与责任免除关联表
CREATE TABLE IF NOT EXISTS t_clause_exclusion (
    clause_id VARCHAR(32) NOT NULL,
    exclusion_id VARCHAR(32) NOT NULL,
    FOREIGN KEY (clause_id) REFERENCES t_clause(id) ON DELETE CASCADE,
    FOREIGN KEY (exclusion_id) REFERENCES t_insurance_exclusion(id) ON DELETE CASCADE
);

-- 创建缴费规则表
CREATE TABLE IF NOT EXISTS t_premium_rule (
    id VARCHAR(32) PRIMARY KEY,
    calculation_method VARCHAR(32) NOT NULL COMMENT '计算方式',
    base_premium DECIMAL(18,2) COMMENT '基础保费',
    premium_rate DECIMAL(18,6) COMMENT '费率',
    payment_method VARCHAR(32) NOT NULL COMMENT '缴费方式',
    payment_term INT COMMENT '缴费年限',
    grace_period_days INT COMMENT '宽限期天数',
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    created_by VARCHAR(32) NOT NULL,
    updated_by VARCHAR(32) NOT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

-- 创建理赔规则表
CREATE TABLE IF NOT EXISTS t_claim_rule (
    id VARCHAR(32) PRIMARY KEY,
    report_deadline_days INT COMMENT '报案时效天数',
    required_materials TEXT COMMENT '理赔所需材料',
    settlement_period_days INT COMMENT '理赔结案时效天数',
    payout_ratio VARCHAR(50) COMMENT '赔付比例',
    deductible_amount VARCHAR(50) COMMENT '免赔额',
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    created_by VARCHAR(32) NOT NULL,
    updated_by VARCHAR(32) NOT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

-- 创建合同变更规则表
CREATE TABLE IF NOT EXISTS t_contract_change_rule (
    id VARCHAR(32) PRIMARY KEY,
    surrender_cash_value_rule TEXT COMMENT '退保现金价值规则',
    renewal_type VARCHAR(32) NOT NULL COMMENT '续保类型',
    reinstatement_condition TEXT COMMENT '复效条件',
    waiting_period_days INT COMMENT '等待期天数',
    free_look_period_days INT COMMENT '犹豫期天数',
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    created_by VARCHAR(32) NOT NULL,
    updated_by VARCHAR(32) NOT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

-- 创建条款与缴费规则关联表
CREATE TABLE IF NOT EXISTS t_clause_premium_rule (
    clause_id VARCHAR(32) PRIMARY KEY,
    premium_rule_id VARCHAR(32) NOT NULL,
    FOREIGN KEY (clause_id) REFERENCES t_clause(id) ON DELETE CASCADE,
    FOREIGN KEY (premium_rule_id) REFERENCES t_premium_rule(id) ON DELETE CASCADE
);

-- 创建条款与理赔规则关联表
CREATE TABLE IF NOT EXISTS t_clause_claim_rule (
    clause_id VARCHAR(32) PRIMARY KEY,
    claim_rule_id VARCHAR(32) NOT NULL,
    FOREIGN KEY (clause_id) REFERENCES t_clause(id) ON DELETE CASCADE,
    FOREIGN KEY (claim_rule_id) REFERENCES t_claim_rule(id) ON DELETE CASCADE
);

-- 创建条款与合同变更规则关联表
CREATE TABLE IF NOT EXISTS t_clause_contract_change_rule (
    clause_id VARCHAR(32) PRIMARY KEY,
    contract_change_rule_id VARCHAR(32) NOT NULL,
    FOREIGN KEY (clause_id) REFERENCES t_clause(id) ON DELETE CASCADE,
    FOREIGN KEY (contract_change_rule_id) REFERENCES t_contract_change_rule(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_product_code ON t_insurance_product(product_code);
CREATE INDEX idx_status ON t_insurance_product(status);
CREATE INDEX idx_product_type ON t_insurance_product(product_type);
CREATE INDEX idx_tenant_id ON t_insurance_product(tenant_id);
CREATE INDEX idx_main_product ON t_insurance_product(main_product);
CREATE INDEX idx_product_id ON t_insurance_product_clause(product_id);
CREATE INDEX idx_clause_id ON t_insurance_product_clause(clause_id);
CREATE INDEX idx_liability_code ON t_insurance_liability(code);
CREATE INDEX idx_liability_status ON t_insurance_liability(status);
CREATE INDEX idx_liability_tenant_id ON t_insurance_liability(tenant_id);
CREATE INDEX idx_prod_liab_product_id ON t_insurance_product_liability(product_id);
CREATE INDEX idx_prod_liab_liability_id ON t_insurance_product_liability(liability_id);
CREATE INDEX idx_exclusion_type ON t_insurance_exclusion(type);
CREATE INDEX idx_exclusion_rule_code ON t_insurance_exclusion(exclusion_rule_code);
CREATE INDEX idx_exclusion_status ON t_insurance_exclusion(status);
CREATE INDEX idx_exclusion_tenant_id ON t_insurance_exclusion(tenant_id);
CREATE INDEX idx_clause_liability_clause_id ON t_clause_liability(clause_id);
CREATE INDEX idx_clause_liability_liability_id ON t_clause_liability(liability_id);
CREATE INDEX idx_clause_exclusion_clause_id ON t_clause_exclusion(clause_id);
CREATE INDEX idx_clause_exclusion_exclusion_id ON t_clause_exclusion(exclusion_id);

-- rollback DROP TABLE IF EXISTS t_clause_contract_change_rule;
-- rollback DROP TABLE IF EXISTS t_clause_claim_rule;
-- rollback DROP TABLE IF EXISTS t_clause_premium_rule;
-- rollback DROP TABLE IF EXISTS t_contract_change_rule;
-- rollback DROP TABLE IF EXISTS t_claim_rule;
-- rollback DROP TABLE IF EXISTS t_premium_rule;
-- rollback DROP TABLE IF EXISTS t_clause_exclusion;
-- rollback DROP TABLE IF EXISTS t_clause_liability;
-- rollback DROP TABLE IF EXISTS t_insurance_exclusion;
-- rollback DROP TABLE IF EXISTS t_insurance_product_liability;
-- rollback DROP TABLE IF EXISTS t_insurance_liability;
-- rollback DROP TABLE IF EXISTS t_insurance_product_clause;
-- rollback DROP TABLE IF EXISTS t_insurance_product;
