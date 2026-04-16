-- =====================================================
-- 物流管理模块 v3.2.0 - 修改订单表字段为可选
-- 说明：现在使用货物明细列表，主订单的重量/单价/金额字段改为可选
-- 日期：2026-04-16
-- =====================================================

-- 记录迁移版本
INSERT INTO db_version (version, description, executed_at)
VALUES ('v3.2.0', '修改订单表字段为可选，支持货物明细模式', NOW())
ON DUPLICATE KEY UPDATE executed_at = NOW();

-- 修改 logistics_order 表，将汇总字段改为可选
ALTER TABLE logistics_order
MODIFY COLUMN weight DECIMAL(10,3) DEFAULT NULL COMMENT '重量（吨）- 已废弃，使用货物明细汇总';

ALTER TABLE logistics_order
MODIFY COLUMN unit_price DECIMAL(10,2) DEFAULT NULL COMMENT '运价（元/吨）- 已废弃，使用货物明细汇总';

ALTER TABLE logistics_order
MODIFY COLUMN total_amount DECIMAL(10,2) DEFAULT NULL COMMENT '总金额- 从货物明细汇总计算';

-- 将 goods_id, goods_name, goods_model 也改为可选（使用货物明细后不需要）
ALTER TABLE logistics_order
MODIFY COLUMN goods_id BIGINT(20) DEFAULT NULL COMMENT '货物ID- 已废弃，使用货物明细';

ALTER TABLE logistics_order
MODIFY COLUMN goods_name VARCHAR(100) DEFAULT NULL COMMENT '货物名称- 已废弃，使用货物明细';

ALTER TABLE logistics_order
MODIFY COLUMN goods_model VARCHAR(100) DEFAULT NULL COMMENT '货物型号- 已废弃，使用货物明细';
