import { Locator, Page } from '@playwright/test';

export class ReceiptDialogPage {
  readonly page: Page;
  readonly dialog: Locator;
  readonly orderSelect: Locator;
  readonly datePicker: Locator;
  readonly submitBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.dialog = page.getByTestId('receipt-dialog');
    this.orderSelect = page.getByTestId('receipt-dialog-order');
    this.datePicker = page.getByTestId('receipt-dialog-date');
    this.submitBtn = page.getByTestId('receipt-dialog-submit-btn');
  }

  async selectOrder(orderNo: string) {
    await this.orderSelect.click();
    await this.page.waitForTimeout(300);
    const dropdown = this.page.locator('.el-select-dropdown:visible').last();
    await dropdown.waitFor({ state: 'visible' });
    await dropdown.locator('.el-select-dropdown__item').filter({ hasText: orderNo }).first().click();
    await this.page.waitForTimeout(300);
  }

  async selectDate(dateStr: string) {
    // el-date-picker 不传递 data-testid，通过 .el-date-editor 类定位
    const dateEditor = this.dialog.locator('.el-date-editor').first();
    const input = dateEditor.locator('input').first();
    await input.click();
    await input.fill(dateStr);
    await input.press('Enter');
    await this.page.waitForTimeout(300);
  }

  async submit() {
    await this.submitBtn.click();
  }

  async waitForVisible() {
    await this.dialog.waitFor({ state: 'visible' });
  }
}
