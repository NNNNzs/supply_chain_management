package com.scm.logistics.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 车队信息对象 logistics_fleet
 *
 * @author scm
 * @date 2026-04-15
 */
public class LogisticsFleet extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 车队ID */
    private Long fleetId;

    /** 车队名称 */
    @Excel(name = "车队名称")
    private String fleetName;

    /** 车队老板姓名 */
    @Excel(name = "车队老板姓名")
    private String ownerName;

    /** 老板联系电话 */
    @Excel(name = "老板联系电话")
    private String ownerPhone;

    /** 车队开票账户名称 */
    @Excel(name = "车队开票账户名称")
    private String accountName;

    /** 车队开票账号 */
    @Excel(name = "车队开票账号")
    private String accountNumber;

    /** 车队开户行 */
    @Excel(name = "车队开户行")
    private String bankName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setFleetId(Long fleetId)
    {
        this.fleetId = fleetId;
    }

    public Long getFleetId()
    {
        return fleetId;
    }

    public void setFleetName(String fleetName)
    {
        this.fleetName = fleetName;
    }

    public String getFleetName()
    {
        return fleetName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerPhone(String ownerPhone)
    {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerPhone()
    {
        return ownerPhone;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getBankName()
    {
        return bankName;
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
            .append("fleetId", getFleetId())
            .append("fleetName", getFleetName())
            .append("ownerName", getOwnerName())
            .append("ownerPhone", getOwnerPhone())
            .append("accountName", getAccountName())
            .append("accountNumber", getAccountNumber())
            .append("bankName", getBankName())
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
