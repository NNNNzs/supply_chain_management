-- v3.3.1 订单货物新增规格字段
-- 在货物型号(goods_model)后新增 goods_specification 字段

ALTER TABLE logistics_order_goods
    ADD COLUMN goods_specification VARCHAR(200) DEFAULT NULL COMMENT '货物规格' AFTER goods_model;

-- 记录版本
INSERT INTO db_version (version, description, executed_at)
VALUES ('v3.3.1', '订单货物新增规格字段', NOW());
