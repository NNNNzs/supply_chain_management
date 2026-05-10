import { test, expect } from '../../fixtures';
import { ReceiptListPage } from '../../pages/receipt-list.page';

test.describe('回单管理 - 搜索回单', () => {
  test('应该能通过回单号和订单号搜索回单', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
    let orderId: number;
    let receiptId: number;

    try {
      // 准备测试数据：获取客户、货物、司机
      const [customers, goods, drivers] = await Promise.all([
        apiClient.getCustomers(),
        apiClient.getGoods(),
        apiClient.getDrivers(),
      ]);

      const customer = customers.rows[0];
      const goodsItem = goods.rows[0];
      const driver = drivers.rows[0];

      // 创建测试订单
      const orderResp = await apiClient.createOrder({
        orderDate: '2026-05-10',
        customerId: customer.customerId,
        pricingMode: 'weight',
        loadingAddress: '测试装货地址',
        unloadingAddress: '测试卸货地址',
        goodsList: [{ goodsId: goodsItem.goodsId, goodsName: goodsItem.goodsName, goodsUnit: goodsItem.goodsUnit || '吨', weight: 10, unitPrice: 100, amount: 1000 }],
        driverId: driver.driverId,
        orderStatus: 'pending',
      });
      expect(orderResp.code).toBe(200);
      // Backend doesn't return orderId in response, query from list
      const orderListResp = await apiClient.getOrders({ pageNum: 1, pageSize: 1, orderByColumn: 'create_time', isAsc: 'desc' });
      orderId = orderListResp.rows[0].orderId;

      // 将订单状态改为运输中
      await apiClient.changeOrderStatus(orderId, 'transporting');

      // 创建回单
      const receiptResp = await apiClient.createReceipt({
        orderId,
        receiptDate: '2026-05-10',
        receiptStatus: 'not_received',
      });
      expect(receiptResp.code).toBe(200);
      receiptId = receiptResp.data.receiptId || receiptResp.data;

      // 获取回单号和订单号
      const receiptListResp = await apiClient.getReceipts({ receiptId });
      const receiptNo = receiptListResp.rows[0].receiptNo;

      const orderData = await apiClient.getOrders({ pageNum: 1, pageSize: 1, orderByColumn: 'create_time', isAsc: 'desc' });
      const orderNo = orderData.rows[0].orderNo;

      // 进入回单列表页
      const receiptListPage = new ReceiptListPage(page);
      await receiptListPage.goto();

      // 通过回单号搜索
      await receiptListPage.searchByReceiptNo(receiptNo);
      const receiptNoRowCount = await receiptListPage.getRowCount();
      expect(receiptNoRowCount).toBeGreaterThanOrEqual(1);

      // 验证搜索结果包含目标回单号
      const receiptRow = await receiptListPage.findRowByReceiptNo(receiptNo);
      await expect(receiptRow).toBeVisible();

      // 通过订单号搜索
      await receiptListPage.searchByOrderNo(orderNo);
      const orderNoRowCount = await receiptListPage.getRowCount();
      expect(orderNoRowCount).toBeGreaterThanOrEqual(1);

      // 验证搜索结果包含目标回单号
      const orderSearchRow = await receiptListPage.findRowByReceiptNo(receiptNo);
      await expect(orderSearchRow).toBeVisible();

      // 测试重置按钮：清空搜索条件并重新查询
      // 先填入搜索条件
      await receiptListPage.searchReceiptNo.clear();
      await receiptListPage.searchOrderNo.clear();
      await receiptListPage.searchBtn.click();
      await page.waitForTimeout(500);

      // 断言无错误
      errorCollector.assertNoErrors();
    } finally {
      // 清理：删除回单和订单
      if (receiptId) {
        await apiClient.deleteReceipt(receiptId).catch(() => {});
      }
      if (orderId) {
        await apiClient.deleteOrder(orderId).catch(() => {});
      }
    }
  });
});
