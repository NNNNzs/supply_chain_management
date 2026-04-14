package com.scm.logistics.domain.vo;

import java.math.BigDecimal;

/**
 * 配载结果VO
 *
 * @author scm
 * @date 2026-04-14
 */
public class AllocationResultVO
{
    /** 运单ID */
    private Long orderId;

    /** 运单号 */
    private String orderNo;

    /** 总装车重量 */
    private BigDecimal totalWeight;

    /** 总分配金额 */
    private BigDecimal totalAmount;

    /** 创建的货物明细数量 */
    private Integer detailCount;

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public BigDecimal getTotalWeight()
    {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight)
    {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public Integer getDetailCount()
    {
        return detailCount;
    }

    public void setDetailCount(Integer detailCount)
    {
        this.detailCount = detailCount;
    }
}
