-- =============================
-- 物流管理系统 - 版本记录表初始化
-- 版本: v3.1.0
-- 说明: 创建数据库版本管理表，记录所有迁移历史
-- 作者: System
-- 日期: 2026-04-16
-- =============================

-- ----------------------------
-- 1、创建版本记录表
-- ----------------------------
DROP TABLE IF EXISTS db_version;
CREATE TABLE db_version (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  version VARCHAR(20) NOT NULL UNIQUE COMMENT '版本号',
  description VARCHAR(500) COMMENT '版本说明',
  executed_at DATETIME NOT NULL COMMENT '执行时间',
  execute_by VARCHAR(50) COMMENT '执行人',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_version (version),
  KEY idx_executed_at (executed_at)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='数据库版本记录表';

-- ----------------------------
-- 2、初始化当前版本
-- ----------------------------
INSERT INTO db_version (version, description, executed_at, execute_by)
VALUES ('v3.1.0', '物流管理系统 v3.1 - 司机/车队重构、订单多货物支持、合并所有脚本', NOW(), 'admin');

-- ----------------------------
-- 3、创建版本历史视图（方便查询）
-- ----------------------------
CREATE OR REPLACE VIEW v_db_version_history AS
SELECT
  version,
  description,
  executed_at,
  execute_by,
  TIMESTAMPDIFF(DAY, executed_at, NOW()) AS days_ago
FROM db_version
ORDER BY executed_at DESC;

-- ----------------------------
-- 4、执行完成提示
-- ----------------------------
SELECT '数据库版本管理表初始化完成！' AS message;
SELECT '当前版本: v3.1.0' AS current_version;
SELECT '以后所有数据库变更请使用增量迁移脚本，存放在 migrations/ 目录下' AS notice;
