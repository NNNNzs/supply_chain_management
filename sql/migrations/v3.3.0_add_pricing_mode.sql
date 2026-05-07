-- ======================================================
-- 包车计价模式
-- 版本: v3.3.0
-- 日期: 2026-05-07
-- 说明: 新增计价方式字段，支持按重量和包车两种计价模式
-- ======================================================

-- 新增计价方式字段
ALTER TABLE logistics_order
ADD COLUMN pricing_mode VARCHAR(20) DEFAULT 'weight' COMMENT '计价方式(weight按重量,charter包车)'
AFTER order_type;

-- 字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('订单计价方式', 'logistics_pricing_mode', '0', 'admin', NOW(), '订单计价方式：按重量、包车');

-- 字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES (1, '按重量', 'weight', 'logistics_pricing_mode', '', 'primary', 'Y', '0', 'admin', NOW(), '按重量计价');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES (2, '包车', 'charter', 'logistics_pricing_mode', '', 'success', 'N', '0', 'admin', NOW(), '包车一口价');

-- 更新版本号
INSERT INTO `db_version` (`version`, `description`, `executed_at`, `execute_by`)
VALUES ('v3.3.0', '包车计价模式', NOW(), 'system')
ON DUPLICATE KEY UPDATE
  `description` = VALUES(`description`),
  `executed_at` = VALUES(`executed_at`);
