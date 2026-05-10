# 开发规范文档

> **项目**: 供应链管理系统
> **版本**: v1.0
> **最后更新**: 2026-04-14

## 文档说明

本文档定义供应链管理系统的开发规范，包括文档管理、代码规范、Git 工作流等。

---

## 一、文档管理规范 ⚠️ 重要

### 1.1 核心原则

**每次业务发生变化，必须同步更新需求文档。**

需求文档是业务逻辑的唯一真实来源，必须保持与实际业务一致。

### 1.2 业务变更定义

以下情况视为业务变更，需要更新需求文档：

- **新增功能**：添加新的业务功能或模块
- **修改功能**：修改现有功能的业务逻辑
- **删除功能**：移除已有的业务功能
- **业务规则调整**：修改业务规则、计算公式、状态流转等
- **数据字段变更**：新增、修改或删除数据字段
- **接口变更**：修改 API 接口的输入输出

### 1.3 需求文档更新流程

```
业务变更发生
    ↓
更新需求文档（docs/logistics/01-requirements.md）
    ↓
记录变更信息（日期、版本、变更内容、变更人）
    ↓
通知团队成员（如有必要）
    ↓
继续开发
```

### 1.4 变更记录格式

在需求文档末尾的「需求变更记录」表中记录：

| 日期 | 版本 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 2026-04-14 | v1.1 | 新增订单取消功能 | XXX |

### 1.5 文档同步要求

业务变更后，需要在以下文档中保持一致：

- **需求文档**（`docs/logistics/01-requirements.md`）：描述业务需求
- **设计文档**（`docs/logistics/02-design.md`）：描述技术实现
- **进度文档**（`docs/logistics/03-progress.md`）：跟踪实现进度

---

## 二、代码规范

### 2.1 后端代码规范

#### 2.1.1 命名规范

```java
// 实体类：名词 + 实体
public class LogisticsOrder { }

// Mapper 接口：实体名 + Mapper
public interface LogisticsOrderMapper { }

// Service 接口：I + 实体名 + Service
public interface ILogisticsOrderService { }

// Service 实现：实体名 + ServiceImpl
public class LogisticsOrderServiceImpl { }

// Controller：实体名 + Controller
public class LogisticsOrderController { }
```

#### 2.1.2 注释规范

```java
/**
 * 订单号生成
 *
 * @param orderType 订单类型（YS-运输，DB-短驳）
 * @param customerCode 客户编码
 * @return 订单号（格式：类型 + 客户编码 + 年月日 + 流水号）
 */
public String generateOrderNo(String orderType, String customerCode) {
    // 实现代码
}
```

#### 2.1.3 依赖包规范 ⚠️ 重要

**Spring Boot 3.x 迁移注意事项**

项目已升级到 Spring Boot 3.x，Java EE API 迁移到 Jakarta EE：

```java
// ❌ 错误：使用旧的 javax.servlet 包
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

// ✅ 正确：使用新的 jakarta.servlet 包
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
```

**常见迁移点**：
- `javax.servlet.*` → `jakarta.servlet.*`
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.validation.*` → `jakarta.validation.*`

**创建新 Controller 时的标准导入**：

```java
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;  // 注意是 jakarta 不是 javax
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.scm.common.annotation.Log;
import com.scm.common.core.controller.BaseController;
import com.scm.common.core.domain.AjaxResult;
```

#### 2.1.4 BaseEntity 字段填充规范 ⚠️ 重要

所有继承 `BaseEntity` 的实体类，在插入和更新操作时必须使用 `BaseEntityUtils` 工具类自动填充审计字段。

**填充规则：**

| 操作 | 方法 | 填充字段 |
|------|------|---------|
| 新增 | `BaseEntityUtils.fillCreateInfo(entity)` | createBy, updateBy, createTime, updateTime |
| 修改 | `BaseEntityUtils.fillUpdateInfo(entity)` | updateBy, updateTime |
| 批量新增 | `BaseEntityUtils.fillCreateInfoForBatch(list)` | 同上，批量处理 |
| 批量修改 | `BaseEntityUtils.fillUpdateInfoForBatch(list)` | 同上，批量处理 |

**使用示例：**

```java
@Service
public class LogisticsCustomerServiceImpl implements ILogisticsCustomerService {

    @Override
    public int insertCustomer(LogisticsCustomer customer) {
        // 校验业务逻辑
        if (!checkCustomerCodeUnique(customer)) {
            throw new ServiceException("客户编码已存在");
        }
        // 填充创建信息
        BaseEntityUtils.fillCreateInfo(customer);
        return customerMapper.insertCustomer(customer);
    }

    @Override
    public int updateCustomer(LogisticsCustomer customer) {
        // 校验业务逻辑
        if (!checkCustomerCodeUnique(customer)) {
            throw new ServiceException("客户编码已存在");
        }
        // 填充更新信息
        BaseEntityUtils.fillUpdateInfo(customer);
        return customerMapper.updateCustomer(customer);
    }
}
```

**注意事项：**
- `fillCreateInfo` 会同时设置 createBy、updateBy、createTime、updateTime
- `fillUpdateInfo` 只更新 updateBy 和 updateTime
- 工具类会自动获取当前登录用户名，无需手动传递
- 获取用户名失败时，时间字段仍会正常填充

### 2.2 前端代码规范

#### 2.2.1 组件命名

```javascript
// 页面组件：index.vue
views/logistics/order/index.vue

// 业务组件：功能名 + Component
components/OrderSelector.vue

// 工具函数：功能名 + utils
utils/priceCalculator.js
```

#### 2.2.2 API 命名

```javascript
// api/logistics/order.js
import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) { }

// 查询订单详细
export function getOrder(orderId) { }

// 新增订单
export function addOrder(data) { }

// 修改订单
export function updateOrder(data) { }

// 删除订单
export function delOrder(orderId) { }
```

### 2.3 数据库规范 ⚠️ 重要

#### 2.3.1 版本管理规范

**生产环境已上线，以后所有数据库变更必须使用增量迁移脚本。**

详细规范请参考：[数据库脚本管理文档](../../sql/README.md)

**核心原则**：
- ✅ **生产环境禁止执行完整初始化脚本**
- ✅ **所有变更必须创建增量迁移脚本**
- ✅ **脚本必须可重复执行（幂等性）**
- ✅ **必须先在测试环境验证**

#### 2.3.2 表命名

```
物流模块表：logistics_ + 功能名
示例：logistics_order, logistics_customer
```

#### 2.3.3 字段命名

```
使用下划线命名法（snake_case）
主键：表名_id
示例：order_id, customer_name
```

#### 2.3.4 数据库变更流程

```
需求变更发生
    ↓
更新需求文档
    ↓
创建增量迁移脚本（sql/migrations/vX.Y.Z_description.sql）
    ↓
在开发环境测试脚本
    ↓
在测试环境验证脚本
    ↓
备份生产数据库
    ↓
在生产环境执行增量脚本
    ↓
验证功能正常
    ↓
更新数据库版本记录
```

#### 2.3.5 增量迁移脚本规范

**文件位置**：`sql/migrations/`

**命名格式**：`vX.Y.Z_brief_description.sql`

**脚本模板**：

```sql
-- =============================
-- 物流管理系统增量迁移脚本
-- 版本: v3.1.1
-- 说明: 添加索引优化司机查询
-- 作者: xxx
-- 日期: 2026-04-17
-- 依赖: v3.1.0
-- =============================

-- 检查版本依赖
SELECT IF((SELECT COUNT(*) FROM db_version WHERE version = 'v3.1.0') > 0,
    '版本依赖检查通过',
    CONCAT('ERROR: 缺少前置版本 v3.1.0')) AS dependency_check;

-- 业务变更（示例：添加索引）
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

-- 更新版本记录
INSERT INTO db_version (version, description, executed_at, execute_by)
VALUES ('v3.1.1', '添加司机电话索引', NOW(), 'admin')
ON DUPLICATE KEY UPDATE executed_at = NOW();

SELECT '迁移脚本 v3.1.1 执行完成！' AS message;
```

---

## 三、Git 工作流

### 3.1 分支策略

```
main（生产环境）
  ↑
  ├── 开发完成后合并到 main
  │
develop（开发环境）
  ↑
  ├── feature/*（功能分支）
  └── bugfix/*（修复分支）
```

### 3.2 提交信息规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）：**
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

**示例：**
```
feat(logistics): 新增订单取消功能

- 新增订单取消接口
- 前端添加取消按钮
- 更新需求文档 v1.1

Closes #123
```

---

## 四、测试规范

### 4.1 E2E 自动化测试 ⚠️ 重要

项目使用 Playwright 进行端到端（E2E）自动化测试，覆盖核心业务流程。

详细文档：[E2E 测试文档](../e2e/README.md)

#### 4.1.1 测试覆盖要求

**每个业务功能必须编写对应的 E2E 测试**，包括但不限于：

| 操作类型 | 必须覆盖的测试 |
|----------|---------------|
| 新增功能 | 创建成功测试 |
| 编辑功能 | 编辑保存测试 |
| 删除功能 | 删除确认测试 |
| 搜索功能 | 搜索过滤测试 |
| 状态变更 | 状态流转测试 |
| 对话框/弹窗 | 打开、填写、提交测试 |

#### 4.1.2 新增功能开发流程

```
需求分析
    ↓
编写 E2E 测试用例（tests/ 目录）
    ↓
前端添加 data-testid（视图组件）
    ↓
实现后端 API
    ↓
实现前端页面
    ↓
运行 E2E 测试验证
    ↓
确认无错误后提交代码
```

#### 4.1.3 文件结构规范

```
新增一个业务模块时，需要创建以下文件：

1. e2e/tests/{模块}/
   ├── {模块}-create.spec.ts    # 创建测试
   ├── {模块}-edit.spec.ts      # 编辑测试
   ├── {模块}-search.spec.ts    # 搜索测试
   ├── {模块}-delete.spec.ts    # 删除测试
   └── ...（根据业务需要添加状态变更等测试）

2. e2e/pages/{模块}.page.ts     # Page Object 页面对象

3. 前端视图添加 data-testid 属性
```

#### 4.1.4 data-testid 命名规范

格式：`{模块}-{类型}-{名称}`

```
模块名：order, receipt, invoice, settlement, customer...
类型名：search, form, table, dialog, btn, detail
名称：功能描述

示例：
- order-search-orderNo     订单搜索-订单号输入框
- order-form-customer      订单表单-客户选择器
- order-table              订单表格
- receipt-confirm-dialog   回单确认对话框
- invoice-merge-submit-btn 发票合并-提交按钮
```

#### 4.1.5 测试编写规范

```typescript
// 1. 从 fixtures 导入（不是 @playwright/test）
import { test, expect } from '../../fixtures';

// 2. 使用 fixtures 提供的认证、错误捕获和 API 客户端
test('描述', async ({ page, authenticatedPage, errorCollector, apiClient }) => {

  // 3. 通过 API 准备测试数据，不依赖生产数据
  const res = await apiClient.createXxx({ ... });

  try {
    // 4. 使用 Page Object 执行 UI 操作
    // 5. 验证结果
  } finally {
    // 6. 清理测试数据
    await apiClient.deleteXxx(id).catch(() => {});
  }

  // 7. 断言无错误
  errorCollector.assertNoErrors();
});
```

#### 4.1.6 运行测试

```bash
cd e2e

# 运行全部测试
pnpm test

# 按模块运行
pnpm run test:order
pnpm run test:receipt
pnpm run test:invoice

# 有头模式调试
pnpm run test:headed

# 查看报告
pnpm run report
```

### 4.2 单元测试

```java
@Test
public void testGenerateOrderNo() {
    // Given
    String orderType = "YS";
    String customerCode = "CZGS";

    // When
    String orderNo = service.generateOrderNo(orderType, customerCode);

    // Then
    assertNotNull(orderNo);
    assertTrue(orderNo.matches("^[A-Z]+-\\d{8}-\\d{3}$"));
}
```

### 4.2 接口测试

```javascript
// 测试订单创建接口
describe('Order API', () => {
  it('should create order successfully', async () => {
    const response = await addOrder(mockOrderData)
    expect(response.code).toBe(200)
  })
})
```

---

## 五、部署规范

### 5.1 自动部署触发条件

- **前端**：修改 `ruoyi-ui/` 目录下的文件
- **后端**：修改 `ruoyi-*/` 或 `pom.xml` 文件

### 5.2 部署前检查

- [ ] 代码通过本地测试
- [ ] 需求文档已更新（如有业务变更）
- [ ] 提交信息符合规范
- [ ] 无敏感信息泄露
- [ ] 数据库变更已创建增量脚本（如有数据库变更）
- [ ] 增量脚本已在测试环境验证（如有数据库变更）

### 5.3 数据库部署规范

**生产环境数据库变更必须遵循以下流程**：

1. **创建增量迁移脚本**：在 `sql/migrations/` 目录下创建
2. **测试环境验证**：确保脚本可重复执行且无副作用
3. **备份生产数据库**：`mysqldump -u username -p database_name > backup.sql`
4. **执行迁移脚本**：按版本顺序执行
5. **验证功能正常**：测试相关功能
6. **更新版本记录**：确认 `db_version` 表已更新
7. **准备回滚方案**：保留备份和回滚脚本

详细文档：[数据库脚本管理](../../sql/README.md)

---

## 六、规范变更记录

| 日期 | 版本 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 2026-04-14 | v1.0 | 初始版本，定义开发规范 | - |
| 2026-04-16 | v1.1 | 新增数据库版本管理规范（增量迁移脚本） | System |
| 2026-04-21 | v1.2 | 新增 Spring Boot 3.x 依赖包规范（javax → jakarta） | System |
| 2026-05-10 | v1.3 | 新增 E2E 自动化测试规范（Playwright） | System |
