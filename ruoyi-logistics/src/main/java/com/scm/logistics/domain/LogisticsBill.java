package com.scm.logistics.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 提单对象 logistics_bill
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsBill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 提单ID */
    private Long billId;

    /** 提单号 */
    @Excel(name = "提单号")
    private String billNo;

    /** 提单日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提单日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date billDate;

    /** 客户ID */
    private Long customerId;

    /** 客户名称（非数据库字段，用于展示） */
    @Excel(name = "客户名称")
    private String customerName;

    /** 装货地址 */
    @Excel(name = "装货地址")
    private String loadingAddress;

    /** 卸货地址 */
    @Excel(name = "卸货地址")
    private String unloadingAddress;

    /** 货物ID */
    private Long goodsId;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 货物型号 */
    @Excel(name = "货物型号")
    private String goodsModel;

    /** 总重量(吨) */
    @Excel(name = "总重量(吨)")
    private BigDecimal totalWeight;

    /** 已分配重量(吨) */
    @Excel(name = "已分配重量(吨)")
    private BigDecimal allocatedWeight;

    /** 剩余重量(吨)（非数据库字段，计算得出） */
    @Excel(name = "剩余重量(吨)")
    private BigDecimal remainWeight;

    /** 运价(元/吨) */
    @Excel(name = "运价(元/吨)")
    private BigDecimal unitPrice;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 提单状态 */
    @Excel(name = "提单状态", readConverterExp = "pending=待配载,partial=部分配载,allocated=已配载,transporting=运输中,completed=已完成")
    private String billStatus;

    /** 优先级 */
    @Excel(name = "优先级", readConverterExp = "low=低,normal=正常,high=高,urgent=紧急")
    private String priority;

    /** 要求完成日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "要求完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date requireDate;

    /** 删除标志 */
    private String delFlag;

    public void setBillId(Long billId)
    {
        this.billId = billId;
    }

    public Long getBillId()
    {
        return billId;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillDate(Date billDate)
    {
        this.billDate = billDate;
    }

    public Date getBillDate()
    {
        return billDate;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public void setLoadingAddress(String loadingAddress)
    {
        this.loadingAddress = loadingAddress;
    }

    public String getLoadingAddress()
    {
        return loadingAddress;
    }

    public void setUnloadingAddress(String unloadingAddress)
    {
        this.unloadingAddress = unloadingAddress;
    }

    public String getUnloadingAddress()
    {
        return unloadingAddress;
    }

    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId()
    {
        return goodsId;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public String getGoodsModel()
    {
        return goodsModel;
    }

    public void setGoodsModel(String goodsModel)
    {
        this.goodsModel = goodsModel;
    }

    public void setTotalWeight(BigDecimal totalWeight)
    {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalWeight()
    {
        return totalWeight;
    }

    public void setAllocatedWeight(BigDecimal allocatedWeight)
    {
        this.allocatedWeight = allocatedWeight;
    }

    public BigDecimal getAllocatedWeight()
    {
        return allocatedWeight;
    }

    public BigDecimal getRemainWeight()
    {
        if (totalWeight != null && allocatedWeight != null)
        {
            return totalWeight.subtract(allocatedWeight);
        }
        return totalWeight;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setBillStatus(String billStatus)
    {
        this.billStatus = billStatus;
    }

    public String getBillStatus()
    {
        return billStatus;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setRequireDate(Date requireDate)
    {
        this.requireDate = requireDate;
    }

    public Date getRequireDate()
    {
        return requireDate;
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
            .append("billId", getBillId())
            .append("billNo", getBillNo())
            .append("billDate", getBillDate())
            .append("customerId", getCustomerId())
            .append("loadingAddress", getLoadingAddress())
            .append("unloadingAddress", getUnloadingAddress())
            .append("goodsId", getGoodsId())
            .append("totalWeight", getTotalWeight())
            .append("allocatedWeight", getAllocatedWeight())
            .append("unitPrice", getUnitPrice())
            .append("totalAmount", getTotalAmount())
            .append("billStatus", getBillStatus())
            .append("priority", getPriority())
            .append("requireDate", getRequireDate())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
