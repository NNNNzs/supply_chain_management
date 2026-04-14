package com.scm.logistics.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 驾驶员单对象 logistics_driver_order
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsDriverOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 驾驶员单ID */
    private Long driverOrderId;

    /** 驾驶员单号 */
    @Excel(name = "驾驶员单号")
    private String driverOrderNo;

    /** 运单ID */
    private Long orderId;

    /** 运单号 */
    @Excel(name = "运单号")
    private String orderNo;

    /** 司机ID */
    private Long driverId;

    /** 司机姓名 */
    @Excel(name = "司机姓名")
    private String driverName;

    /** 司机电话 */
    @Excel(name = "司机电话")
    private String driverPhone;

    /** 车辆ID */
    private Long vehicleId;

    /** 车牌号 */
    @Excel(name = "车牌号")
    private String vehiclePlate;

    /** 车辆载重(吨) */
    @Excel(name = "车辆载重(吨)")
    private BigDecimal loadCapacity;

    /** 实际装车重量(吨) */
    @Excel(name = "实际装车重量(吨)")
    private BigDecimal actualWeight;

    /** 配载单价(元/吨) */
    @Excel(name = "配载单价(元/吨)")
    private BigDecimal loadingUnitPrice;

    /** 运费支出(元) */
    @Excel(name = "运费支出(元)")
    private BigDecimal freightCost;

    /** 代垫付金额(元) */
    @Excel(name = "代垫付金额(元)")
    private BigDecimal advancePayment;

    /** 付款方式 */
    @Excel(name = "付款方式")
    private String paymentMethod;

    /** 收款人 */
    @Excel(name = "收款人")
    private String payee;

    /** 派车日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "派车日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dispatchDate;

    /** 派车状态 */
    @Excel(name = "派车状态", readConverterExp = "pending=待出车,transporting=运输中,completed=已完成,cancelled=已取消")
    private String dispatchStatus;

    /** 结算状态 */
    @Excel(name = "结算状态", readConverterExp = "unsettled=未结算,partial=部分结算,settled=已结算")
    private String settlementStatus;

    /** 回单状态 */
    @Excel(name = "回单状态", readConverterExp = "not_received=未收到,received=已收到")
    private String receiptStatus;

    /** 删除标志 */
    private String delFlag;

    public void setDriverOrderId(Long driverOrderId)
    {
        this.driverOrderId = driverOrderId;
    }

    public Long getDriverOrderId()
    {
        return driverOrderId;
    }

    public void setDriverOrderNo(String driverOrderNo)
    {
        this.driverOrderNo = driverOrderNo;
    }

    public String getDriverOrderNo()
    {
        return driverOrderNo;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public Long getDriverId()
    {
        return driverId;
    }

    public void setDriverId(Long driverId)
    {
        this.driverId = driverId;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
    }

    public String getDriverPhone()
    {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone)
    {
        this.driverPhone = driverPhone;
    }

    public Long getVehicleId()
    {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId)
    {
        this.vehicleId = vehicleId;
    }

    public String getVehiclePlate()
    {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate)
    {
        this.vehiclePlate = vehiclePlate;
    }

    public BigDecimal getLoadCapacity()
    {
        return loadCapacity;
    }

    public void setLoadCapacity(BigDecimal loadCapacity)
    {
        this.loadCapacity = loadCapacity;
    }

    public BigDecimal getActualWeight()
    {
        return actualWeight;
    }

    public void setActualWeight(BigDecimal actualWeight)
    {
        this.actualWeight = actualWeight;
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

    public BigDecimal getAdvancePayment()
    {
        return advancePayment;
    }

    public void setAdvancePayment(BigDecimal advancePayment)
    {
        this.advancePayment = advancePayment;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPayee()
    {
        return payee;
    }

    public void setPayee(String payee)
    {
        this.payee = payee;
    }

    public Date getDispatchDate()
    {
        return dispatchDate;
    }

    public void setDispatchDate(Date dispatchDate)
    {
        this.dispatchDate = dispatchDate;
    }

    public String getDispatchStatus()
    {
        return dispatchStatus;
    }

    public void setDispatchStatus(String dispatchStatus)
    {
        this.dispatchStatus = dispatchStatus;
    }

    public String getSettlementStatus()
    {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus)
    {
        this.settlementStatus = settlementStatus;
    }

    public String getReceiptStatus()
    {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus)
    {
        this.receiptStatus = receiptStatus;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("driverOrderId", getDriverOrderId())
            .append("driverOrderNo", getDriverOrderNo())
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("driverId", getDriverId())
            .append("driverName", getDriverName())
            .append("driverPhone", getDriverPhone())
            .append("vehicleId", getVehicleId())
            .append("vehiclePlate", getVehiclePlate())
            .append("loadCapacity", getLoadCapacity())
            .append("actualWeight", getActualWeight())
            .append("loadingUnitPrice", getLoadingUnitPrice())
            .append("freightCost", getFreightCost())
            .append("advancePayment", getAdvancePayment())
            .append("paymentMethod", getPaymentMethod())
            .append("payee", getPayee())
            .append("dispatchDate", getDispatchDate())
            .append("dispatchStatus", getDispatchStatus())
            .append("settlementStatus", getSettlementStatus())
            .append("receiptStatus", getReceiptStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
