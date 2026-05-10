import { test, expect } from '../../fixtures';
import { OrderListPage } from '../../pages/order-list.page';
import { waitForElMessage } from '../../helpers/element-plus';

test.describe('订单搜索', () => {
  let testOrderId: number;
  let testOrderNo: string;
  let apiClient: any;

  test.beforeAll(async ({ apiClient: client }) => {
    apiClient = client;
  });

  test('应该能按订单号搜索并重置', async ({ page, authenticatedPage, errorCollector, apiClient: client }) => {
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
      loadingAddress: '搜索测试装货地址',
      unloadingAddress: '搜索测试卸货地址',
      driverId: driver.driverId,
      driverName: driver.driverName,
      vehicleId: driver.vehicleId || '',
      vehiclePlate: driver.vehiclePlate || '',
      goodsList: [{
        goodsId: goods.goodsId,
        goodsName: goods.goodsName,
        goodsUnit: goods.goodsUnit || '吨',
        weight: 8,
        unitPrice: 80,
      }],
      remark: '搜索测试订单',
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

    // 按订单号搜索
    await orderListPage.searchByOrderNo(testOrderNo);

    // 验证搜索结果包含目标订单
    const searchResultRow = await orderListPage.findRowByOrderNo(testOrderNo);
    await expect(searchResultRow).toBeVisible({ timeout: 5000 });

    // 验证搜索结果只有匹配的订单（至少1条）
    const rowCount = await orderListPage.getRowCount();
    expect(rowCount).toBeGreaterThanOrEqual(1);

    // 测试重置按钮
    await orderListPage.resetSearch();

    // 重置后搜索框应该为空
    await expect(orderListPage.searchOrderNo).toHaveValue('');

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
