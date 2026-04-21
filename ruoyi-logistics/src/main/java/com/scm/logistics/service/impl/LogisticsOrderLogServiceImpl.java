package com.scm.logistics.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.domain.LogisticsOrderLog;
import com.scm.logistics.mapper.LogisticsOrderLogMapper;
import com.scm.logistics.service.ILogisticsOrderLogService;

/**
 * 订单操作日志Service业务层处理
 *
 * @author scm
 * @date 2026-04-21
 */
@Service
public class LogisticsOrderLogServiceImpl implements ILogisticsOrderLogService
{
    @Autowired
    private LogisticsOrderLogMapper orderLogMapper;

    /**
     * 查询订单操作日志
     *
     * @param logId 订单操作日志主键
     * @return 订单操作日志
     */
    @Override
    public LogisticsOrderLog selectOrderLogById(Long logId)
    {
        return orderLogMapper.selectOrderLogById(logId);
    }

    /**
     * 查询订单操作日志列表
     *
     * @param orderLog 订单操作日志
     * @return 订单操作日志
     */
    @Override
    public List<LogisticsOrderLog> selectOrderLogList(LogisticsOrderLog orderLog)
    {
        return orderLogMapper.selectOrderLogList(orderLog);
    }

    /**
     * 根据订单ID查询操作日志列表
     *
     * @param orderId 订单ID
     * @return 订单操作日志集合
     */
    @Override
    public List<LogisticsOrderLog> selectOrderLogByOrderId(Long orderId)
    {
        return orderLogMapper.selectOrderLogByOrderId(orderId);
    }

    /**
     * 新增订单操作日志
     *
     * @param orderLog 订单操作日志
     * @return 结果
     */
    @Override
    public int insertOrderLog(LogisticsOrderLog orderLog)
    {
        return orderLogMapper.insertOrderLog(orderLog);
    }

    /**
     * 批量新增订单操作日志
     *
     * @param orderLogs 订单操作日志集合
     * @return 结果
     */
    @Override
    public int batchInsertOrderLog(List<LogisticsOrderLog> orderLogs)
    {
        return orderLogMapper.batchInsertOrderLog(orderLogs);
    }

    /**
     * 删除订单操作日志
     *
     * @param logId 订单操作日志主键
     * @return 结果
     */
    @Override
    public int deleteOrderLogById(Long logId)
    {
        return orderLogMapper.deleteOrderLogById(logId);
    }

    /**
     * 批量删除订单操作日志
     *
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteOrderLogByIds(Long[] logIds)
    {
        return orderLogMapper.deleteOrderLogByIds(logIds);
    }

    /**
     * 记录订单创建操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 结果
     */
    @Override
    public int logOrderCreate(Long orderId, String orderNo, Long operatorId, String operatorName)
    {
        LogisticsOrderLog log = new LogisticsOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperationType("create");
        log.setOperationContent("创建订单");
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationTime(new Date());
        return orderLogMapper.insertOrderLog(log);
    }

    /**
     * 记录订单修改操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param content 操作内容
     * @return 结果
     */
    @Override
    public int logOrderUpdate(Long orderId, String orderNo, Long operatorId, String operatorName, String content)
    {
        LogisticsOrderLog log = new LogisticsOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperationType("update");
        log.setOperationContent(content != null ? content : "修改订单信息");
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationTime(new Date());
        return orderLogMapper.insertOrderLog(log);
    }

    /**
     * 记录订单状态变更操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param beforeStatus 变更前状态
     * @param afterStatus 变更后状态
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 结果
     */
    @Override
    public int logOrderStatusChange(Long orderId, String orderNo, String beforeStatus, String afterStatus, Long operatorId, String operatorName)
    {
        LogisticsOrderLog log = new LogisticsOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperationType("status_change");
        log.setOperationContent("订单状态变更");
        log.setBeforeValue(getStatusName(beforeStatus));
        log.setAfterValue(getStatusName(afterStatus));
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationTime(new Date());
        return orderLogMapper.insertOrderLog(log);
    }

    /**
     * 记录订单开票操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param invoiceBatchNo 发票批次号
     * @return 结果
     */
    @Override
    public int logOrderInvoice(Long orderId, String orderNo, Long operatorId, String operatorName, String invoiceBatchNo)
    {
        LogisticsOrderLog log = new LogisticsOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperationType("invoice");
        log.setOperationContent("订单开票");
        log.setAfterValue(invoiceBatchNo);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationTime(new Date());
        return orderLogMapper.insertOrderLog(log);
    }

    /**
     * 记录订单结算操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param settlementNo 结算单号
     * @return 结果
     */
    @Override
    public int logOrderSettlement(Long orderId, String orderNo, Long operatorId, String operatorName, String settlementNo)
    {
        LogisticsOrderLog log = new LogisticsOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperationType("settlement");
        log.setOperationContent("订单结算");
        log.setAfterValue(settlementNo);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationTime(new Date());
        return orderLogMapper.insertOrderLog(log);
    }

    /**
     * 记录订单删除操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 结果
     */
    @Override
    public int logOrderDelete(Long orderId, String orderNo, Long operatorId, String operatorName)
    {
        LogisticsOrderLog log = new LogisticsOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperationType("delete");
        log.setOperationContent("删除订单");
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationTime(new Date());
        return orderLogMapper.insertOrderLog(log);
    }

    /**
     * 获取状态名称
     *
     * @param status 状态代码
     * @return 状态名称
     */
    private String getStatusName(String status)
    {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "pending":
                return "待运输";
            case "transporting":
                return "运输中";
            case "completed":
                return "已完成";
            case "cancelled":
                return "已取消";
            default:
                return status;
        }
    }
}
