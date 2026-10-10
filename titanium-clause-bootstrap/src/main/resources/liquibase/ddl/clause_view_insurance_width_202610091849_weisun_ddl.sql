--liquibase formatted sql
--changeset weisun:clause-view-insurance-width-20261009
-- G10/g10-02：列宽统一。三级险种码最长 PROFESSIONAL_LIABILITY=23 字符，VARCHAR(32) 足够；
-- 本列 50 为冗余宽度，收敛至 32 并补齐语义注释（值为 metadata InsuranceProductType 三级码）。
-- ⚠ 收窄前置：库内实际值须 ≤32 字符（由 g10-03 对账 SQL ① 验证；若存在超长脏值须先回滚本 changeset）。
ALTER TABLE t_clause_view
    MODIFY COLUMN insurance_type VARCHAR(32) NULL COMMENT '险种类型(权威三级码, metadata InsuranceProductType)';
--rollback ALTER TABLE t_clause_view MODIFY COLUMN insurance_type VARCHAR(50) NULL COMMENT '险种类型';
