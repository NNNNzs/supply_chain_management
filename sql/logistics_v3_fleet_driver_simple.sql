-- 物流管理系统 v3.0 - 司机/车队管理重构脚本（简化版）
-- 功能：拆分车队和司机为独立实体
-- 日期：2026-04-15
-- 说明：手动分步执行，每步都有检查

-- ============================================================
-- 第一步：创建车队表
-- ============================================================
DROP TABLE IF EXISTS logistics_fleet;

CREATE TABLE logistics_fleet (
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
-- 第二步：修改司机表
-- ============================================================

-- 2.1 删除可能存在的旧字段（忽略错误）
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS driver_type;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_owner_name;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_owner_phone;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_account_name;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_account_number;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS fleet_bank_name;
ALTER TABLE logistics_driver DROP COLUMN IF EXISTS driver_code;

-- 2.2 添加新字段
ALTER TABLE logistics_driver ADD COLUMN driver_type VARCHAR(20) DEFAULT 'individual' COMMENT '司机类型（individual个人，fleet车队）' AFTER driver_license;
ALTER TABLE logistics_driver ADD COLUMN fleet_id BIGINT DEFAULT NULL COMMENT '所属车队ID（车队司机时关联）' AFTER driver_type;

-- 2.3 添加索引
ALTER TABLE logistics_driver ADD INDEX idx_driver_type (driver_type);
ALTER TABLE logistics_driver ADD INDEX idx_fleet_id (fleet_id);

-- ============================================================
-- 第三步：插入示例数据
-- ============================================================
INSERT INTO logistics_fleet (fleet_name, owner_name, owner_phone, account_name, account_number, bank_name, status, create_by, create_time, remark)
VALUES
('顺风物流车队', '王老板', '13800001001', '顺风物流有限公司', '6222021234567890123', '工商银行', '0', 'admin', NOW(), '测试车队1'),
('安达运输车队', '李老板', '13800001002', '安达运输有限公司', '6222029876543210123', '建设银行', '0', 'admin', NOW(), '测试车队2');

-- ============================================================
-- 第四步：菜单配置（需要手动调整parent_id）
-- ============================================================

-- 4.1 先查询物流管理的菜单ID，记下这个数字
-- SELECT menu_id FROM sys_menu WHERE menu_name = '物流管理';

-- 4.2 手动替换下面的 @PARENT_ID 为实际值，然后执行以下语句

-- 插入车队管理主菜单
-- INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
-- VALUES
-- ('车队管理', @PARENT_ID, 4, 'fleet', 'logistics/fleet/index', 1, 0, 'C', '0', '0', 'logistics:fleet:list', 'truck', 'admin', NOW(), '车队管理菜单');

-- 4.3 记录刚才插入的菜单ID（假设是 XXX）

-- 插入车队管理按钮权限（替换 @FLEET_MENU_ID）
-- INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
-- ('车队查询', @FLEET_MENU_ID, 1, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:query', '#', 'admin', NOW()),
-- ('车队新增', @FLEET_MENU_ID, 2, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:add', '#', 'admin', NOW()),
-- ('车队修改', @FLEET_MENU_ID, 3, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:edit', '#', 'admin', NOW()),
-- ('车队删除', @FLEET_MENU_ID, 4, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:remove', '#', 'admin', NOW()),
-- ('车队导出', @FLEET_MENU_ID, 5, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:export', '#', 'admin', NOW());

SELECT '前两步执行完成，菜单配置需要手动执行' AS status;
