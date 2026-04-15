# 物流管理系统 v3.0 数据库更新说明

## 更新时间
2026-04-15

## 更新内容

### 1. 司机管理升级
添加司机/车队分类支持，区分个人司机和车队司机。

**执行脚本**：`logistics_v3_driver_update.sql`

**新增字段**：
- `driver_type` - 司机类型（individual个人，fleet车队）
- `fleet_owner_name` - 车队老板姓名
- `fleet_owner_phone` - 老板联系电话
- `fleet_account_name` - 车队开票账户名称
- `fleet_account_number` - 车队开票账号
- `fleet_bank_name` - 车队开户行

### 2. 订单多货物明细支持
创建订单货物明细表，支持一个订单包含多个货物明细。

**执行脚本**：`logistics_v3_order_goods.sql`

**新建表**：
- `logistics_order_goods` - 订单货物明细表

**新增字段**：
- `actual_load_weight` - 实际装车重量（吨）

## 更新步骤

### 方式一：手动执行（推荐）

1. **备份数据库**
   ```sql
   -- 备份当前数据库
   mysqldump -u username -p database_name > backup_$(date +%Y%m%d).sql
   ```

2. **执行司机管理升级脚本**
   ```bash
   mysql -u username -p database_name < sql/logistics_v3_driver_update.sql
   ```

3. **执行订单多货物明细脚本**
   ```bash
   mysql -u username -p database_name < sql/logistics_v3_order_goods.sql
   ```

### 方式二：使用 Navicat 或其他工具

1. 打开 Navicat，连接到数据库
2. 选择对应的数据库
3. 点击"查询" → "新建查询"
4. 分别打开并执行两个 SQL 脚本文件
5. 检查执行结果，确保无错误

## 验证更新

### 验证司机表
```sql
-- 查看司机表结构
DESC logistics_driver;

-- 应该包含以下新字段：
-- driver_type, fleet_owner_name, fleet_owner_phone,
-- fleet_account_name, fleet_account_number, fleet_bank_name
```

### 验证订单货物明细表
```sql
-- 查看订单货物明细表是否创建成功
SHOW TABLES LIKE 'logistics_order_goods';

-- 查看表结构
DESC logistics_order_goods;
```

### 验证订单表
```sql
-- 查看订单表是否有 actual_load_weight 字段
DESC logistics_order;
```

## 注意事项

1. **数据迁移**：现有司机的 `driver_type` 字段会自动设置为 `individual`（个人司机）
2. **兼容性**：保留原有字段，确保向后兼容
3. **索引优化**：脚本中已包含必要的索引创建语句

## 回滚方案

如果需要回滚更新：

```sql
-- 回滚司机表变更
ALTER TABLE logistics_driver DROP COLUMN driver_type;
ALTER TABLE logistics_driver DROP COLUMN fleet_owner_name;
ALTER TABLE logistics_driver DROP COLUMN fleet_owner_phone;
ALTER TABLE logistics_driver DROP COLUMN fleet_account_name;
ALTER TABLE logistics_driver DROP COLUMN fleet_account_number;
ALTER TABLE logistics_driver DROP COLUMN fleet_bank_name;

-- 回滚订单表变更
ALTER TABLE logistics_order DROP COLUMN actual_load_weight;

-- 删除订单货物明细表
DROP TABLE IF EXISTS logistics_order_goods;
```

## 更新后操作

1. **重启后端服务**
   ```bash
   # 如果使用 Docker
   docker-compose restart backend

   # 或者重启 Spring Boot 应用
   ```

2. **清除浏览器缓存**
   - 前端页面使用了新的字段和组件
   - 建议清除缓存后重新访问

3. **验证功能**
   - 测试司机管理的新增/修改功能
   - 测试司机类型切换（个人/车队）
   - 测试订单管理的多货物明细功能
   - 测试树形表格展示

## 问题反馈

如遇到问题，请检查：
1. SQL 脚本是否正确执行
2. 数据库用户是否有足够的权限
3. 是否存在字段名冲突
4. 查看后端日志是否有错误信息
