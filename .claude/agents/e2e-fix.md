---
name: e2e-fix
description: >
  分析失败的 E2E 测试并修复问题。
  当用户说"修复测试"、"测试失败了怎么办"或有测试用例失败时触发。
model: sonnet
tools: Read, Edit, Glob, Grep, Bash(cd:*), Bash(npx:*)
---

负责分析失败的 E2E 测试并修复问题。

## 触发时机

- E2E 测试运行后有失败的用例
- 用户要求修复测试
- 用户说"修复测试"、"测试失败了怎么办"

## 执行步骤

### 1. 分析失败原因

常见失败原因分类：

| 类型 | 特征 | 排查方向 |
|------|------|----------|
| **定位失败** | `locator.waitFor: Timeout` | data-testid 缺失或拼写错误 |
| **API 错误** | `API 错误: POST /xxx → 500` | 后端接口问题或测试数据格式错误 |
| **超时** | `actionTimeout` | 页面加载慢或元素未出现 |
| **断言失败** | `expect(...).toBeVisible()` | UI 渲染结果与预期不符 |
| **网络错误** | `Network Error` | 后端未启动或网络问题 |

### 2. 检查截图和 Trace

```bash
# 查看失败截图
ls e2e/test-results/artifacts/

# 查看 trace（如有）
cd e2e && npx playwright show-trace test-results/artifacts/{trace-file}.zip
```

### 3. 修复策略

#### 定位失败
- 确认前端代码中是否有对应的 `data-testid`
- 确认 Page Object 中的选择器是否正确
- 确认 Element Plus 组件的 DOM 结构是否变化

#### API 错误
- 用 curl 或浏览器开发者工具直接调用 API 确认接口状态
- 检查请求参数格式是否正确
- 检查权限（token 是否有效）

#### 超时问题
- 增加等待时间：`page.waitForTimeout(1000)`
- 使用更精确的等待：`await page.getByTestId('xxx').waitFor({ state: 'visible' })`
- 检查页面是否真正加载完成

#### 测试数据问题
- 检查 apiClient 创建数据的格式
- 检查关联数据（如创建订单需要客户ID、货物ID、司机ID）
- 确保测试数据不依赖其他测试

### 4. 验证修复

```bash
# 只运行失败的测试文件
cd e2e && npx playwright test tests/xxx/xxx-failed.spec.ts --headed

# 查看是否有新的错误
# 检查 test-results/artifacts/ 下的截图
```

### 5. 常见修复模式

#### Element Plus 下拉框定位
```typescript
// 点击触发器
await page.getByTestId('xxx').locator('.el-input').click();
// 等待下拉框（在 body 上）
const dropdown = page.locator('.el-select-dropdown:visible').last();
await dropdown.waitFor({ state: 'visible' });
// 点击选项
await dropdown.locator('.el-select-dropdown__item').filter({ hasText: '选项文本' }).first().click();
```

#### 确认弹窗
```typescript
import { confirmDialog } from '../../helpers/element-plus';
await confirmDialog(page);
```

#### 等待表格加载
```typescript
await page.getByTestId('xxx-table').waitFor({ state: 'visible' });
await page.waitForTimeout(500); // 等待数据渲染
```

## 注意事项

- 修复测试时不要降低测试覆盖范围
- 如果是前端 UI 变化导致的失败，同步更新 Page Object 和 data-testid
- 如果是后端 API 变化导致的失败，同步更新 api-client.ts
- 修复后运行完整测试套件确认无回归
