# 物流管理系统数据库脚本

> **文档版本**: v1.0
> **最后更新**: 2026-04-16
> **数据库版本**: v3.1

---

## 📋 目录

- [脚本说明](#脚本说明)
- [版本管理规范](#版本管理规范)
- [生产环境部署](#生产环境部署)
- [变更历史](#变更历史)

---

## 📁 脚本说明

### 一、基础框架脚本

| 文件 | 说明 | 状态 |
|------|------|------|
| `ry_20260321.sql` | 若依框架基础表（系统管理、权限等） | ✅ 稳定 |
| `quartz.sql` | 定时任务表 | ✅ 稳定 |

### 二、物流模块脚本

| 文件 | 说明 | 状态 |
|------|------|------|
| `logistics.sql` | 物流模块完整初始化脚本（v3.1） | ✅ 当前版本 |

### 三、增量迁移脚本

| 文件 | 说明 | 目标版本 | 状态 |
|------|------|----------|------|
| `migrations/` | 增量迁移脚本目录（未来版本） | - | 📁 待创建 |

---

## 📐 版本管理规范

### 版本命名规则

```
v主版本.次版本.修订版本

示例: v3.1.0
- v3: 主版本（重大架构变更）
- 1: 次版本（功能模块新增）
- 0: 修订版本（bug修复、小优化）
```

### 脚本类型定义

#### 1. 完整初始化脚本

**文件名**: `logistics.sql`
**用途**: 全新环境初始化，包含所有表结构和基础数据
**执行方式**:
```bash
mysql -u username -p database_name < logistics.sql
```

**包含内容**:
- 所有物流相关表结构（DROP + CREATE）
- 菜单权限配置（INSERT IGNORE，可重复执行）
- 示例测试数据

**⚠️ 警告**: 此脚本会删除并重建所有物流表，**仅用于全新环境或开发环境**，生产环境禁止执行！

#### 2. 增量迁移脚本

**目录**: `migrations/`
**文件命名**: `vX.Y.Z_description.sql`

**示例**:
```
migrations/
├── v3.1.1_add_driver_phone_index.sql
├── v3.2.0_add_settlement_report.sql
└── v3.2.1_fix_order_amount_calculate.sql
```

**脚本规范**:
```sql
-- =============================
-- 物流管理系统增量迁移脚本
-- 版本: v3.1.1
-- 说明: 添加司机电话索引优化查询
-- 作者: xxx
-- 日期: 2026-04-17
-- =============================

-- 检查版本（防止重复执行）
-- @echo 检查数据库版本...

-- 1. 添加索引（如果不存在）
SET @index_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'logistics_driver'
    AND INDEX_NAME = 'idx_driver_phone'
);

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_driver_phone ON logistics_driver(driver_phone)',
    'SELECT "Index already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 更新版本记录
INSERT INTO db_version (version, description, executed_at)
VALUES ('v3.1.1', '添加司机电话索引', NOW())
ON DUPLICATE KEY UPDATE executed_at = NOW();

-- 执行完成提示
SELECT '迁移脚本 v3.1.1 执行完成！' AS message;
```

**增量脚本特点**:
- ✅ 可重复执行（幂等性）
- ✅ 向后兼容
- ✅ 自动记录版本
- ✅ 回滚友好

#### 3. 回滚脚本

**文件命名**: `vX.Y.Z_description_rollback.sql`

**示例**:
```sql
-- =============================
-- 回滚脚本: v3.1.1
-- 说明: 删除司机电话索引
-- =============================

DROP INDEX IF EXISTS idx_driver_phone ON logistics_driver;

-- 删除版本记录
DELETE FROM db_version WHERE version = 'v3.1.1';

SELECT '回滚脚本 v3.1.1 执行完成！' AS message;
```

---

## 🚀 生产环境部署

### 部署前检查清单

- [ ] 备份数据库
- [ ] 在测试环境验证脚本
- [ ] 检查脚本版本号
- [ ] 确认执行顺序
- [ ] 准备回滚方案

### 部署流程

#### 1. 备份数据库

```bash
# 备份整个数据库
mysqldump -u username -p database_name > backup_$(date +%Y%m%d_%H%M%S).sql

# 或仅备份物流相关表
mysqldump -u username -p database_name logistics_* > backup_logistics_$(date +%Y%m%d_%H%M%S).sql
```

#### 2. 执行增量迁移

```bash
# 按版本顺序执行
cd migrations/

# 示例：从 v3.1.0 升级到 v3.1.2
mysql -u username -p database_name < v3.1.1_add_driver_phone_index.sql
mysql -u username -p database_name < v3.1.2_fix_order_calculate.sql
```

#### 3. 验证迁移结果

```sql
-- 查看数据库版本
SELECT * FROM db_version ORDER BY executed_at DESC LIMIT 10;

-- 验证表结构
SHOW TABLES LIKE 'logistics%';

-- 验证索引
SHOW INDEX FROM logistics_driver;
```

#### 4. 重启应用服务

```bash
# Docker 环境
docker-compose restart backend

# 或重启 Spring Boot 应用
systemctl restart supply-chain-backend
```

#### 5. 功能验证

- [ ] 登录系统
- [ ] 检查菜单权限
- [ ] 测试核心功能
- [ ] 查看日志是否有错误

### 紧急回滚

```bash
# 1. 停止应用服务
docker-compose stop backend

# 2. 恢复数据库备份
mysql -u username -p database_name < backup_20260416_120000.sql

# 3. 执行回滚脚本（如果有）
mysql -u username -p database_name < migrations/v3.1.1_xxx_rollback.sql

# 4. 重启应用服务
docker-compose start backend
```

---

## 📊 数据库版本表

### 创建版本记录表

```sql
-- 数据库版本管理表
CREATE TABLE IF NOT EXISTS db_version (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  version VARCHAR(20) NOT NULL UNIQUE COMMENT '版本号',
  description VARCHAR(500) COMMENT '版本说明',
  executed_at DATETIME NOT NULL COMMENT '执行时间',
  execute_by VARCHAR(50) COMMENT '执行人',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_version (version),
  KEY idx_executed_at (executed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库版本记录表';

-- 初始化当前版本
INSERT INTO db_version (version, description, executed_at, execute_by)
VALUES ('v3.1.0', '物流管理系统 v3.1 - 司机/车队重构、订单多货物支持', NOW(), 'admin');
```

### 版本历史查询

```sql
-- 查看所有版本历史
SELECT * FROM db_version ORDER BY executed_at DESC;

-- 查看当前版本
SELECT * FROM db_version ORDER BY executed_at DESC LIMIT 1;
```

---

## 📝 变更历史

### 物流模块版本历史

| 版本 | 日期 | 说明 | 脚本文件 | 作者 |
|------|------|------|----------|------|
| v1.0.0 | 2026-04-10 | 物流模块初始版本（客户、货物、司机、车辆、订单） | logistics.sql | - |
| v2.0.0 | 2026-04-14 | 新增提单管理、配载管理、回单管理、发票管理、财务结算 | - | - |
| v3.0.0 | 2026-04-15 | 订单多货物明细支持、司机/车队分类管理 | - | - |
| v3.1.0 | 2026-04-16 | 司机/车队管理重构（车队作为独立实体）、合并所有脚本为单一 logistics.sql | logistics.sql | System |

### 下一步计划

- [ ] v3.1.1: 创建 migrations 目录结构
- [ ] v3.1.2: 添加版本记录表
- [ ] v3.2.0: 财务结算模块完善

---

## ⚠️ 注意事项

### 开发环境

- 可以使用完整初始化脚本快速重建
- 建议使用 Docker Compose 快速启停
- 定期清理测试数据

### 生产环境

- **禁止执行完整初始化脚本**
- **只执行增量迁移脚本**
- **每次变更前必须备份**
- **必须有回滚方案**

### 通用规范

1. **SQL 脚本规范**
   - 使用 UTF-8 编码
   - 每个语句以分号结尾
   - 添加适当注释
   - 使用大写写 SQL 关键字

2. **命名规范**
   - 表名：`logistics_模块名_功能名`
   - 索引：`idx_表名_字段名`
   - 唯一索引：`uk_表名_字段名`
   - 外键：`fk_表名_引用表名`

3. **字段规范**
   - 主键：`xxx_id` (BIGINT AUTO_INCREMENT)
   - 状态：`status` (CHAR(1), 0正常 1停用)
   - 删除标志：`del_flag` (CHAR(1), 0存在 2删除)
   - 创建时间：`create_time` (DATETIME)
   - 更新时间：`update_time` (DATETIME)

---

## 📞 问题反馈

如有问题请联系：
- 技术支持：xxx@xxx.com
- 文档维护：xxx
