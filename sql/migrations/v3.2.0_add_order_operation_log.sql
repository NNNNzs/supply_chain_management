-- ======================================================
-- 订单操作日志表
-- 版本: v3.2.0
-- 日期: 2026-04-21
-- 说明: 记录订单的所有操作历史，包括创建、修改、状态变更、开票、结算等
-- ======================================================

-- 创建订单操作日志表
CREATE TABLE IF NOT EXISTS `logistics_order_log` (
  `log_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `operation_type` VARCHAR(30) NOT NULL COMMENT '操作类型：create-创建,update-修改,status_change-状态变更,invoice-开票,settlement-结算,delete-删除',
  `operation_content` VARCHAR(500) DEFAULT NULL COMMENT '操作内容描述',
  `before_value` VARCHAR(200) DEFAULT NULL COMMENT '操作前值',
  `after_value` VARCHAR(200) DEFAULT NULL COMMENT '操作后值',
  `operator_id` BIGINT(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
  `operation_time` DATETIME NOT NULL COMMENT '操作时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

-- 更新版本号
INSERT INTO `db_version` (`version`, `description`, `executed_at`, `execute_by`)
VALUES ('v3.2.0', '订单操作日志表', NOW(), 'system')
ON DUPLICATE KEY UPDATE
  `description` = VALUES(`description`),
  `executed_at` = VALUES(`executed_at`);
