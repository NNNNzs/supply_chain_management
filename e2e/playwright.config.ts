import { defineConfig, devices } from '@playwright/test';
import * as dotenv from 'dotenv';

// 加载环境变量：根目录 .env（含账号密码）→ .env.test（测试配置）→ .env.test.local（本地覆盖）
dotenv.config({ path: '../.env' });
dotenv.config({ path: '.env.test' });
dotenv.config({ path: '.env.test.local' });

export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: 1,
  reporter: [
    ['html', { open: 'never', outputFolder: 'test-results/html' }],
    ['json', { outputFile: 'test-results/results.json' }],
    ['list'],
  ],
  outputDir: 'test-results/artifacts',

  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:3700',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    actionTimeout: 10000,
    navigationTimeout: 15000,
  },

  projects: [
    {
      name: 'chrome',
      use: {
        ...devices['Desktop Chrome'],
        channel: 'chrome',
      },
    },
  ],

  webServer: {
    command: 'pnpm run dev --prefix ../ruoyi-ui',
    port: 3700,
    reuseExistingServer: true,
    timeout: 30000,
  },
});
