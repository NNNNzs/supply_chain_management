package com.scm.logistics.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 财务结算对象 logistics_settlement
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsSettlement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 结算ID */
    private Long settlementId;

    /** 结算单号 */
    @Excel(name = "结算单号")
    private String settlementNo;

    /** 客户ID */
    private Long customerId;

    /** 结算类型（income收入，expenditure支出） */
    @Excel(name = "结算类型", readConverterExp = "income=收入,expenditure=支出")
    private String settlementType;

    /** 结算日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结算日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date settlementDate;

    /** 结算开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结算开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结算结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结算结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 结算总金额 */
    @Excel(name = "结算总金额")
    private BigDecimal totalAmount;

    /** 已付金额 */
    @Excel(name = "已付金额")
    private BigDecimal paidAmount;

    /** 未付金额 */
    @Excel(name = "未付金额")
    private BigDecimal unpaidAmount;

    /** 付款方式 */
    @Excel(name = "付款方式")
    private String paymentMethod;

    /** 结算状态（draft草稿，confirmed已确认，completed已完成） */
    @Excel(name = "结算状态", readConverterExp = "draft=草稿,confirmed=已确认,completed=已完成")
    private String settlementStatus;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    /** 账户名 */
    @Excel(name = "账户名")
    private String accountName;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setSettlementId(Long settlementId)
    {
        this.settlementId = settlementId;
    }

    public Long getSettlementId()
    {
        return settlementId;
    }

    public void setSettlementNo(String settlementNo)
    {
        this.settlementNo = settlementNo;
    }

    public String getSettlementNo()
    {
        return settlementNo;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setSettlementType(String settlementType)
    {
        this.settlementType = settlementType;
    }

    public String getSettlementType()
    {
        return settlementType;
    }

    public void setSettlementDate(Date settlementDate)
    {
        this.settlementDate = settlementDate;
    }

    public Date getSettlementDate()
    {
        return settlementDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    public void setUnpaidAmount(BigDecimal unpaidAmount)
    {
        this.unpaidAmount = unpaidAmount;
    }

    public BigDecimal getUnpaidAmount()
    {
        return unpaidAmount;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setSettlementStatus(String settlementStatus)
    {
        this.settlementStatus = settlementStatus;
    }

    public String getSettlementStatus()
    {
        return settlementStatus;
    }

    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount()
    {
        return bankAccount;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getAccountName()
    {
        return accountName;
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
            .append("settlementId", getSettlementId())
            .append("settlementNo", getSettlementNo())
            .append("customerId", getCustomerId())
            .append("settlementType", getSettlementType())
            .append("settlementDate", getSettlementDate())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("totalAmount", getTotalAmount())
            .append("paidAmount", getPaidAmount())
            .append("unpaidAmount", getUnpaidAmount())
            .append("paymentMethod", getPaymentMethod())
            .append("settlementStatus", getSettlementStatus())
            .append("bankAccount", getBankAccount())
            .append("accountName", getAccountName())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
