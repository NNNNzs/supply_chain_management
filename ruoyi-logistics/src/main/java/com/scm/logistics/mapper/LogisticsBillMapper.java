package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsBill;

/**
 * 提单Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsBillMapper
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
     * 删除提单
     *
     * @param billId 提单主键
     * @return 结果
     */
    public int deleteLogisticsBillByBillId(Long billId);

    /**
     * 批量删除提单
     *
     * @param billIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsBillByBillIds(Long[] billIds);

    /**
     * 校验提单号是否唯一
     *
     * @param billNo 提单号
     * @return 结果
     */
    public LogisticsBill checkBillNoUnique(String billNo);

    /**
     * 查询待配载提单列表
     *
     * @param logisticsBill 提单
     * @return 提单集合
     */
    public List<LogisticsBill> selectPendingBills(LogisticsBill logisticsBill);
}
