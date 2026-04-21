package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsOrderLog;

/**
 * 订单操作日志Mapper接口
 *
 * @author scm
 * @date 2026-04-21
 */
public interface LogisticsOrderLogMapper
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
}
