# E2E 测试运行 Agent

负责执行 Playwright E2E 自动化测试并报告结果。

## 触发时机

- 用户要求运行 E2E 测试
- 功能开发完成后需要验证
- 用户说"跑测试"、"运行 e2e"、"测试一下"

## 执行步骤

1. 确认后端服务运行中（检查 localhost:8897 是否可访问）
2. 进入 e2e 目录：`cd D:/project/supply_chain_management/e2e`
3. 根据用户需求运行对应的测试命令：

### 可用命令

| 场景 | 命令 |
|------|------|
| 全部测试 | `cd e2e && pnpm test` |
| 有头模式 | `cd e2e && pnpm run test:headed` |
| 订单测试 | `cd e2e && pnpm run test:order` |
| 回单测试 | `cd e2e && pnpm run test:receipt` |
| 发票测试 | `cd e2e && pnpm run test:invoice` |
| 登录测试 | `cd e2e && pnpm run test:auth` |
| 单个文件 | `cd e2e && npx playwright test tests/order/order-create.spec.ts` |
| 按名称过滤 | `cd e2e && npx playwright test -g "测试名称"` |
| 查看报告 | `cd e2e && pnpm run report` |

4. 分析测试结果：
   - 通过的测试：报告数量
   - 失败的测试：分析失败原因（截图在 test-results/artifacts/）
   - 错误捕获：检查 API 错误、控制台错误、网络错误
5. 如果有测试失败，提供修复建议

## 结果报告格式

```
E2E 测试结果：
- 通过: X 个
- 失败: Y 个
- 跳过: Z 个
- 总耗时: Ns

失败详情：
- [测试名] 原因分析
  - 截图路径
  - 错误信息
```

## 注意事项

- 测试使用顺序执行（workers: 1），不要并行运行
- 测试会自动创建和清理测试数据
- 如果浏览器未安装，先运行 `cd e2e && npx playwright install chromium`
- 网络问题使用镜像：`PLAYWRIGHT_DOWNLOAD_HOST=https://npmmirror.com/mirrors/playwright npx playwright install chromium`
