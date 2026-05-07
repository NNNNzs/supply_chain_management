package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsOrderLog;

/**
 * 订单操作日志Service接口
 *
 * @author scm
 * @date 2026-04-21
 */
public interface ILogisticsOrderLogService
{
    /**
     * 查询订单操作日志
     *
     * @param logId 订单操作日志主键
     * @return 订单操作日志
     */
    public LogisticsOrderLog selectOrderLogById(Long logId);

    /**
     * 查询订单操作日志列表
     *
     * @param orderLog 订单操作日志
     * @return 订单操作日志集合
     */
    public List<LogisticsOrderLog> selectOrderLogList(LogisticsOrderLog orderLog);

    /**
     * 根据订单ID查询操作日志列表
     *
     * @param orderId 订单ID
     * @return 订单操作日志集合
     */
    public List<LogisticsOrderLog> selectOrderLogByOrderId(Long orderId);

    /**
     * 新增订单操作日志
     *
     * @param orderLog 订单操作日志
     * @return 结果
     */
    public int insertOrderLog(LogisticsOrderLog orderLog);

    /**
     * 批量新增订单操作日志
     *
     * @param orderLogs 订单操作日志集合
     * @return 结果
     */
    public int batchInsertOrderLog(List<LogisticsOrderLog> orderLogs);

    /**
     * 删除订单操作日志
     *
     * @param logId 订单操作日志主键
     * @return 结果
     */
    public int deleteOrderLogById(Long logId);

    /**
     * 批量删除订单操作日志
     *
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrderLogByIds(Long[] logIds);

    /**
     * 记录订单创建操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 结果
     */
    public int logOrderCreate(Long orderId, String orderNo, Long operatorId, String operatorName);

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
    public int logOrderUpdate(Long orderId, String orderNo, Long operatorId, String operatorName, String content);

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
    public int logOrderStatusChange(Long orderId, String orderNo, String beforeStatus, String afterStatus, Long operatorId, String operatorName);

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
    public int logOrderInvoice(Long orderId, String orderNo, Long operatorId, String operatorName, String invoiceBatchNo);

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
    public int logOrderSettlement(Long orderId, String orderNo, Long operatorId, String operatorName, String settlementNo);

    /**
     * 记录订单删除操作
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 结果
     */
    public int logOrderDelete(Long orderId, String orderNo, Long operatorId, String operatorName);

    /**
     * 记录订单删除操作（带自定义内容）
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param content 操作内容
     * @return 结果
     */
    public int logOrderDelete(Long orderId, String orderNo, Long operatorId, String operatorName, String content);
}
