--liquibase formatted sql
--changeset weisun:clause-no-1
ALTER TABLE t_clause_view ADD COLUMN clause_no VARCHAR(32) NULL COMMENT '条款号（系统生成）';
CREATE UNIQUE INDEX uk_clause_view_no ON t_clause_view (tenant_id, clause_no);
--rollback DROP INDEX uk_clause_view_no ON t_clause_view;
--rollback ALTER TABLE t_clause_view DROP COLUMN clause_no;
