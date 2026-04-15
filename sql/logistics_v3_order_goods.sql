-- 物流管理系统 v3.0 - 订单货物明细表
-- 功能：支持一个订单包含多个货物明细
-- 日期：2026-04-15

-- 1. 创建订单货物明细表
DROP TABLE IF EXISTS logistics_order_goods;
CREATE TABLE logistics_order_goods (
  detail_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '明细ID',
  order_id        bigint(20)      NOT NULL                   COMMENT '订单ID',
  goods_id        bigint(20)      NOT NULL                   COMMENT '货物ID',
  goods_name      varchar(100)    NOT NULL                   COMMENT '货物名称（冗余字段）',
  goods_model     varchar(100)    DEFAULT NULL               COMMENT '货物型号',
  goods_unit      varchar(20)     DEFAULT NULL               COMMENT '计量单位',
  weight          decimal(10,2)   NOT NULL                   COMMENT '重量（吨）',
  unit_price      decimal(10,2)   NOT NULL                   COMMENT '单价（元/吨）',
  amount          decimal(10,2)   NOT NULL                   COMMENT '金额（元）',
  remark          varchar(500)    DEFAULT NULL               COMMENT '备注',
  del_flag        char(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by       varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     datetime                                    COMMENT '创建时间',
  update_by       varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     datetime                                    COMMENT '更新时间',
  PRIMARY KEY (detail_id),
  KEY idx_order_id (order_id),
  KEY idx_goods_id (goods_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='订单货物明细表';

-- 2. 为订单表添加实际装车重量字段（如果没有的话）
-- 检查字段是否存在，不存在则添加
SET @column_exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
    AND table_name = 'logistics_order'
    AND column_name = 'actual_load_weight'
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE logistics_order ADD COLUMN actual_load_weight decimal(10,2) DEFAULT NULL COMMENT ''实际装车重量（吨）'' AFTER total_weight',
    'SELECT ''Column already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 修改订单表移除提单相关字段（可选，根据实际需求）
-- 注意：这里使用 ALTER TABLE ... DROP COLUMN 需要谨慎操作
-- 建议先备份数据，确认不再需要提单关联后再执行

-- ALTER TABLE logistics_order DROP COLUMN bill_id;
-- ALTER TABLE logistics_order DROP COLUMN bill_detail_id;

-- 4. 添加索引优化查询
ALTER TABLE logistics_order_goods ADD INDEX idx_order_goods (order_id, goods_id);
