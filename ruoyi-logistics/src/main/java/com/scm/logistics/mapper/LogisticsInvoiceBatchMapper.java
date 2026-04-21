package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsInvoiceBatch;

/**
 * 发票批次Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsInvoiceBatchMapper
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
     * 删除发票批次
     *
     * @param batchId 发票批次主键
     * @return 结果
     */
    public int deleteLogisticsInvoiceBatchByBatchId(Long batchId);

    /**
     * 批量删除发票批次
     *
     * @param batchIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsInvoiceBatchByBatchIds(Long[] batchIds);

    /**
     * 根据批次号前缀查询最大序号
     *
     * @param prefix 批次号前缀
     * @return 最大序号
     */
    public Integer selectMaxSeqByBatchNoPrefix(String prefix);
}
