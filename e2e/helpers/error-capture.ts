import { Page } from '@playwright/test';

export interface ErrorCollector {
  getConsoleErrors: () => string[];
  getNetworkErrors: () => string[];
  getApiErrors: () => Array<{ url: string; status: number; method: string }>;
  assertNoErrors: () => void;
}

/**
 * 创建错误收集器，监听页面控制台错误、网络错误和 API 错误
 */
export function captureErrors(page: Page): ErrorCollector {
  const consoleErrors: string[] = [];
  const networkErrors: string[] = [];
  const apiErrors: Array<{ url: string; status: number; method: string }> = [];

  page.on('console', (msg) => {
    if (msg.type() === 'error') {
      consoleErrors.push(`[Console Error] ${msg.text()}`);
    }
  });

  page.on('response', (response) => {
    if (response.status() >= 400) {
      const url = response.url();
      // 过滤掉 captchaImage 等非业务接口的错误
      if (url.includes('/dev-api/') || url.includes('/prod-api/')) {
        apiErrors.push({
          url: url.replace(/^.*\/dev-api/, '/api').replace(/^.*\/prod-api/, '/api'),
          status: response.status(),
          method: response.request().method(),
        });
      }
    }
  });

  page.on('requestfailed', (request) => {
    networkErrors.push(
      `[Network Error] ${request.method()} ${request.url()}: ${request.failure()?.message || 'unknown'}`,
    );
  });

  return {
    getConsoleErrors: () => [...consoleErrors],
    getNetworkErrors: () => [...networkErrors],
    getApiErrors: () => [...apiErrors],
    assertNoErrors: () => {
      const errors: string[] = [];

      if (apiErrors.length > 0) {
        errors.push(
          'API 错误:\n' +
            apiErrors.map((e) => `  ${e.method} ${e.url} → ${e.status}`).join('\n'),
        );
      }

      if (consoleErrors.length > 0) {
        errors.push(
          '控制台错误:\n' + consoleErrors.map((e) => `  ${e}`).join('\n'),
        );
      }

      if (networkErrors.length > 0) {
        errors.push(
          '网络错误:\n' + networkErrors.map((e) => `  ${e}`).join('\n'),
        );
      }

      if (errors.length > 0) {
        throw new Error(`测试过程中捕获到错误:\n${errors.join('\n\n')}`);
      }
    },
  };
}
