package com.scm.logistics.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * 结算明细对象 logistics_settlement_detail
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsSettlementDetail
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 结算ID */
    private Long settlementId;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 已结算金额 */
    @Excel(name = "已结算金额")
    private BigDecimal settlementAmount;

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getDetailId()
    {
        return detailId;
    }

    public void setSettlementId(Long settlementId)
    {
        this.settlementId = settlementId;
    }

    public Long getSettlementId()
    {
        return settlementId;
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

    public void setSettlementAmount(BigDecimal settlementAmount)
    {
        this.settlementAmount = settlementAmount;
    }

    public BigDecimal getSettlementAmount()
    {
        return settlementAmount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("settlementId", getSettlementId())
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("amount", getAmount())
            .append("settlementAmount", getSettlementAmount())
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
