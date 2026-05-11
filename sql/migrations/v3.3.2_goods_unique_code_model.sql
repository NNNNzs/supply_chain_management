-- v3.3.2 货物唯一索引改为编码+型号联合唯一
-- 原因：同一货物编码下可能有多种型号（如 XC 线材有 SCM435、SWRCH10A 等）
-- 执行时间：2026-05-11

-- 先检查是否已有 goods_model 为 NULL 的重复 goods_code 记录
-- 对于 goods_model 为 NULL 的记录，MySQL 唯一索引允许 NULL 值
-- 所以 NULL 和 NULL 不算重复，这里需要把 NULL 统一为空字符串
UPDATE logistics_goods SET goods_model = '' WHERE goods_model IS NULL;

-- 修改 goods_model 列为 NOT NULL DEFAULT ''
ALTER TABLE logistics_goods MODIFY COLUMN goods_model VARCHAR(100) NOT NULL DEFAULT '' COMMENT '货物型号';

-- 删除旧的唯一索引，创建新的联合唯一索引
ALTER TABLE logistics_goods DROP INDEX uk_goods_code,
ADD UNIQUE KEY uk_goods_code_model (goods_code, goods_model);

-- 记录版本
INSERT INTO db_version (version, description) VALUES ('v3.3.2', '货物唯一索引改为编码+型号联合唯一');
