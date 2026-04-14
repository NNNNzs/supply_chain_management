package com.scm.logistics.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 提单运单明细对象 logistics_bill_order_detail
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsBillOrderDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 提单ID */
    private Long billId;

    /** 提单货物明细ID */
    private Long billItemId;

    /** 提单号（非数据库字段，用于展示） */
    @Excel(name = "提单号")
    private String billNo;

    /** 货物名称（非数据库字段，用于展示） */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 运单ID */
    private Long orderId;

    /** 运单号（非数据库字段，用于展示） */
    @Excel(name = "运单号")
    private String orderNo;

    /** 分配重量(吨) */
    @Excel(name = "分配重量(吨)")
    private BigDecimal allocatedWeight;

    /** 运价(元/吨) */
    @Excel(name = "运价(元/吨)")
    private BigDecimal unitPrice;

    /** 分配金额 */
    @Excel(name = "分配金额")
    private BigDecimal allocatedAmount;

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getDetailId()
    {
        return detailId;
    }

    public void setBillId(Long billId)
    {
        this.billId = billId;
    }

    public Long getBillId()
    {
        return billId;
    }

    public void setBillItemId(Long billItemId)
    {
        this.billItemId = billItemId;
    }

    public Long getBillItemId()
    {
        return billItemId;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
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

    public void setAllocatedWeight(BigDecimal allocatedWeight)
    {
        this.allocatedWeight = allocatedWeight;
    }

    public BigDecimal getAllocatedWeight()
    {
        return allocatedWeight;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount)
    {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getAllocatedAmount()
    {
        return allocatedAmount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("billId", getBillId())
            .append("billItemId", getBillItemId())
            .append("orderId", getOrderId())
            .append("allocatedWeight", getAllocatedWeight())
            .append("unitPrice", getUnitPrice())
            .append("allocatedAmount", getAllocatedAmount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
