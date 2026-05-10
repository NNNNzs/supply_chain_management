import { Locator, Page } from '@playwright/test';

export class OrderListPage {
  readonly page: Page;
  readonly searchForm: Locator;
  readonly searchOrderNo: Locator;
  readonly searchCustomer: Locator;
  readonly searchDate: Locator;
  readonly searchStatus: Locator;
  readonly searchPricing: Locator;
  readonly searchBtn: Locator;
  readonly resetBtn: Locator;
  readonly addBtn: Locator;
  readonly orderTable: Locator;
  readonly mergeInvoiceBtn: Locator;
  readonly addReceiptBtn: Locator;
  readonly invoiceDialog: Locator;
  readonly invoiceSubmitBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.searchForm = page.getByTestId('order-search-form');
    this.searchOrderNo = page.getByTestId('order-search-orderNo').locator('input');
    this.searchCustomer = page.getByTestId('order-search-customer');
    this.searchDate = page.getByTestId('order-search-date');
    this.searchStatus = page.getByTestId('order-search-status');
    this.searchPricing = page.getByTestId('order-search-pricing');
    this.searchBtn = page.getByTestId('order-search-btn');
    this.resetBtn = page.getByTestId('order-reset-btn');
    this.addBtn = page.getByTestId('order-add-btn');
    this.orderTable = page.getByTestId('order-table');
    this.mergeInvoiceBtn = page.getByTestId('order-merge-invoice-btn');
    this.addReceiptBtn = page.getByTestId('order-add-receipt-btn');
    this.invoiceDialog = page.getByTestId('order-invoice-dialog');
    this.invoiceSubmitBtn = page.getByTestId('order-invoice-submit-btn');
  }

  async goto() {
    await this.page.goto('/logistics/order');
    await this.orderTable.waitFor({ state: 'visible' });
  }

  async searchByOrderNo(orderNo: string) {
    await this.searchOrderNo.fill(orderNo);
    await this.searchBtn.click();
    await this.page.waitForTimeout(500);
  }

  async resetSearch() {
    await this.resetBtn.click();
    await this.page.waitForTimeout(500);
  }

  async clickAdd() {
    await this.addBtn.click();
    await this.page.waitForURL('**/logistics/order/form');
  }

  async findRowByOrderNo(orderNo: string): Promise<Locator> {
    return this.orderTable.locator('tbody tr').filter({ hasText: orderNo }).first();
  }

  async clickRowEdit(orderNo: string) {
    const row = await this.findRowByOrderNo(orderNo);
    await row.locator('.el-button').filter({ hasText: '修改' }).click();
  }

  async clickRowDelete(orderNo: string) {
    const row = await this.findRowByOrderNo(orderNo);
    await row.locator('.el-button').filter({ hasText: '删除' }).click();
  }

  async clickRowOrderNo(orderNo: string) {
    const row = await this.findRowByOrderNo(orderNo);
    await row.locator('.el-link').click();
  }

  async changeStatus(orderNo: string, statusText: string) {
    const row = await this.findRowByOrderNo(orderNo);
    await row.locator('.el-dropdown').locator('.el-button').click();
    const dropdown = this.page.locator('.el-dropdown-menu:visible').last();
    await dropdown.waitFor({ state: 'visible' });
    await dropdown.locator('.el-dropdown-menu__item').filter({ hasText: statusText }).first().click();
  }

  async selectRowCheckbox(orderNo: string) {
    const row = await this.findRowByOrderNo(orderNo);
    await row.locator('.el-checkbox').click();
  }

  async getRowCount(): Promise<number> {
    return this.orderTable.locator('tbody tr').count();
  }
}
