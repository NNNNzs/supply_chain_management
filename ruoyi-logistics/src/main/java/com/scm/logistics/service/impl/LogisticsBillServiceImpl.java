package com.scm.logistics.service.impl;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsBillMapper;
import com.scm.logistics.mapper.LogisticsBillOrderDetailMapper;
import com.scm.logistics.domain.LogisticsBill;
import com.scm.logistics.domain.LogisticsBillOrderDetail;
import com.scm.logistics.service.ILogisticsBillService;

/**
 * 提单Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsBillServiceImpl implements ILogisticsBillService
{
    @Autowired
    private LogisticsBillMapper logisticsBillMapper;

    @Autowired
    private LogisticsBillOrderDetailMapper logisticsBillOrderDetailMapper;

    /**
     * 查询提单
     *
     * @param billId 提单主键
     * @return 提单
     */
    @Override
    public LogisticsBill selectLogisticsBillByBillId(Long billId)
    {
        return logisticsBillMapper.selectLogisticsBillByBillId(billId);
    }

    /**
     * 查询提单列表
     *
     * @param logisticsBill 提单
     * @return 提单
     */
    @Override
    public List<LogisticsBill> selectLogisticsBillList(LogisticsBill logisticsBill)
    {
        return logisticsBillMapper.selectLogisticsBillList(logisticsBill);
    }

    /**
     * 新增提单
     *
     * @param logisticsBill 提单
     * @return 结果
     */
    @Override
    public int insertLogisticsBill(LogisticsBill logisticsBill)
    {
        return logisticsBillMapper.insertLogisticsBill(logisticsBill);
    }

    /**
     * 修改提单
     *
     * @param logisticsBill 提单
     * @return 结果
     */
    @Override
    public int updateLogisticsBill(LogisticsBill logisticsBill)
    {
        return logisticsBillMapper.updateLogisticsBill(logisticsBill);
    }

    /**
     * 批量删除提单
     *
     * @param billIds 需要删除的提单主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsBillByBillIds(Long[] billIds)
    {
        return logisticsBillMapper.deleteLogisticsBillByBillIds(billIds);
    }

    /**
     * 删除提单信息
     *
     * @param billId 提单主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsBillByBillId(Long billId)
    {
        return logisticsBillMapper.deleteLogisticsBillByBillId(billId);
    }

    /**
     * 查询待配载提单列表
     *
     * @return 待配载提单集合
     */
    @Override
    public List<LogisticsBill> selectPendingBills()
    {
        LogisticsBill query = new LogisticsBill();
        query.setBillStatus("pending");
        return logisticsBillMapper.selectLogisticsBillList(query);
    }

    /**
     * 查询提单分配明细
     *
     * @param billId 提单ID
     * @return 分配明细集合
     */
    @Override
    public List<Object> selectBillAllocations(Long billId)
    {
        LogisticsBillOrderDetail detailQuery = new LogisticsBillOrderDetail();
        detailQuery.setBillId(billId);
        return new ArrayList<>(logisticsBillOrderDetailMapper.selectLogisticsBillOrderDetailList(detailQuery));
    }
}
