import { Locator, Page } from '@playwright/test';

export class LoginPage {
  readonly page: Page;
  readonly form: Locator;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly captchaInput: Locator;
  readonly captchaImage: Locator;
  readonly submitButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.form = page.getByTestId('login-form');
    this.usernameInput = page.getByTestId('login-username').locator('input');
    this.passwordInput = page.getByTestId('login-password').locator('input');
    this.captchaInput = page.getByTestId('login-captcha').locator('input');
    this.captchaImage = page.getByTestId('login-captcha-img');
    this.submitButton = page.getByTestId('login-submit');
  }

  async goto() {
    await this.page.goto('/login');
    await this.form.waitFor({ state: 'visible' });
  }

  async login(username: string, password: string, captcha?: string) {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    if (captcha && await this.captchaInput.isVisible()) {
      await this.captchaInput.fill(captcha);
    }
    await this.submitButton.click();
  }
}
