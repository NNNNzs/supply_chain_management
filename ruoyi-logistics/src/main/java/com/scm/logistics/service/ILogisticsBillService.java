package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsBill;

/**
 * 提单Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsBillService
{
    /**
     * 查询提单
     *
     * @param billId 提单主键
     * @return 提单
     */
    public LogisticsBill selectLogisticsBillByBillId(Long billId);

    /**
     * 查询提单列表
     *
     * @param logisticsBill 提单
     * @return 提单集合
     */
    public List<LogisticsBill> selectLogisticsBillList(LogisticsBill logisticsBill);

    /**
     * 新增提单
     *
     * @param logisticsBill 提单
     * @return 结果
     */
    public int insertLogisticsBill(LogisticsBill logisticsBill);

    /**
     * 修改提单
     *
     * @param logisticsBill 提单
     * @return 结果
     */
    public int updateLogisticsBill(LogisticsBill logisticsBill);

    /**
     * 批量删除提单
     *
     * @param billIds 需要删除的提单主键集合
     * @return 结果
     */
    public int deleteLogisticsBillByBillIds(Long[] billIds);

    /**
     * 删除提单信息
     *
     * @param billId 提单主键
     * @return 结果
     */
    public int deleteLogisticsBillByBillId(Long billId);

    /**
     * 查询待配载提单列表
     *
     * @return 待配载提单集合
     */
    public List<LogisticsBill> selectPendingBills();

    /**
     * 查询提单分配明细
     *
     * @param billId 提单ID
     * @return 分配明细集合
     */
    public List<Object> selectBillAllocations(Long billId);
}
