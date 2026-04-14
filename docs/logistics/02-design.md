# 物流管理系统设计文档

> **文档类型**: 设计文档
> **项目**: 供应链管理系统 - 物流管理模块
> **版本**: v1.0
> **最后更新**: 2026-04-14

## 文档说明

本文档描述物流管理系统的技术设计和实现方案。

---

## 一、技术架构

### 1.1 技术栈

**后端：**
- Spring Boot 3.x
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
│ logistics_  │         │ logistics_  │
│  customer   │1       n│   order     │
└─────────────┘─────────└─────────────┘
       │                      │
       │                      │
       │ n              1     │ n
┌─────────────┐         ┌─────────────┐
│ logistics_  │         │ logistics_  │
│ settlement  │─────────│   receipt   │
└─────────────┘         └─────────────┘
                              │
                              │
                              │ 1
                       ┌─────────────┐
                       │ logistics_  │
                       │  invoice_*  │
                       └─────────────┘

┌─────────────┐         ┌─────────────┐
│ logistics_  │1       n│ logistics_  │
│  driver     │─────────│   order     │
└─────────────┘         └─────────────┘

┌─────────────┐         ┌─────────────┐
│ logistics_  │1       n│ logistics_  │
│  vehicle    │─────────│   order     │
└─────────────┘         └─────────────┘
```

### 2.2 数据表清单

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| logistics_customer | 客户信息表 | customer_id, customer_code, customer_name, credit_limit |
| logistics_goods | 货物信息表 | goods_id, goods_code, goods_name, reference_price |
| logistics_driver | 司机信息表 | driver_id, driver_code, driver_name, bank_account |
| logistics_vehicle | 车辆信息表 | vehicle_id, plate_number, load_capacity, default_driver_id |
| logistics_order | 运输订单表 | order_id, order_no, customer_id, driver_id, total_amount |
| logistics_receipt | 回单信息表 | receipt_id, receipt_no, order_id, receipt_image |
| logistics_invoice_batch | 发票批次表 | batch_id, batch_no, customer_id, total_amount |
| logistics_invoice_detail | 发票批次明细表 | detail_id, batch_id, order_id, amount |
| logistics_settlement | 财务结算表 | settlement_id, settlement_no, customer_id, total_amount |
| logistics_settlement_detail | 结算明细表 | detail_id, settlement_id, order_id, amount |

---

## 三、核心模块设计

### 3.1 订单管理模块

#### 3.1.1 订单号生成器

```java
/**
 * 订单号生成规则：类型(2位) + 客户编码 + 年月日 + 流水号(4位)
 * 示例：YSCZGS202604140001
 */
public String generateOrderNo(String orderType, String customerCode) {
    // 1. 获取当前日期
    String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    // 2. 查询当天该客户订单数量
    int count = orderMapper.countTodayOrders(customerCode, dateStr);

    // 3. 生成流水号
    String serial = String.format("%04d", count + 1);

    // 4. 拼接订单号
    return orderType + customerCode + dateStr + serial;
}
```

#### 3.1.2 金额计算器

```java
/**
 * 订单金额自动计算
 */
public void calculateAmount(LogisticsOrder order) {
    // 总金额 = 重量 × 运价
    BigDecimal totalAmount = order.getWeight().multiply(order.getUnitPrice());
    order.setTotalAmount(totalAmount);
}
```

#### 3.1.3 司机联动逻辑

```java
/**
 * 选择司机后自动带出车牌号和电话
 */
public void linkDriverInfo(LogisticsOrder order) {
    LogisticsDriver driver = driverService.selectById(order.getDriverId());
    order.setDriverPhone(driver.getDriverPhone());
    order.setPlateNumber(driver.getCommonPlateNumber());
}
```

### 3.2 回单管理模块

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

### 3.3 发票管理模块

#### 3.3.1 合并开票流程

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

#### 3.3.2 取消合并流程

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

### 3.4 财务结算模块

#### 3.4.1 应收结算单生成

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
| DELETE | /api/logistics/order/{ids} | 删除订单 |
| POST | /api/logistics/order/import | 导入订单 |
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
├── base/                          # 基础数据管理
│   ├── customer/index.vue         # 客户管理
│   ├── goods/index.vue            # 货物管理
│   ├── driver/index.vue           # 司机管理
│   └── vehicle/index.vue          # 车辆管理
├── business/                      # 运输业务管理
│   ├── order/
│   │   ├── index.vue             # 订单列表
│   │   └── form.vue              # 订单表单（独立页面）
│   ├── receipt/index.vue         # 回单管理
│   └── invoice/index.vue         # 发票管理
└── settlement/                    # 财务结算管理
    ├── receivable/index.vue      # 应收结算
    ├── payable/index.vue         # 应付结算
    └── report/index.vue          # 财务报表
```

### 5.2 组件复用

```
components/logistics/
├── ImageUpload.vue               # 图片上传组件
├── OrderSelector.vue             # 订单选择器
└── CustomerSelector.vue          # 客户选择器
```

---

## 六、设计变更记录

| 日期 | 版本 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 2026-04-14 | v1.0 | 初始版本，定义完整技术设计 | - |
