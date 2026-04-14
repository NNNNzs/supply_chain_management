# 物流管理系统后端实现清单

## 已完成

1. ✅ 创建 ruoyi-logistics 模块
2. ✅ 配置 pom.xml 依赖
3. ✅ 创建数据库表 SQL 脚本
4. ✅ 创建菜单权限 SQL 脚本

## 待实现（使用代码生成器）

### 使用若依代码生成器生成代码

1. 执行 `sql/logistics.sql` 脚本
2. 访问系统 → 系统工具 → 代码生成
3. 点击"导入"按钮，选择要导入的表
4. 编辑生成配置（基本信息、字段信息、生成信息）
5. 点击"生成代码"下载代码包
6. 将生成的代码复制到对应目录

### 需要生成代码的表

| 表名 | 业务名称 | 功能模块 |
|------|----------|----------|
| logistics_customer | 客户 | 客户管理 |
| logistics_goods | 货物 | 货物管理 |
| logistics_driver | 司机 | 司机管理 |
| logistics_vehicle | 车辆 | 车辆管理 |
| logistics_order | 订单 | 订单管理 |
| logistics_receipt | 回单 | 回单管理 |
| logistics_invoice_batch | 发票批次 | 发票管理 |
| logistics_invoice_detail | 发票明细 | 发票管理 |
| logistics_settlement | 结算 | 财务结算 |
| logistics_settlement_detail | 结算明细 | 财务结算 |

### 代码生成配置建议

#### 1. 基本信息
- 生成包路径：`com.scm.logistics`
- 生成模块名：`logistics`
- 生成业务名：按表名（如 customer、goods、order 等）
- 生成功能名：按业务名称

#### 2. 字段配置
需要设置为"查询"的字段：
- 客户：customer_code, customer_name, status
- 货物：goods_code, goods_name
- 司机：driver_code, driver_name, driver_phone
- 车辆：vehicle_plate
- 订单：order_no, customer_id, order_date, order_status
- 发票：batch_no, customer_id, invoice_status

需要设置为"必填"的字段：
- 所有业务表的编码、名称字段
- 订单表的：customer_id, loading_address, unloading_address, weight, unit_price

#### 3. 生成信息
- 生成路径：勾选"主子表生成"（如发票批次和明细）
- 模板类型：单表（crud）或 树表（tree）

### 代码复制位置说明

生成代码下载后，解压得到以下文件：

```
压缩包/
├── sql/               # SQL 脚本（菜单 SQL，可选）
├── api/               # 前端 API 文件（已手动创建，可忽略）
├── domain/            # 后端实体类 → ruoyi-logistics/src/main/java/com/scm/logistics/domain/
├── mapper/            # 后端 Mapper 接口 → ruoyi-logistics/src/main/java/com/scm/logistics/mapper/
├── mapper/xml/        # 后端 Mapper XML → ruoyi-logistics/src/main/resources/mapper/logistics/
├── service/           # 后端 Service 接口 → ruoyi-logistics/src/main/java/com/scm/logistics/service/
├── service/impl/      # 后端 Service 实现 → ruoyi-logistics/src/main/java/com/scm/logistics/service/impl/
└── controller/        # 后端 Controller → ruoyi-admin/src/main/java/com/scm/web/controller/logistics/
```

### 手动实现的业务逻辑

代码生成器生成的是基础 CRUD 代码，以下业务逻辑需要手动添加：

#### 1. 订单号生成逻辑

位置：`LogisticsOrderServiceImpl.java`

```java
@Override
public void generateOrderNo(LogisticsOrder order) {
    if (StringUtils.isEmpty(order.getOrderNo())) {
        // 订单号格式：类型(2位) + 客户编码 + 年月日 + 流水号
        LogisticsCustomer customer = customerMapper.selectCustomerById(order.getCustomerId());
        String dateStr = DateUtils.parseDateToStr("yyyyMMdd", order.getOrderDate());

        // 查询当天该客户的订单数量作为流水号
        String prefix = "YS" + customer.getCustomerCode() + dateStr;
        int count = orderMapper.selectCountByPrefix(prefix);
        String serialNo = String.format("%04d", count + 1);

        order.setOrderNo(prefix + serialNo);
    }
}
```

#### 2. 订单金额自动计算

```java
@Override
public void calculateAmount(LogisticsOrder order) {
    if (order.getWeight() != null && order.getUnitPrice() != null) {
        order.setTotalAmount(order.getWeight().multiply(new BigDecimal(order.getUnitPrice())));
    }
}
```

#### 3. 合并开票功能

位置：`LogisticsInvoiceBatchServiceImpl.java`

```java
@Override
@Transactional
public void mergeInvoice(InvoiceMergeDTO dto) {
    // 1. 创建发票批次
    LogisticsInvoiceBatch batch = new LogisticsInvoiceBatch();
    batch.setBatchNo(generateBatchNo());
    batch.setCustomerId(dto.getCustomerId());
    batch.setInvoiceDate(dto.getInvoiceDate());
    batch.setInvoiceType(dto.getInvoiceType());
    batch.setTaxRate(dto.getTaxRate());

    // 2. 查询订单并计算金额
    List<LogisticsOrder> orders = orderMapper.selectOrdersByIds(dto.getOrderIds());
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (LogisticsOrder order : orders) {
        totalAmount = totalAmount.add(order.getTotalAmount());
    }
    batch.setTotalAmount(totalAmount);
    batch.setOrderCount(orders.size());
    batch.setTaxAmount(totalAmount.multiply(new BigDecimal(dto.getTaxRate())).divide(new BigDecimal("100")));

    // 3. 插入批次
    invoiceBatchMapper.insertInvoiceBatch(batch);

    // 4. 插入明细
    for (LogisticsOrder order : orders) {
        LogisticsInvoiceDetail detail = new LogisticsInvoiceDetail();
        detail.setBatchId(batch.getBatchId());
        detail.setOrderId(order.getOrderId());
        detail.setOrderNo(order.getOrderNo());
        detail.setAmount(order.getTotalAmount());
        invoiceDetailMapper.insertInvoiceDetail(detail);

        // 5. 更新订单开票状态
        order.setInvoiceStatus("invoiced");
        order.setInvoiceBatchNo(batch.getBatchNo());
        orderMapper.updateOrder(order);
    }
}
```

#### 4. 取消合并开票

```java
@Override
@Transactional
public void cancelMerge(Long batchId) {
    // 1. 查询批次明细
    List<LogisticsInvoiceDetail> details = invoiceDetailMapper.selectByBatchId(batchId);

    // 2. 恢复订单开票状态
    for (LogisticsInvoiceDetail detail : details) {
        LogisticsOrder order = orderMapper.selectOrderById(detail.getOrderId());
        if (order != null) {
            order.setInvoiceStatus("not_invoiced");
            order.setInvoiceBatchNo(null);
            orderMapper.updateOrder(order);
        }
    }

    // 3. 删除明细
    invoiceDetailMapper.deleteByBatchId(batchId);

    // 4. 删除批次
    invoiceBatchMapper.deleteInvoiceBatchById(batchId);
}
```

### Controller 路径配置

确保所有 Controller 的 `@RequestMapping` 路径为：

- `/logistics/customer` - 客户管理
- `/logistics/goods` - 货物管理
- `/logistics/driver` - 司机管理
- `/logistics/vehicle` - 车辆管理
- `/logistics/order` - 订单管理
- `/logistics/receipt` - 回单管理
- `/logistics/invoice` - 发票管理
- `/logistics/settlement` - 结算管理

### 跨域配置

如果前端和后端分离部署，需要在 Controller 或全局配置中添加跨域支持：

```java
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/logistics/customer")
public class LogisticsCustomerController extends BaseController {
    ...
}
```

### 编译和运行

1. 在项目根目录执行：
   ```bash
   mvn clean install
   ```

2. 启动后端服务：
   ```bash
   cd ruoyi-admin
   mvn spring-boot:run
   ```

### 验证

1. 访问 Swagger 文档：`http://localhost:8080/swagger-ui.html`
2. 检查物流管理相关接口是否显示
3. 使用 Postman 测试接口
