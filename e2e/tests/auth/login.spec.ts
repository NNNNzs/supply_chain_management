import { test, expect } from '../../fixtures';
import { LoginPage } from '../../pages/login.page';

test.describe('登录测试', () => {
  test('应该能成功登录并跳转到首页', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    const username = process.env.USER_NAME || 'admin';
    const password = process.env.USER_PASSWORD || '';

    await loginPage.login(username, password);

    // 验证跳转到首页（非登录页）
    await page.waitForURL(/^(?!.*\/login).+$/, { timeout: 10000 });

    // 验证页面上有内容（登录后的布局）
    await expect(page.locator('.app-wrapper, .sidebar-container, .navbar').first()).toBeVisible({ timeout: 5000 });
  });

  test('应该显示验证码图片（如果验证码开启）', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    // 验证码可能开启也可能关闭
    const captchaVisible = await loginPage.captchaInput.isVisible().catch(() => false);
    if (captchaVisible) {
      await expect(loginPage.captchaImage).toBeVisible();
    }
  });

  test('空用户名应该显示验证错误', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    // 不填用户名，直接点登录
    await loginPage.submitButton.click();

    // 等待验证消息
    await page.waitForTimeout(500);

    // Element Plus 表单验证会显示错误信息
    const hasError = await page.locator('.el-form-item__error').first().isVisible().catch(() => false);
    expect(hasError).toBeTruthy();
  });
});
