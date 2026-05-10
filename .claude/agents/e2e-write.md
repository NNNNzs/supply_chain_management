---
name: e2e-write
description: >
  为新增或修改的业务功能编写 Playwright E2E 测试脚本。
  当用户说"写测试"、"添加 e2e 测试"、"补充测试脚本"时触发。
model: sonnet
tools: Read, Write, Edit, Glob, Grep, Bash(cd:*), Bash(npx:*)
---

负责为新增或修改的业务功能编写对应的 Playwright E2E 测试脚本。

## 触发时机

- 新增业务功能时
- 修改已有业务功能时
- 用户明确要求编写测试
- 用户说"写测试"、"添加 e2e 测试"、"补充测试脚本"

## 执行步骤

### 1. 分析功能需求

确定需要测试的操作类型：
- CRUD（创建、读取、更新、删除）
- 搜索/过滤
- 状态变更
- 对话框交互
- 特殊业务流程

### 2. 添加 data-testid

在前端 Vue 组件中为关键元素添加 `data-testid`：

**命名规范**：`{模块}-{类型}-{名称}`

| 元素类型 | testid 格式 | 示例 |
|----------|-------------|------|
| 搜索表单 | `{module}-search-form` | `order-search-form` |
| 搜索字段 | `{module}-search-{field}` | `order-search-orderNo` |
| 搜索按钮 | `{module}-search-btn` | `order-search-btn` |
| 重置按钮 | `{module}-reset-btn` | `order-reset-btn` |
| 新增按钮 | `{module}-add-btn` | `order-add-btn` |
| 数据表格 | `{module}-table` | `order-table` |
| 表单页 | `{module}-form` | `order-form` |
| 表单字段 | `{module}-form-{field}` | `order-form-date` |
| 保存按钮 | `{module}-form-save-btn` | `order-form-save-btn` |
| 对话框 | `{module}-dialog` 或 `{module}-{action}-dialog` | `receipt-confirm-dialog` |
| 对话框提交 | `{module}-dialog-submit-btn` | `receipt-dialog-submit-btn` |

### 3. 创建 Page Object

在 `e2e/pages/{module}.page.ts` 创建页面对象类：

```typescript
import { Locator, Page } from '@playwright/test';

export class XxxPage {
  readonly page: Page;
  // 搜索相关
  readonly searchForm: Locator;
  readonly searchBtn: Locator;
  readonly addBtn: Locator;
  // 表格相关
  readonly table: Locator;

  constructor(page: Page) {
    this.page = page;
    this.searchForm = page.getByTestId('xxx-search-form');
    this.searchBtn = page.getByTestId('xxx-search-btn');
    this.addBtn = page.getByTestId('xxx-add-btn');
    this.table = page.getByTestId('xxx-table');
  }

  async goto() {
    await this.page.goto('/logistics/xxx');
    await this.table.waitFor({ state: 'visible' });
  }
}
```

### 4. 编写测试脚本

在 `e2e/tests/{module}/` 目录下创建测试文件：

```typescript
import { test, expect } from '../../fixtures';
import { XxxPage } from '../../pages/xxx.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('模块名称', () => {
  test('测试描述', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
    // 1. 准备测试数据（通过 API，不依赖生产数据）
    let testId: number;
    try {
      const res = await apiClient.createXxx({ /* 测试数据 */ });
      testId = res.data.xxxId;

      // 2. 执行 UI 操作
      const xxxPage = new XxxPage(page);
      await xxxPage.goto();

      // 3. 验证结果
      await waitForElMessage(page, 'success');
    } finally {
      // 4. 清理测试数据
      if (testId) await apiClient.deleteXxx(testId).catch(() => {});
    }

    // 5. 断言无错误
    errorCollector.assertNoErrors();
  });
});
```

### 5. 更新 API Client

如果新增了 API 端点，在 `e2e/helpers/api-client.ts` 中添加对应方法。

### 6. 更新 Element Plus 辅助

如果遇到新的 Element Plus 组件类型，在 `e2e/helpers/element-plus.ts` 中添加辅助函数。

## 必须遵守的规范

1. **从 fixtures 导入**：`import { test, expect } from '../../fixtures'`，不是 `@playwright/test`
2. **API 创建数据**：测试数据通过 `apiClient` 创建，不依赖生产数据
3. **finally 清理**：所有测试数据在 `finally` 块中清理
4. **错误断言**：每个测试结束调用 `errorCollector.assertNoErrors()`
5. **Page Object**：UI 操作封装在 Page Object 中，不直接在测试中写选择器
6. **data-testid**：选择器使用 `data-testid`，不依赖 CSS 类名或 DOM 结构

## 测试类型清单

为每个模块编写以下测试（按需）：

| 文件 | 测试内容 |
|------|----------|
| `{module}-create.spec.ts` | 新增功能 |
| `{module}-edit.spec.ts` | 编辑功能 |
| `{module}-search.spec.ts` | 搜索/过滤 |
| `{module}-delete.spec.ts` | 删除功能 |
| `{module}-status-change.spec.ts` | 状态流转（如有状态字段） |
| `{module}-detail.spec.ts` | 详情查看（如有详情页） |
