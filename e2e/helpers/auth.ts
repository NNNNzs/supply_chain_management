import { APIRequestContext, Page } from '@playwright/test';

const BASE_URL = process.env.BASE_URL || 'http://localhost:3700';
const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8897';

/**
 * 通过 API 直接登录后端，获取 token
 */
export async function apiLogin(
  username: string,
  password: string,
): Promise<string> {
  // 直接请求后端，绕过前端代理，更稳定
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password, code: '', uuid: '' }),
  });

  const data = await response.json();
  if (data.code !== 200) {
    throw new Error(`登录失败: ${data.msg || JSON.stringify(data)}`);
  }
  return data.token;
}

/**
 * API 登录后注入 Cookie，跳过 UI 登录流程
 */
export async function loginWithCookie(
  page: Page,
  username: string,
  password: string,
): Promise<void> {
  const token = await apiLogin(username, password);

  const hostname = new URL(BASE_URL).hostname;

  // 先注入 Cookie（不需要先导航）
  await page.context().addCookies([{
    name: 'Admin-Token',
    value: token,
    domain: hostname,
    path: '/',
  }]);

  // 导航到首页，触发路由守卫调用 /getInfo 加载用户信息
  await page.goto('/');
  await page.waitForURL(/^(?!.*\/login).+$/, { timeout: 15000 });
}
