# 司机/车队管理重构 - 执行指南

## 变更概述

将原有的司机管理拆分为两个独立的管理模块：
1. **车队管理**：管理车队信息和车队结算账户
2. **司机管理**：管理司机信息，区分个人司机和车队司机

## 数据库变更

### 执行脚本
```bash
# 文件位置：sql/logistics_v3_fleet_driver_refactor.sql
```

### 主要变更
1. **新增车队表** `logistics_fleet`
2. **修改司机表** `logistics_driver`
   - 删除 `driver_code` 字段（使用自增ID）
   - 删除原有车队相关字段
   - 新增 `driver_type` 字段（个人/车队）
   - 新增 `fleet_id` 字段（关联车队）

## 后端变更

### 新增文件
| 文件 | 说明 |
|------|------|
| `ruoyi-logistics/domain/LogisticsFleet.java` | 车队实体类 |
| `ruoyi-logistics/mapper/LogisticsFleetMapper.java` | 车队Mapper接口 |
| `ruoyi-logistics/mapper/logistics/LogisticsFleetMapper.xml` | 车队Mapper XML |
| `ruoyi-logistics/service/ILogisticsFleetService.java` | 车队Service接口 |
| `ruoyi-logistics/service/impl/LogisticsFleetServiceImpl.java` | 车队Service实现 |
| `ruoyi-admin/controller/logistics/LogisticsFleetController.java` | 车队Controller |

### 修改文件
| 文件 | 变更说明 |
|------|----------|
| `ruoyi-logistics/domain/LogisticsDriver.java` | 删除driver_code，添加driver_type、fleet_id |
| `ruoyi-logistics/mapper/logistics/LogisticsDriverMapper.xml` | 更新SQL，关联车队表查询 |

## 前端变更

### 新增文件
| 文件 | 说明 |
|------|------|
| `ruoyi-ui/src/api/logistics/fleet.js` | 车队API |
| `ruoyi-ui/src/views/logistics/fleet/index.vue` | 车队管理页面 |

### 修改文件
| 文件 | 变更说明 |
|------|----------|
| `ruoyi-ui/src/api/logistics/driver.js` | 删除driverCode相关参数 |
| `ruoyi-ui/src/views/logistics/driver/index.vue` | 去掉司机编码，添加车队选择 |

## 执行步骤

### 1. 执行数据库脚本
```sql
-- 在数据库中执行
source sql/logistics_v3_fleet_driver_refactor.sql
```

### 2. 重新编译后端
```bash
# 在项目根目录执行
mvn clean package
```

### 3. 重启后端服务

### 4. 刷新前端页面
```bash
cd ruoyi-ui
npm run dev
```

## 业务规则

### 个人司机
- 有个人银行账户（银行账号、开户行、账户姓名）
- 运费直接结算到个人账户

### 车队司机
- 关联到车队
- 无个人银行账户
- 运费结算到车队账户

### 车队管理
- 车队可以有多个司机
- 删除车队前检查是否有关联司机
- 车队有独立的结算账户信息

## 菜单配置

执行SQL脚本后会自动创建以下菜单：
- 车队管理（含查询、新增、修改、删除、导出权限）

如需手动配置菜单，请参考SQL脚本中的菜单配置部分。
