# E2E 自动化测试

供应链管理系统的端到端（E2E）自动化测试框架，基于 Playwright + TypeScript。

## 前提条件

- Node.js >= 18
- pnpm
- 后端服务运行中（端口 8897）
- 前端服务运行中（端口 3700，或让 Playwright 自动启动）
- 数据库中验证码已关闭（`sys_config` 表 `sys.account.captchaEnabled` = `false`）

## 快速开始

```bash
# 1. 安装依赖
cd e2e && pnpm install

# 2. 安装浏览器（首次或更新时）
npx playwright install chromium
# 如遇网络问题，使用国内镜像：
# PLAYWRIGHT_DOWNLOAD_HOST=https://npmmirror.com/mirrors/playwright npx playwright install chromium

# 3. 运行全部测试
pnpm test

# 4. 查看报告
pnpm run report
```

## 运行命令

| 命令 | 说明 |
|------|------|
| `pnpm test` | 运行全部测试（无头模式） |
| `pnpm run test:headed` | 有头模式，可观察浏览器操作 |
| `pnpm run test:ui` | Playwright UI 模式，可调试 |
| `pnpm run test:order` | 只运行订单管理测试 |
| `pnpm run test:receipt` | 只运行回单管理测试 |
| `pnpm run test:invoice` | 只运行发票管理测试 |
| `pnpm run test:auth` | 只运行登录测试 |
| `pnpm run report` | 查看 HTML 测试报告 |
| `npx playwright test tests/order/order-create.spec.ts` | 运行单个测试文件 |
| `npx playwright test -g "应该能成功创建新订单"` | 按测试名称过滤运行 |

## 项目结构

```
e2e/
├── playwright.config.ts      # Playwright 配置
├── .env.test                  # 测试环境变量（BASE_URL、账号密码）
├── fixtures/
│   └── index.ts               # 自定义 fixtures（认证、错误捕获、API 客户端）
├── helpers/
│   ├── auth.ts                # 登录辅助（API 登录 + Cookie 注入）
│   ├── element-plus.ts        # Element Plus 组件交互辅助函数
│   ├── navigation.ts          # 页面导航
│   ├── api-client.ts          # 后端 API 直接调用（测试数据准备/清理）
│   └── error-capture.ts       # 控制台/API/网络错误捕获
├── pages/                     # Page Object Model 页面对象
│   ├── login.page.ts
│   ├── order-list.page.ts
│   ├── order-form.page.ts
│   ├── receipt-list.page.ts
│   ├── receipt-dialog.page.ts
│   └── invoice-list.page.ts
├── tests/                     # 测试脚本
│   ├── auth/login.spec.ts
│   ├── order/                 # 订单管理（6 个）
│   ├── receipt/               # 回单管理（4 个）
│   └── invoice/               # 发票管理（5 个）
└── test-results/              # 测试结果（自动生成）
    ├── html/                  # HTML 报告
    ├── results.json           # JSON 结果
    └── artifacts/             # 失败截图、视频、trace
```

## 编写新测试

### 基本模板

```typescript
import { test, expect } from '../../fixtures';
import { YourPage } from '../../pages/your-page.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('功能模块名称', () => {
  test('测试描述', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
    // 1. 准备测试数据（通过 API）
    // 2. 执行 UI 操作
    // 3. 验证结果
    // 4. 清理测试数据
    // 5. 断言无错误
    errorCollector.assertNoErrors();
  });
});
```

### 可用的 Fixtures

| Fixture | 说明 |
|---------|------|
| `authenticatedPage` | 自动完成 API 登录并注入 Cookie |
| `errorCollector` | 自动捕获控制台/API/网络错误 |
| `apiClient` | 已认证的 API 客户端，用于测试数据准备/清理 |

### API Client 方法

```typescript
// 订单
apiClient.createOrder(data)          // 创建订单
apiClient.getOrder(orderId)          // 获取订单
apiClient.getOrders(params)          // 查询订单列表
apiClient.updateOrder(data)          // 更新订单
apiClient.deleteOrder(orderId)       // 删除订单
apiClient.changeOrderStatus(id, s)   // 变更订单状态

// 回单
apiClient.createReceipt(data)        // 创建回单
apiClient.deleteReceipt(id)          // 删除回单
apiClient.confirmReceipt(id, name)   // 确认回单

// 发票
apiClient.mergeInvoice(data)         // 合并开票
apiClient.issueInvoice(batchId)      // 开具发票
apiClient.cancelInvoice(batchId)     // 作废发票
apiClient.deleteInvoice(batchId)     // 删除发票

// 基础数据
apiClient.getCustomers()             // 获取客户列表
apiClient.getGoods()                 // 获取货物列表
apiClient.getDrivers()               // 获取司机列表
```

### Element Plus 辅助函数

```typescript
import { selectOption, filterableSelect, datePicker, treeSelect,
         autocomplete, confirmDialog, waitForElMessage, fillInput,
         fillInputNumber, fillTextarea, radioSelect, checkTableRow,
         tableRowByCellText, clickRowAction, clickRowDropdownItem
} from '../helpers/element-plus';
```

| 函数 | 说明 |
|------|------|
| `selectOption(page, testId, text)` | 选择 el-select 选项 |
| `filterableSelect(page, testId, search, text?)` | 可搜索选择器 |
| `datePicker(page, testId, 'YYYY-MM-DD')` | 选择日期 |
| `treeSelect(page, testId, nodeText)` | 树形选择 |
| `autocomplete(page, testId, text)` | 自动完成输入 |
| `confirmDialog(page)` | 点击确认弹窗 |
| `waitForElMessage(page, 'success')` | 等待 ElMessage 提示 |
| `radioSelect(page, testId, label)` | 单选按钮选择 |
| `fillInput(page, testId, value)` | 填写输入框 |
| `fillInputNumber(page, testId, num)` | 填写数字输入框 |
| `fillTextarea(page, testId, value)` | 填写文本域 |

### 添加 data-testid

在前端 Vue 组件中添加 `data-testid` 属性供测试定位：

```vue
<!-- 输入框 -->
<el-input v-model="form.name" data-testid="module-field-name" />

<!-- 选择器 -->
<el-select v-model="form.type" data-testid="module-field-type">

<!-- 按钮 -->
<el-button @click="handleSubmit" data-testid="module-submit-btn">提交</el-button>

<!-- 表格 -->
<el-table :data="list" data-testid="module-table">

<!-- 对话框 -->
<el-dialog title="新增" v-model="dialogVisible" data-testid="module-dialog">
```

**命名规范**：`{模块}-{类型}-{名称}`，例如 `order-search-orderNo`、`receipt-confirm-dialog`。

### 测试数据管理

- **创建**：通过 `apiClient` 在测试开始时创建
- **清理**：在 `finally` 块或 `afterAll` 中删除
- **禁止**：依赖数据库中已有的生产数据

```typescript
let orderId: number;
try {
  // 创建测试数据
  const res = await apiClient.createOrder({ ... });
  orderId = res.data.orderId;

  // ... 执行测试 ...
} finally {
  // 清理测试数据
  if (orderId) await apiClient.deleteOrder(orderId).catch(() => {});
}
```

## 错误捕获

每个测试自动捕获三类错误：

1. **控制台错误**：`console.error()` 输出
2. **API 错误**：HTTP 状态码 >= 400 的接口请求
3. **网络错误**：请求失败（超时、断网等）

测试结束时调用 `errorCollector.assertNoErrors()` 断言无错误。如果检测到错误，会抛出详细的错误信息。

## 常见问题

### Q: 浏览器下载失败
```bash
PLAYWRIGHT_DOWNLOAD_HOST=https://npmmirror.com/mirrors/playwright npx playwright install chromium
```

### Q: 测试超时
检查后端服务是否正常运行。修改 `playwright.config.ts` 中的 `actionTimeout` 和 `navigationTimeout`。

### Q: Element Plus 组件无法定位
Element Plus 的下拉组件（select、date-picker）渲染到 `<body>` 上，不在组件 DOM 树内。使用 `helpers/element-plus.ts` 中的辅助函数处理。

### Q: 登录失败
确认 `.env.test` 中的 `USER_NAME` 和 `USER_PASSWORD` 正确，且后端验证码已关闭。
