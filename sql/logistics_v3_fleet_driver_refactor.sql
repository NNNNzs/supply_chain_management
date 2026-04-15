-- 物流管理系统 v3.0 - 司机/车队管理重构脚本
-- 功能：拆分车队和司机为独立实体
-- 日期：2026-04-15

-- ============================================================
-- 一、创建车队表
-- ============================================================
CREATE TABLE IF NOT EXISTS logistics_fleet (
  fleet_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '车队ID',
  fleet_name VARCHAR(100) NOT NULL COMMENT '车队名称',
  owner_name VARCHAR(50) DEFAULT NULL COMMENT '车队老板姓名',
  owner_phone VARCHAR(20) DEFAULT NULL COMMENT '老板联系电话',
  account_name VARCHAR(100) DEFAULT NULL COMMENT '车队开票账户名称',
  account_number VARCHAR(50) DEFAULT NULL COMMENT '车队开票账号',
  bank_name VARCHAR(100) DEFAULT NULL COMMENT '车队开户行',
  status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (fleet_id),
  KEY idx_status (status),
  KEY idx_del_flag (del_flag)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='车队信息表';

-- ============================================================
-- 二、修改司机表
-- ============================================================

-- 1. 删除原有车队相关字段
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS driver_type;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_owner_name;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_owner_phone;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_account_name;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_account_number;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_bank_name;

-- 2. 删除司机编码字段（使用自增ID）
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS driver_code;

-- 3. 新增车队关联字段
ALTER TABLE logistics_driver ADD COLUMN driver_type VARCHAR(20) DEFAULT 'individual' COMMENT '司机类型（individual个人，fleet车队）' AFTER driver_license;
ALTER TABLE logistics_driver ADD COLUMN fleet_id BIGINT DEFAULT NULL COMMENT '所属车队ID（车队司机时关联）' AFTER driver_type;

-- 4. 添加索引
ALTER TABLE logistics_driver ADD INDEX idx_driver_type (driver_type);
ALTER TABLE logistics_driver ADD INDEX idx_fleet_id (fleet_id);

-- 5. 添加外键约束（可选）
ALTER TABLE logistics_driver ADD CONSTRAINT fk_driver_fleet FOREIGN KEY (fleet_id) REFERENCES logistics_fleet(fleet_id) ON DELETE SET NULL;

-- ============================================================
-- 三、菜单配置
-- ============================================================

-- 获取现有司机管理菜单的parent_id
-- 假设物流管理的菜单ID，请根据实际情况调整
SET @logistics_parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '物流管理' LIMIT 1);

-- 插入车队管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (
  '车队管理',
  @logistics_parent_id,
  4,
  'fleet',
  'logistics/fleet/index',
  1,
  0,
  'C',
  '0',
  '0',
  'logistics:fleet:list',
  'truck',
  'admin',
  NOW(),
  '',
  NULL,
  '车队管理菜单'
);

-- 获取刚插入的车队管理菜单ID
SET @fleet_menu_id = LAST_INSERT_ID();

-- 车队管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('车队查询', @fleet_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:query', '#', 'admin', NOW()),
('车队新增', @fleet_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:add', '#', 'admin', NOW()),
('车队修改', @fleet_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:edit', '#', 'admin', NOW()),
('车队删除', @fleet_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:remove', '#', 'admin', NOW()),
('车队导出', @fleet_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:export', '#', 'admin', NOW());

-- ============================================================
-- 四、示例数据
-- ============================================================

-- 插入测试车队
INSERT INTO logistics_fleet (fleet_id, fleet_name, owner_name, owner_phone, account_name, account_number, bank_name, status, create_by, create_time, remark)
VALUES
(1, '顺风物流车队', '王老板', '13800001001', '顺风物流有限公司', '6222021234567890123', '工商银行', '0', 'admin', NOW(), '测试车队1'),
(2, '安达运输车队', '李老板', '13800001002', '安达运输有限公司', '6222029876543210123', '建设银行', '0', 'admin', NOW(), '测试车队2');

-- 更新现有司机数据（关联测试车队）
UPDATE logistics_driver SET driver_type = 'fleet', fleet_id = 1 WHERE driver_id = 1;
