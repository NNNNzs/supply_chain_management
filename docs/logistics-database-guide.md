# 物流管理系统数据库脚本

## 数据库脚本位置

```
/Users/nnnnzs/project/supply_chain_management/sql/logistics.sql
```

## 执行方式

### 1. 通过命令行执行

```bash
mysql -u your_username -p your_database < /Users/nnnnzs/project/supply_chain_management/sql/logistics.sql
```

### 2. 通过 Navicat 或其他数据库工具执行

1. 打开 Navicat
2. 连接到你的数据库
3. 点击 "查询" → "新建查询"
4. 复制 `sql/logistics.sql` 文件内容
5. 点击 "运行"

## 脚本包含内容

### 数据表（13张）

1. **logistics_customer** - 客户信息表
2. **logistics_goods** - 货物信息表
3. **logistics_driver** - 司机信息表
4. **logistics_vehicle** - 车辆信息表
5. **logistics_bill** - 提单表（委托单）
6. **logistics_bill_item** - 提单货物明细表
7. **logistics_bill_order_detail** - 提单运单明细表
8. **logistics_order** - 运单表（货运单/派车单）
9. **logistics_receipt** - 回单信息表
10. **logistics_invoice_batch** - 发票批次表
11. **logistics_invoice_detail** - 发票批次明细表
12. **logistics_settlement** - 财务结算表
13. **logistics_settlement_detail** - 结算明细表

### 菜单权限

脚本包含完整的菜单和权限配置，菜单ID从 2000 开始：

- 2000 - 物流管理（一级目录）
- 2100 - 基础数据管理（二级目录）
  - 2101 - 客户管理
  - 2102 - 货物管理
  - 2103 - 司机管理
  - 2104 - 车辆管理
- 2200 - 运输业务管理（二级目录）
  - 2204 - 提单管理
  - 2205 - 配载管理
  - 2201 - 运单管理
  - 2202 - 回单管理
  - 2203 - 发票管理
- 2300 - 财务结算管理（二级目录）
  - 2301 - 应收结算
  - 2302 - 应付结算
  - 2303 - 财务报表

### 测试数据

脚本包含初始测试数据：
- 2个客户（常州品晟、上海佳钰）
- 4种货物（线材、精线、银亮棒、圆钢）
- 1个司机（李正鹏）
- 3辆车（皖E14341、皖E12345、鲁NG7049）

## 执行后验证

执行脚本后，请验证以下内容：

1. **表是否创建成功**
   ```sql
   SHOW TABLES LIKE 'logistics%';
   ```

2. **菜单是否插入成功**
   ```sql
   SELECT * FROM sys_menu WHERE menu_id >= 2000 ORDER BY menu_id;
   ```

3. **测试数据是否插入成功**
   ```sql
   SELECT * FROM logistics_customer;
   SELECT * FROM logistics_goods;
   ```

## 权限配置说明

脚本创建的权限标识符格式：`logistics:模块:操作`

例如：
- `logistics:customer:list` - 客户列表
- `logistics:customer:add` - 客户新增
- `logistics:customer:edit` - 客户修改
- `logistics:customer:remove` - 客户删除
- `logistics:order:import` - 订单导入
- `logistics:invoice:merge` - 合并开票

## 后续操作

1. 执行 SQL 脚本
2. 在若依后台系统中，将物流管理权限分配给相应的角色
3. 重新登录系统，即可看到"物流管理"菜单

## 注意事项

1. 脚本使用了 `drop table if exists`，会先删除已存在的表，谨慎使用
2. 菜单ID从 2000 开始，避免与系统现有菜单冲突
3. 如需修改测试数据，可以直接编辑 SQL 文件
