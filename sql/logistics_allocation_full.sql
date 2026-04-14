-- =============================
-- 配载管理模块完整脚本
-- 版本: v5.0
-- 说明: 提单支持多货物明细，运单合并驾驶员单（运单即货运单/派车单）
-- 执行方式: 在Navicat中执行整个脚本
-- 注意: 物流相关表会被重建，历史数据会丢失
-- =============================

-- =============================
-- 1、提单表(客户委托单据)
-- =============================
DROP TABLE IF EXISTS logistics_bill_order_detail;
DROP TABLE IF EXISTS logistics_bill_item;
DROP TABLE IF EXISTS logistics_bill;
DROP TABLE IF EXISTS logistics_driver_order;

CREATE TABLE logistics_bill (
  bill_id            BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '提单ID',
  bill_no            VARCHAR(50)     NOT NULL                   COMMENT '提单号',
  bill_date          DATETIME        NOT NULL                   COMMENT '提单日期',
  customer_id        BIGINT(20)      NOT NULL                   COMMENT '客户ID',
  loading_address    VARCHAR(255)    NOT NULL                   COMMENT '装货地址',
  unloading_address  VARCHAR(255)    NOT NULL                   COMMENT '卸货地址',
  total_weight       DECIMAL(10,3)   DEFAULT 0                 COMMENT '总重量(吨)，从货物明细汇总',
  allocated_weight   DECIMAL(10,3)   DEFAULT 0                 COMMENT '已分配重量(吨)，从货物明细汇总',
  total_amount       DECIMAL(10,2)   DEFAULT 0                 COMMENT '总金额，从货物明细汇总',
  bill_status        VARCHAR(20)     DEFAULT 'pending'          COMMENT '提单状态(pending待配载,partial部分配载,allocated已配载,transporting运输中,completed已完成)',
  priority           VARCHAR(20)     DEFAULT 'normal'           COMMENT '优先级(normal正常,urgent紧急)',
  require_date       DATETIME        DEFAULT NULL               COMMENT '要求完成日期',
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
-- 2、提单货物明细表(一个提单可包含多种货物)
-- =============================
CREATE TABLE logistics_bill_item (
  item_id            BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '明细ID',
  bill_id            BIGINT(20)      NOT NULL                   COMMENT '提单ID',
  goods_id           BIGINT(20)      DEFAULT NULL               COMMENT '货物ID',
  goods_name         VARCHAR(100)    NOT NULL                   COMMENT '货物名称',
  goods_model        VARCHAR(100)    DEFAULT NULL               COMMENT '货物型号',
  weight             DECIMAL(10,3)   NOT NULL                   COMMENT '重量(吨)',
  allocated_weight   DECIMAL(10,3)   DEFAULT 0                 COMMENT '已分配重量(吨)',
  unit_price         DECIMAL(10,2)   NOT NULL                   COMMENT '运价(元/吨)',
  amount             DECIMAL(10,2)   NOT NULL                   COMMENT '金额(元)',
  sort_order         INT             DEFAULT 0                 COMMENT '排序号',
  del_flag           CHAR(1)         DEFAULT '0'                COMMENT '删除标志(0存在 2删除)',
  create_by          VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time        DATETIME                                   COMMENT '创建时间',
  update_by          VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time        DATETIME                                   COMMENT '更新时间',
  remark             VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (item_id),
  KEY idx_bill_id (bill_id),
  KEY idx_goods_id (goods_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '提单货物明细表';

-- =============================
-- 3、提单运单明细表(提单与运单的关联，支持拆分和配载)
-- =============================
CREATE TABLE logistics_bill_order_detail (
  detail_id          BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '明细ID',
  bill_id            BIGINT(20)      NOT NULL                   COMMENT '提单ID',
  bill_item_id       BIGINT(20)      DEFAULT NULL               COMMENT '提单货物明细ID',
  order_id           BIGINT(20)      NOT NULL                   COMMENT '运单ID',
  allocated_weight   DECIMAL(10,3)   NOT NULL                   COMMENT '分配重量(吨)',
  unit_price         DECIMAL(10,2)   NOT NULL                   COMMENT '运价(元/吨)',
  allocated_amount   DECIMAL(10,2)   NOT NULL                   COMMENT '分配金额',
  create_by          VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time        DATETIME                                   COMMENT '创建时间',
  remark             VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (detail_id),
  KEY idx_bill_id (bill_id),
  KEY idx_bill_item_id (bill_item_id),
  KEY idx_order_id (order_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '提单运单明细表';

-- =============================
-- 4、运单表扩展字段(合并驾驶员单)
-- 说明: 已有的 logistics_order 表新增以下字段，如字段已存在会报错可忽略
-- =============================
ALTER TABLE logistics_order ADD COLUMN vehicle_id BIGINT(20) DEFAULT NULL COMMENT '车辆ID' AFTER driver_id;
ALTER TABLE logistics_order ADD COLUMN load_capacity DECIMAL(10,2) DEFAULT NULL COMMENT '车辆载重(吨)' AFTER vehicle_plate;
ALTER TABLE logistics_order ADD COLUMN actual_weight DECIMAL(10,3) DEFAULT NULL COMMENT '实际装车重量(吨)' AFTER weight;
ALTER TABLE logistics_order ADD COLUMN dispatch_date DATE DEFAULT NULL COMMENT '派车日期' AFTER order_status;

-- 删除驾驶员单表（已合并到运单）
DROP TABLE IF EXISTS logistics_driver_order;

-- =============================
-- 5、菜单权限插入
-- 父级菜单: 2200 (运输业务管理)
-- =============================

-- 提单管理菜单 (ID 2204)
DELETE FROM sys_menu WHERE menu_id BETWEEN 2204 AND 22046;
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2204, '提单管理', 2200, 4, 'bill', 'logistics/bill/index', '', '', 1, 0, 'C', '0', '0', 'logistics:bill:list', 'shopping', 'admin', sysdate(), '', null, '提单管理菜单');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (22041, '提单查询', 2204, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (22042, '提单新增', 2204, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (22043, '提单修改', 2204, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (22044, '提单删除', 2204, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:remove', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (22045, '提单导出', 2204, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:export', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (22046, '提单导入', 2204, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:import', '#', 'admin', sysdate(), '', null, '');

-- 配载管理菜单 (ID 2205)
DELETE FROM sys_menu WHERE menu_id BETWEEN 2205 AND 22051;
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2205, '配载管理', 2200, 5, 'allocation', 'logistics/allocation/index', '', '', 1, 0, 'C', '0', '0', 'logistics:allocation:list', 'link', 'admin', sysdate(), '', null, '配载管理菜单');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (22051, '配载操作', 2205, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:operate', '#', 'admin', sysdate(), '', null, '');

-- 删除驾驶员单菜单（已合并到运单，不再需要）
DELETE FROM sys_menu WHERE menu_id BETWEEN 2206 AND 22065;
