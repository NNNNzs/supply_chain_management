package com.scm.logistics.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 订单货物明细对象 logistics_order_goods
 *
 * @author scm
 * @date 2026-04-15
 */
public class LogisticsOrderGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 货物ID */
    @Excel(name = "货物ID")
    private Long goodsId;

    /** 货物名称（冗余字段） */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 货物型号 */
    @Excel(name = "货物型号")
    private String goodsModel;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String goodsUnit;

    /** 重量（吨） */
    @Excel(name = "重量")
    private java.math.BigDecimal weight;

    /** 单价（元/吨） */
    @Excel(name = "单价")
    private java.math.BigDecimal unitPrice;

    /** 金额（元） */
    @Excel(name = "金额")
    private java.math.BigDecimal amount;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getDetailId()
    {
        return detailId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId()
    {
        return goodsId;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsModel(String goodsModel)
    {
        this.goodsModel = goodsModel;
    }

    public String getGoodsModel()
    {
        return goodsModel;
    }

    public void setGoodsUnit(String goodsUnit)
    {
        this.goodsUnit = goodsUnit;
    }

    public String getGoodsUnit()
    {
        return goodsUnit;
    }

    public void setWeight(java.math.BigDecimal weight)
    {
        this.weight = weight;
    }

    public java.math.BigDecimal getWeight()
    {
        return weight;
    }

    public void setUnitPrice(java.math.BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public java.math.BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setAmount(java.math.BigDecimal amount)
    {
        this.amount = amount;
    }

    public java.math.BigDecimal getAmount()
    {
        return amount;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("orderId", getOrderId())
            .append("goodsId", getGoodsId())
            .append("goodsName", getGoodsName())
            .append("goodsModel", getGoodsModel())
            .append("goodsUnit", getGoodsUnit())
            .append("weight", getWeight())
            .append("unitPrice", getUnitPrice())
            .append("amount", getAmount())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
