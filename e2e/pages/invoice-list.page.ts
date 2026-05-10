import { Locator, Page } from '@playwright/test';

export class InvoiceListPage {
  readonly page: Page;
  readonly searchForm: Locator;
  readonly searchBatchNo: Locator;
  readonly searchCustomer: Locator;
  readonly searchStatus: Locator;
  readonly searchBtn: Locator;
  readonly addBtn: Locator;
  readonly issueBtn: Locator;
  readonly cancelBtn: Locator;
  readonly cancelMergeBtn: Locator;
  readonly invoiceTable: Locator;
  readonly mergeDialog: Locator;
  readonly mergeCustomer: Locator;
  readonly mergeDate: Locator;
  readonly mergeType: Locator;
  readonly mergeTaxRate: Locator;
  readonly mergeOrdersTable: Locator;
  readonly mergeSubmitBtn: Locator;
  readonly detailDialog: Locator;

  constructor(page: Page) {
    this.page = page;
    this.searchForm = page.getByTestId('invoice-search-form');
    this.searchBatchNo = page.getByTestId('invoice-search-batchNo');
    this.searchCustomer = page.getByTestId('invoice-search-customer');
    this.searchStatus = page.getByTestId('invoice-search-status');
    this.searchBtn = page.getByTestId('invoice-search-btn');
    this.addBtn = page.getByTestId('invoice-add-btn');
    this.issueBtn = page.getByTestId('invoice-issue-btn');
    this.cancelBtn = page.getByTestId('invoice-cancel-btn');
    this.cancelMergeBtn = page.getByTestId('invoice-cancel-merge-btn');
    this.invoiceTable = page.getByTestId('invoice-table');
    this.mergeDialog = page.getByTestId('invoice-merge-dialog');
    this.mergeCustomer = page.getByTestId('invoice-merge-customer');
    this.mergeDate = page.getByTestId('invoice-merge-date');
    this.mergeType = page.getByTestId('invoice-merge-type');
    this.mergeTaxRate = page.getByTestId('invoice-merge-tax-rate');
    this.mergeOrdersTable = page.getByTestId('invoice-merge-orders-table');
    this.mergeSubmitBtn = page.getByTestId('invoice-merge-submit-btn');
    this.detailDialog = page.getByTestId('invoice-detail-dialog');
  }

  async goto() {
    await this.page.goto('/business/invoice');
    await this.invoiceTable.waitFor({ state: 'visible' });
  }

  async searchByBatchNo(batchNo: string) {
    await this.searchBatchNo.fill(batchNo);
    await this.searchBtn.click();
    await this.page.waitForTimeout(500);
  }

  async clickAdd() {
    await this.addBtn.click();
    await this.mergeDialog.waitFor({ state: 'visible' });
  }

  async selectMergeCustomer(customerName: string) {
    await this.mergeCustomer.click();
    await this.page.waitForTimeout(300);
    // el-select 的搜索输入在下拉面板中
    const searchInput = this.page.locator('.el-select-dropdown:visible input, .el-select__popper:visible input').last();
    if (await searchInput.count() > 0) {
      await searchInput.fill(customerName);
    }
    await this.page.waitForTimeout(300);
    const dropdown = this.page.locator('.el-select-dropdown:visible').last();
    await dropdown.waitFor({ state: 'visible' });
    await dropdown.locator('.el-select-dropdown__item').filter({ hasText: customerName }).first().click();
    await this.page.waitForTimeout(1000);
  }

  async selectMergeOrder(orderNo: string) {
    const row = this.mergeOrdersTable.locator('tbody tr').filter({ hasText: orderNo }).first();
    await row.locator('.el-checkbox').click();
  }

  async submitMerge() {
    await this.mergeSubmitBtn.click();
  }

  async selectRowCheckbox(batchNo: string) {
    const row = this.invoiceTable.locator('tbody tr').filter({ hasText: batchNo }).first();
    await row.locator('.el-checkbox').click();
  }

  async clickIssue() {
    await this.issueBtn.click();
  }

  async clickCancel() {
    await this.cancelBtn.click();
  }

  async clickCancelMerge() {
    await this.cancelMergeBtn.click();
  }

  async clickRowView(batchNo: string) {
    const row = this.invoiceTable.locator('tbody tr').filter({ hasText: batchNo }).first();
    await row.locator('.el-button').filter({ hasText: '详情' }).click();
  }

  async clickRowDelete(batchNo: string) {
    const row = this.invoiceTable.locator('tbody tr').filter({ hasText: batchNo }).first();
    await row.locator('.el-button').filter({ hasText: '删除' }).click();
  }

  async getRowCount(): Promise<number> {
    return this.invoiceTable.locator('tbody tr').count();
  }
}
