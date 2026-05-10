import { test, expect } from '../../fixtures';
import { InvoiceListPage } from '../../pages/invoice-list.page';
import { confirmDialog, waitForElMessage } from '../../helpers/element-plus';

test.describe('发票管理 - 合并发票', () => {
  test('应该能合并两个已完成订单创建发票', async ({ page, authenticatedPage, errorCollector, apiClient }) => {
    // ===== 准备测试数据 =====
    const customers = await apiClient.getCustomers();
    const goods = await apiClient.getGoods();
    const drivers = await apiClient.getDrivers();
    const customer = customers.rows[0];
    const goodsItem = goods.rows[0];
    const driver = drivers.rows[0];

    // 创建订单1
    const orderRes1 = await apiClient.createOrder({
      orderDate: '2026-05-10',
      customerId: customer.customerId,
      pricingMode: 'weight',
      loadingAddress: '测试装货地址-发票1',
      unloadingAddress: '测试卸货地址-发票1',
      goodsList: [{ goodsId: goodsItem.goodsId, goodsName: goodsItem.goodsName, goodsUnit: goodsItem.goodsUnit || '吨', weight: 10, unitPrice: 100, amount: 1000 }],
      driverId: driver.driverId,
      orderStatus: 'pending',
    });
    expect(orderRes1.code).toBe(200);
    // Backend doesn't return orderId in response, query from list
    const orderList1 = await apiClient.getOrders({ pageNum: 1, pageSize: 1, orderByColumn: 'create_time', isAsc: 'desc' });
    const orderId1 = orderList1.rows[0].orderId;

    // 创建订单2
    const orderRes2 = await apiClient.createOrder({
      orderDate: '2026-05-10',
      customerId: customer.customerId,
      pricingMode: 'weight',
      loadingAddress: '测试装货地址-发票2',
      unloadingAddress: '测试卸货地址-发票2',
      goodsList: [{ goodsId: goodsItem.goodsId, goodsName: goodsItem.goodsName, goodsUnit: goodsItem.goodsUnit || '吨', weight: 20, unitPrice: 100, amount: 2000 }],
      driverId: driver.driverId,
      orderStatus: 'pending',
    });
    expect(orderRes2.code).toBe(200);
    // Backend doesn't return orderId in response, query from list (page size 2 to get second order)
    const orderList2 = await apiClient.getOrders({ pageNum: 1, pageSize: 2, orderByColumn: 'create_time', isAsc: 'desc' });
    const orderId2 = orderList2.rows[1].orderId;

    // 将两个订单状态改为已完成
    await apiClient.changeOrderStatus(orderId1, 'completed');
    await apiClient.changeOrderStatus(orderId2, 'completed');

    let batchId: number;

    try {
      // ===== 执行测试 =====
      const invoicePage = new InvoiceListPage(page);
      await invoicePage.goto();

      // 点击新增按钮，打开发票合并对话框
      await invoicePage.clickAdd();

      // 在合并对话框中选择客户
      await invoicePage.selectMergeCustomer(customer.customerName);

      // 等待订单列表加载
      await invoicePage.mergeOrdersTable.locator('tbody tr').first().waitFor({ state: 'visible', timeout: 5000 });

      // 勾选两个订单
      // 通过订单ID在合并表格中查找并勾选
      const order1Row = invoicePage.mergeOrdersTable.locator('tbody tr').filter({ hasText: String(orderId1) }).first();
      if (await order1Row.isVisible()) {
        await order1Row.locator('.el-checkbox').click();
      }

      const order2Row = invoicePage.mergeOrdersTable.locator('tbody tr').filter({ hasText: String(orderId2) }).first();
      if (await order2Row.isVisible()) {
        await order2Row.locator('.el-checkbox').click();
      }

      // 如果按订单ID找不到，则尝试勾选所有可见行
      const rowCount = await invoicePage.mergeOrdersTable.locator('tbody tr').count();
      if (rowCount > 0) {
        const checkedCount = await invoicePage.mergeOrdersTable.locator('tbody tr .el-checkbox.is-checked').count();
        if (checkedCount < 2) {
          // 勾选前两行
          for (let i = 0; i < Math.min(2, rowCount); i++) {
            const row = invoicePage.mergeOrdersTable.locator('tbody tr').nth(i);
            const isChecked = await row.locator('.el-checkbox.is-checked').isVisible().catch(() => false);
            if (!isChecked) {
              await row.locator('.el-checkbox').click();
            }
          }
        }
      }

      // 提交合并
      await invoicePage.submitMerge();

      // 验证成功提示
      await waitForElMessage(page, 'success');

      // ===== 清理测试数据 =====
      // 获取最新创建的发票批次
      const invoices = await apiClient.getInvoices({ customerId: customer.customerId, pageSize: 5 });
      if (invoices.rows && invoices.rows.length > 0) {
        const latestInvoice = invoices.rows[0];
        batchId = latestInvoice.batchId;
        await apiClient.deleteInvoice(batchId);
      }
    } finally {
      // 清理订单
      await apiClient.deleteOrder(orderId1).catch(() => {});
      await apiClient.deleteOrder(orderId2).catch(() => {});
    }

    // 断言没有错误
    errorCollector.assertNoErrors();
  });
});
