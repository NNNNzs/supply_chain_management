import { test, expect } from '../../fixtures';
import { ReceiptListPage } from '../../pages/receipt-list.page';
import { ReceiptDialogPage } from '../../pages/receipt-dialog.page';
import { waitForElMessage } from '../../helpers/element-plus';

test.describe('回单管理 - 新增回单', () => {
  test('应该能成功创建回单', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
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
      const orderListResp = await apiClient.getOrders({ pageNum: 1, pageSize: 1 });
      orderId = orderListResp.rows[0].orderId;

      // 将订单状态改为运输中（回单要求订单在运输中）
      await apiClient.changeOrderStatus(orderId, 'transporting');

      // 进入回单列表页
      const receiptListPage = new ReceiptListPage(page);
      await receiptListPage.goto();

      // 点击新增按钮
      await receiptListPage.clickAdd();

      // 在弹窗中填写回单信息
      const receiptDialog = new ReceiptDialogPage(page);
      await receiptDialog.waitForVisible();

      // 获取订单号用于选择
      const orderData = await apiClient.getOrders({ pageNum: 1, pageSize: 1 });
      const orderNo = orderData.rows[0].orderNo;

      // 选择订单
      await receiptDialog.selectOrder(orderNo);

      // 填写回单日期
      await receiptDialog.selectDate('2026-05-10');

      // 提交
      await receiptDialog.submit();

      // 验证成功提示
      await waitForElMessage(page, 'success');

      // 验证列表中能看到新建的回单
      const receiptResp = await apiClient.getReceipts({ orderId });
      expect(receiptResp.rows.length).toBeGreaterThan(0);
      receiptId = receiptResp.rows[0].receiptId;

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
