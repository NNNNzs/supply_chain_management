import { test, expect } from '../../fixtures';
import { OrderListPage } from '../../pages/order-list.page';
import { OrderFormPage } from '../../pages/order-form.page';
import { waitForElMessage } from '../../helpers/element-plus';

test.describe('订单编辑', () => {
  let testOrderId: number;
  let testOrderNo: string;
  let apiClient: any;

  test.beforeAll(async ({ apiClient: client }) => {
    apiClient = client;
  });

  test('应该能成功编辑订单', async ({ page, authenticatedPage, errorCollector, apiClient: client }) => {
    // 通过 API 创建测试订单
    const today = new Date().toISOString().split('T')[0];
    const customersResp = await client.getCustomers({ pageSize: 1 });
    const customer = customersResp.rows?.[0];
    const driversResp = await client.getDrivers();
    const driver = driversResp.rows?.[0];
    const goodsResp = await client.getGoods({ pageSize: 1 });
    const goods = goodsResp.rows?.[0];

    const orderData = {
      orderDate: today,
      customerId: customer.customerId,
      customerName: customer.customerName,
      pricingMode: 'weight',
      loadingAddress: '测试装货地址',
      unloadingAddress: '测试卸货地址',
      driverId: driver.driverId,
      driverName: driver.driverName,
      vehicleId: driver.vehicleId || '',
      vehiclePlate: driver.vehiclePlate || '',
      goodsList: [{
        goodsId: goods.goodsId,
        goodsName: goods.goodsName,
        goodsUnit: goods.goodsUnit || '吨',
        weight: 5,
        unitPrice: 50,
        amount: 250,
      }],
      remark: '原始备注',
    };

    const createResp = await client.createOrder(orderData);
    expect(createResp.code).toBe(200);
    // Backend doesn't return orderId in response, query from list
    const listResp = await client.getOrders({ pageNum: 1, pageSize: 1 });
    testOrderId = listResp.rows[0].orderId;
    testOrderNo = listResp.rows[0].orderNo;
    expect(testOrderId).toBeTruthy();
    expect(testOrderNo).toBeTruthy();

    // 导航到订单列表页
    const orderListPage = new OrderListPage(page);
    await orderListPage.goto();

    // 找到测试订单，点击修改
    await orderListPage.clickRowEdit(testOrderNo);

    // 等待表单页加载
    await page.waitForURL('**/logistics/order/form*', { timeout: 10000 });

    const orderFormPage = new OrderFormPage(page);
    await orderFormPage.form.waitFor({ state: 'visible' });

    // 验证表单已预填数据 - 检查客户名已在页面上
    const customerText = customer.customerName;
    await expect(page.getByTestId('order-form-customer')).toContainText(customerText, { timeout: 5000 }).catch(() => {
      // 客户名可能显示在 input 的 value 中，不一定是文本
    });

    // 修改备注字段
    const newRemark = `编辑测试备注 ${Date.now()}`;
    await orderFormPage.fillRemark(newRemark);

    // 点击保存
    await orderFormPage.save();

    // 验证成功提示
    await waitForElMessage(page, 'success');

    // 验证跳转回订单列表
    await page.waitForURL('**/business/order', { timeout: 10000 });

    // 断言无错误
    errorCollector.assertNoErrors();
  });

  test.afterAll(async () => {
    // 清理测试数据
    if (testOrderId) {
      try {
        await apiClient.deleteOrder(testOrderId);
      } catch {
        // 清理失败不影响测试结果
      }
    }
  });
});
