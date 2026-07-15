--liquibase formatted sql
-- 说明: 条款配置表(责任/免除/费率/规则/审批)为非事件溯源 CRUD 配置, 依方案清单 §3.3 表名与核心字段 + 七件套建表
--changeset weisun:clause-init-1
CREATE TABLE IF NOT EXISTS t_coverage (
    id            VARCHAR(32)   NOT NULL COMMENT '主键(雪花)',
    clause_id     VARCHAR(36)   NOT NULL COMMENT '所属条款ID',
    coverage_code VARCHAR(50)   COMMENT '责任代码',
    coverage_name VARCHAR(128)  COMMENT '责任名称',
    trigger_type  VARCHAR(32)   COMMENT '给付触发类型code(DEATH/SURVIVAL等)',
    payout_type   VARCHAR(32)   COMMENT '给付方式code(FIXED等)',
    payout_ratio  DECIMAL(18,6) COMMENT '给付比例',
    waiting_days  INT           COMMENT '等待期天数',
    description   TEXT          COMMENT '责任描述',
    tenant_id     VARCHAR(32)   NOT NULL COMMENT '租户ID',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by    VARCHAR(32)   NOT NULL DEFAULT 'system' COMMENT '创建人',
    updated_by    VARCHAR(32)   NOT NULL DEFAULT 'system' COMMENT '更新人',
    is_deleted    TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是)',
    PRIMARY KEY (id),
    KEY idx_coverage_tenant (tenant_id),
    KEY idx_coverage_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保险责任';
--rollback DROP TABLE IF EXISTS t_coverage;

--changeset weisun:clause-init-2
CREATE TABLE IF NOT EXISTS t_exclusion (
    id                VARCHAR(32)  NOT NULL COMMENT '主键(雪花)',
    clause_id         VARCHAR(36)  NOT NULL COMMENT '所属条款ID',
    exclusion_content TEXT         COMMENT '责任免除内容',
    tenant_id         VARCHAR(32)  NOT NULL COMMENT '租户ID',
    create_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by        VARCHAR(32)  NOT NULL DEFAULT 'system' COMMENT '创建人',
    updated_by        VARCHAR(32)  NOT NULL DEFAULT 'system' COMMENT '更新人',
    is_deleted        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是)',
    PRIMARY KEY (id),
    KEY idx_exclusion_tenant (tenant_id),
    KEY idx_exclusion_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='责任免除';
--rollback DROP TABLE IF EXISTS t_exclusion;

--changeset weisun:clause-init-3
CREATE TABLE IF NOT EXISTS t_age_gender_rate (
    id            VARCHAR(32)   NOT NULL COMMENT '主键(雪花)',
    clause_id     VARCHAR(36)   NOT NULL COMMENT '所属条款ID',
    age           INT           COMMENT '年龄',
    gender        VARCHAR(16)   COMMENT '性别code',
    rate          DECIMAL(18,6) COMMENT '费率',
    payment_term  VARCHAR(32)   COMMENT '缴费期',
    coverage_term VARCHAR(32)   COMMENT '保障期',
    tenant_id     VARCHAR(32)   NOT NULL COMMENT '租户ID',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by    VARCHAR(32)   NOT NULL DEFAULT 'system' COMMENT '创建人',
    updated_by    VARCHAR(32)   NOT NULL DEFAULT 'system' COMMENT '更新人',
    is_deleted    TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是)',
    PRIMARY KEY (id),
    KEY idx_age_gender_rate_tenant (tenant_id),
    KEY idx_age_gender_rate_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年龄性别费率表';
--rollback DROP TABLE IF EXISTS t_age_gender_rate;

--changeset weisun:clause-init-4
CREATE TABLE IF NOT EXISTS t_premium_rule (
    id          VARCHAR(32) NOT NULL COMMENT '主键(雪花)',
    clause_id   VARCHAR(36) NOT NULL COMMENT '所属条款ID',
    rule_json   TEXT        COMMENT '保费规则(JSON)',
    tenant_id   VARCHAR(32) NOT NULL COMMENT '租户ID',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by  VARCHAR(32) NOT NULL DEFAULT 'system' COMMENT '创建人',
    updated_by  VARCHAR(32) NOT NULL DEFAULT 'system' COMMENT '更新人',
    is_deleted  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是)',
    PRIMARY KEY (id),
    KEY idx_premium_rule_tenant (tenant_id),
    KEY idx_premium_rule_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保费规则';
--rollback DROP TABLE IF EXISTS t_premium_rule;

--changeset weisun:clause-init-5
CREATE TABLE IF NOT EXISTS t_claim_rule (
    id          VARCHAR(32) NOT NULL COMMENT '主键(雪花)',
    clause_id   VARCHAR(36) NOT NULL COMMENT '所属条款ID',
    rule_json   TEXT        COMMENT '理赔规则(JSON)',
    tenant_id   VARCHAR(32) NOT NULL COMMENT '租户ID',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by  VARCHAR(32) NOT NULL DEFAULT 'system' COMMENT '创建人',
    updated_by  VARCHAR(32) NOT NULL DEFAULT 'system' COMMENT '更新人',
    is_deleted  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是)',
    PRIMARY KEY (id),
    KEY idx_claim_rule_tenant (tenant_id),
    KEY idx_claim_rule_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='理赔规则';
--rollback DROP TABLE IF EXISTS t_claim_rule;

--changeset weisun:clause-init-6
CREATE TABLE IF NOT EXISTS t_contract_change_rule (
    id          VARCHAR(32) NOT NULL COMMENT '主键(雪花)',
    clause_id   VARCHAR(36) NOT NULL COMMENT '所属条款ID',
    rule_json   TEXT        COMMENT '合同变更规则(JSON)',
    tenant_id   VARCHAR(32) NOT NULL COMMENT '租户ID',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by  VARCHAR(32) NOT NULL DEFAULT 'system' COMMENT '创建人',
    updated_by  VARCHAR(32) NOT NULL DEFAULT 'system' COMMENT '更新人',
    is_deleted  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是)',
    PRIMARY KEY (id),
    KEY idx_contract_change_rule_tenant (tenant_id),
    KEY idx_contract_change_rule_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同变更规则';
--rollback DROP TABLE IF EXISTS t_contract_change_rule;

--changeset weisun:clause-init-7
CREATE TABLE IF NOT EXISTS t_clause_approval_record (
    id          VARCHAR(32)  NOT NULL COMMENT '主键(雪花)',
    clause_id   VARCHAR(36)  NOT NULL COMMENT '所属条款ID',
    approver    VARCHAR(64)  COMMENT '审批人',
    result      VARCHAR(32)  COMMENT '审批结果code',
    opinion     TEXT         COMMENT '审批意见',
    tenant_id   VARCHAR(32)  NOT NULL COMMENT '租户ID',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by  VARCHAR(32)  NOT NULL DEFAULT 'system' COMMENT '创建人',
    updated_by  VARCHAR(32)  NOT NULL DEFAULT 'system' COMMENT '更新人',
    is_deleted  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是)',
    PRIMARY KEY (id),
    KEY idx_clause_approval_tenant (tenant_id),
    KEY idx_clause_approval_clause (clause_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='条款审批记录';
--rollback DROP TABLE IF EXISTS t_clause_approval_record;
