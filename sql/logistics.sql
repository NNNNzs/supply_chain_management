-- ----------------------------
-- 物流管理系统数据库表和菜单
-- 更新时间：2026-04-14
-- ----------------------------

-- ----------------------------
-- 1、客户信息表
-- ----------------------------
drop table if exists logistics_customer;
create table logistics_customer (
  customer_id      bigint(20)      not null auto_increment    comment '客户ID',
  customer_code    varchar(50)     not null                   comment '客户编码',
  customer_name    varchar(100)    not null                   comment '客户名称',
  contact_person   varchar(50)     default null               comment '联系人',
  contact_phone    varchar(20)     default null               comment '联系电话',
  customer_address varchar(255)    default null               comment '客户地址',
  settlement_type  varchar(20)     default 'monthly'          comment '结算方式（monthly月结，cash现结）',
  credit_limit     decimal(10,2)   default 0                  comment '信用额度',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag         char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (customer_id),
  unique key uk_customer_code (customer_code)
) engine=innodb auto_increment=1000 comment = '客户信息表';

-- ----------------------------
-- 初始化-客户信息表数据
-- ----------------------------
insert into logistics_customer values(1, 'CZPS', '常州品晟', '张三', '13800138000', '江苏省常州市', 'monthly', 50000, '0', '0', 'admin', sysdate(), '', null, '测试客户1');
insert into logistics_customer values(2, 'SHJY', '上海佳钰', '李四', '13800138001', '上海市', 'monthly', 30000, '0', '0', 'admin', sysdate(), '', null, '测试客户2');

-- ----------------------------
-- 2、货物信息表
-- ----------------------------
drop table if exists logistics_goods;
create table logistics_goods (
  goods_id        bigint(20)      not null auto_increment    comment '货物ID',
  goods_code      varchar(50)     not null                   comment '货物编码',
  goods_name      varchar(100)    not null                   comment '货物名称',
  goods_model     varchar(100)    default null               comment '货物型号',
  goods_unit      varchar(20)     default '吨'               comment '计量单位',
  goods_category  varchar(50)     default null               comment '货物分类',
  unit_price      decimal(10,2)   default 0                  comment '参考单价',
  status          char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag        char(1)         default '0'                comment '删除标志',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (goods_id),
  unique key uk_goods_code (goods_code)
) engine=innodb auto_increment=1000 comment = '货物信息表';

-- ----------------------------
-- 初始化-货物信息表数据
-- ----------------------------
insert into logistics_goods values(1, 'XC001', '线材', 'Q235', '吨', '钢材', 65, '0', '0', 'admin', sysdate(), '', null, '普通线材');
insert into logistics_goods values(2, 'JX001', '精线', '45#', '吨', '钢材', 80, '0', '0', 'admin', sysdate(), '', null, '精拉线材');
insert into logistics_goods values(3, 'YB001', '银亮棒', '40Cr', '吨', '钢材', 170, '0', '0', 'admin', sysdate(), '', null, '银亮棒材');
insert into logistics_goods values(4, 'YG001', '圆钢', 'Q235', '吨', '钢材', 250, '0', '0', 'admin', sysdate(), '', null, '普通圆钢');

-- ----------------------------
-- 3、司机信息表
-- ----------------------------
drop table if exists logistics_driver;
create table logistics_driver (
  driver_id       bigint(20)      not null auto_increment    comment '司机ID',
  driver_code     varchar(50)     not null                   comment '司机编码',
  driver_name     varchar(50)     not null                   comment '司机姓名',
  driver_phone    varchar(20)     not null                   comment '司机电话',
  id_card         varchar(18)     default null               comment '身份证号',
  driver_license  varchar(50)     default null               comment '驾驶证号',
  vehicle_plate   varchar(20)     default null               comment '常用车牌号',
  bank_account    varchar(50)     default null               comment '银行账号',
  bank_name       varchar(100)    default null               comment '开户行',
  account_name    varchar(50)     default null               comment '账户姓名',
  status          char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag        char(1)         default '0'                comment '删除标志',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (driver_id),
  unique key uk_driver_code (driver_code)
) engine=innodb auto_increment=1000 comment = '司机信息表';

-- ----------------------------
-- 初始化-司机信息表数据
-- ----------------------------
insert into logistics_driver values(1, 'SJ001', '李正鹏', '13900139001', '320102199001011234', 'C1234567890', '鲁NG7049', '6222021234567890123', '工商银行', '李正鹏', '0', '0', 'admin', sysdate(), '', null, '测试司机1');

-- ----------------------------
-- 4、车辆信息表
-- ----------------------------
drop table if exists logistics_vehicle;
create table logistics_vehicle (
  vehicle_id      bigint(20)      not null auto_increment    comment '车辆ID',
  vehicle_plate   varchar(20)     not null                   comment '车牌号',
  vehicle_type    varchar(50)     default null               comment '车辆类型',
  vehicle_length  decimal(5,2)    default null               comment '车长（米）',
  load_capacity   decimal(10,2)   default null               comment '载重（吨）',
  driver_id       bigint(20)      default null               comment '默认司机ID',
  status          char(1)         default '0'                comment '状态（0正常 1维修 2停用）',
  del_flag        char(1)         default '0'                comment '删除标志',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (vehicle_id),
  unique key uk_vehicle_plate (vehicle_plate)
) engine=innodb auto_increment=1000 comment = '车辆信息表';

-- ----------------------------
-- 初始化-车辆信息表数据
-- ----------------------------
insert into logistics_vehicle values(1, '皖E14341', '货车', 13.00, 35.00, null, '0', '0', 'admin', sysdate(), '', null, '测试车辆1');
insert into logistics_vehicle values(2, '皖E12345', '货车', 9.60, 20.00, null, '0', '0', 'admin', sysdate(), '', null, '测试车辆2');
insert into logistics_vehicle values(3, '鲁NG7049', '货车', 13.00, 40.00, 1, '0', '0', 'admin', sysdate(), '', null, '测试车辆3');

-- ----------------------------
-- 5、运输订单表
-- ----------------------------
drop table if exists logistics_order;
create table logistics_order (
  order_id            bigint(20)      not null auto_increment    comment '订单ID',
  order_no            varchar(50)     not null                   comment '订单号',
  order_date          date            not null                   comment '订单日期',
  customer_id         bigint(20)      not null                   comment '客户ID',
  loading_address     varchar(255)    not null                   comment '装货地址',
  unloading_address   varchar(255)    not null                   comment '卸货地址',
  goods_id            bigint(20)      default null               comment '货物ID',
  goods_name          varchar(100)    default null               comment '货物名称',
  goods_model         varchar(100)    default null               comment '货物型号',
  weight              decimal(10,3)   not null                   comment '重量（吨）',
  unit_price          decimal(10,2)   not null                   comment '运价（元/吨）',
  total_amount        decimal(10,2)   not null                   comment '总金额',
  advance_payment     decimal(10,2)   default 0                 comment '代垫付金额',
  vehicle_plate       varchar(20)     default null               comment '车牌号',
  driver_id           bigint(20)      default null               comment '司机ID',
  driver_phone        varchar(20)     default null               comment '司机电话',
  loading_unit_price  decimal(10,2)   default 0                 comment '配载单价',
  freight_cost        decimal(10,2)   default 0                 comment '运费支出',
  settlement_status   varchar(20)     default 'unsettled'       comment '结算状态（unsettled未结算，partial部分结算，settled已结算）',
  payment_method      varchar(50)     default null               comment '付款方式',
  payee               varchar(50)     default null               comment '收款人',
  receipt_status      varchar(20)     default 'not_received'    comment '回单状态（not_received未收到，received已收到）',
  invoice_status      varchar(20)     default 'not_invoiced'     comment '开票状态（not_invoiced未开票，invoiced已开票）',
  invoice_date        date            default null               comment '开票日期',
  invoice_batch_no    varchar(50)     default null               comment '发票批次号',
  order_status        varchar(20)     default 'pending'          comment '订单状态（pending待运输，transporting运输中，completed已完成，cancelled已取消）',
  del_flag            char(1)         default '0'                comment '删除标志',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  update_by           varchar(64)     default ''                 comment '更新者',
  update_time         datetime                                   comment '更新时间',
  remark              varchar(500)    default null               comment '备注',
  primary key (order_id),
  unique key uk_order_no (order_no),
  key idx_customer_id (customer_id),
  key idx_order_date (order_date),
  key idx_settlement_status (settlement_status)
) engine=innodb auto_increment=1000 comment = '运输订单表';

-- ----------------------------
-- 6、回单信息表
-- ----------------------------
drop table if exists logistics_receipt;
create table logistics_receipt (
  receipt_id      bigint(20)      not null auto_increment    comment '回单ID',
  receipt_no      varchar(50)     not null                   comment '回单编号',
  order_id        bigint(20)      not null                   comment '订单ID',
  receipt_date    date            default null               comment '回单日期',
  receipt_image   varchar(255)    default null               comment '回单图片路径',
  receipt_status  varchar(20)     default 'not_received'    comment '回单状态（not_received未收到，received已收到）',
  receiver        varchar(50)     default null               comment '接收人',
  receive_time    datetime                                   comment '接收时间',
  remark          varchar(500)    default null               comment '备注',
  del_flag        char(1)         default '0'                comment '删除标志',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  primary key (receipt_id),
  unique key uk_receipt_no (receipt_no),
  key idx_order_id (order_id)
) engine=innodb auto_increment=1000 comment = '回单信息表';

-- ----------------------------
-- 7、发票批次表
-- ----------------------------
drop table if exists logistics_invoice_batch;
create table logistics_invoice_batch (
  batch_id        bigint(20)      not null auto_increment    comment '批次ID',
  batch_no        varchar(50)     not null                   comment '批次号',
  customer_id     bigint(20)      not null                   comment '客户ID',
  invoice_date    date            not null                   comment '开票日期',
  total_amount    decimal(12,2)   not null                   comment '开票总金额',
  order_count     int             not null                   comment '订单数量',
  invoice_status  varchar(20)     default 'draft'            comment '发票状态（draft草稿，issued已开具，cancelled已作废）',
  invoice_type    varchar(20)     default 'ordinary'         comment '发票类型（ordinary普通发票，vat增值税发票）',
  tax_rate        decimal(5,2)    default 0                  comment '税率',
  tax_amount      decimal(12,2)   default 0                 comment '税额',
  remark          varchar(500)    default null               comment '备注',
  del_flag        char(1)         default '0'                comment '删除标志',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  primary key (batch_id),
  unique key uk_batch_no (batch_no),
  key idx_customer_id (customer_id),
  key idx_invoice_date (invoice_date)
) engine=innodb auto_increment=1000 comment = '发票批次表';

-- ----------------------------
-- 8、发票批次明细表
-- ----------------------------
drop table if exists logistics_invoice_detail;
create table logistics_invoice_detail (
  detail_id       bigint(20)      not null auto_increment    comment '明细ID',
  batch_id        bigint(20)      not null                   comment '批次ID',
  order_id        bigint(20)      not null                   comment '订单ID',
  order_no        varchar(50)     not null                   comment '订单号',
  amount          decimal(10,2)   not null                   comment '金额',
  remark          varchar(500)    default null               comment '备注',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  primary key (detail_id),
  key idx_batch_id (batch_id),
  key idx_order_id (order_id)
) engine=innodb auto_increment=1000 comment = '发票批次明细表';

-- ----------------------------
-- 9、财务结算表
-- ----------------------------
drop table if exists logistics_settlement;
create table logistics_settlement (
  settlement_id       bigint(20)      not null auto_increment    comment '结算ID',
  settlement_no       varchar(50)     not null                   comment '结算单号',
  customer_id         bigint(20)      not null                   comment '客户ID',
  settlement_type     varchar(20)     not null                   comment '结算类型（income收入，expenditure支出）',
  settlement_date     date            not null                   comment '结算日期',
  start_date          date            default null               comment '结算开始日期',
  end_date            date            default null               comment '结算结束日期',
  total_amount        decimal(12,2)   not null                   comment '结算总金额',
  paid_amount         decimal(12,2)   default 0                 comment '已付金额',
  unpaid_amount       decimal(12,2)   default 0                 comment '未付金额',
  payment_method      varchar(50)     default null               comment '付款方式',
  settlement_status   varchar(20)     default 'draft'            comment '结算状态（draft草稿，confirmed已确认，completed已完成）',
  bank_account        varchar(50)     default null               comment '银行账号',
  account_name        varchar(50)     default null               comment '账户名',
  remark              varchar(500)    default null               comment '备注',
  del_flag            char(1)         default '0'                comment '删除标志',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  update_by           varchar(64)     default ''                 comment '更新者',
  update_time         datetime                                   comment '更新时间',
  primary key (settlement_id),
  unique key uk_settlement_no (settlement_no),
  key idx_customer_id (customer_id),
  key idx_settlement_date (settlement_date)
) engine=innodb auto_increment=1000 comment = '财务结算表';

-- ----------------------------
-- 10、结算明细表
-- ----------------------------
drop table if exists logistics_settlement_detail;
create table logistics_settlement_detail (
  detail_id           bigint(20)      not null auto_increment    comment '明细ID',
  settlement_id       bigint(20)      not null                   comment '结算ID',
  order_id            bigint(20)      not null                   comment '订单ID',
  order_no            varchar(50)     not null                   comment '订单号',
  amount              decimal(10,2)   not null                   comment '金额',
  settlement_amount   decimal(10,2)   default 0                 comment '已结算金额',
  remark              varchar(500)    default null               comment '备注',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  primary key (detail_id),
  key idx_settlement_id (settlement_id),
  key idx_order_id (order_id)
) engine=innodb auto_increment=1000 comment = '结算明细表';

-- ----------------------------
-- 11、菜单表
-- ----------------------------
-- 物流管理系统一级菜单
INSERT INTO sys_menu VALUES (2000, '物流管理', 0, 5, 'logistics', NULL, '', '', 1, 0, 'M', '0', '0', '', 'logistics', 'admin', sysdate(), '', NULL, '物流管理目录');

-- 基础数据管理
INSERT INTO sys_menu VALUES (2100, '基础数据管理', '2000', 1, 'base', '', '', '', 1, 0, 'M', '0', '0', '', 'tree', 'admin', sysdate(), '', NULL, '基础数据管理目录');

-- 客户管理
INSERT INTO sys_menu VALUES (2101, '客户管理', '2100', 1, 'customer', 'logistics/customer/index', '', '', 1, 0, 'C', '0', '0', 'logistics:customer:list', 'user', 'admin', sysdate(), '', NULL, '客户管理菜单');
INSERT INTO sys_menu VALUES (21101, '客户查询', '2101', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21102, '客户新增', '2101', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21103, '客户修改', '2101', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21104, '客户删除', '2101', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21105, '客户导出', '2101', 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:export', '#', 'admin', sysdate(), '', NULL, '');

-- 货物管理
INSERT INTO sys_menu VALUES (2102, '货物管理', '2100', 2, 'goods', 'logistics/goods/index', '', '', 1, 0, 'C', '0', '0', 'logistics:goods:list', 'goods', 'admin', sysdate(), '', NULL, '货物管理菜单');
INSERT INTO sys_menu VALUES (21106, '货物查询', '2102', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21107, '货物新增', '2102', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21108, '货物修改', '2102', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21109, '货物删除', '2102', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:remove', '#', 'admin', sysdate(), '', NULL, '');

-- 司机管理
INSERT INTO sys_menu VALUES (2103, '司机管理', '2100', 3, 'driver', 'logistics/driver/index', '', '', 1, 0, 'C', '0', '0', 'logistics:driver:list', 'peoples', 'admin', sysdate(), '', NULL, '司机管理菜单');
INSERT INTO sys_menu VALUES (21110, '司机查询', '2103', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21111, '司机新增', '2103', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21112, '司机修改', '2103', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21113, '司机删除', '2103', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:remove', '#', 'admin', sysdate(), '', NULL, '');

-- 车辆管理
INSERT INTO sys_menu VALUES (2104, '车辆管理', '2100', 4, 'vehicle', 'logistics/vehicle/index', '', '', 1, 0, 'C', '0', '0', 'logistics:vehicle:list', 'truck', 'admin', sysdate(), '', NULL, '车辆管理菜单');
INSERT INTO sys_menu VALUES (21114, '车辆查询', '2104', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21115, '车辆新增', '2104', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21116, '车辆修改', '2104', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21117, '车辆删除', '2104', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:remove', '#', 'admin', sysdate(), '', NULL, '');

-- 运输业务管理
INSERT INTO sys_menu VALUES (2200, '运输业务管理', '2000', 2, 'business', '', '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', NULL, '运输业务管理目录');

-- 订单管理
INSERT INTO sys_menu VALUES (2201, '订单管理', '2200', 1, 'order', 'logistics/order/index', '', '', 1, 0, 'C', '0', '0', 'logistics:order:list', 'list', 'admin', sysdate(), '', NULL, '订单管理菜单');
INSERT INTO sys_menu VALUES (21201, '订单查询', '2201', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21202, '订单新增', '2201', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21203, '订单修改', '2201', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21204, '订单删除', '2201', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21205, '订单导出', '2201', 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:export', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21206, '订单导入', '2201', 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:import', '#', 'admin', sysdate(), '', NULL, '');

-- 回单管理
INSERT INTO sys_menu VALUES (2202, '回单管理', '2200', 2, 'receipt', 'logistics/receipt/index', '', '', 1, 0, 'C', '0', '0', 'logistics:receipt:list', 'edit', 'admin', sysdate(), '', NULL, '回单管理菜单');
INSERT INTO sys_menu VALUES (21207, '回单查询', '2202', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21208, '回单上传', '2202', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:upload', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21209, '回单确认', '2202', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:confirm', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21210, '回单删除', '2202', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:remove', '#', 'admin', sysdate(), '', NULL, '');

-- 发票管理
INSERT INTO sys_menu VALUES (2203, '发票管理', '2200', 3, 'invoice', 'logistics/invoice/index', '', '', 1, 0, 'C', '0', '0', 'logistics:invoice:list', 'money', 'admin', sysdate(), '', NULL, '发票管理菜单');
INSERT INTO sys_menu VALUES (21211, '发票批次查询', '2203', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21212, '合并开票', '2203', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:merge', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21213, '发票开具', '2203', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:issue', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21214, '发票作废', '2203', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:cancel', '#', 'admin', sysdate(), '', NULL, '');

-- 财务结算管理
INSERT INTO sys_menu VALUES (2300, '财务结算管理', '2000', 3, 'settlement', '', '', '', 1, 0, 'M', '0', '0', '', 'money', 'admin', sysdate(), '', NULL, '财务结算管理目录');

-- 应收结算
INSERT INTO sys_menu VALUES (2301, '应收结算', '2300', 1, 'receivable', 'logistics/settlement/receivable', '', '', 1, 0, 'C', '0', '0', 'logistics:receivable:list', 'money', 'admin', sysdate(), '', NULL, '应收结算菜单');
INSERT INTO sys_menu VALUES (21301, '结算单查询', '2301', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receivable:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21302, '生成结算单', '2301', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receivable:create', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21303, '结算确认', '2301', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receivable:confirm', '#', 'admin', sysdate(), '', NULL, '');

-- 应付结算
INSERT INTO sys_menu VALUES (2302, '应付结算', '2300', 2, 'payable', 'logistics/settlement/payable', '', '', 1, 0, 'C', '0', '0', 'logistics:payable:list', 'payment', 'admin', sysdate(), '', NULL, '应付结算菜单');
INSERT INTO sys_menu VALUES (21304, '结算单查询', '2302', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:payable:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21305, '生成结算单', '2302', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:payable:create', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (21306, '付款确认', '2302', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:payable:confirm', '#', 'admin', sysdate(), '', NULL, '');

-- 财务报表
INSERT INTO sys_menu VALUES (2303, '财务报表', '2300', 3, 'report', 'logistics/report/index', '', '', 1, 0, 'C', '0', '0', 'logistics:report:list', 'chart', 'admin', sysdate(), '', NULL, '财务报表菜单');
