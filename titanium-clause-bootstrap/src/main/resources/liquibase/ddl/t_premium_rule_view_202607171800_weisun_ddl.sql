--liquibase formatted sql
--changeset weisun:clause-premium-rule-version-1
-- 为 t_premium_rule_view 添加费率表编码和版本字段，支持多版本费率表管理（BILL-2）
ALTER TABLE t_premium_rule_view
    ADD COLUMN table_code    VARCHAR(64) COMMENT '费率表编码(支持多版本费率表管理,BILL-2)' AFTER rule_set_code,
    ADD COLUMN table_version VARCHAR(32) COMMENT '费率表版本(支持按版本精确查询,BILL-2)' AFTER table_code;
--rollback ALTER TABLE t_premium_rule_view DROP COLUMN table_version, DROP COLUMN table_code;

--changeset weisun:clause-premium-rule-version-2
-- 为费率表版本查询创建索引（支持按 tableCode+version 精确匹配，BILL-2）
CREATE INDEX idx_premium_rule_view_table ON t_premium_rule_view(clause_id, table_code, table_version, tenant_id);
--rollback DROP INDEX idx_premium_rule_view_table ON t_premium_rule_view;
