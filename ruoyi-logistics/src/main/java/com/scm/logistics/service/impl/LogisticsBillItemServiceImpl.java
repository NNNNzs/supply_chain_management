package com.scm.logistics.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsBillItemMapper;
import com.scm.logistics.domain.LogisticsBillItem;
import com.scm.logistics.service.ILogisticsBillItemService;

/**
 * 提单货物明细Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsBillItemServiceImpl implements ILogisticsBillItemService
{
    @Autowired
    private LogisticsBillItemMapper logisticsBillItemMapper;

    /**
     * 查询提单货物明细
     *
     * @param itemId 明细主键
     * @return 提单货物明细
     */
    @Override
    public LogisticsBillItem selectLogisticsBillItemByItemId(Long itemId)
    {
        return logisticsBillItemMapper.selectLogisticsBillItemByItemId(itemId);
    }

    /**
     * 根据提单ID查询货物明细列表
     *
     * @param billId 提单ID
     * @return 货物明细集合
     */
    @Override
    public List<LogisticsBillItem> selectItemsByBillId(Long billId)
    {
        return logisticsBillItemMapper.selectItemsByBillId(billId);
    }

    /**
     * 查询待配载的提单货物明细
     *
     * @return 待配载货物明细集合
     */
    @Override
    public List<LogisticsBillItem> selectPendingBillItems()
    {
        return logisticsBillItemMapper.selectPendingBillItems();
    }

    /**
     * 新增提单货物明细
     *
     * @param billItem 提单货物明细
     * @return 结果
     */
    @Override
    public int insertLogisticsBillItem(LogisticsBillItem billItem)
    {
        return logisticsBillItemMapper.insertLogisticsBillItem(billItem);
    }

    /**
     * 修改提单货物明细
     *
     * @param billItem 提单货物明细
     * @return 结果
     */
    @Override
    public int updateLogisticsBillItem(LogisticsBillItem billItem)
    {
        return logisticsBillItemMapper.updateLogisticsBillItem(billItem);
    }

    /**
     * 根据提单ID删除货物明细
     *
     * @param billId 提单ID
     * @return 结果
     */
    @Override
    public int deleteItemsByBillId(Long billId)
    {
        return logisticsBillItemMapper.softDeleteItemsByBillId(billId);
    }
}
