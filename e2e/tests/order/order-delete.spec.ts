import { test, expect } from '../../fixtures';
import { OrderListPage } from '../../pages/order-list.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('订单删除', () => {
  let testOrderId: number;
  let testOrderNo: string;
  let apiClient: any;
  let orderDeleted = false;

  test.beforeAll(async ({ apiClient: client }) => {
    apiClient = client;
  });

  test('应该能成功删除订单', async ({ page, authenticatedPage, errorCollector, apiClient: client }) => {
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
      loadingAddress: '删除测试装货地址',
      unloadingAddress: '删除测试卸货地址',
      driverId: driver.driverId,
      driverName: driver.driverName,
      vehicleId: driver.vehicleId || '',
      vehiclePlate: driver.vehiclePlate || '',
      goodsList: [{
        goodsId: goods.goodsId,
        goodsName: goods.goodsName,
        goodsUnit: goods.goodsUnit || '吨',
        weight: 3,
        unitPrice: 30,
        amount: 90,
      }],
      remark: '删除测试订单',
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

    // 搜索并找到测试订单
    await orderListPage.searchByOrderNo(testOrderNo);
    const row = await orderListPage.findRowByOrderNo(testOrderNo);
    await expect(row).toBeVisible({ timeout: 5000 });

    // 点击删除按钮
    await orderListPage.clickRowDelete(testOrderNo);

    // 确认删除弹窗
    await confirmDialog(page);

    // 验证成功提示
    await waitForElMessage(page, 'success');

    // 标记已删除，afterAll 不需要再清理
    orderDeleted = true;

    // 等待列表刷新后验证订单已不存在
    await page.waitForTimeout(1000);
    await orderListPage.searchByOrderNo(testOrderNo);
    const rowCount = await orderListPage.getRowCount();
    expect(rowCount).toBe(0);

    // 断言无错误
    errorCollector.assertNoErrors();
  });

  test.afterAll(async () => {
    // 仅在测试未删除时清理
    if (testOrderId && !orderDeleted) {
      try {
        await apiClient.deleteOrder(testOrderId);
      } catch {
        // 清理失败不影响测试结果
      }
    }
  });
});
