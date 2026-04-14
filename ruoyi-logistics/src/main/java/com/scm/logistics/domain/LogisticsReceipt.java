package com.scm.logistics.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 回单信息对象 logistics_receipt
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsReceipt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 回单ID */
    private Long receiptId;

    /** 回单编号 */
    @Excel(name = "回单编号")
    private String receiptNo;

    /** 订单ID */
    private Long orderId;

    /** 订单号（关联查询） */
    @Excel(name = "订单号")
    private String orderNo;

    /** 回单日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "回单日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date receiptDate;

    /** 回单图片路径 */
    @Excel(name = "回单图片路径")
    private String receiptImage;

    /** 回单状态（not_received未收到，received已收到） */
    @Excel(name = "回单状态", readConverterExp = "not_received=未收到,received=已收到")
    private String receiptStatus;

    /** 接收人 */
    @Excel(name = "接收人")
    private String receiver;

    /** 接收时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "接收时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptNo(String receiptNo)
    {
        this.receiptNo = receiptNo;
    }

    public String getReceiptNo()
    {
        return receiptNo;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setReceiptDate(Date receiptDate)
    {
        this.receiptDate = receiptDate;
    }

    public Date getReceiptDate()
    {
        return receiptDate;
    }

    public void setReceiptImage(String receiptImage)
    {
        this.receiptImage = receiptImage;
    }

    public String getReceiptImage()
    {
        return receiptImage;
    }

    public void setReceiptStatus(String receiptStatus)
    {
        this.receiptStatus = receiptStatus;
    }

    public String getReceiptStatus()
    {
        return receiptStatus;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public Date getReceiveTime()
    {
        return receiveTime;
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
            .append("receiptId", getReceiptId())
            .append("receiptNo", getReceiptNo())
            .append("orderId", getOrderId())
            .append("receiptDate", getReceiptDate())
            .append("receiptImage", getReceiptImage())
            .append("receiptStatus", getReceiptStatus())
            .append("receiver", getReceiver())
            .append("receiveTime", getReceiveTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
