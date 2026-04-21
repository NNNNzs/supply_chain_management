package com.scm.logistics.service;

import java.util.List;
import com.scm.common.core.domain.AjaxResult;
import com.scm.logistics.domain.LogisticsInvoiceBatch;
import com.scm.logistics.domain.LogisticsOrder;

/**
 * 发票批次Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsInvoiceBatchService
{
    /**
     * 查询发票批次
     *
     * @param batchId 发票批次主键
     * @return 发票批次
     */
    public LogisticsInvoiceBatch selectLogisticsInvoiceBatchByBatchId(Long batchId);

    /**
     * 查询发票批次列表
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 发票批次集合
     */
    public List<LogisticsInvoiceBatch> selectLogisticsInvoiceBatchList(LogisticsInvoiceBatch logisticsInvoiceBatch);

    /**
     * 新增发票批次
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 结果
     */
    public int insertLogisticsInvoiceBatch(LogisticsInvoiceBatch logisticsInvoiceBatch);

    /**
     * 修改发票批次
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 结果
     */
    public int updateLogisticsInvoiceBatch(LogisticsInvoiceBatch logisticsInvoiceBatch);

    /**
     * 批量删除发票批次
     *
     * @param batchIds 需要删除的发票批次主键集合
     * @return 结果
     */
    public int deleteLogisticsInvoiceBatchByBatchIds(Long[] batchIds);

    /**
     * 删除发票批次信息
     *
     * @param batchId 发票批次主键
     * @return 结果
     */
    public int deleteLogisticsInvoiceBatchByBatchId(Long batchId);

    /**
     * 查询可开票订单列表
     *
     * @param logisticsOrder 订单查询条件
     * @return 订单集合
     */
    public List<LogisticsOrder> selectInvoiceableOrderList(LogisticsOrder logisticsOrder);

    /**
     * 合并开票
     *
     * @param customerId 客户ID
     * @param invoiceDate 开票日期
     * @param invoiceType 发票类型
     * @param taxRate 税率
     * @param orderIds 订单ID列表
     * @return 结果
     */
    public AjaxResult mergeInvoice(Long customerId, String invoiceDate, String invoiceType, Double taxRate, List<Long> orderIds);

    /**
     * 取消合并（恢复订单为未开票状态）
     *
     * @param batchId 批次ID
     * @return 结果
     */
    public int cancelMerge(Long batchId);
}
