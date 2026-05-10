import { test, expect } from '../../fixtures';
import { InvoiceListPage } from '../../pages/invoice-list.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('发票管理 - 开具发票', () => {
  test('应该能成功开具已合并的发票', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
    // ===== 准备测试数据 =====
    const customers = await apiClient.getCustomers();
    const goods = await apiClient.getGoods();
    const drivers = await apiClient.getDrivers();
    const customer = customers.rows[0];
    const goodsItem = goods.rows[0];
    const driver = drivers.rows[0];

    // 创建已完成订单
    const orderRes = await apiClient.createOrder({
      orderDate: '2026-05-10',
      customerId: customer.customerId,
      pricingMode: 'weight',
      loadingAddress: '测试装货地址-开具',
      unloadingAddress: '测试卸货地址-开具',
      goodsList: [{ goodsId: goodsItem.goodsId, goodsName: goodsItem.goodsName, goodsUnit: goodsItem.goodsUnit || '吨', weight: 10, unitPrice: 100, amount: 1000 }],
      driverId: driver.driverId,
      orderStatus: 'pending',
    });
    expect(orderRes.code).toBe(200);
    // Backend doesn't return orderId in response, query from list
    const orderList = await apiClient.getOrders({ pageNum: 1, pageSize: 1 });
    const orderId = orderList.rows[0].orderId;

    // 将订单状态改为已完成
    await apiClient.changeOrderStatus(orderId, 'completed');

    // 通过 API 合并发票
    const invoiceRes = await apiClient.mergeInvoice({
      customerId: customer.customerId,
      invoiceDate: '2026-05-10',
      invoiceType: 'ordinary',
      taxRate: 0,
      orderIds: [orderId],
    });
    const batchId = invoiceRes.data.batchId || invoiceRes.data;
    const batchNo = invoiceRes.data.batchNo;

    try {
      // ===== 执行测试 =====
      const invoicePage = new InvoiceListPage(page);
      await invoicePage.goto();

      // 如果有批次号，先搜索定位；否则直接勾选
      if (batchNo) {
        await invoicePage.searchByBatchNo(batchNo);
      }

      // 勾选发票行
      await invoicePage.selectRowCheckbox(batchNo || String(batchId));

      // 点击开具按钮
      await invoicePage.clickIssue();

      // 确认弹窗
      await confirmDialog(page);

      // 验证成功提示
      await waitForElMessage(page, 'success');
    } finally {
      // 清理：取消合并并删除发票，删除订单
      await apiClient.cancelMergeInvoice(batchId).catch(() => {});
      await apiClient.deleteInvoice(batchId).catch(() => {});
      await apiClient.deleteOrder(orderId).catch(() => {});
    }

    // 断言没有错误
    errorCollector.assertNoErrors();
  });
});
