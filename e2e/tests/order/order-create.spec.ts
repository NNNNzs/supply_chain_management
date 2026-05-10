import { test, expect } from '../../fixtures';
import { OrderListPage } from '../../pages/order-list.page';
import { OrderFormPage } from '../../pages/order-form.page';
import { waitForElMessage } from '../../helpers/element-plus';

test.describe('订单创建', () => {
  test('应该能成功创建新订单', async ({ page, authenticatedPage, errorCollector }) => {
    const orderListPage = new OrderListPage(page);
    const orderFormPage = new OrderFormPage(page);

    // 导航到订单列表页
    await orderListPage.goto();

    // 点击新增按钮，跳转到表单页
    await orderListPage.clickAdd();
    await expect(page).toHaveURL(/\/logistics\/order\/form/);

    // 等待表单加载完成
    await orderFormPage.form.waitFor({ state: 'visible' });

    // 填写订单日期（今天）
    const today = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
    await orderFormPage.selectDate(today);

    // 选择第一个可用客户
    const customerTrigger = page.getByTestId('order-form-customer').locator('.el-input');
    await customerTrigger.click();
    const customerDropdown = page.locator('.el-select-dropdown:visible').last();
    await customerDropdown.waitFor({ state: 'visible' });
    const firstCustomerOption = customerDropdown.locator('.el-select-dropdown__item').first();
    await firstCustomerOption.click();
    await customerDropdown.waitFor({ state: 'hidden' }).catch(() => {});

    // 选择计价模式：按重量
    await orderFormPage.selectPricingMode('按重量');

    // 填写装卸地址（使用 autocomplete）
    await orderFormPage.fillLoadingAddress('测试装货地址');
    await orderFormPage.fillUnloadingAddress('测试卸货地址');

    // 在货物表格中选择第一个货物，填写重量和单价
    // 点击货物选择下拉，选择第一个
    const firstGoodsRow = orderFormPage.goodsTable.locator('tbody tr').first();
    await firstGoodsRow.locator('.el-select').first().click();
    const goodsDropdown = page.locator('.el-select-dropdown:visible').last();
    await goodsDropdown.waitFor({ state: 'visible' });
    const firstGoodsOption = goodsDropdown.locator('.el-select-dropdown__item').first();
    await firstGoodsOption.click();
    await page.waitForTimeout(300);

    // 填写重量 = 10
    await orderFormPage.fillGoodsWeight(0, 10);

    // 填写单价 = 100
    await orderFormPage.fillGoodsUnitPrice(0, 100);

    // 选择第一个司机（树形选择器）
    const driverTrigger = page.getByTestId('order-form-driver').locator('.el-input');
    await driverTrigger.click();
    const driverDropdown = page.locator('.el-tree-select__popper:visible, .el-popper:visible').last();
    await driverDropdown.waitFor({ state: 'visible' }).catch(() => {});
    await page.waitForTimeout(300);
    const firstDriverNode = driverDropdown.locator('.el-tree-node').first();
    await firstDriverNode.locator('.el-tree-node__content').click();
    await page.waitForTimeout(200);

    // 点击保存
    await orderFormPage.save();

    // 验证成功提示
    await waitForElMessage(page, 'success');

    // 验证跳转回订单列表页
    await page.waitForURL('**/logistics/order', { timeout: 10000 });
    await expect(page).toHaveURL(/\/logistics\/order$/);

    // 断言无错误
    errorCollector.assertNoErrors();
  });
});
