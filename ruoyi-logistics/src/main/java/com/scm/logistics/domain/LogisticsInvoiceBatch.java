package com.scm.logistics.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发票批次对象 logistics_invoice_batch
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsInvoiceBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 批次ID */
    private Long batchId;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 客户ID */
    private Long customerId;

    /** 客户名称（非数据库字段） */
    private String customerName;

    /** 开票日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开票日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;

    /** 开票总金额 */
    @Excel(name = "开票总金额")
    private BigDecimal totalAmount;

    /** 订单数量 */
    @Excel(name = "订单数量")
    private Integer orderCount;

    /** 发票状态（draft草稿，issued已开具，cancelled已作废） */
    @Excel(name = "发票状态", readConverterExp = "draft=草稿,issued=已开具,cancelled=已作废")
    private String invoiceStatus;

    /** 发票类型（ordinary普通发票，vat增值税发票） */
    @Excel(name = "发票类型", readConverterExp = "ordinary=普通发票,vat=增值税发票")
    private String invoiceType;

    /** 税率 */
    @Excel(name = "税率")
    private BigDecimal taxRate;

    /** 税额 */
    @Excel(name = "税额")
    private BigDecimal taxAmount;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 订单明细列表（非数据库字段） */
    private List<LogisticsInvoiceDetail> orderList;

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }

    public Long getBatchId()
    {
        return batchId;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setInvoiceDate(Date invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }

    public Date getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setOrderCount(Integer orderCount)
    {
        this.orderCount = orderCount;
    }

    public Integer getOrderCount()
    {
        return orderCount;
    }

    public void setInvoiceStatus(String invoiceStatus)
    {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceStatus()
    {
        return invoiceStatus;
    }

    public void setInvoiceType(String invoiceType)
    {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceType()
    {
        return invoiceType;
    }

    public void setTaxRate(BigDecimal taxRate)
    {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxRate()
    {
        return taxRate;
    }

    public void setTaxAmount(BigDecimal taxAmount)
    {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxAmount()
    {
        return taxAmount;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public List<LogisticsInvoiceDetail> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<LogisticsInvoiceDetail> orderList) {
        this.orderList = orderList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("batchId", getBatchId())
            .append("batchNo", getBatchNo())
            .append("customerId", getCustomerId())
            .append("invoiceDate", getInvoiceDate())
            .append("totalAmount", getTotalAmount())
            .append("orderCount", getOrderCount())
            .append("invoiceStatus", getInvoiceStatus())
            .append("invoiceType", getInvoiceType())
            .append("taxRate", getTaxRate())
            .append("taxAmount", getTaxAmount())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
