import { test as base, expect } from '@playwright/test';
import { loginWithCookie } from '../helpers/auth';
import { captureErrors, ErrorCollector } from '../helpers/error-capture';
import { ApiClient } from '../helpers/api-client';

type TestFixtures = {
  authenticatedPage: void;
  errorCollector: ErrorCollector;
  apiClient: ApiClient;
};

const username = process.env.USER_NAME || 'admin';
const password = process.env.USER_PASSWORD || '';

/**
 * 扩展的 test fixture，自动处理认证和错误捕获
 */
export const test = base.extend<TestFixtures>({
  // 自动登录并注入 Cookie，等待用户信息加载完成
  authenticatedPage: async ({ page }, use) => {
    await loginWithCookie(page, username, password);
    await use();
  },

  // 自动捕获页面错误
  errorCollector: async ({ page }, use) => {
    const collector = captureErrors(page);
    await use(collector);
  },

  // API 客户端（已认证）
  apiClient: async ({}, use) => {
    const client = new ApiClient();
    await client.login(username, password);
    await use(client);
  },
});

export { expect };
