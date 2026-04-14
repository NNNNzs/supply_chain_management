package com.scm.logistics.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 司机信息对象 logistics_driver
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsDriver extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 司机ID */
    private Long driverId;

    /** 司机编码 */
    @Excel(name = "司机编码")
    private String driverCode;

    /** 司机姓名 */
    @Excel(name = "司机姓名")
    private String driverName;

    /** 司机电话 */
    @Excel(name = "司机电话")
    private String driverPhone;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 驾驶证号 */
    @Excel(name = "驾驶证号")
    private String driverLicense;

    /** 常用车牌号 */
    @Excel(name = "常用车牌号")
    private String vehiclePlate;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bankName;

    /** 账户姓名 */
    @Excel(name = "账户姓名")
    private String accountName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setDriverId(Long driverId)
    {
        this.driverId = driverId;
    }

    public Long getDriverId()
    {
        return driverId;
    }

    public void setDriverCode(String driverCode)
    {
        this.driverCode = driverCode;
    }

    public String getDriverCode()
    {
        return driverCode;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverPhone(String driverPhone)
    {
        this.driverPhone = driverPhone;
    }

    public String getDriverPhone()
    {
        return driverPhone;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setDriverLicense(String driverLicense)
    {
        this.driverLicense = driverLicense;
    }

    public String getDriverLicense()
    {
        return driverLicense;
    }

    public void setVehiclePlate(String vehiclePlate)
    {
        this.vehiclePlate = vehiclePlate;
    }

    public String getVehiclePlate()
    {
        return vehiclePlate;
    }

    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount()
    {
        return bankAccount;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getAccountName()
    {
        return accountName;
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
            .append("driverId", getDriverId())
            .append("driverCode", getDriverCode())
            .append("driverName", getDriverName())
            .append("driverPhone", getDriverPhone())
            .append("idCard", getIdCard())
            .append("driverLicense", getDriverLicense())
            .append("vehiclePlate", getVehiclePlate())
            .append("bankAccount", getBankAccount())
            .append("bankName", getBankName())
            .append("accountName", getAccountName())
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
