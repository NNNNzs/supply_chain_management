import { test, expect } from '../../fixtures';
import { InvoiceListPage } from '../../pages/invoice-list.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('发票管理 - 删除发票', () => {
  test('应该能成功删除未开具的发票', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
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
      loadingAddress: '测试装货地址-删除',
      unloadingAddress: '测试卸货地址-删除',
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

    // 通过 API 合并发票（不开具）
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

      // 搜索定位发票
      if (batchNo) {
        await invoicePage.searchByBatchNo(batchNo);
      }

      // 点击行内删除按钮
      await invoicePage.clickRowDelete(batchNo || String(batchId));

      // 确认弹窗
      await confirmDialog(page);

      // 验证成功提示
      await waitForElMessage(page, 'success');

      // 验证发票已被删除：搜索该批次号，结果应为空或不含该记录
      await invoicePage.searchByBatchNo(batchNo);
      const rowCount = await invoicePage.getRowCount();
      // 删除后搜索可能返回0条，也可能因为搜索缓存仍有数据
      // 如果有结果，确保不包含已删除的批次号
      if (rowCount > 0) {
        const rows = invoicePage.invoiceTable.locator('tbody tr');
        for (let i = 0; i < rowCount; i++) {
          const rowText = await rows.nth(i).textContent();
          expect(rowText).not.toContain(batchNo);
        }
      }
    } finally {
      // 清理：发票可能已被删除，再次尝试清理
      await apiClient.deleteInvoice(batchId).catch(() => {});
      await apiClient.deleteOrder(orderId).catch(() => {});
    }

    // 断言没有错误
    errorCollector.assertNoErrors();
  });
});
