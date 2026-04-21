package com.scm.logistics.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.scm.common.core.domain.BaseEntity;

/**
 * 订单操作日志对象 logistics_order_log
 *
 * @author scm
 * @date 2026-04-21
 */
public class LogisticsOrderLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 操作类型：create-创建,update-修改,status_change-状态变更,invoice-开票,settlement-结算,delete-删除 */
    private String operationType;

    /** 操作内容描述 */
    private String operationContent;

    /** 操作前值 */
    private String beforeValue;

    /** 操作后值 */
    private String afterValue;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getLogId()
    {
        return logId;
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

    public String getOperationType()
    {
        return operationType;
    }

    public void setOperationType(String operationType)
    {
        this.operationType = operationType;
    }

    public void setOperationContent(String operationContent)
    {
        this.operationContent = operationContent;
    }

    public String getOperationContent()
    {
        return operationContent;
    }

    public void setBeforeValue(String beforeValue)
    {
        this.beforeValue = beforeValue;
    }

    public String getBeforeValue()
    {
        return beforeValue;
    }

    public void setAfterValue(String afterValue)
    {
        this.afterValue = afterValue;
    }

    public String getAfterValue()
    {
        return afterValue;
    }

    public void setOperatorId(Long operatorId)
    {
        this.operatorId = operatorId;
    }

    public Long getOperatorId()
    {
        return operatorId;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }

    public String getOperatorName()
    {
        return operatorName;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOperationTime()
    {
        return operationTime;
    }

    public void setOperationTime(Date operationTime)
    {
        this.operationTime = operationTime;
    }
}
