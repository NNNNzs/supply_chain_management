package com.scm.logistics.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * 发票批次明细对象 logistics_invoice_detail
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsInvoiceDetail
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 批次ID */
    private Long batchId;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getDetailId()
    {
        return detailId;
    }

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }

    public Long getBatchId()
    {
        return batchId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("batchId", getBatchId())
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("amount", getAmount())
            .append("remark", getRemark())
            .toString();
    }

    /** 备注 */
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
