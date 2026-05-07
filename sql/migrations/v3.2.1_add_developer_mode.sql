-- ======================================================
-- 添加开发者模式参数配置
-- 版本: v3.2.1
-- 日期: 2026-05-07
-- 说明: 添加开发者模式开关，开启后可绕过订单删除校验
-- ======================================================

-- 插入开发者模式配置参数
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES ('开发者模式', 'sys.developer.mode', 'false', 'Y', '开发者模式：开启后可绕过业务校验强制删除数据（仅开发环境使用）', 'admin', NOW(), '', NULL)
ON DUPLICATE KEY UPDATE
  `config_value` = 'false',
  `remark` = '开发者模式：开启后可绕过业务校验强制删除数据（仅开发环境使用）';

-- 更新版本号
INSERT INTO `db_version` (`version`, `description`, `executed_at`, `execute_by`)
VALUES ('v3.2.1', '开发者模式参数配置', NOW(), 'system')
ON DUPLICATE KEY UPDATE
  `description` = VALUES(`description`),
  `executed_at` = VALUES(`executed_at`);
