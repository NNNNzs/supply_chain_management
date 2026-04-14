package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsReceipt;

/**
 * 回单信息Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsReceiptService
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
     * 批量删除回单信息
     *
     * @param receiptIds 需要删除的回单信息主键集合
     * @return 结果
     */
    public int deleteLogisticsReceiptByReceiptIds(Long[] receiptIds);

    /**
     * 删除回单信息信息
     *
     * @param receiptId 回单信息主键
     * @return 结果
     */
    public int deleteLogisticsReceiptByReceiptId(Long receiptId);

    /**
     * 生成回单编号
     * 格式：HD + 年月日 + 流水号(4位)
     *
     * @return 回单编号
     */
    public String generateReceiptNo();

    /**
     * 确认回单
     *
     * @param receiptId 回单ID
     * @param receiver 接收人
     * @return 结果
     */
    public int confirmReceipt(Long receiptId, String receiver);

    /**
     * 根据订单ID查询回单信息
     *
     * @param orderId 订单ID
     * @return 回单信息
     */
    public LogisticsReceipt selectLogisticsReceiptByOrderId(Long orderId);
}
