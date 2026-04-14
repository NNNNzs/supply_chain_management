-- =============================
-- 配载管理模块完整脚本
-- 版本: v3.0
-- 说明: 新增提单配载管理功能，支持提单拆分和配载
-- 执行方式: 在Navicat中执行整个脚本
-- =============================

-- =============================
-- 1、提单表(客户委托单据)
-- =============================
DROP TABLE IF EXISTS logistics_bill;
CREATE TABLE logistics_bill (
  bill_id            BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '提单ID',
  bill_no            VARCHAR(50)     NOT NULL                   COMMENT '提单号',
  bill_date          DATE            NOT NULL                   COMMENT '提单日期',
  customer_id        BIGINT(20)      NOT NULL                   COMMENT '客户ID',
  loading_address    VARCHAR(255)    NOT NULL                   COMMENT '装货地址',
  unloading_address  VARCHAR(255)    NOT NULL                   COMMENT '卸货地址',
  goods_id           BIGINT(20)      DEFAULT NULL               COMMENT '货物ID',
  goods_name         VARCHAR(100)    DEFAULT NULL               COMMENT '货物名称',
  goods_model        VARCHAR(100)    DEFAULT NULL               COMMENT '货物型号',
  total_weight       DECIMAL(10,3)   NOT NULL                   COMMENT '总重量(吨)',
  allocated_weight   DECIMAL(10,3)   DEFAULT 0                 COMMENT '已分配重量(吨)',
  unit_price         DECIMAL(10,2)   NOT NULL                   COMMENT '运价(元/吨)',
  total_amount       DECIMAL(10,2)   NOT NULL                   COMMENT '总金额',
  bill_status        VARCHAR(20)     DEFAULT 'pending'          COMMENT '提单状态(pending待配载,partial部分配载,allocated已配载,transporting运输中,completed已完成)',
  priority           VARCHAR(20)     DEFAULT 'normal'           COMMENT '优先级(normal正常,urgent紧急)',
  require_date       DATE            DEFAULT NULL               COMMENT '要求完成日期',
  del_flag           CHAR(1)         DEFAULT '0'                COMMENT '删除标志(0存在 2删除)',
  create_by          VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time        DATETIME                                   COMMENT '创建时间',
  update_by          VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time        DATETIME                                   COMMENT '更新时间',
  remark             VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (bill_id),
  UNIQUE KEY uk_bill_no (bill_no),
  KEY idx_customer_id (customer_id),
  KEY idx_bill_date (bill_date),
  KEY idx_bill_status (bill_status)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '提单表';

-- =============================
-- 2、提单运单明细表(提单与运单的关联，支持拆分和配载)
-- =============================
DROP TABLE IF EXISTS logistics_bill_order_detail;
CREATE TABLE logistics_bill_order_detail (
  detail_id          BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '明细ID',
  bill_id            BIGINT(20)      NOT NULL                   COMMENT '提单ID',
  order_id           BIGINT(20)      NOT NULL                   COMMENT '运单ID',
  allocated_weight   DECIMAL(10,3)   NOT NULL                   COMMENT '分配重量(吨)',
  unit_price         DECIMAL(10,2)   NOT NULL                   COMMENT '运价(元/吨)',
  allocated_amount   DECIMAL(10,2)   NOT NULL                   COMMENT '分配金额',
  create_by          VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time        DATETIME                                   COMMENT '创建时间',
  remark             VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (detail_id),
  KEY idx_bill_id (bill_id),
  KEY idx_order_id (order_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '提单运单明细表';

-- =============================
-- 3、驾驶员单表(派车单据)
-- =============================
DROP TABLE IF EXISTS logistics_driver_order;
CREATE TABLE logistics_driver_order (
  driver_order_id    BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '驾驶员单ID',
  driver_order_no    VARCHAR(50)     NOT NULL                   COMMENT '驾驶员单号',
  order_id           BIGINT(20)      NOT NULL                   COMMENT '运单ID',
  order_no           VARCHAR(50)     NOT NULL                   COMMENT '运单号',
  driver_id          BIGINT(20)      NOT NULL                   COMMENT '司机ID',
  driver_name        VARCHAR(50)     NOT NULL                   COMMENT '司机姓名',
  driver_phone       VARCHAR(20)     NOT NULL                   COMMENT '司机电话',
  vehicle_id         BIGINT(20)      DEFAULT NULL               COMMENT '车辆ID',
  vehicle_plate      VARCHAR(20)     NOT NULL                   COMMENT '车牌号',
  load_capacity      DECIMAL(10,2)   DEFAULT NULL               COMMENT '车辆载重(吨)',
  actual_weight      DECIMAL(10,3)   NOT NULL                   COMMENT '实际装车重量(吨)',
  loading_unit_price DECIMAL(10,2)   DEFAULT 0                 COMMENT '配载单价',
  freight_cost       DECIMAL(10,2)   DEFAULT 0                 COMMENT '运费支出',
  advance_payment    DECIMAL(10,2)   DEFAULT 0                 COMMENT '代垫付金额',
  payment_method     VARCHAR(50)     DEFAULT NULL               COMMENT '付款方式',
  payee              VARCHAR(50)     DEFAULT NULL               COMMENT '收款人',
  dispatch_date      DATE            NOT NULL                   COMMENT '派车日期',
  dispatch_status    VARCHAR(20)     DEFAULT 'pending'          COMMENT '派车状态',
  settlement_status  VARCHAR(20)     DEFAULT 'unsettled'        COMMENT '结算状态',
  receipt_status     VARCHAR(20)     DEFAULT 'not_received'     COMMENT '回单状态',
  del_flag           CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by          VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time        DATETIME                                   COMMENT '创建时间',
  update_by          VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time        DATETIME                                   COMMENT '更新时间',
  remark             VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (driver_order_id),
  UNIQUE KEY uk_driver_order_no (driver_order_no),
  KEY idx_order_id (order_id),
  KEY idx_driver_id (driver_id),
  KEY idx_vehicle_plate (vehicle_plate),
  KEY idx_dispatch_date (dispatch_date),
  KEY idx_dispatch_status (dispatch_status)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '驾驶员单表';

-- =============================
-- 4、扩展订单表字段
-- 说明: 如果字段已存在会报错，可以忽略或手动跳过
-- =============================
ALTER TABLE logistics_order ADD COLUMN order_type VARCHAR(20) DEFAULT 'transport' COMMENT '订单类型(transport运输,shuttle短驳)' AFTER order_no;
ALTER TABLE logistics_order ADD COLUMN source_type VARCHAR(20) DEFAULT 'manual' COMMENT '来源类型(manual手工创建,bill提单生成)' AFTER order_type;
ALTER TABLE logistics_order ADD COLUMN total_weight DECIMAL(10,3) DEFAULT 0 COMMENT '总装车重量(吨)' AFTER weight;
ALTER TABLE logistics_order ADD COLUMN allocated_amount DECIMAL(10,2) DEFAULT 0 COMMENT '已分配金额(元)' AFTER total_amount;
ALTER TABLE logistics_order ADD COLUMN dispatch_status VARCHAR(20) DEFAULT 'not_dispatched' COMMENT '派车状态' AFTER order_status;
ALTER TABLE logistics_order MODIFY COLUMN freight_cost DECIMAL(10,2) DEFAULT NULL;

-- =============================
-- 5、菜单权限插入
-- 父级菜单: 2200 (运输业务管理)
-- =============================
-- 提单管理菜单 (ID 2204)
DELETE FROM sys_menu WHERE menu_id BETWEEN 2204 AND 22046;
INSERT INTO sys_menu VALUES (2204, '提单管理', 2200, 4, 'bill', 'logistics/bill/index', '', '', 1, 0, 'C', '0', '0', 'logistics:bill:list', 'shopping', 'admin', NOW(), '', NULL, '提单管理菜单');
INSERT INTO sys_menu VALUES (22041, '提单查询', 2204, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22042, '提单新增', 2204, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22043, '提单修改', 2204, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22044, '提单删除', 2204, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22045, '提单导出', 2204, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:export', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22046, '提单导入', 2204, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:import', '#', 'admin', NOW(), '', NULL, '');

-- 配载管理菜单 (ID 2205)
DELETE FROM sys_menu WHERE menu_id BETWEEN 2205 AND 22051;
INSERT INTO sys_menu VALUES (2205, '配载管理', 2200, 5, 'allocation', 'logistics/allocation/index', '', '', 1, 0, 'C', '0', '0', 'logistics:allocation:list', 'link', 'admin', NOW(), '', NULL, '配载管理菜单');
INSERT INTO sys_menu VALUES (22051, '配载操作', 2205, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:operate', '#', 'admin', NOW(), '', NULL, '');

-- 驾驶员单管理菜单 (ID 2206)
DELETE FROM sys_menu WHERE menu_id BETWEEN 2206 AND 22065;
INSERT INTO sys_menu VALUES (2206, '驾驶员单', 2200, 6, 'driver-order', 'logistics/driver-order/index', '', '', 1, 0, 'C', '0', '0', 'logistics:driverOrder:list', 'user', 'admin', NOW(), '', NULL, '驾驶员单管理菜单');
INSERT INTO sys_menu VALUES (22061, '驾驶员单查询', 2206, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driverOrder:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22062, '驾驶员单新增', 2206, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driverOrder:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22063, '驾驶员单修改', 2206, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driverOrder:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22064, '驾驶员单删除', 2206, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driverOrder:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (22065, '驾驶员单导出', 2206, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driverOrder:export', '#', 'admin', NOW(), '', NULL, '');
