package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsBillItem;

/**
 * 提单货物明细Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsBillItemService
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
     * 查询待配载的提单货物明细
     *
     * @return 待配载货物明细集合
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
}
