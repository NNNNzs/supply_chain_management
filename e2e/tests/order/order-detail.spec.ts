import { test, expect } from '../../fixtures';
import { OrderListPage } from '../../pages/order-list.page';
import { waitForElMessage } from '../../helpers/element-plus';

test.describe('订单详情', () => {
  let testOrderId: number;
  let testOrderNo: string;
  let apiClient: any;
  let customerName: string;

  test.beforeAll(async ({ apiClient: client }) => {
    apiClient = client;
  });

  test('应该能查看订单详情页', async ({ page, authenticatedPage, errorCollector, apiClient: client }) => {
    // 通过 API 创建测试订单
    const today = new Date().toISOString().split('T')[0];
    const customersResp = await client.getCustomers({ pageSize: 1 });
    const customer = customersResp.rows?.[0];
    customerName = customer.customerName;
    const driversResp = await client.getDrivers();
    const driver = driversResp.rows?.[0];
    const goodsResp = await client.getGoods({ pageSize: 1 });
    const goods = goodsResp.rows?.[0];

    const orderData = {
      orderDate: today,
      customerId: customer.customerId,
      customerName: customer.customerName,
      pricingMode: 'weight',
      loadingAddress: '详情测试装货地址',
      unloadingAddress: '详情测试卸货地址',
      driverId: driver.driverId,
      driverName: driver.driverName,
      vehicleId: driver.vehicleId || '',
      vehiclePlate: driver.vehiclePlate || '',
      goodsList: [{
        goodsId: goods.goodsId,
        goodsName: goods.goodsName,
        goodsUnit: goods.goodsUnit || '吨',
        weight: 6,
        unitPrice: 60,
        amount: 360,
      }],
      remark: '详情测试订单备注',
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

    // 点击订单号链接，打开详情页
    await orderListPage.clickRowOrderNo(testOrderNo);

    // 等待详情页加载
    await page.waitForURL(/\/logistics\/order\/detail/, { timeout: 10000 });
    await page.waitForTimeout(500);

    // 验证详情页显示订单基本信息
    // 检查订单号是否显示在详情页
    const detailContent = page.locator('.app-container');
    await expect(detailContent).toBeVisible({ timeout: 5000 });

    // 验证订单号显示在详情页
    await expect(page.getByText(testOrderNo).first()).toBeVisible({ timeout: 5000 });

    // 验证客户名称显示在详情页
    await expect(page.getByText(customerName).first()).toBeVisible({ timeout: 5000 });

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
