package com.scm.logistics.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 货物信息对象 logistics_goods
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 货物ID */
    private Long goodsId;

    /** 货物编码 */
    @Excel(name = "货物编码")
    private String goodsCode;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 货物型号 */
    @Excel(name = "货物型号")
    private String goodsModel;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String goodsUnit;

    /** 货物分类 */
    @Excel(name = "货物分类")
    private String goodsCategory;

    /** 参考单价 */
    @Excel(name = "参考单价")
    private Double unitPrice;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId()
    {
        return goodsId;
    }

    public void setGoodsCode(String goodsCode)
    {
        this.goodsCode = goodsCode;
    }

    public String getGoodsCode()
    {
        return goodsCode;
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

    public void setGoodsCategory(String goodsCategory)
    {
        this.goodsCategory = goodsCategory;
    }

    public String getGoodsCategory()
    {
        return goodsCategory;
    }

    public void setUnitPrice(Double unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public Double getUnitPrice()
    {
        return unitPrice;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
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
            .append("goodsId", getGoodsId())
            .append("goodsCode", getGoodsCode())
            .append("goodsName", getGoodsName())
            .append("goodsModel", getGoodsModel())
            .append("goodsUnit", getGoodsUnit())
            .append("goodsCategory", getGoodsCategory())
            .append("unitPrice", getUnitPrice())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
