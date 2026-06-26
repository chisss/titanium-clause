-- liquibase formatted sql

-- changeset sunwei:20260211_clause_domain_refactor

-- ① t_clause表：新增parent_clause_id和description字段
ALTER TABLE t_clause ADD COLUMN parent_clause_id VARCHAR(32) COMMENT '父条款ID(修订来源)';
ALTER TABLE t_clause ADD COLUMN description TEXT COMMENT '条款描述';
CREATE INDEX idx_parent_clause ON t_clause(parent_clause_id);

-- ①.1 t_clause表：新增clause_type字段（枚举化改造：条款类型 MAIN/ADDITIONAL/EXCLUSION，
--      与险种类型 insurance_type 区分；此前条款类型被错误复用 insurance_type 列存储）
ALTER TABLE t_clause ADD COLUMN clause_type VARCHAR(32) COMMENT '条款类型 MAIN/ADDITIONAL/EXCLUSION';
CREATE INDEX idx_clause_type ON t_clause(clause_type);

-- ② 审批记录表（新建）
CREATE TABLE IF NOT EXISTS t_clause_approval_record (
    id VARCHAR(32) PRIMARY KEY,
    clause_id VARCHAR(32) NOT NULL,
    approval_type VARCHAR(32) NOT NULL COMMENT 'LEGAL/ACTUARIAL/MANAGEMENT',
    approver_id VARCHAR(32) NOT NULL,
    approver_name VARCHAR(100),
    approval_status VARCHAR(32) NOT NULL COMMENT 'PENDING/APPROVED/REJECTED',
    approval_comment TEXT,
    approval_time DATETIME,
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (clause_id) REFERENCES t_clause(id)
);
CREATE INDEX idx_approval_clause ON t_clause_approval_record(clause_id);

-- ③ 条款快照表（新建，供Policy域保单生效冻结使用）
CREATE TABLE IF NOT EXISTS t_clause_snapshot (
    id VARCHAR(32) PRIMARY KEY,
    clause_id VARCHAR(32) NOT NULL,
    clause_version VARCHAR(20) NOT NULL,
    snapshot_content JSON NOT NULL COMMENT '条款完整快照',
    business_id VARCHAR(32) COMMENT '关联业务单号(保单号)',
    business_type VARCHAR(32) COMMENT 'POLICY/ENDORSEMENT',
    tenant_id VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL
);
CREATE INDEX idx_snapshot_clause ON t_clause_snapshot(clause_id, clause_version);
CREATE INDEX idx_snapshot_business ON t_clause_snapshot(business_id);

-- rollback ALTER TABLE t_clause DROP COLUMN parent_clause_id;
-- rollback ALTER TABLE t_clause DROP COLUMN description;
-- rollback ALTER TABLE t_clause DROP COLUMN clause_type;
-- rollback DROP INDEX idx_clause_type ON t_clause;
-- rollback DROP INDEX idx_parent_clause ON t_clause;
-- rollback DROP TABLE IF EXISTS t_clause_snapshot;
-- rollback DROP TABLE IF EXISTS t_clause_approval_record;
