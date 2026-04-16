# 数据库增量迁移脚本

> **最后更新**: 2026-04-16

---

## 📁 目录说明

此目录用于存放物流管理系统的**增量迁移脚本**，每次数据库变更都应创建对应的迁移脚本。

---

## 📝 脚本命名规范

### 格式

```
vX.Y.Z_brief_description.sql

v: 版本标识
X.Y.Z: 版本号
brief_description: 简短描述（英文，用下划线分隔）
```

### 示例

```
migrations/
├── v3.1.1_init_version_tracker.sql
├── v3.1.2_add_driver_phone_index.sql
├── v3.2.0_add_settlement_report.sql
├── v3.2.0_add_settlement_report_rollback.sql
└── v3.2.1_fix_order_amount_calculate.sql
```

---

## 📋 脚本模板

### 增量迁移脚本模板

```sql
-- =============================
-- 物流管理系统增量迁移脚本
-- 版本: v3.1.1
-- 说明: 添加索引优化司机查询
-- 作者: xxx
-- 日期: 2026-04-17
-- 依赖: v3.1.0
-- =============================

-- 检查版本依赖（确保前置版本已执行）
SELECT IF((SELECT COUNT(*) FROM db_version WHERE version = 'v3.1.0') > 0,
    '版本依赖检查通过',
    CONCAT('ERROR: 缺少前置版本 v3.1.0')) AS dependency_check;

-- 1. 业务变更开始
-- ----------------------------------------------------------------

-- 示例：添加索引（先检查是否存在）
SET @index_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'logistics_driver'
    AND INDEX_NAME = 'idx_driver_phone'
);

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_driver_phone ON logistics_driver(driver_phone)',
    'SELECT "Index idx_driver_phone already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------------------------------------------
-- 2. 业务变更结束

-- 3. 更新版本记录
INSERT INTO db_version (version, description, executed_at, execute_by)
VALUES ('v3.1.1', '添加司机电话索引', NOW(), 'admin')
ON DUPLICATE KEY UPDATE executed_at = NOW();

-- 4. 执行完成提示
SELECT CONCAT('迁移脚本 v3.1.1 执行完成！') AS message;
```

### 回滚脚本模板

```sql
-- =============================
-- 回滚脚本: v3.1.1
-- 说明: 删除司机电话索引
-- 作者: xxx
-- 日期: 2026-04-17
-- =============================

-- 1. 回滚业务变更
DROP INDEX IF EXISTS idx_driver_phone ON logistics_driver;

-- 2. 删除版本记录
DELETE FROM db_version WHERE version = 'v3.1.1';

-- 3. 执行完成提示
SELECT CONCAT('回滚脚本 v3.1.1 执行完成！') AS message;
```

---

## 🚀 执行流程

### 开发环境

```bash
# 1. 创建迁移脚本
# 在 migrations/ 目录下创建 v3.1.1_xxx.sql

# 2. 测试脚本
mysql -u root -p database_name < migrations/v3.1.1_xxx.sql

# 3. 验证结果
mysql -u root -p database_name -e "SELECT * FROM db_version ORDER BY executed_at DESC LIMIT 5;"
```

### 生产环境

```bash
# 1. 备份数据库
mysqldump -u username -p database_name > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 在测试环境验证脚本
# （确保脚本可重复执行且无副作用）

# 3. 执行迁移（按版本顺序）
mysql -u username -p database_name < migrations/v3.1.1_xxx.sql

# 4. 验证迁移结果
mysql -u username -p database_name -e "SELECT * FROM v_db_version_history LIMIT 5;"

# 5. 重启应用服务
docker-compose restart backend
```

---

## ⚠️ 编写规范

### 必须遵守的规则

1. **幂等性**: 脚本可以重复执行而不产生错误
2. **向后兼容**: 不能破坏现有数据和功能
3. **原子性**: 使用事务确保数据一致性
4. **可回滚**: 提供对应的回滚脚本

### 禁止的操作

- ❌ 使用 `DROP TABLE`（除非是废弃表）
- ❌ 直接修改生产数据（如 UPDATE/DELETE 历史）
- ❌ 删除索引或约束（除非确实不需要）
- ❌ 修改字段类型（可能导致数据丢失）

### 推荐的做法

- ✅ 先检查对象是否存在（表、索引、字段）
- ✅ 使用 `IF EXISTS` / `IF NOT EXISTS`
- ✅ 添加详细注释说明变更原因
- ✅ 包含版本依赖检查
- ✅ 更新版本记录表

---

## 📊 当前数据库版本

### 查询当前版本

```sql
-- 查看最新版本
SELECT * FROM v_db_version_history LIMIT 1;

-- 查看所有版本历史
SELECT * FROM v_db_version_history;

-- 查看物流相关表
SHOW TABLES LIKE 'logistics%';
```

### 版本历史

| 版本 | 日期 | 说明 | 状态 |
|------|------|------|------|
| v3.1.0 | 2026-04-16 | 司机/车队重构、订单多货物支持、合并所有脚本 | ✅ 已部署 |

---

## 📝 待办事项

- [ ] 创建 v3.1.1 版本追踪表脚本
- [ ] 为每个历史版本创建增量脚本
- [ ] 建立自动化测试流程
