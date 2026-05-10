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
    await this.orderSelect.locator('.el-input').click();
    const dropdown = this.page.locator('.el-select-dropdown:visible').last();
    await dropdown.waitFor({ state: 'visible' });
    await dropdown.locator('.el-select-dropdown__item').filter({ hasText: orderNo }).first().click();
  }

  async selectDate(dateStr: string) {
    await this.datePicker.locator('input').fill(dateStr);
    await this.datePicker.locator('input').press('Enter');
  }

  async submit() {
    await this.submitBtn.click();
  }

  async waitForVisible() {
    await this.dialog.waitFor({ state: 'visible' });
  }
}
