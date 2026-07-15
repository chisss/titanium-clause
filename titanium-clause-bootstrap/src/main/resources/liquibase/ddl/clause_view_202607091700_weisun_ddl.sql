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
