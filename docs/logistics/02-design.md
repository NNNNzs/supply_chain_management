# 物流管理系统设计文档

> **文档类型**: 设计文档
> **项目**: 供应链管理系统 - 物流管理模块
> **版本**: v2.1
> **最后更新**: 2026-04-14

## 文档说明

本文档描述物流管理系统的技术设计和实现方案。

---

## 一、技术架构

### 1.1 技术栈

**后端：**
- Spring Boot 4.0.3
- MyBatis
- MySQL
- Java 17

**前端：**
- Vue 3
- Element Plus
- Vite
- Pinia

**权限：**
- 若依权限系统（基于角色-菜单-按钮）

### 1.2 整体架构

```
┌─────────────────────────────────────────────────────┐
│                   前端层 (Vue 3)                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ 基础数据  │  │ 运输业务  │  │ 财务结算  │          │
│  │  管理页面  │  │  管理页面  │  │  管理页面  │          │
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
                         ↓ HTTP/REST
┌─────────────────────────────────────────────────────┐
│                 Controller 层                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ 基础数据  │  │ 运输业务  │  │ 财务结算  │          │
│  │ Controller│  │ Controller│  │ Controller│          │
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│                  Service 层                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ 基础数据  │  │ 运输业务  │  │ 财务结算  │          │
│  │ Service  │  │ Service  │  │ Service  │          │
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│                  Mapper 层                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ 基础数据  │  │ 运输业务  │  │ 财务结算  │          │
│  │ Mapper   │  │ Mapper   │  │ Mapper   │          │
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│                  数据库层 (MySQL)                    │
│  logistics_customer  logistics_order                │
│  logistics_goods     logistics_receipt              │
│  logistics_driver    logistics_invoice_*            │
│  logistics_vehicle   logistics_settlement_*         │
└─────────────────────────────────────────────────────┘
```

---

## 二、数据库设计

### 2.1 ER 图

```
┌─────────────┐         ┌─────────────┐
│ logistics_  │1       n│ logistics_  │
│  customer   │─────────│    bill     │
└─────────────┘         └─────────────┘
                              │ 1
                              │
                              │ n
                       ┌─────────────┐
                       │ logistics_  │
                       │  bill_item  │
                       └─────────────┘
                              │ n
                              │
                              │
                       ┌─────────────┐
                       │ logistics_  │
                       │ bill_order  │
                       │  _detail    │
                       └─────────────┘
                              │ n
                              │
                              │ 1
┌─────────────┐         ┌─────────────┐
│ logistics_  │1       n│ logistics_  │
│   order     │─────────│   receipt   │
└─────────────┘         └─────────────┘
       │
       │ 1
       │
┌─────────────┐
│ logistics_  │
│  invoice_*  │
└─────────────┘

┌─────────────┐         ┌─────────────┐
│ logistics_  │1       n│ logistics_  │
│  driver     │─────────│    order     │
└─────────────┘         └─────────────┘

┌─────────────┐         ┌─────────────┐
│ logistics_  │1       n│ logistics_  │
│  vehicle    │─────────│    order     │
└─────────────┘         └─────────────┘

┌─────────────┐         ┌─────────────┐
│ logistics_  │1       n│ logistics_  │
│ settlement  │─────────│    order     │
└─────────────┘         └─────────────┘
```

### 2.2 数据表清单

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| logistics_customer | 客户信息表 | customer_id, customer_code, customer_name, credit_limit |
| logistics_goods | 货物信息表 | goods_id, goods_code, goods_name, reference_price |
| logistics_driver | 司机信息表 | driver_id, driver_code, driver_name, bank_account |
| logistics_vehicle | 车辆信息表 | vehicle_id, plate_number, load_capacity, default_driver_id |
| logistics_bill | 提单表（委托单） | bill_id, bill_no, customer_id, total_weight, allocated_weight, bill_status |
| logistics_bill_item | 提单货物明细表 | item_id, bill_id, goods_name, weight, allocated_weight, unit_price |
| logistics_bill_order_detail | 提单运单明细表 | detail_id, bill_id, bill_item_id, order_id, allocated_weight |
| logistics_order | 运单表（货运单/派车单） | order_id, order_no, customer_id, driver_id, vehicle_id, total_amount |
| logistics_receipt | 回单信息表 | receipt_id, receipt_no, order_id, receipt_image |
| logistics_invoice_batch | 发票批次表 | batch_id, batch_no, customer_id, total_amount |
| logistics_invoice_detail | 发票批次明细表 | detail_id, batch_id, order_id, amount |
| logistics_settlement | 财务结算表 | settlement_id, settlement_no, customer_id, total_amount |
| logistics_settlement_detail | 结算明细表 | detail_id, settlement_id, order_id, amount |

---

## 三、核心模块设计

### 3.1 提单管理模块

#### 3.1.1 提单号生成器

```java
/**
 * 提单号生成规则：客户编码 + 年月日 + 流水号(4位)
 * 示例：CZGS202604140001
 * 特点：系统自动生成，不允许手动输入；防重检查包括已删除记录
 */
private void generateBillNo(LogisticsBill bill) {
    if (StringUtils.isEmpty(bill.getBillNo())) {
        // 1. 获取客户信息
        LogisticsCustomer customer = customerMapper.selectCustomerById(bill.getCustomerId());

        // 2. 拼接前缀
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(bill.getBillDate());
        String prefix = customer.getCustomerCode() + dateStr;

        // 3. 查询当天该客户的提单数量
        List<LogisticsBill> bills = billMapper.selectBillsByCustomerId(bill.getCustomerId());
        int count = 0;
        for (LogisticsBill b : bills) {
            if (b.getBillNo() != null && b.getBillNo().startsWith(prefix)) {
                count++;
            }
        }

        // 4. 循环生成提单号，检查数据库中是否存在（包括已删除记录）
        String serialNo;
        String candidateBillNo;
        int attempt = 0;
        do {
            serialNo = String.format("%04d", count + 1 + attempt);
            candidateBillNo = prefix + serialNo;
            attempt++;
        } while (billMapper.checkBillNoExistsInDb(candidateBillNo) != null);

        bill.setBillNo(candidateBillNo);
    }
}
```

#### 3.1.2 提单汇总计算

```java
/**
 * 从货物明细汇总提单数据
 */
public void computeBillTotalsFromItems(LogisticsBill bill) {
    List<LogisticsBillItem> items = bill.getBillItems();

    // 汇总总重量和总金额
    BigDecimal totalWeight = items.stream()
        .map(LogisticsBillItem::getWeight)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalAmount = items.stream()
        .map(item -> item.getWeight().multiply(item.getUnitPrice()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    bill.setTotalWeight(totalWeight);
    bill.setTotalAmount(totalAmount);
}
```

#### 3.1.3 提单状态更新

```java
/**
 * 根据货物明细分配情况更新提单状态
 */
public void updateBillStatus(Long billId) {
    // 查询提单的所有货物明细
    List<LogisticsBillItem> items = billItemMapper.selectItemsByBillId(billId);

    // 检查是否所有明细都已完全分配
    boolean fullyAllocated = items.stream()
        .allMatch(item -> {
            BigDecimal remain = item.getWeight()
                .subtract(item.getAllocatedWeight() != null ? item.getAllocatedWeight() : BigDecimal.ZERO);
            return remain.compareTo(BigDecimal.ZERO) <= 0;
        });

    LogisticsBill bill = billMapper.selectById(billId);
    if (fullyAllocated) {
        bill.setBillStatus("allocated");
    } else if (hasAllocation(billId)) {
        bill.setBillStatus("partial");
    } else {
        bill.setBillStatus("pending");
    }
    billMapper.updateById(bill);
}
```

---

### 3.2 配载管理模块

#### 3.2.1 智能推荐货物

```java
/**
 * 根据车辆载重推荐可配载的货物明细
 */
public List<BillAllocationItem> recommendBills(Double loadCapacity) {
    // 查询待配载的货物明细
    List<LogisticsBillItem> pendingItems = billItemMapper.selectPendingBillItems();

    // 转换为分配项并按剩余重量排序（优先推荐接近载重的货物）
    return pendingItems.stream()
        .map(this::convertToAllocationItem)
        .sorted(Comparator.comparing(BillAllocationItem::getRemainWeight).reversed())
        .collect(Collectors.toList());
}
```

#### 3.2.2 创建运单并分配提单

```java
/**
 * 配载核心逻辑：创建运单并分配提单货物明细
 */
@Transactional
public AllocationResultVO createOrderWithBills(
    List<BillAllocationItem> allocationItems,
    Long driverId,
    Long vehicleId,
    String loadingDate
) {
    // 1. 创建运单（含司机、车辆信息）
    LogisticsOrder order = buildOrderFromBills(allocationItems);
    order.setSourceType("bill");
    order.setDriverId(driverId);
    order.setVehicleId(vehicleId);
    order.setDispatchDate(DateUtils.parseDate(loadingDate));
    orderMapper.insertOrder(order);

    // 2. 创建提单运单明细关联
    List<LogisticsBillOrderDetail> details = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (BillAllocationItem item : allocationItems) {
        LogisticsBillOrderDetail detail = new LogisticsBillOrderDetail();
        detail.setBillId(item.getBillId());
        detail.setBillItemId(item.getBillItemId());
        detail.setOrderId(order.getOrderId());
        detail.setAllocatedWeight(item.getAllocWeight());
        detail.setUnitPrice(item.getUnitPrice());
        detail.setAllocatedAmount(item.getAllocWeight().multiply(item.getUnitPrice()));
        details.add(detail);

        totalAmount = totalAmount.add(detail.getAllocatedAmount());

        // 更新提单货物明细的已分配重量
        LogisticsBillItem billItem = billItemMapper.selectById(item.getBillItemId());
        billItem.setAllocatedWeight(billItem.getAllocatedWeight().add(item.getAllocWeight()));
        billItemMapper.updateById(billItem);
    }

    // 批量插入明细
    billOrderDetailMapper.batchInsert(details);

    // 3. 更新运单汇总数据
    order.setTotalAmount(totalAmount);
    order.setFreightCost(totalAmount);
    orderMapper.updateOrder(order);

    // 4. 更新提单状态
    for (BillAllocationItem item : allocationItems) {
        updateBillStatus(item.getBillId());
    }

    // 5. 返回配载结果
    return buildAllocationResult(order, allocationItems.size());
}
```

---

### 3.3 运单管理模块

#### 3.3.1 运单号生成器

```java
/**
 * 运单号生成规则：类型(2位) + 客户编码 + 年月日 + 流水号(4位)
 * 示例：YSCZGS202604140001
 * 特点：防重检查包括已删除记录，循环递增流水号直到找到未使用的号码
 */
@Override
public void generateOrderNo(LogisticsOrder logisticsOrder) {
    if (StringUtils.isEmpty(logisticsOrder.getOrderNo())) {
        LogisticsCustomer customer = customerMapper.selectCustomerById(logisticsOrder.getCustomerId());
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(logisticsOrder.getOrderDate());
        String prefix = "YS" + customer.getCustomerCode() + dateStr;

        List<LogisticsOrder> orders = orderMapper.selectOrdersByCustomerId(logisticsOrder.getCustomerId());
        int count = 0;
        for (LogisticsOrder order : orders) {
            if (order.getOrderNo() != null && order.getOrderNo().startsWith(prefix)) {
                count++;
            }
        }

        // 循环检查数据库中是否存在（包括已删除记录）
        String serialNo;
        String candidateOrderNo;
        int attempt = 0;
        do {
            serialNo = String.format("%04d", count + 1 + attempt);
            candidateOrderNo = prefix + serialNo;
            attempt++;
        } while (orderMapper.checkOrderNoExistsInDb(candidateOrderNo) != null);

        logisticsOrder.setOrderNo(candidateOrderNo);
    }
}
```

#### 3.3.2 金额计算器

```java
/**
 * 运单金额自动计算
 */
public void calculateAmount(LogisticsOrder order) {
    // 总金额 = 重量 × 运价
    BigDecimal totalAmount = order.getWeight().multiply(order.getUnitPrice());
    order.setTotalAmount(totalAmount);
}
```

#### 3.3.3 司机车辆联动逻辑

```java
/**
 * 选择司机后自动带出司机信息
 */
public void linkDriverInfo(LogisticsOrder order) {
    LogisticsDriver driver = driverService.selectById(order.getDriverId());
    if (driver != null) {
        order.setDriverPhone(driver.getDriverPhone());
    }
}

/**
 * 选择车辆后自动带出车辆信息和默认司机
 */
public void linkVehicleInfo(LogisticsOrder order) {
    LogisticsVehicle vehicle = vehicleService.selectById(order.getVehicleId());
    if (vehicle != null) {
        order.setVehiclePlate(vehicle.getVehiclePlate());
        order.setLoadCapacity(vehicle.getLoadCapacity());
        if (order.getDriverId() == null && vehicle.getDefaultDriverId() != null) {
            order.setDriverId(vehicle.getDefaultDriverId());
            linkDriverInfo(order);
        }
    }
}
```

#### 3.3.4 订单状态更改（专用接口）

```java
/**
 * 更改订单状态 - 专用接口
 * 特点：只更新状态字段，不影响其他数据，不触发订单号生成
 *
 * Controller:
 *   @PutMapping("/changeStatus/{orderId}")
 *   public AjaxResult changeStatus(@PathVariable Long orderId, @RequestBody LogisticsOrder order) {
 *       return toAjax(orderService.changeOrderStatus(orderId, order.getOrderStatus()));
 *   }
 */
@Override
public int changeOrderStatus(Long orderId, String orderStatus) {
    LogisticsOrder order = new LogisticsOrder();
    order.setOrderId(orderId);
    order.setOrderStatus(orderStatus);
    return orderMapper.updateOrder(order);
}
```

**设计要点**：
- 使用独立方法而非 `updateOrder`，避免触发订单号生成和金额计算
- Controller 路由必须放在 `@DeleteMapping` 之前，避免路径冲突
- 前端使用专门的 `changeStatus` API 而非通用的 `update` API

---

### 3.4 回单管理模块

#### 3.2.1 回单编号生成

```java
/**
 * 回单号生成规则：HD + 年月日 + 流水号(4位)
 * 示例：HD202604140001
 */
public String generateReceiptNo() {
    String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    int count = receiptMapper.countTodayReceipts(dateStr);
    String serial = String.format("%04d", count + 1);
    return "HD" + dateStr + serial;
}
```

#### 3.2.2 回单确认流程

```java
/**
 * 回单确认
 */
public void confirmReceipt(Long receiptId, String receiver) {
    LogisticsReceipt receipt = new LogisticsReceipt();
    receipt.setReceiptId(receiptId);
    receipt.setReceiptStatus("received");
    receipt.setReceiver(receiver);
    receipt.setReceiveTime(LocalDateTime.now());
    receiptMapper.updateById(receipt);

    // 更新订单回单状态
    LogisticsOrder order = orderMapper.selectByReceiptId(receiptId);
    if (order != null) {
        order.setReceiptStatus("received");
        orderMapper.updateById(order);
    }
}
```

### 3.5 删除保护设计

所有模块的删除操作统一采用以下保护策略：

#### 3.5.1 保护规则

| 模块 | 状态保护 | 关联保护 | 删除方式 |
|------|----------|----------|----------|
| 客户管理 | - | 有关联提单不能删除 | 逻辑删除 |
| 货物管理 | - | - | 逻辑删除 |
| 提单管理 | 运输中/已完成不能删除 | 有关联运单不能删除 | 逻辑删除(del_flag='2') |
| 运单管理 | 已完成不能删除 | 已结算不能删除 | 逻辑删除 |

#### 3.5.2 级联更新

```java
/**
 * 删除运单时的级联处理
 */
@Override
@Transactional
public int deleteOrderById(Long orderId) {
    LogisticsOrder order = orderMapper.selectOrderById(orderId);
    if (order == null) throw new ServiceException("订单不存在");
    if ("completed".equals(order.getOrderStatus())) throw new ServiceException("已完成的订单不能删除");
    if ("settled".equals(order.getSettlementStatus())) throw new ServiceException("已结算的订单不能删除");

    // 1. 获取关联的提单ID列表
    List<Long> billIds = billOrderDetailMapper.selectBillIdsByOrderId(orderId);

    // 2. 删除运单的提单关联明细
    billOrderDetailMapper.deleteDetailsByOrderId(orderId);

    // 3. 更新关联提单的状态和已分配重量
    for (Long billId : billIds) {
        updateBillStatusAfterOrderDelete(billId);
    }

    // 4. 逻辑删除订单
    return orderMapper.deleteOrderById(orderId);
}

/**
 * 删除运单后更新提单状态
 */
private void updateBillStatusAfterOrderDelete(Long billId) {
    Double allocatedWeight = billOrderDetailMapper.sumAllocatedWeightByBillId(billId);
    LogisticsBill bill = billMapper.selectLogisticsBillByBillId(billId);
    if (bill != null) {
        bill.setAllocatedWeight(allocatedWeight != null
            ? BigDecimal.valueOf(allocatedWeight) : BigDecimal.ZERO);
        if (bill.getAllocatedWeight().compareTo(BigDecimal.ZERO) == 0)
            bill.setBillStatus("pending");
        else if (bill.getAllocatedWeight().compareTo(bill.getTotalWeight()) < 0)
            bill.setBillStatus("partial");
        else
            bill.setBillStatus("allocated");
        billMapper.updateBillStatusAndWeight(bill);
    }
}
```

#### 3.5.3 逻辑删除与唯一约束冲突

**问题**：MySQL 的唯一约束检查所有行（包括 del_flag='2' 的已删除记录），导致逻辑删除后无法创建相同号码的新记录。

**解决方案**：生成号码时使用 `checkXxxExistsInDb()` 方法检查数据库中所有记录（不限 del_flag），循环递增流水号直到找到未使用的号码。

---

### 3.6 发票管理模块

#### 3.6.1 合并开票流程

```java
/**
 * 合并开票核心逻辑
 */
@Transactional
public void createInvoiceBatch(InvoiceBatchVO batchVO) {
    // 1. 创建发票批次记录
    LogisticsInvoiceBatch batch = new LogisticsInvoiceBatch();
    batch.setBatchNo(generateBatchNo());
    batch.setCustomerId(batchVO.getCustomerId());
    batch.setInvoiceDate(LocalDate.now());
    batch.setInvoiceType(batchVO.getInvoiceType());
    batch.setTaxRate(batchVO.getTaxRate());

    // 2. 计算开票总金额和税额
    BigDecimal totalAmount = BigDecimal.ZERO;
    List<LogisticsInvoiceDetail> details = new ArrayList<>();

    for (Long orderId : batchVO.getOrderIds()) {
        LogisticsOrder order = orderMapper.selectById(orderId);
        totalAmount = totalAmount.add(order.getTotalAmount());

        // 创建发票明细
        LogisticsInvoiceDetail detail = new LogisticsInvoiceDetail();
        detail.setOrderId(orderId);
        detail.setOrderNo(order.getOrderNo());
        detail.setAmount(order.getTotalAmount());
        details.add(detail);

        // 更新订单开票状态
        order.setInvoiceStatus("invoiced");
        order.setBatchNo(batch.getBatchNo());
        orderMapper.updateById(order);
    }

    // 3. 计算税额
    BigDecimal taxAmount = totalAmount.multiply(batch.getTaxRate())
                                      .divide(new BigDecimal("100"));

    batch.setTotalAmount(totalAmount);
    batch.setTaxAmount(taxAmount);
    batch.setOrderCount(batchVO.getOrderIds().size());
    batch.setInvoiceStatus("pending");

    // 4. 保存批次和明细
    invoiceBatchMapper.insert(batch);
    details.forEach(d -> {
        d.setBatchId(batch.getBatchId());
        invoiceDetailMapper.insert(d);
    });
}
```

#### 3.6.2 取消合并流程

```java
/**
 * 取消合并开票
 */
@Transactional
public void cancelInvoiceBatch(Long batchId) {
    // 1. 查询批次明细
    List<LogisticsInvoiceDetail> details = invoiceDetailMapper.selectByBatchId(batchId);

    // 2. 恢复订单开票状态
    details.forEach(detail -> {
        LogisticsOrder order = orderMapper.selectById(detail.getOrderId());
        order.setInvoiceStatus("uninvoiced");
        order.setBatchNo(null);
        orderMapper.updateById(order);
    });

    // 3. �除批次和明细
    invoiceDetailMapper.deleteByBatchId(batchId);
    invoiceBatchMapper.deleteById(batchId);
}
```

### 3.7 财务结算模块

#### 3.7.1 应收结算单生成

```java
/**
 * 生成应收结算单
 */
@Transactional
public void createReceivableSettlement(SettlementVO settlementVO) {
    // 1. 查询符合条件的订单
    List<LogisticsOrder> orders = orderMapper.selectOrdersForSettlement(
        settlementVO.getCustomerId(),
        settlementVO.getStartDate(),
        settlementVO.getEndDate()
    );

    // 2. 计算结算总金额
    BigDecimal totalAmount = orders.stream()
        .map(LogisticsOrder::getTotalAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // 3. 创建结算单
    LogisticsSettlement settlement = new LogisticsSettlement();
    settlement.setSettlementNo(generateSettlementNo());
    settlement.setCustomerId(settlementVO.getCustomerId());
    settlement.setSettlementType("receivable");
    settlement.setSettlementDate(LocalDate.now());
    settlement.setStartDate(settlementVO.getStartDate());
    settlement.setEndDate(settlementVO.getEndDate());
    settlement.setTotalAmount(totalAmount);
    settlement.setPaidAmount(BigDecimal.ZERO);
    settlement.setUnpaidAmount(totalAmount);
    settlement.setSettlementStatus("draft");

    settlementMapper.insert(settlement);

    // 4. 创建结算明细
    orders.forEach(order -> {
        LogisticsSettlementDetail detail = new LogisticsSettlementDetail();
        detail.setSettlementId(settlement.getSettlementId());
        detail.setOrderId(order.getOrderId());
        detail.setOrderNo(order.getOrderNo());
        detail.setAmount(order.getTotalAmount());
        settlementDetailMapper.insert(detail);
    });
}
```

---

## 四、接口设计

### 4.1 RESTful API 规范

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/logistics/order/list | 查询订单列表 |
| GET | /api/logistics/order/{id} | 获取订单详情 |
| POST | /api/logistics/order | 创建订单 |
| PUT | /api/logistics/order | 更新订单 |
| PUT | /api/logistics/order/changeStatus/{orderId} | 更改订单状态（专用接口） |
| DELETE | /api/logistics/order/{ids} | 删除订单 |
| POST | /api/logistics/order/importData | 导入订单 |
| GET | /api/logistics/receipt/list | 查询回单列表 |
| POST | /api/logistics/receipt/confirm | 确认回单 |

### 4.2 响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,
    "rows": []
  }
}
```

---

## 五、前端设计

### 5.1 页面结构

```
views/logistics/
├── customer/index.vue             # 客户管理
├── goods/index.vue                # 货物管理
├── driver/index.vue               # 司机管理
├── vehicle/index.vue              # 车辆管理（默认司机下拉选择）
├── bill/index.vue                 # 提单管理（多货物明细、展开式表格）
├── allocation/index.vue           # 配载管理（三栏布局）
├── order/
│   ├── index.vue                  # 运单列表（URL参数自动搜索）
│   └── form.vue                   # 运单表单（独立页面）
├── receipt/index.vue              # 回单管理
├── business/invoice/index.vue     # 发票管理
└── settlement/                    # 财务结算管理（待实现）
    ├── receivable/index.vue       # 应收结算
    ├── payable/index.vue          # 应付结算
    └── report/index.vue           # 财务报表
```

### 5.2 组件复用

```
components/
├── GoodsDialog.vue                # 货物新增弹窗（可复用，提单页和货物管理共用）
```

**GoodsDialog 组件设计**：
- Props: `modelValue`（v-model 控制显隐）, `title`
- Emits: `update:modelValue`, `success`（新增成功后回调）
- 表单字段: goodsName, goodsModel, goodsCategory, goodsUnit, unitPrice
- 货物编码自动生成（GD + 时间戳）

---

## 六、设计变更记录

| 日期 | 版本 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 2026-04-14 | v1.0 | 初始版本，定义完整技术设计 | - |
| 2026-04-14 | v2.0 | 新增提单和配载模块设计；合并驾驶员单到运单；更新ER图和表结构 | - |
| 2026-04-14 | v2.1 | 新增删除保护设计、运单号防重逻辑、状态更改专用接口、GoodsDialog组件设计；更新提单号生成器实现；更新API和页面结构 | - |
