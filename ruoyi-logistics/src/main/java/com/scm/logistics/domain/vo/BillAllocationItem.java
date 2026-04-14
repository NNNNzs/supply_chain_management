package com.scm.logistics.domain.vo;

import java.math.BigDecimal;

/**
 * 提单分配项
 * 用于配载管理时记录每个提单货物明细分配的重量
 *
 * @author scm
 * @date 2026-04-14
 */
public class BillAllocationItem
{
    /** 提单ID */
    private Long billId;

    /** 提单号 */
    private String billNo;

    /** 提单货物明细ID */
    private Long billItemId;

    /** 货物名称 */
    private String goodsName;

    /** 货物型号 */
    private String goodsModel;

    /** 客户名称 */
    private String customerName;

    /** 装货地址 */
    private String loadingAddress;

    /** 卸货地址 */
    private String unloadingAddress;

    /** 货物总重量 */
    private BigDecimal totalWeight;

    /** 已分配重量 */
    private BigDecimal allocatedWeight;

    /** 剩余重量 */
    private BigDecimal remainWeight;

    /** 本次分配重量 */
    private BigDecimal allocWeight;

    /** 运价 */
    private BigDecimal unitPrice;

    public Long getBillId()
    {
        return billId;
    }

    public void setBillId(Long billId)
    {
        this.billId = billId;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public Long getBillItemId()
    {
        return billItemId;
    }

    public void setBillItemId(Long billItemId)
    {
        this.billItemId = billItemId;
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

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getLoadingAddress()
    {
        return loadingAddress;
    }

    public void setLoadingAddress(String loadingAddress)
    {
        this.loadingAddress = loadingAddress;
    }

    public String getUnloadingAddress()
    {
        return unloadingAddress;
    }

    public void setUnloadingAddress(String unloadingAddress)
    {
        this.unloadingAddress = unloadingAddress;
    }

    public BigDecimal getTotalWeight()
    {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight)
    {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getAllocatedWeight()
    {
        return allocatedWeight;
    }

    public void setAllocatedWeight(BigDecimal allocatedWeight)
    {
        this.allocatedWeight = allocatedWeight;
    }

    public BigDecimal getRemainWeight()
    {
        if (totalWeight != null && allocatedWeight != null)
        {
            return totalWeight.subtract(allocatedWeight);
        }
        return totalWeight;
    }

    public BigDecimal getAllocWeight()
    {
        return allocWeight;
    }

    public void setAllocWeight(BigDecimal allocWeight)
    {
        this.allocWeight = allocWeight;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }
}
