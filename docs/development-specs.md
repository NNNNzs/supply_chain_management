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

### 2.3 数据库规范

#### 2.3.1 表命名

```
物流模块表：logistics_ + 功能名
示例：logistics_order, logistics_customer
```

#### 2.3.2 字段命名

```
使用下划线命名法（snake_case）
主键：表名_id
示例：order_id, customer_name
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

### 4.1 单元测试

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
    assertTrue(orderNo.startsWith("YSCZGS"));
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

---

## 六、规范变更记录

| 日期 | 版本 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 2026-04-14 | v1.0 | 初始版本，定义开发规范 | - |
