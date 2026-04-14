package com.scm.logistics.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.core.domain.BaseEntity;
import com.scm.common.xss.Xss;

/**
 * 客户信息对象 logistics_customer
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 客户ID */
    private Long customerId;

    /** 客户编码 */
    private String customerCode;

    /** 客户名称 */
    private String customerName;

    /** 联系人 */
    private String contactPerson;

    /** 联系电话 */
    private String contactPhone;

    /** 客户地址 */
    private String customerAddress;

    /** 结算方式（monthly月结，cash现结） */
    private String settlementType;

    /** 信用额度 */
    private Double creditLimit;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    @Xss(message = "客户编码不能包含脚本字符")
    @NotBlank(message = "客户编码不能为空")
    @Size(min = 0, max = 50, message = "客户编码长度不能超过50个字符")
    public String getCustomerCode()
    {
        return customerCode;
    }

    public void setCustomerCode(String customerCode)
    {
        this.customerCode = customerCode;
    }

    @Xss(message = "客户名称不能包含脚本字符")
    @NotBlank(message = "客户名称不能为空")
    @Size(min = 0, max = 100, message = "客户名称长度不能超过100个字符")
    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    @Xss(message = "联系人不能包含脚本字符")
    @Size(min = 0, max = 50, message = "联系人长度不能超过50个字符")
    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    @Size(min = 0, max = 20, message = "联系电话长度不能超过20个字符")
    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    @Xss(message = "客户地址不能包含脚本字符")
    @Size(min = 0, max = 255, message = "客户地址长度不能超过255个字符")
    public String getCustomerAddress()
    {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress)
    {
        this.customerAddress = customerAddress;
    }

    public void setSettlementType(String settlementType)
    {
        this.settlementType = settlementType;
    }

    public String getSettlementType()
    {
        return settlementType;
    }

    public void setCreditLimit(Double creditLimit)
    {
        this.creditLimit = creditLimit;
    }

    public Double getCreditLimit()
    {
        return creditLimit;
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
            .append("customerId", getCustomerId())
            .append("customerCode", getCustomerCode())
            .append("customerName", getCustomerName())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("customerAddress", getCustomerAddress())
            .append("settlementType", getSettlementType())
            .append("creditLimit", getCreditLimit())
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
