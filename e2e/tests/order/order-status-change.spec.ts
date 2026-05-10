import { test, expect } from '../../fixtures';
import { OrderListPage } from '../../pages/order-list.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('订单状态变更', () => {
  let testOrderId: number;
  let testOrderNo: string;
  let apiClient: any;

  test.beforeAll(async ({ apiClient: client }) => {
    apiClient = client;
  });

  test('应该能成功变更订单状态为运输中', async ({ page, authenticatedPage, errorCollector, apiClient: client }) => {
    // 通过 API 创建测试订单（状态：待调度）
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
      loadingAddress: '状态测试装货地址',
      unloadingAddress: '状态测试卸货地址',
      driverId: driver.driverId,
      driverName: driver.driverName,
      vehicleId: driver.vehicleId || '',
      vehiclePlate: driver.vehiclePlate || '',
      goodsList: [{
        goodsId: goods.goodsId,
        goodsName: goods.goodsName,
        goodsUnit: goods.goodsUnit || '吨',
        weight: 12,
        unitPrice: 120,
      }],
      remark: '状态变更测试订单',
    };

    const createResp = await client.createOrder(orderData);
    expect(createResp.code).toBe(200);
    // Backend doesn't return orderId in response, query from list
    const listResp = await client.getOrders({ pageNum: 1, pageSize: 1, orderByColumn: 'create_time', isAsc: 'desc' });
    testOrderId = listResp.rows[0].orderId;
    testOrderNo = listResp.rows[0].orderNo;
    expect(testOrderId).toBeTruthy();
    expect(testOrderNo).toBeTruthy();

    // 导航到订单列表页
    const orderListPage = new OrderListPage(page);
    await orderListPage.goto();

    // 找到测试订单，使用下拉菜单变更状态为"运输中"
    await orderListPage.changeStatus(testOrderNo, '运输中');

    // 确认弹窗
    await confirmDialog(page);

    // 验证成功提示
    await waitForElMessage(page, 'success');

    // 等待表格刷新
    await page.waitForTimeout(1000);

    // 验证状态已变更：重新搜索该订单，检查状态列
    await orderListPage.searchByOrderNo(testOrderNo);
    const row = await orderListPage.findRowByOrderNo(testOrderNo);
    await expect(row).toBeVisible({ timeout: 5000 });
    await expect(row).toContainText('运输中');

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
