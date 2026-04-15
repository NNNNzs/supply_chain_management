-- 物流管理系统 v3.0 - 司机/车队管理重构脚本（修复版）
-- 功能：拆分车队和司机为独立实体
-- 日期：2026-04-15
-- 说明：此脚本已处理各种边界情况，可安全重复执行

-- ============================================================
-- 一、创建车队表
-- ============================================================
-- 先删除表（如果存在）
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
-- 二、修改司机表
-- ============================================================

-- 删除原有外键约束（如果存在）
-- 需要先查询外键名称并删除
SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'logistics_driver'
    AND CONSTRAINT_NAME = 'fk_driver_fleet'
);
SET @sql = IF(@constraint_name IS NOT NULL,
    CONCAT('ALTER TABLE logistics_driver DROP FOREIGN KEY ', @constraint_name),
    'SELECT "No foreign key to drop" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 1. 删除原有车队相关字段（安全删除）
SET @dbname = DATABASE();
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'driver_type'
  ) > 0,
  'ALTER TABLE logistics_driver DROP COLUMN driver_type',
  'SELECT "Column driver_type does not exist" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'fleet_owner_name'
  ) > 0,
  'ALTER TABLE logistics_driver DROP COLUMN fleet_owner_name',
  'SELECT "Column fleet_owner_name does not exist" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'fleet_owner_phone'
  ) > 0,
  'ALTER TABLE logistics_driver DROP COLUMN fleet_owner_phone',
  'SELECT "Column fleet_owner_phone does not exist" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'fleet_account_name'
  ) > 0,
  'ALTER TABLE logistics_driver DROP COLUMN fleet_account_name',
  'SELECT "Column fleet_account_name does not exist" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'fleet_account_number'
  ) > 0,
  'ALTER TABLE logistics_driver DROP COLUMN fleet_account_number',
  'SELECT "Column fleet_account_number does not exist" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'fleet_bank_name'
  ) > 0,
  'ALTER TABLE logistics_driver DROP COLUMN fleet_bank_name',
  'SELECT "Column fleet_bank_name does not exist" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 删除司机编码字段（如果存在）
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'driver_code'
  ) > 0,
  'ALTER TABLE logistics_driver DROP COLUMN driver_code',
  'SELECT "Column driver_code does not exist" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 添加新字段（如果不存在）
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'driver_type'
  ) = 0,
  'ALTER TABLE logistics_driver ADD COLUMN driver_type VARCHAR(20) DEFAULT "individual" COMMENT "司机类型（individual个人，fleet车队）" AFTER driver_license',
  'SELECT "Column driver_type already exists" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND COLUMN_NAME = 'fleet_id'
  ) = 0,
  'ALTER TABLE logistics_driver ADD COLUMN fleet_id BIGINT DEFAULT NULL COMMENT "所属车队ID（车队司机时关联）" AFTER driver_type',
  'SELECT "Column fleet_id already exists" AS message'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. 添加索引（如果不存在）
SET @index_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND INDEX_NAME = 'idx_driver_type'
);
SET @sql = IF(@index_exists = 0,
    'ALTER TABLE logistics_driver ADD INDEX idx_driver_type (driver_type)',
    'SELECT "Index idx_driver_type already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = 'logistics_driver'
    AND INDEX_NAME = 'idx_fleet_id'
);
SET @sql = IF(@index_exists = 0,
    'ALTER TABLE logistics_driver ADD INDEX idx_fleet_id (fleet_id)',
    'SELECT "Index idx_fleet_id already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================
-- 三、示例数据
-- ============================================================

-- 插入测试车队（如果不存在）
INSERT IGNORE INTO logistics_fleet (fleet_id, fleet_name, owner_name, owner_phone, account_name, account_number, bank_name, status, create_by, create_time, remark)
VALUES
(1, '顺风物流车队', '王老板', '13800001001', '顺风物流有限公司', '6222021234567890123', '工商银行', '0', 'admin', NOW(), '测试车队1'),
(2, '安达运输车队', '李老板', '13800001002', '安达运输有限公司', '6222029876543210123', '建设银行', '0', 'admin', NOW(), '测试车队2');

-- ============================================================
-- 四、菜单配置
-- ============================================================

-- 获取物流管理菜单ID
SET @logistics_parent_id = NULL;
SELECT menu_id INTO @logistics_parent_id FROM sys_menu WHERE menu_name = '物流管理' LIMIT 1;

-- 只有在找到父菜单时才插入
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT
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
WHERE @logistics_parent_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '车队管理' AND parent_id = @logistics_parent_id);

-- 获取刚插入的车队管理菜单ID
SET @fleet_menu_id = LAST_INSERT_ID();

-- 车队管理按钮权限（只有在车队菜单存在时才插入）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '车队查询', @fleet_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:query', '#', 'admin', NOW()
WHERE @fleet_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '车队查询' AND parent_id = @fleet_menu_id);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '车队新增', @fleet_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:add', '#', 'admin', NOW()
WHERE @fleet_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '车队新增' AND parent_id = @fleet_menu_id);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '车队修改', @fleet_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:edit', '#', 'admin', NOW()
WHERE @fleet_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '车队修改' AND parent_id = @fleet_menu_id);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '车队删除', @fleet_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:remove', '#', 'admin', NOW()
WHERE @fleet_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '车队删除' AND parent_id = @fleet_menu_id);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '车队导出', @fleet_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'logistics:fleet:export', '#', 'admin', NOW()
WHERE @fleet_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '车队导出' AND parent_id = @fleet_menu_id);

SELECT '脚本执行完成' AS status;
