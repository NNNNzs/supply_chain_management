import { Locator, Page } from '@playwright/test';
import { filterableSelect, datePicker, treeSelect, autocomplete, fillInputNumber, fillTextarea } from '../helpers/element-plus';

export class OrderFormPage {
  readonly page: Page;
  readonly form: Locator;
  readonly saveBtn: Locator;
  readonly cancelBtn: Locator;
  readonly dateInput: Locator;
  readonly customerSelect: Locator;
  readonly pricingRadio: Locator;
  readonly loadingAddress: Locator;
  readonly unloadingAddress: Locator;
  readonly goodsTable: Locator;
  readonly addGoodsBtn: Locator;
  readonly driverSelect: Locator;
  readonly vehicleInput: Locator;
  readonly driverPhoneInput: Locator;
  readonly loadingPriceInput: Locator;
  readonly freightCostInput: Locator;
  readonly advancePaymentInput: Locator;
  readonly remarkInput: Locator;

  constructor(page: Page) {
    this.page = page;
    this.form = page.getByTestId('order-form');
    this.saveBtn = page.getByTestId('order-form-save-btn');
    this.cancelBtn = page.getByTestId('order-form-cancel-btn');
    this.dateInput = page.getByTestId('order-form-date');
    this.customerSelect = page.getByTestId('order-form-customer');
    this.pricingRadio = page.getByTestId('order-form-pricing');
    this.loadingAddress = page.getByTestId('order-form-loading-address');
    this.unloadingAddress = page.getByTestId('order-form-unloading-address');
    this.goodsTable = page.getByTestId('order-form-goods-table');
    this.addGoodsBtn = page.getByTestId('order-form-add-goods-btn');
    this.driverSelect = page.getByTestId('order-form-driver');
    this.vehicleInput = page.getByTestId('order-form-vehicle');
    this.driverPhoneInput = page.getByTestId('order-form-driver-phone');
    this.loadingPriceInput = page.getByTestId('order-form-loading-price');
    this.freightCostInput = page.getByTestId('order-form-freight-cost');
    this.advancePaymentInput = page.getByTestId('order-form-advance-payment');
    this.remarkInput = page.getByTestId('order-form-remark');
  }

  async goto() {
    await this.page.goto('/logistics/order/form');
    await this.form.waitFor({ state: 'visible' });
  }

  async selectDate(dateStr: string) {
    await datePicker(this.page, 'order-form-date', dateStr);
  }

  async selectCustomer(customerName: string) {
    await filterableSelect(this.page, 'order-form-customer', customerName);
  }

  async selectPricingMode(mode: string) {
    await this.pricingRadio.locator('.el-radio').filter({ hasText: mode }).click();
  }

  async fillLoadingAddress(address: string) {
    await autocomplete(this.page, 'order-form-loading-address', address);
  }

  async fillUnloadingAddress(address: string) {
    await autocomplete(this.page, 'order-form-unloading-address', address);
  }

  async selectGoods(rowIndex: number, goodsName: string) {
    const row = this.goodsTable.locator('tbody tr').nth(rowIndex);
    await row.locator('.el-select').first().click();
    const dropdown = this.page.locator('.el-select-dropdown:visible').last();
    await dropdown.waitFor({ state: 'visible' });
    await dropdown.locator('.el-select-dropdown__item').filter({ hasText: goodsName }).first().click();
    await this.page.waitForTimeout(300);
  }

  async fillGoodsWeight(rowIndex: number, weight: number) {
    const row = this.goodsTable.locator('tbody tr').nth(rowIndex);
    const input = row.locator('.el-input-number input').first();
    await input.clear();
    await input.fill(String(weight));
    await input.press('Tab');
  }

  async fillGoodsUnitPrice(rowIndex: number, price: number) {
    const row = this.goodsTable.locator('tbody tr').nth(rowIndex);
    const inputs = row.locator('.el-input-number input');
    const count = await inputs.count();
    const input = inputs.nth(count > 1 ? 1 : 0);
    await input.clear();
    await input.fill(String(price));
    await input.press('Tab');
  }

  async selectDriver(driverName: string) {
    await treeSelect(this.page, 'order-form-driver', driverName);
  }

  async fillRemark(remark: string) {
    await this.remarkInput.fill(remark);
  }

  async save() {
    await this.saveBtn.click();
  }

  async cancel() {
    await this.cancelBtn.click();
  }
}
