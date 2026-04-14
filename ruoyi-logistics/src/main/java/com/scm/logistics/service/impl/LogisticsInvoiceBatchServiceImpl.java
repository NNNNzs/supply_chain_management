package com.scm.logistics.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsInvoiceBatchMapper;
import com.scm.logistics.domain.LogisticsInvoiceBatch;
import com.scm.logistics.service.ILogisticsInvoiceBatchService;

/**
 * 发票批次Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsInvoiceBatchServiceImpl implements ILogisticsInvoiceBatchService
{
    @Autowired
    private LogisticsInvoiceBatchMapper logisticsInvoiceBatchMapper;

    /**
     * 查询发票批次
     *
     * @param batchId 发票批次主键
     * @return 发票批次
     */
    @Override
    public LogisticsInvoiceBatch selectLogisticsInvoiceBatchByBatchId(Long batchId)
    {
        return logisticsInvoiceBatchMapper.selectLogisticsInvoiceBatchByBatchId(batchId);
    }

    /**
     * 查询发票批次列表
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 发票批次
     */
    @Override
    public List<LogisticsInvoiceBatch> selectLogisticsInvoiceBatchList(LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        return logisticsInvoiceBatchMapper.selectLogisticsInvoiceBatchList(logisticsInvoiceBatch);
    }

    /**
     * 新增发票批次
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 结果
     */
    @Override
    public int insertLogisticsInvoiceBatch(LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        return logisticsInvoiceBatchMapper.insertLogisticsInvoiceBatch(logisticsInvoiceBatch);
    }

    /**
     * 修改发票批次
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 结果
     */
    @Override
    public int updateLogisticsInvoiceBatch(LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        return logisticsInvoiceBatchMapper.updateLogisticsInvoiceBatch(logisticsInvoiceBatch);
    }

    /**
     * 批量删除发票批次
     *
     * @param batchIds 需要删除的发票批次主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsInvoiceBatchByBatchIds(Long[] batchIds)
    {
        return logisticsInvoiceBatchMapper.deleteLogisticsInvoiceBatchByBatchIds(batchIds);
    }

    /**
     * 删除发票批次信息
     *
     * @param batchId 发票批次主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsInvoiceBatchByBatchId(Long batchId)
    {
        return logisticsInvoiceBatchMapper.deleteLogisticsInvoiceBatchByBatchId(batchId);
    }
}
