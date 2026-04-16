-- =============================
-- 物流管理系统数据库脚本
-- 版本: v3.1
-- 更新时间: 2026-04-16
-- 说明: 完整的物流管理系统数据库表结构和菜单配置
-- =============================

-- =============================
-- 一、基础数据表
-- =============================

-- ----------------------------
-- 1、客户信息表
-- ----------------------------
DROP TABLE IF EXISTS logistics_customer;
CREATE TABLE logistics_customer (
  customer_id      BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '客户ID',
  customer_code    VARCHAR(50)     NOT NULL                   COMMENT '客户编码',
  customer_name    VARCHAR(100)    NOT NULL                   COMMENT '客户名称',
  contact_person   VARCHAR(50)     DEFAULT NULL               COMMENT '联系人',
  contact_phone    VARCHAR(20)     DEFAULT NULL               COMMENT '联系电话',
  customer_address VARCHAR(255)    DEFAULT NULL               COMMENT '客户地址',
  settlement_type  VARCHAR(20)     DEFAULT 'monthly'          COMMENT '结算方式（monthly月结，cash现结）',
  credit_limit     DECIMAL(10,2)   DEFAULT 0                  COMMENT '信用额度',
  status           CHAR(1)         DEFAULT '0'                COMMENT '状态（0正常 1停用）',
  del_flag         CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_by        VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time      DATETIME                                   COMMENT '创建时间',
  update_by        VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time      DATETIME                                   COMMENT '更新时间',
  remark           VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (customer_id),
  UNIQUE KEY uk_customer_code (customer_code)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '客户信息表';

-- ----------------------------
-- 初始化客户数据
-- ----------------------------
INSERT INTO logistics_customer VALUES(1, 'CZPS', '常州品晟', '张三', '13800138000', '江苏省常州市', 'monthly', 50000, '0', '0', 'admin', NOW(), '', NULL, '测试客户1');
INSERT INTO logistics_customer VALUES(2, 'SHJY', '上海佳钰', '李四', '13800138001', '上海市', 'monthly', 30000, '0', '0', 'admin', NOW(), '', NULL, '测试客户2');

-- ----------------------------
-- 2、货物信息表
-- ----------------------------
DROP TABLE IF EXISTS logistics_goods;
CREATE TABLE logistics_goods (
  goods_id        BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '货物ID',
  goods_code      VARCHAR(50)     NOT NULL                   COMMENT '货物编码',
  goods_name      VARCHAR(100)    NOT NULL                   COMMENT '货物名称',
  goods_model     VARCHAR(100)    DEFAULT NULL               COMMENT '货物型号',
  goods_unit      VARCHAR(20)     DEFAULT '吨'               COMMENT '计量单位',
  goods_category  VARCHAR(50)     DEFAULT NULL               COMMENT '货物分类',
  unit_price      DECIMAL(10,2)   DEFAULT 0                  COMMENT '参考单价',
  status          CHAR(1)         DEFAULT '0'                COMMENT '状态（0正常 1停用）',
  del_flag        CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by       VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  update_by       VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     DATETIME                                   COMMENT '更新时间',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (goods_id),
  UNIQUE KEY uk_goods_code (goods_code)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '货物信息表';

-- ----------------------------
-- 初始化货物数据
-- ----------------------------
INSERT INTO logistics_goods VALUES(1, 'GD202604161001', '线材', 'Q235', '吨', '钢材', 65, '0', '0', 'admin', NOW(), '', NULL, '普通线材');
INSERT INTO logistics_goods VALUES(2, 'GD202604161002', '精线', '45#', '吨', '钢材', 80, '0', '0', 'admin', NOW(), '', NULL, '精拉线材');
INSERT INTO logistics_goods VALUES(3, 'GD202604161003', '银亮棒', '40Cr', '吨', '钢材', 170, '0', '0', 'admin', NOW(), '', NULL, '银亮棒材');
INSERT INTO logistics_goods VALUES(4, 'GD202604161004', '圆钢', 'Q235', '吨', '钢材', 250, '0', '0', 'admin', NOW(), '', NULL, '普通圆钢');

-- ----------------------------
-- 3、车队信息表
-- ----------------------------
DROP TABLE IF EXISTS logistics_fleet;
CREATE TABLE logistics_fleet (
  fleet_id        BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '车队ID',
  fleet_name      VARCHAR(100)    NOT NULL                   COMMENT '车队名称',
  owner_name      VARCHAR(50)     DEFAULT NULL               COMMENT '车队老板姓名',
  owner_phone     VARCHAR(20)     DEFAULT NULL               COMMENT '老板联系电话',
  account_name    VARCHAR(100)    DEFAULT NULL               COMMENT '车队开票账户名称',
  account_number  VARCHAR(50)     DEFAULT NULL               COMMENT '车队开票账号',
  bank_name       VARCHAR(100)    DEFAULT NULL               COMMENT '车队开户行',
  status          CHAR(1)         DEFAULT '0'                COMMENT '状态（0正常 1停用）',
  del_flag        CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_by       VARCHAR(64)     DEFAULT NULL               COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  update_by       VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     DATETIME                                   COMMENT '更新时间',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (fleet_id),
  KEY idx_status (status),
  KEY idx_del_flag (del_flag)
) ENGINE=INNODB AUTO_INCREMENT=1 COMMENT = '车队信息表';

-- ----------------------------
-- 初始化车队数据
-- ----------------------------
INSERT INTO logistics_fleet (fleet_id, fleet_name, owner_name, owner_phone, account_name, account_number, bank_name, status, create_by, create_time, remark)
VALUES
(1, '顺风物流车队', '王老板', '13800001001', '顺风物流有限公司', '6222021234567890123', '工商银行', '0', 'admin', NOW(), '测试车队1'),
(2, '安达运输车队', '李老板', '13800001002', '安达运输有限公司', '6222029876543210123', '建设银行', '0', 'admin', NOW(), '测试车队2');

-- ----------------------------
-- 4、司机信息表
-- ----------------------------
DROP TABLE IF EXISTS logistics_driver;
CREATE TABLE logistics_driver (
  driver_id       BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '司机ID',
  driver_name     VARCHAR(50)     NOT NULL                   COMMENT '司机姓名',
  driver_phone    VARCHAR(20)     NOT NULL                   COMMENT '司机电话',
  id_card         VARCHAR(18)     DEFAULT NULL               COMMENT '身份证号',
  driver_license  VARCHAR(50)     DEFAULT NULL               COMMENT '驾驶证号',
  driver_type     VARCHAR(20)     DEFAULT 'individual'       COMMENT '司机类型（individual个人，fleet车队）',
  fleet_id        BIGINT(20)      DEFAULT NULL               COMMENT '所属车队ID（车队司机时关联）',
  vehicle_plate   VARCHAR(20)     DEFAULT NULL               COMMENT '常用车牌号',
  bank_account    VARCHAR(50)     DEFAULT NULL               COMMENT '银行账号',
  bank_name       VARCHAR(100)    DEFAULT NULL               COMMENT '开户行',
  account_name    VARCHAR(50)     DEFAULT NULL               COMMENT '账户姓名',
  status          CHAR(1)         DEFAULT '0'                COMMENT '状态（0正常 1停用）',
  del_flag        CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by       VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  update_by       VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     DATETIME                                   COMMENT '更新时间',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (driver_id),
  KEY idx_driver_type (driver_type),
  KEY idx_fleet_id (fleet_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '司机信息表';

-- ----------------------------
-- 初始化司机数据
-- ----------------------------
INSERT INTO logistics_driver VALUES(1, '李正鹏', '13900139001', '320102199001011234', 'C1234567890', 'individual', NULL, '鲁NG7049', '6222021234567890123', '工商银行', '李正鹏', '0', '0', 'admin', NOW(), '', NULL, '测试司机1');

-- ----------------------------
-- 5、车辆信息表
-- ----------------------------
DROP TABLE IF EXISTS logistics_vehicle;
CREATE TABLE logistics_vehicle (
  vehicle_id      BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '车辆ID',
  vehicle_plate   VARCHAR(20)     NOT NULL                   COMMENT '车牌号',
  vehicle_type    VARCHAR(50)     DEFAULT NULL               COMMENT '车辆类型',
  vehicle_length  DECIMAL(5,2)    DEFAULT NULL               COMMENT '车长（米）',
  load_capacity   DECIMAL(10,2)   DEFAULT NULL               COMMENT '载重（吨）',
  driver_id       BIGINT(20)      DEFAULT NULL               COMMENT '默认司机ID',
  status          CHAR(1)         DEFAULT '0'                COMMENT '状态（0正常 1维修 2停用）',
  del_flag        CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by       VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  update_by       VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     DATETIME                                   COMMENT '更新时间',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (vehicle_id),
  UNIQUE KEY uk_vehicle_plate (vehicle_plate)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '车辆信息表';

-- ----------------------------
-- 初始化车辆数据
-- ----------------------------
INSERT INTO logistics_vehicle VALUES(1, '皖E14341', '货车', 13.00, 35.00, NULL, '0', '0', 'admin', NOW(), '', NULL, '测试车辆1');
INSERT INTO logistics_vehicle VALUES(2, '皖E12345', '货车', 9.60, 20.00, NULL, '0', '0', 'admin', NOW(), '', NULL, '测试车辆2');
INSERT INTO logistics_vehicle VALUES(3, '鲁NG7049', '货车', 13.00, 40.00, 1, '0', '0', 'admin', NOW(), '', NULL, '测试车辆3');

-- =============================
-- 二、运输业务表
-- =============================

-- ----------------------------
-- 6、提单表（已废弃，保留结构）
-- ----------------------------
DROP TABLE IF EXISTS logistics_bill_order_detail;
DROP TABLE IF EXISTS logistics_bill_item;
DROP TABLE IF EXISTS logistics_bill;

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
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '提单表（已废弃）';

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
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '提单货物明细表（已废弃）';

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
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '提单运单明细表（已废弃）';

-- ----------------------------
-- 7、运单表（货运单/派车单）
-- ----------------------------
DROP TABLE IF EXISTS logistics_order;
CREATE TABLE logistics_order (
  order_id            BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '订单ID',
  order_no            VARCHAR(50)     NOT NULL                   COMMENT '订单号',
  order_type          VARCHAR(20)     DEFAULT 'transport'       COMMENT '订单类型(transport运输,shuttle短驳)',
  source_type         VARCHAR(20)     DEFAULT 'manual'          COMMENT '来源类型(manual手工创建)',
  order_date          DATE            NOT NULL                   COMMENT '订单日期',
  customer_id         BIGINT(20)      NOT NULL                   COMMENT '客户ID',
  loading_address     VARCHAR(255)    NOT NULL                   COMMENT '装货地址',
  unloading_address   VARCHAR(255)    NOT NULL                   COMMENT '卸货地址',
  goods_id            BIGINT(20)      DEFAULT NULL               COMMENT '货物ID',
  goods_name          VARCHAR(100)    DEFAULT NULL               COMMENT '货物名称',
  goods_model         VARCHAR(100)    DEFAULT NULL               COMMENT '货物型号',
  weight              DECIMAL(10,3)   NOT NULL                   COMMENT '重量（吨）',
  actual_weight       DECIMAL(10,3)   DEFAULT NULL               COMMENT '实际装车重量(吨)',
  total_weight        DECIMAL(10,3)   DEFAULT '0.000'           COMMENT '总装车重量(吨,从货物明细汇总)',
  actual_load_weight  DECIMAL(10,2)   DEFAULT NULL               COMMENT '实际装车重量（吨）',
  unit_price          DECIMAL(10,2)   NOT NULL                   COMMENT '运价（元/吨）',
  total_amount        DECIMAL(10,2)   NOT NULL                   COMMENT '总金额',
  allocated_amount    DECIMAL(10,2)   DEFAULT '0.00'             COMMENT '已分配金额(元,从提单明细汇总)',
  advance_payment     DECIMAL(10,2)   DEFAULT '0.00'             COMMENT '代垫付金额',
  vehicle_plate       VARCHAR(20)     DEFAULT NULL               COMMENT '车牌号',
  load_capacity       DECIMAL(10,2)   DEFAULT NULL               COMMENT '车辆载重(吨)',
  driver_id           BIGINT(20)      DEFAULT NULL               COMMENT '司机ID',
  vehicle_id          BIGINT(20)      DEFAULT NULL               COMMENT '车辆ID',
  driver_phone        VARCHAR(20)     DEFAULT NULL               COMMENT '司机电话',
  loading_unit_price  DECIMAL(10,2)   DEFAULT '0.00'            COMMENT '配载单价',
  freight_cost        DECIMAL(10,2)   DEFAULT NULL               COMMENT '运费支出',
  settlement_status   VARCHAR(20)     DEFAULT 'unsettled'       COMMENT '结算状态（unsettled未结算，partial部分结算，settled已结算）',
  payment_method      VARCHAR(50)     DEFAULT NULL               COMMENT '付款方式',
  payee               VARCHAR(50)     DEFAULT NULL               COMMENT '收款人',
  receipt_status      VARCHAR(20)     DEFAULT 'not_received'    COMMENT '回单状态（not_received未收到，received已收到）',
  invoice_status      VARCHAR(20)     DEFAULT 'not_invoiced'     COMMENT '开票状态（not_invoiced未开票，invoiced已开票）',
  invoice_date        DATE            DEFAULT NULL               COMMENT '开票日期',
  invoice_batch_no    VARCHAR(50)     DEFAULT NULL               COMMENT '发票批次号',
  order_status        VARCHAR(20)     DEFAULT 'pending'          COMMENT '订单状态（pending待运输，transporting运输中，completed已完成，cancelled已取消）',
  dispatch_date       DATE            DEFAULT NULL               COMMENT '派车日期',
  dispatch_status     VARCHAR(20)     DEFAULT 'not_dispatched'   COMMENT '派车状态(not_dispatched未派车,partial_dispatched部分派车,dispatched已派车)',
  del_flag            CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by           VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time         DATETIME                                   COMMENT '创建时间',
  update_by           VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time         DATETIME                                   COMMENT '更新时间',
  remark              VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (order_id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_customer_id (customer_id),
  KEY idx_order_date (order_date),
  KEY idx_settlement_status (settlement_status)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '运输订单表';

-- ----------------------------
-- 8、订单货物明细表
-- ----------------------------
DROP TABLE IF EXISTS logistics_order_goods;
CREATE TABLE logistics_order_goods (
  detail_id       BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '明细ID',
  order_id        BIGINT(20)      NOT NULL                   COMMENT '订单ID',
  goods_id        BIGINT(20)      NOT NULL                   COMMENT '货物ID',
  goods_name      VARCHAR(100)    NOT NULL                   COMMENT '货物名称（冗余字段）',
  goods_model     VARCHAR(100)    DEFAULT NULL               COMMENT '货物型号',
  goods_unit      VARCHAR(20)     DEFAULT NULL               COMMENT '计量单位',
  weight          DECIMAL(10,2)   NOT NULL                   COMMENT '重量（吨）',
  unit_price      DECIMAL(10,2)   NOT NULL                   COMMENT '单价（元/吨）',
  amount          DECIMAL(10,2)   NOT NULL                   COMMENT '金额（元）',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  del_flag        CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by       VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  update_by       VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (detail_id),
  KEY idx_order_id (order_id),
  KEY idx_goods_id (goods_id),
  KEY idx_order_goods (order_id, goods_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT='订单货物明细表';

-- ----------------------------
-- 9、回单信息表
-- ----------------------------
DROP TABLE IF EXISTS logistics_receipt;
CREATE TABLE logistics_receipt (
  receipt_id      BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '回单ID',
  receipt_no      VARCHAR(50)     NOT NULL                   COMMENT '回单编号',
  order_id        BIGINT(20)      NOT NULL                   COMMENT '订单ID',
  receipt_date    DATE            DEFAULT NULL               COMMENT '回单日期',
  receipt_image   VARCHAR(255)    DEFAULT NULL               COMMENT '回单图片路径',
  receipt_status  VARCHAR(20)     DEFAULT 'not_received'    COMMENT '回单状态（not_received未收到，received已收到）',
  receiver        VARCHAR(50)     DEFAULT NULL               COMMENT '接收人',
  receive_time    DATETIME                                   COMMENT '接收时间',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  del_flag        CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by       VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  update_by       VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (receipt_id),
  UNIQUE KEY uk_receipt_no (receipt_no),
  KEY idx_order_id (order_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '回单信息表';

-- ----------------------------
-- 10、发票批次表
-- ----------------------------
DROP TABLE IF EXISTS logistics_invoice_batch;
CREATE TABLE logistics_invoice_batch (
  batch_id        BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '批次ID',
  batch_no        VARCHAR(50)     NOT NULL                   COMMENT '批次号',
  customer_id     BIGINT(20)      NOT NULL                   COMMENT '客户ID',
  invoice_date    DATE            NOT NULL                   COMMENT '开票日期',
  total_amount    DECIMAL(12,2)   NOT NULL                   COMMENT '开票总金额',
  order_count     INT             NOT NULL                   COMMENT '订单数量',
  invoice_status  VARCHAR(20)     DEFAULT 'draft'            COMMENT '发票状态（draft草稿，issued已开具，cancelled已作废）',
  invoice_type    VARCHAR(20)     DEFAULT 'ordinary'         COMMENT '发票类型（ordinary普通发票，vat增值税发票）',
  tax_rate        DECIMAL(5,2)    DEFAULT 0                  COMMENT '税率',
  tax_amount      DECIMAL(12,2)   DEFAULT 0                 COMMENT '税额',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  del_flag        CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by       VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  update_by       VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time     DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (batch_id),
  UNIQUE KEY uk_batch_no (batch_no),
  KEY idx_customer_id (customer_id),
  KEY idx_invoice_date (invoice_date)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '发票批次表';

-- ----------------------------
-- 11、发票批次明细表
-- ----------------------------
DROP TABLE IF EXISTS logistics_invoice_detail;
CREATE TABLE logistics_invoice_detail (
  detail_id       BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '明细ID',
  batch_id        BIGINT(20)      NOT NULL                   COMMENT '批次ID',
  order_id        BIGINT(20)      NOT NULL                   COMMENT '订单ID',
  order_no        VARCHAR(50)     NOT NULL                   COMMENT '订单号',
  amount          DECIMAL(10,2)   NOT NULL                   COMMENT '金额',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  create_by       VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time     DATETIME                                   COMMENT '创建时间',
  PRIMARY KEY (detail_id),
  KEY idx_batch_id (batch_id),
  KEY idx_order_id (order_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '发票批次明细表';

-- =============================
-- 三、财务结算表
-- =============================

-- ----------------------------
-- 12、财务结算表
-- ----------------------------
DROP TABLE IF EXISTS logistics_settlement;
CREATE TABLE logistics_settlement (
  settlement_id       BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '结算ID',
  settlement_no       VARCHAR(50)     NOT NULL                   COMMENT '结算单号',
  customer_id         BIGINT(20)      NOT NULL                   COMMENT '客户ID',
  settlement_type     VARCHAR(20)     NOT NULL                   COMMENT '结算类型（income收入，expenditure支出）',
  settlement_date     DATE            NOT NULL                   COMMENT '结算日期',
  start_date          DATE            DEFAULT NULL               COMMENT '结算开始日期',
  end_date            DATE            DEFAULT NULL               COMMENT '结算结束日期',
  total_amount        DECIMAL(12,2)   NOT NULL                   COMMENT '结算总金额',
  paid_amount         DECIMAL(12,2)   DEFAULT 0                 COMMENT '已付金额',
  unpaid_amount       DECIMAL(12,2)   DEFAULT 0                 COMMENT '未付金额',
  payment_method      VARCHAR(50)     DEFAULT NULL               COMMENT '付款方式',
  settlement_status   VARCHAR(20)     DEFAULT 'draft'            COMMENT '结算状态（draft草稿，confirmed已确认，completed已完成）',
  bank_account        VARCHAR(50)     DEFAULT NULL               COMMENT '银行账号',
  account_name        VARCHAR(50)     DEFAULT NULL               COMMENT '账户名',
  remark              VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  del_flag            CHAR(1)         DEFAULT '0'                COMMENT '删除标志',
  create_by           VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time         DATETIME                                   COMMENT '创建时间',
  update_by           VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time         DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (settlement_id),
  UNIQUE KEY uk_settlement_no (settlement_no),
  KEY idx_customer_id (customer_id),
  KEY idx_settlement_date (settlement_date)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '财务结算表';

-- ----------------------------
-- 13、结算明细表
-- ----------------------------
DROP TABLE IF EXISTS logistics_settlement_detail;
CREATE TABLE logistics_settlement_detail (
  detail_id           BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '明细ID',
  settlement_id       BIGINT(20)      NOT NULL                   COMMENT '结算ID',
  order_id            BIGINT(20)      NOT NULL                   COMMENT '订单ID',
  order_no            VARCHAR(50)     NOT NULL                   COMMENT '订单号',
  amount              DECIMAL(10,2)   NOT NULL                   COMMENT '金额',
  settlement_amount   DECIMAL(10,2)   DEFAULT 0                 COMMENT '已结算金额',
  remark              VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  create_by           VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time         DATETIME                                   COMMENT '创建时间',
  PRIMARY KEY (detail_id),
  KEY idx_settlement_id (settlement_id),
  KEY idx_order_id (order_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '结算明细表';

-- =============================
-- 四、菜单权限配置
-- =============================

-- ----------------------------
-- 1、物流管理一级菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (2000, '物流管理', 0, 5, 'logistics', NULL, '', '', 1, 0, 'M', '0', '0', '', 'logistics', 'admin', NOW(), '', NULL, '物流管理目录');

-- ----------------------------
-- 2、基础数据管理
-- ----------------------------
INSERT INTO sys_menu VALUES (2100, '基础数据管理', 2000, 1, 'base', '', '', '', 1, 0, 'M', '0', '0', '', 'tree', 'admin', NOW(), '', NULL, '基础数据管理目录');

-- 客户管理
INSERT INTO sys_menu VALUES (2101, '客户管理', 2100, 1, 'customer', 'logistics/customer/index', '', '', 1, 0, 'C', '0', '0', 'logistics:customer:list', 'user', 'admin', NOW(), '', NULL, '客户管理菜单');
INSERT INTO sys_menu VALUES (21101, '客户查询', 2101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21102, '客户新增', 2101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21103, '客户修改', 2101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21104, '客户删除', 2101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21105, '客户导出', 2101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:export', '#', 'admin', NOW(), '', NULL, '');

-- 货物管理
INSERT INTO sys_menu VALUES (2102, '货物管理', 2100, 2, 'goods', 'logistics/goods/index', '', '', 1, 0, 'C', '0', '0', 'logistics:goods:list', 'goods', 'admin', NOW(), '', NULL, '货物管理菜单');
INSERT INTO sys_menu VALUES (21106, '货物查询', 2102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21107, '货物新增', 2102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21108, '货物修改', 2102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21109, '货物删除', 2102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21122, '货物导出', 2102, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:export', '#', 'admin', NOW(), '', NULL, '');

-- 司机管理
INSERT INTO sys_menu VALUES (2103, '司机管理', 2100, 3, 'driver', 'logistics/driver/index', '', '', 1, 0, 'C', '0', '0', 'logistics:driver:list', 'peoples', 'admin', NOW(), '', NULL, '司机管理菜单');
INSERT INTO sys_menu VALUES (21110, '司机查询', 2103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21111, '司机新增', 2103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21112, '司机修改', 2103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21113, '司机删除', 2103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21120, '司机导出', 2103, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:export', '#', 'admin', NOW(), '', NULL, '');

-- 车辆管理
INSERT INTO sys_menu VALUES (2104, '车辆管理', 2100, 4, 'vehicle', 'logistics/vehicle/index', '', '', 1, 0, 'C', '0', '0', 'logistics:vehicle:list', 'truck', 'admin', NOW(), '', NULL, '车辆管理菜单');
INSERT INTO sys_menu VALUES (21114, '车辆查询', 2104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21115, '车辆新增', 2104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21116, '车辆修改', 2104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21117, '车辆删除', 2104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21121, '车辆导出', 2104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:export', '#', 'admin', NOW(), '', NULL, '');

-- ----------------------------
-- 3、运输业务管理
-- ----------------------------
INSERT INTO sys_menu VALUES (2200, '运输业务管理', 2000, 2, 'business', '', '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '', NULL, '运输业务管理目录');

-- 订单管理
INSERT INTO sys_menu VALUES (2201, '订单管理', 2200, 1, 'order', 'logistics/order/index', '', '', 1, 0, 'C', '0', '0', 'logistics:order:list', 'list', 'admin', NOW(), '', NULL, '订单管理菜单');
INSERT INTO sys_menu VALUES (21201, '订单查询', 2201, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21202, '订单新增', 2201, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21203, '订单修改', 2201, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21204, '订单删除', 2201, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21205, '订单导出', 2201, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:export', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21206, '订单导入', 2201, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:import', '#', 'admin', NOW(), '', NULL, '');

-- 提单管理（已废弃）
INSERT INTO sys_menu VALUES (2204, '提单管理', 2200, 4, 'bill', 'logistics/bill/index', '', '', 1, 0, 'C', '0', '0', 'logistics:bill:list', 'documentation', 'admin', NOW(), '', NULL, '提单管理菜单（已废弃）');
INSERT INTO sys_menu VALUES (21220, '提单查询', 2204, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21221, '提单新增', 2204, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21222, '提单修改', 2204, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21223, '提单删除', 2204, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21224, '提单导入', 2204, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:import', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21225, '提单导出', 2204, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:export', '#', 'admin', NOW(), '', NULL, '');

-- 配载管理（已废弃）
INSERT INTO sys_menu VALUES (2205, '配载管理', 2200, 5, 'allocation', 'logistics/allocation/index', '', '', 1, 0, 'C', '0', '0', 'logistics:allocation:list', 'guide', 'admin', NOW(), '', NULL, '配载管理菜单（已废弃）');
INSERT INTO sys_menu VALUES (21230, '配载查询', 2205, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21231, '配载新增', 2205, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21232, '配载修改', 2205, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21233, '配载删除', 2205, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21234, '创建运单', 2205, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:create', '#', 'admin', NOW(), '', NULL, '');

-- 回单管理
INSERT INTO sys_menu VALUES (2202, '回单管理', 2200, 2, 'receipt', 'logistics/receipt/index', '', '', 1, 0, 'C', '0', '0', 'logistics:receipt:list', 'edit', 'admin', NOW(), '', NULL, '回单管理菜单');
INSERT INTO sys_menu VALUES (21207, '回单查询', 2202, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21208, '回单上传', 2202, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:upload', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21209, '回单确认', 2202, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:confirm', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21210, '回单删除', 2202, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21215, '回单新增', 2202, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21216, '回单修改', 2202, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21217, '回单导出', 2202, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:export', '#', 'admin', NOW(), '', NULL, '');

-- 发票管理
INSERT INTO sys_menu VALUES (2203, '发票管理', 2200, 3, 'invoice', 'logistics/invoice/index', '', '', 1, 0, 'C', '0', '0', 'logistics:invoice:list', 'money', 'admin', NOW(), '', NULL, '发票管理菜单');
INSERT INTO sys_menu VALUES (21211, '发票批次查询', 2203, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21212, '合并开票', 2203, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:merge', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21213, '发票开具', 2203, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:issue', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21214, '发票作废', 2203, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:cancel', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21218, '发票删除', 2203, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:remove', '#', 'admin', NOW(), '', NULL, '');

-- ----------------------------
-- 4、财务结算管理
-- ----------------------------
INSERT INTO sys_menu VALUES (2300, '财务结算管理', 2000, 3, 'settlement', '', '', '', 1, 0, 'M', '0', '0', '', 'money', 'admin', NOW(), '', NULL, '财务结算管理目录');

-- 应收结算
INSERT INTO sys_menu VALUES (2301, '应收结算', 2300, 1, 'receivable', 'logistics/settlement/receivable', '', '', 1, 0, 'C', '0', '0', 'logistics:receivable:list', 'money', 'admin', NOW(), '', NULL, '应收结算菜单');
INSERT INTO sys_menu VALUES (21260, '应收查询', 2301, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:settlement:receivable:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21261, '应收新增', 2301, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:settlement:receivable:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21262, '应收确认', 2301, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:settlement:receivable:confirm', '#', 'admin', NOW(), '', NULL, '');

-- 应付结算
INSERT INTO sys_menu VALUES (2302, '应付结算', 2300, 2, 'payable', 'logistics/settlement/payable', '', '', 1, 0, 'C', '0', '0', 'logistics:payable:list', 'payment', 'admin', NOW(), '', NULL, '应付结算菜单');
INSERT INTO sys_menu VALUES (21270, '应付查询', 2302, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:settlement:payable:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21271, '应付新增', 2302, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:settlement:payable:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21272, '付款确认', 2302, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:settlement:payable:pay', '#', 'admin', NOW(), '', NULL, '');

-- 财务报表
INSERT INTO sys_menu VALUES (2303, '财务报表', 2300, 3, 'report', 'logistics/report/index', '', '', 1, 0, 'C', '0', '0', 'logistics:report:list', 'chart', 'admin', NOW(), '', NULL, '财务报表菜单');
INSERT INTO sys_menu VALUES (21280, '报表查询', 2303, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:report:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (21281, '报表导出', 2303, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:report:export', '#', 'admin', NOW(), '', NULL, '');

-- =============================
-- 执行完成提示
-- =============================
SELECT '物流管理系统数据库脚本执行完成！' AS message;
SELECT COUNT(*) AS '已创建表数量' FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name LIKE 'logistics%';
SELECT COUNT(*) AS '已配置菜单数量' FROM sys_menu WHERE menu_id BETWEEN 2000 AND 2399;
