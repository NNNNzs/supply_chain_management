package com.scm.logistics.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 提单货物明细对象 logistics_bill_item
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsBillItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long itemId;

    /** 提单ID */
    private Long billId;

    /** 货物ID */
    private Long goodsId;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 货物型号 */
    @Excel(name = "货物型号")
    private String goodsModel;

    /** 重量(吨) */
    @Excel(name = "重量(吨)")
    private BigDecimal weight;

    /** 已分配重量(吨) */
    @Excel(name = "已分配重量(吨)")
    private BigDecimal allocatedWeight;

    /** 运价(元/吨) */
    @Excel(name = "运价(元/吨)")
    private BigDecimal unitPrice;

    /** 金额(元) */
    @Excel(name = "金额(元)")
    private BigDecimal amount;

    /** 排序号 */
    private Integer sortOrder;

    /** 删除标志 */
    private String delFlag;

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setBillId(Long billId)
    {
        this.billId = billId;
    }

    public Long getBillId()
    {
        return billId;
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

    public void setWeight(BigDecimal weight)
    {
        this.weight = weight;
    }

    public BigDecimal getWeight()
    {
        return weight;
    }

    public void setAllocatedWeight(BigDecimal allocatedWeight)
    {
        this.allocatedWeight = allocatedWeight;
    }

    public BigDecimal getAllocatedWeight()
    {
        return allocatedWeight;
    }

    /**
     * 获取剩余可分配重量
     */
    public BigDecimal getRemainWeight()
    {
        if (weight != null && allocatedWeight != null)
        {
            return weight.subtract(allocatedWeight);
        }
        return weight;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
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
            .append("itemId", getItemId())
            .append("billId", getBillId())
            .append("goodsId", getGoodsId())
            .append("goodsName", getGoodsName())
            .append("goodsModel", getGoodsModel())
            .append("weight", getWeight())
            .append("allocatedWeight", getAllocatedWeight())
            .append("unitPrice", getUnitPrice())
            .append("amount", getAmount())
            .append("sortOrder", getSortOrder())
            .append("remark", getRemark())
            .toString();
    }
}
