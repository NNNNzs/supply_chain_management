import { Page } from '@playwright/test';

const ROUTES = {
  login: '/login',
  home: '/',
  orderList: '/logistics/order',
  orderForm: '/logistics/order/form',
  receiptList: '/logistics/receipt',
  invoiceList: '/logistics/invoice',
} as const;

/**
 * 导航到订单列表页
 */
export async function goToOrderList(page: Page): Promise<void> {
  await page.goto(ROUTES.orderList);
  await page.waitForURL(`**${ROUTES.orderList}`);
  await page.waitForTimeout(500);
}

/**
 * 导航到订单表单页（新建）
 */
export async function goToOrderForm(page: Page): Promise<void> {
  await page.goto(ROUTES.orderForm);
  await page.waitForURL(`**${ROUTES.orderForm}`);
  await page.waitForTimeout(500);
}

/**
 * 导航到订单编辑页
 */
export async function goToOrderEdit(page: Page, orderId: string | number): Promise<void> {
  await page.goto(`${ROUTES.orderForm}?id=${orderId}`);
  await page.waitForTimeout(500);
}

/**
 * 导航到订单详情页
 */
export async function goToOrderDetail(page: Page, orderId: string | number): Promise<void> {
  await page.goto(`${ROUTES.orderList}/detail/${orderId}`);
  await page.waitForTimeout(500);
}

/**
 * 导航到回单列表页
 */
export async function goToReceiptList(page: Page): Promise<void> {
  await page.goto(ROUTES.receiptList);
  await page.waitForURL(`**${ROUTES.receiptList}`);
  await page.waitForTimeout(500);
}

/**
 * 导航到发票列表页
 */
export async function goToInvoiceList(page: Page): Promise<void> {
  await page.goto(ROUTES.invoiceList);
  await page.waitForURL(`**${ROUTES.invoiceList}`);
  await page.waitForTimeout(500);
}

/**
 * 导航到登录页
 */
export async function goToLogin(page: Page): Promise<void> {
  await page.goto(ROUTES.login);
  await page.waitForURL(`**${ROUTES.login}`);
  await page.waitForTimeout(500);
}
