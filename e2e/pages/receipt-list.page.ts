import { Locator, Page } from '@playwright/test';

export class ReceiptListPage {
  readonly page: Page;
  readonly searchForm: Locator;
  readonly searchReceiptNo: Locator;
  readonly searchOrderNo: Locator;
  readonly searchStatus: Locator;
  readonly searchBtn: Locator;
  readonly addBtn: Locator;
  readonly receiptTable: Locator;
  readonly confirmDialog: Locator;
  readonly confirmReceiverInput: Locator;
  readonly confirmSubmitBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.searchForm = page.getByTestId('receipt-search-form');
    this.searchReceiptNo = page.getByTestId('receipt-search-receiptNo').locator('input');
    this.searchOrderNo = page.getByTestId('receipt-search-orderNo').locator('input');
    this.searchStatus = page.getByTestId('receipt-search-status');
    this.searchBtn = page.getByTestId('receipt-search-btn');
    this.addBtn = page.getByTestId('receipt-add-btn');
    this.receiptTable = page.getByTestId('receipt-table');
    this.confirmDialog = page.getByTestId('receipt-confirm-dialog');
    this.confirmReceiverInput = page.getByTestId('receipt-confirm-receiver').locator('input');
    this.confirmSubmitBtn = page.getByTestId('receipt-confirm-submit-btn');
  }

  async goto() {
    await this.page.goto('/logistics/receipt');
    await this.receiptTable.waitFor({ state: 'visible' });
  }

  async searchByReceiptNo(receiptNo: string) {
    await this.searchReceiptNo.fill(receiptNo);
    await this.searchBtn.click();
    await this.page.waitForTimeout(500);
  }

  async searchByOrderNo(orderNo: string) {
    await this.searchOrderNo.fill(orderNo);
    await this.searchBtn.click();
    await this.page.waitForTimeout(500);
  }

  async clickAdd() {
    await this.addBtn.click();
  }

  async findRowByReceiptNo(receiptNo: string): Promise<Locator> {
    return this.receiptTable.locator('tbody tr').filter({ hasText: receiptNo }).first();
  }

  async clickRowConfirm(receiptNo: string) {
    const row = await this.findRowByReceiptNo(receiptNo);
    await row.locator('.el-button').filter({ hasText: '确认' }).click();
  }

  async clickRowDelete(receiptNo: string) {
    const row = await this.findRowByReceiptNo(receiptNo);
    await row.locator('.el-button').filter({ hasText: '删除' }).click();
  }

  async submitConfirm(receiver: string) {
    await this.confirmReceiverInput.fill(receiver);
    await this.confirmSubmitBtn.click();
  }

  async getRowCount(): Promise<number> {
    return this.receiptTable.locator('tbody tr').count();
  }
}
