-- 物流管理系统 v3.0 - 司机管理升级脚本
-- 功能：添加司机/车队分类支持
-- 日期：2026-04-15

-- 1. 为司机表添加司机类型和车队相关字段
ALTER TABLE logistics_driver ADD COLUMN driver_type varchar(20) DEFAULT 'individual' COMMENT '司机类型（individual个人，fleet车队）' AFTER driver_license;

ALTER TABLE logistics_driver ADD COLUMN fleet_owner_name varchar(50) DEFAULT NULL COMMENT '车队老板姓名' AFTER driver_type;

ALTER TABLE logistics_driver ADD COLUMN fleet_owner_phone varchar(20) DEFAULT NULL COMMENT '老板联系电话' AFTER fleet_owner_name;

ALTER TABLE logistics_driver ADD COLUMN fleet_account_name varchar(100) DEFAULT NULL COMMENT '车队开票账户名称' AFTER bank_name;

ALTER TABLE logistics_driver ADD COLUMN fleet_account_number varchar(50) DEFAULT NULL COMMENT '车队开票账号' AFTER fleet_account_name;

ALTER TABLE logistics_driver ADD COLUMN fleet_bank_name varchar(100) DEFAULT NULL COMMENT '车队开户行' AFTER fleet_account_number;

-- 2. 更新现有数据，默认为个人司机
UPDATE logistics_driver SET driver_type = 'individual' WHERE driver_type IS NULL;

-- 3. 添加索引优化查询
ALTER TABLE logistics_driver ADD INDEX idx_driver_type (driver_type);
