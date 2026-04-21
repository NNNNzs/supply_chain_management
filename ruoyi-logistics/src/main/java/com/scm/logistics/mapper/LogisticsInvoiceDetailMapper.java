package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsInvoiceDetail;

/**
 * 发票批次明细Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsInvoiceDetailMapper
{
    /**
     * 查询发票批次明细
     *
     * @param detailId 发票批次明细主键
     * @return 发票批次明细
     */
    public LogisticsInvoiceDetail selectLogisticsInvoiceDetailByDetailId(Long detailId);

    /**
     * 查询发票批次明细列表
     *
     * @param logisticsInvoiceDetail 发票批次明细
     * @return 发票批次明细集合
     */
    public List<LogisticsInvoiceDetail> selectLogisticsInvoiceDetailList(LogisticsInvoiceDetail logisticsInvoiceDetail);

    /**
     * 新增发票批次明细
     *
     * @param logisticsInvoiceDetail 发票批次明细
     * @return 结果
     */
    public int insertLogisticsInvoiceDetail(LogisticsInvoiceDetail logisticsInvoiceDetail);

    /**
     * 修改发票批次明细
     *
     * @param logisticsInvoiceDetail 发票批次明细
     * @return 结果
     */
    public int updateLogisticsInvoiceDetail(LogisticsInvoiceDetail logisticsInvoiceDetail);

    /**
     * 删除发票批次明细
     *
     * @param detailId 发票批次明细主键
     * @return 结果
     */
    public int deleteLogisticsInvoiceDetailByDetailId(Long detailId);

    /**
     * 批量删除发票批次明细
     *
     * @param detailIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsInvoiceDetailByDetailIds(Long[] detailIds);

    /**
     * 根据批次ID查询明细列表
     *
     * @param batchId 批次ID
     * @return 发票批次明细集合
     */
    public List<LogisticsInvoiceDetail> selectDetailsByBatchId(Long batchId);

    /**
     * 根据订单ID查询明细
     *
     * @param orderId 订单ID
     * @return 发票批次明细
     */
    public LogisticsInvoiceDetail selectDetailByOrderId(Long orderId);

    /**
     * 根据批次ID删除明细
     *
     * @param batchId 批次ID
     * @return 结果
     */
    public int deleteDetailsByBatchId(Long batchId);
}
