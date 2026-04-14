package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsReceipt;

/**
 * 回单信息Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsReceiptMapper
{
    /**
     * 查询回单信息
     *
     * @param receiptId 回单信息主键
     * @return 回单信息
     */
    public LogisticsReceipt selectLogisticsReceiptByReceiptId(Long receiptId);

    /**
     * 查询回单信息列表
     *
     * @param logisticsReceipt 回单信息
     * @return 回单信息集合
     */
    public List<LogisticsReceipt> selectLogisticsReceiptList(LogisticsReceipt logisticsReceipt);

    /**
     * 新增回单信息
     *
     * @param logisticsReceipt 回单信息
     * @return 结果
     */
    public int insertLogisticsReceipt(LogisticsReceipt logisticsReceipt);

    /**
     * 修改回单信息
     *
     * @param logisticsReceipt 回单信息
     * @return 结果
     */
    public int updateLogisticsReceipt(LogisticsReceipt logisticsReceipt);

    /**
     * 删除回单信息
     *
     * @param receiptId 回单信息主键
     * @return 结果
     */
    public int deleteLogisticsReceiptByReceiptId(Long receiptId);

    /**
     * 批量删除回单信息
     *
     * @param receiptIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsReceiptByReceiptIds(Long[] receiptIds);

    /**
     * 根据订单ID查询回单信息
     *
     * @param orderId 订单ID
     * @return 回单信息
     */
    public LogisticsReceipt selectLogisticsReceiptByOrderId(Long orderId);
}
