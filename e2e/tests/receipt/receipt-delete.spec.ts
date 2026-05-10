import { test, expect } from '../../fixtures';
import { ReceiptListPage } from '../../pages/receipt-list.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('回单管理 - 删除回单', () => {
  test('应该能成功删除回单', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
    let orderId: number;
    let receiptId: number;
    let receiptDeleted = false;

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

      // 获取回单号
      const receiptListResp = await apiClient.getReceipts({ receiptId });
      const receiptNo = receiptListResp.rows[0].receiptNo;

      // 进入回单列表页
      const receiptListPage = new ReceiptListPage(page);
      await receiptListPage.goto();

      // 找到回单行，点击删除按钮
      await receiptListPage.clickRowDelete(receiptNo);

      // 确认删除弹窗
      await confirmDialog(page);

      // 验证成功提示
      await waitForElMessage(page, 'success');

      // 标记已删除，避免 finally 中重复删除
      receiptDeleted = true;

      // 验证列表中不再有该回单
      await receiptListPage.searchByReceiptNo(receiptNo);
      const rowCount = await receiptListPage.getRowCount();
      expect(rowCount).toBe(0);

      // 断言无错误
      errorCollector.assertNoErrors();
    } finally {
      // 清理：如果 UI 删除失败，回单可能还存在
      if (!receiptDeleted && receiptId) {
        await apiClient.deleteReceipt(receiptId).catch(() => {});
      }
      if (orderId) {
        await apiClient.deleteOrder(orderId).catch(() => {});
      }
    }
  });
});
