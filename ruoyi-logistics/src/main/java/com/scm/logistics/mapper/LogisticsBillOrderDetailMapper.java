package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsBillOrderDetail;

/**
 * 提单运单明细Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsBillOrderDetailMapper
{
    /**
     * 查询提单运单明细
     *
     * @param detailId 提单运单明细主键
     * @return 提单运单明细
     */
    public LogisticsBillOrderDetail selectLogisticsBillOrderDetailByDetailId(Long detailId);

    /**
     * 查询提单运单明细列表
     *
     * @param logisticsBillOrderDetail 提单运单明细
     * @return 提单运单明细集合
     */
    public List<LogisticsBillOrderDetail> selectLogisticsBillOrderDetailList(LogisticsBillOrderDetail logisticsBillOrderDetail);

    /**
     * 新增提单运单明细
     *
     * @param logisticsBillOrderDetail 提单运单明细
     * @return 结果
     */
    public int insertLogisticsBillOrderDetail(LogisticsBillOrderDetail logisticsBillOrderDetail);

    /**
     * 批量新增提单运单明细
     *
     * @param list 提单运单明细集合
     * @return 结果
     */
    public int batchInsertLogisticsBillOrderDetail(List<LogisticsBillOrderDetail> list);

    /**
     * 修改提单运单明细
     *
     * @param logisticsBillOrderDetail 提单运单明细
     * @return 结果
     */
    public int updateLogisticsBillOrderDetail(LogisticsBillOrderDetail logisticsBillOrderDetail);

    /**
     * 删除提单运单明细
     *
     * @param detailId 提单运单明细主键
     * @return 结果
     */
    public int deleteLogisticsBillOrderDetailByDetailId(Long detailId);

    /**
     * 批量删除提单运单明细
     *
     * @param detailIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsBillOrderDetailByDetailIds(Long[] detailIds);

    /**
     * 根据提单ID查询明细列表
     *
     * @param billId 提单ID
     * @return 明细集合
     */
    public List<LogisticsBillOrderDetail> selectDetailsByBillId(Long billId);

    /**
     * 根据运单ID查询明细列表
     *
     * @param orderId 运单ID
     * @return 明细集合
     */
    public List<LogisticsBillOrderDetail> selectDetailsByOrderId(Long orderId);

    /**
     * 删除运单的所有明细
     *
     * @param orderId 运单ID
     * @return 结果
     */
    public int deleteDetailsByOrderId(Long orderId);

    /**
     * 统计提单已分配重量
     *
     * @param billId 提单ID
     * @return 已分配重量
     */
    public Double sumAllocatedWeightByBillId(Long billId);

    /**
     * 统计运单已分配金额
     *
     * @param orderId 运单ID
     * @return 已分配金额
     */
    public Double sumAllocatedAmountByOrderId(Long orderId);
}
