package com.scm.logistics.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.scm.common.core.domain.BaseEntity;
import com.scm.common.xss.Xss;

/**
 * 运输订单对象 logistics_order
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 订单类型（transport运输，shuttle短驳） */
    private String orderType;

    /** 来源类型（manual手工创建，bill提单生成） */
    private String sourceType;

    /** 订单日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;

    /** 客户ID */
    private Long customerId;

    /** 客户名称（非数据库字段，用于查询展示） */
    private String customerName;

    /** 装货地址 */
    private String loadingAddress;

    /** 卸货地址 */
    private String unloadingAddress;

    /** 货物ID */
    private Long goodsId;

    /** 货物名称（非数据库字段） */
    private String goodsName;

    /** 货物型号 */
    private String goodsModel;

    /** 重量（吨） */
    private BigDecimal weight;

    /** 实际装车重量(吨) */
    private BigDecimal actualWeight;

    /** 车辆ID */
    private Long vehicleId;

    /** 车辆载重(吨) */
    private BigDecimal loadCapacity;

    /** 派车日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dispatchDate;

    /** 运价（元/吨） */
    private BigDecimal unitPrice;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 总装车重量（吨，从驾驶员单汇总） */
    private BigDecimal totalWeight;

    /** 已分配金额（元，从提单明细汇总） */
    private BigDecimal allocatedAmount;

    /** 代垫付金额 */
    private BigDecimal advancePayment;

    /** 车牌号 */
    private String vehiclePlate;

    /** 司机ID */
    private Long driverId;

    /** 司机电话 */
    private String driverPhone;

    /** 配载单价 */
    private BigDecimal loadingUnitPrice;

    /** 运费支出 */
    private BigDecimal freightCost;

    /** 结算状态（unsettled未结算，partial部分结算，settled已结算） */
    private String settlementStatus;

    /** 付款方式 */
    private String paymentMethod;

    /** 收款人 */
    private String payee;

    /** 回单状态（not_received未收到，received已收到） */
    private String receiptStatus;

    /** 开票状态（not_invoiced未开票，invoiced已开票） */
    private String invoiceStatus;

    /** 开票日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date invoiceDate;

    /** 发票批次号 */
    private String invoiceBatchNo;

    /** 订单状态（pending待运输，transporting运输中，completed已完成，cancelled已取消） */
    private String orderStatus;

    /** 派车状态（not_dispatched未派车，partial_dispatched部分派车，dispatched已派车） */
    private String dispatchStatus;

    /** 货物明细列表（非数据库字段） */
    private List<LogisticsOrderGoods> goodsList;

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    @Xss(message = "订单号不能包含脚本字符")
    @Size(min = 0, max = 50, message = "订单号长度不能超过50个字符")
    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderType()
    {
        return orderType;
    }

    public void setOrderType(String orderType)
    {
        this.orderType = orderType;
    }

    public String getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(Date orderDate)
    {
        this.orderDate = orderDate;
    }

    @NotNull(message = "客户不能为空")
    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    @Xss(message = "装货地址不能包含脚本字符")
    @NotBlank(message = "装货地址不能为空")
    @Size(min = 0, max = 255, message = "装货地址长度不能超过255个字符")
    public String getLoadingAddress()
    {
        return loadingAddress;
    }

    public void setLoadingAddress(String loadingAddress)
    {
        this.loadingAddress = loadingAddress;
    }

    @Xss(message = "卸货地址不能包含脚本字符")
    @NotBlank(message = "卸货地址不能为空")
    @Size(min = 0, max = 255, message = "卸货地址长度不能超过255个字符")
    public String getUnloadingAddress()
    {
        return unloadingAddress;
    }

    public void setUnloadingAddress(String unloadingAddress)
    {
        this.unloadingAddress = unloadingAddress;
    }

    public Long getGoodsId()
    {
        return goodsId;
    }

    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    @Xss(message = "货物型号不能包含脚本字符")
    @Size(min = 0, max = 100, message = "货物型号长度不能超过100个字符")
    public String getGoodsModel()
    {
        return goodsModel;
    }

    public void setGoodsModel(String goodsModel)
    {
        this.goodsModel = goodsModel;
    }

    @NotNull(message = "重量不能为空")
    public BigDecimal getWeight()
    {
        return weight;
    }

    public void setWeight(BigDecimal weight)
    {
        this.weight = weight;
    }

    public BigDecimal getActualWeight()
    {
        return actualWeight;
    }

    public void setActualWeight(BigDecimal actualWeight)
    {
        this.actualWeight = actualWeight;
    }

    public Long getVehicleId()
    {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId)
    {
        this.vehicleId = vehicleId;
    }

    public BigDecimal getLoadCapacity()
    {
        return loadCapacity;
    }

    public void setLoadCapacity(BigDecimal loadCapacity)
    {
        this.loadCapacity = loadCapacity;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getDispatchDate()
    {
        return dispatchDate;
    }

    public void setDispatchDate(Date dispatchDate)
    {
        this.dispatchDate = dispatchDate;
    }

    @NotNull(message = "运价不能为空")
    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalWeight()
    {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight)
    {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getAllocatedAmount()
    {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount)
    {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getAdvancePayment()
    {
        return advancePayment;
    }

    public void setAdvancePayment(BigDecimal advancePayment)
    {
        this.advancePayment = advancePayment;
    }

    @Xss(message = "车牌号不能包含脚本字符")
    @Size(min = 0, max = 20, message = "车牌号长度不能超过20个字符")
    public String getVehiclePlate()
    {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate)
    {
        this.vehiclePlate = vehiclePlate;
    }

    public Long getDriverId()
    {
        return driverId;
    }

    public void setDriverId(Long driverId)
    {
        this.driverId = driverId;
    }

    @Size(min = 0, max = 20, message = "司机电话长度不能超过20个字符")
    public String getDriverPhone()
    {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone)
    {
        this.driverPhone = driverPhone;
    }

    public BigDecimal getLoadingUnitPrice()
    {
        return loadingUnitPrice;
    }

    public void setLoadingUnitPrice(BigDecimal loadingUnitPrice)
    {
        this.loadingUnitPrice = loadingUnitPrice;
    }

    public BigDecimal getFreightCost()
    {
        return freightCost;
    }

    public void setFreightCost(BigDecimal freightCost)
    {
        this.freightCost = freightCost;
    }

    public String getSettlementStatus()
    {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus)
    {
        this.settlementStatus = settlementStatus;
    }

    @Size(min = 0, max = 50, message = "付款方式长度不能超过50个字符")
    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    @Size(min = 0, max = 50, message = "收款人长度不能超过50个字符")
    public String getPayee()
    {
        return payee;
    }

    public void setPayee(String payee)
    {
        this.payee = payee;
    }

    public String getReceiptStatus()
    {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus)
    {
        this.receiptStatus = receiptStatus;
    }

    public String getInvoiceStatus()
    {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus)
    {
        this.invoiceStatus = invoiceStatus;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }

    @Size(min = 0, max = 50, message = "发票批次号长度不能超过50个字符")
    public String getInvoiceBatchNo()
    {
        return invoiceBatchNo;
    }

    public void setInvoiceBatchNo(String invoiceBatchNo)
    {
        this.invoiceBatchNo = invoiceBatchNo;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public String getDispatchStatus()
    {
        return dispatchStatus;
    }

    public void setDispatchStatus(String dispatchStatus)
    {
        this.dispatchStatus = dispatchStatus;
    }

    public List<LogisticsOrderGoods> getGoodsList()
    {
        return goodsList;
    }

    public void setGoodsList(List<LogisticsOrderGoods> goodsList)
    {
        this.goodsList = goodsList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("orderDate", getOrderDate())
            .append("customerId", getCustomerId())
            .append("loadingAddress", getLoadingAddress())
            .append("unloadingAddress", getUnloadingAddress())
            .append("goodsId", getGoodsId())
            .append("goodsName", getGoodsName())
            .append("goodsModel", getGoodsModel())
            .append("weight", getWeight())
            .append("unitPrice", getUnitPrice())
            .append("totalAmount", getTotalAmount())
            .append("advancePayment", getAdvancePayment())
            .append("vehiclePlate", getVehiclePlate())
            .append("driverId", getDriverId())
            .append("driverPhone", getDriverPhone())
            .append("loadingUnitPrice", getLoadingUnitPrice())
            .append("freightCost", getFreightCost())
            .append("settlementStatus", getSettlementStatus())
            .append("paymentMethod", getPaymentMethod())
            .append("payee", getPayee())
            .append("receiptStatus", getReceiptStatus())
            .append("invoiceStatus", getInvoiceStatus())
            .append("invoiceDate", getInvoiceDate())
            .append("invoiceBatchNo", getInvoiceBatchNo())
            .append("orderStatus", getOrderStatus())
            .append("remark", getRemark())
            .toString();
    }
}
