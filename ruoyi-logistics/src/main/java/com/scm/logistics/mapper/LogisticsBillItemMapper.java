package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsBillItem;

/**
 * 提单货物明细Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsBillItemMapper
{
    /**
     * 查询提单货物明细
     *
     * @param itemId 明细主键
     * @return 提单货物明细
     */
    public LogisticsBillItem selectLogisticsBillItemByItemId(Long itemId);

    /**
     * 根据提单ID查询货物明细列表
     *
     * @param billId 提单ID
     * @return 货物明细集合
     */
    public List<LogisticsBillItem> selectItemsByBillId(Long billId);

    /**
     * 查询待配载的提单货物明细（包含提单信息）
     *
     * @return 货物明细集合
     */
    public List<LogisticsBillItem> selectPendingBillItems();

    /**
     * 新增提单货物明细
     *
     * @param billItem 提单货物明细
     * @return 结果
     */
    public int insertLogisticsBillItem(LogisticsBillItem billItem);

    /**
     * 批量新增提单货物明细
     *
     * @param billItems 提单货物明细列表
     * @return 结果
     */
    public int batchInsertLogisticsBillItem(List<LogisticsBillItem> billItems);

    /**
     * 修改提单货物明细
     *
     * @param billItem 提单货物明细
     * @return 结果
     */
    public int updateLogisticsBillItem(LogisticsBillItem billItem);

    /**
     * 根据提单ID删除货物明细
     *
     * @param billId 提单ID
     * @return 结果
     */
    public int deleteItemsByBillId(Long billId);

    /**
     * 根据提单ID软删除货物明细
     *
     * @param billId 提单ID
     * @return 结果
     */
    public int softDeleteItemsByBillId(Long billId);

    /**
     * 根据提单ID汇总已分配重量
     *
     * @param billId 提单ID
     * @return 已分配重量
     */
    public java.math.BigDecimal sumAllocatedWeightByBillId(Long billId);
}
