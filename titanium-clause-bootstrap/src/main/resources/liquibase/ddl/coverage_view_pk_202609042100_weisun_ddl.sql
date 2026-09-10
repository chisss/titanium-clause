--liquibase formatted sql
--changeset weisun:clause-revision-1
-- 修订→新版本机制（dev-514 / 缺口G9）：责任读模型按条款版本隔离。
-- 原主键仅 coverage_id，修订版本继承原条款责任（责任ID复用）时，责任读模型与原条款冲突
-- （更新改写原条款行的 clause_id、移除误删原条款行的责任）。调整为 (coverage_id, clause_id)
-- 复合主键后，原条款与新版本条款各持一行，责任随版本独立增删改。
-- coverage_id 原为唯一主键，故存量数据无重复 (coverage_id, clause_id) 组合，改主键安全。
ALTER TABLE t_coverage_view DROP PRIMARY KEY, ADD PRIMARY KEY (coverage_id, clause_id);
--rollback ALTER TABLE t_coverage_view DROP PRIMARY KEY, ADD PRIMARY KEY (coverage_id);
