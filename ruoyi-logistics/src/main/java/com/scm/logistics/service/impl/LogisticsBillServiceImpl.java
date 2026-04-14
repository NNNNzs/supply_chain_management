package com.scm.logistics.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scm.common.exception.ServiceException;
import com.scm.common.utils.StringUtils;
import com.scm.logistics.domain.LogisticsBill;
import com.scm.logistics.domain.LogisticsBillItem;
import com.scm.logistics.domain.LogisticsBillOrderDetail;
import com.scm.logistics.domain.LogisticsCustomer;
import com.scm.logistics.mapper.LogisticsBillItemMapper;
import com.scm.logistics.mapper.LogisticsBillMapper;
import com.scm.logistics.mapper.LogisticsBillOrderDetailMapper;
import com.scm.logistics.mapper.LogisticsCustomerMapper;
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
    private LogisticsBillItemMapper logisticsBillItemMapper;

    @Autowired
    private LogisticsBillOrderDetailMapper logisticsBillOrderDetailMapper;

    @Autowired
    private LogisticsCustomerMapper customerMapper;

    /**
     * 查询提单（含货物明细）
     *
     * @param billId 提单主键
     * @return 提单
     */
    @Override
    public LogisticsBill selectLogisticsBillByBillId(Long billId)
    {
        LogisticsBill bill = logisticsBillMapper.selectLogisticsBillByBillId(billId);
        if (bill != null)
        {
            List<LogisticsBillItem> items = logisticsBillItemMapper.selectItemsByBillId(billId);
            bill.setBillItems(items);
        }
        return bill;
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
        List<LogisticsBill> list = logisticsBillMapper.selectLogisticsBillList(logisticsBill);
        // 为每个提单加载货物明细
        for (LogisticsBill bill : list)
        {
            if (bill != null)
            {
                List<LogisticsBillItem> items = logisticsBillItemMapper.selectItemsByBillId(bill.getBillId());
                bill.setBillItems(items);
            }
        }
        return list;
    }

    /**
     * 新增提单（含货物明细，级联保存）
     *
     * @param logisticsBill 提单
     * @return 结果
     */
    @Override
    @Transactional
    public int insertLogisticsBill(LogisticsBill logisticsBill)
    {
        // 生成提单号
        generateBillNo(logisticsBill);

        // 汇总明细到主表
        computeBillTotalsFromItems(logisticsBill);

        // 保存提单主表
        int rows = logisticsBillMapper.insertLogisticsBill(logisticsBill);

        // 保存货物明细
        if (logisticsBill.getBillItems() != null && !logisticsBill.getBillItems().isEmpty())
        {
            for (LogisticsBillItem item : logisticsBill.getBillItems())
            {
                item.setBillId(logisticsBill.getBillId());
                logisticsBillItemMapper.insertLogisticsBillItem(item);
            }
        }

        return rows;
    }

    /**
     * 修改提单（含货物明细，差量更新）
     *
     * @param logisticsBill 提单
     * @return 结果
     */
    @Override
    @Transactional
    public int updateLogisticsBill(LogisticsBill logisticsBill)
    {
        // 汇总明细到主表
        computeBillTotalsFromItems(logisticsBill);

        // 更新提单主表
        int rows = logisticsBillMapper.updateLogisticsBill(logisticsBill);

        // 差量更新货物明细
        if (logisticsBill.getBillItems() != null)
        {
            List<LogisticsBillItem> items = logisticsBill.getBillItems();
            for (LogisticsBillItem item : items)
            {
                item.setBillId(logisticsBill.getBillId());
                if (item.getItemId() != null)
                {
                    logisticsBillItemMapper.updateLogisticsBillItem(item);
                }
                else
                {
                    logisticsBillItemMapper.insertLogisticsBillItem(item);
                }
            }
        }

        return rows;
    }

    /**
     * 批量删除提单（级联删除明细）
     *
     * @param billIds 需要删除的提单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteLogisticsBillByBillIds(Long[] billIds)
    {
        for (Long billId : billIds)
        {
            deleteLogisticsBillByBillId(billId);
        }
        return billIds.length;
    }

    /**
     * 删除提单信息（级联删除明细）
     *
     * @param billId 提单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteLogisticsBillByBillId(Long billId)
    {
        // 检查提单状态
        LogisticsBill bill = logisticsBillMapper.selectLogisticsBillByBillId(billId);
        if (bill == null)
        {
            throw new ServiceException("提单不存在");
        }
        if ("completed".equals(bill.getBillStatus()) || "transporting".equals(bill.getBillStatus()))
        {
            throw new ServiceException("运输中或已完成的提单不能删除");
        }

        // 检查是否有关联的运单
        int orderCount = logisticsBillOrderDetailMapper.countOrdersByBillId(billId);
        if (orderCount > 0)
        {
            throw new ServiceException("该提单已关联 " + orderCount + " 个运单，请先删除运单关联");
        }

        // 软删除货物明细
        logisticsBillItemMapper.softDeleteItemsByBillId(billId);

        // 逻辑删除提单
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
        List<LogisticsBillOrderDetail> details = logisticsBillOrderDetailMapper.selectLogisticsBillOrderDetailList(detailQuery);
        return new java.util.ArrayList<>(details);
    }

    /**
     * 从货物明细汇总计算主表的 totalWeight、totalAmount
     */
    private void computeBillTotalsFromItems(LogisticsBill bill)
    {
        List<LogisticsBillItem> items = bill.getBillItems();
        if (items != null && !items.isEmpty())
        {
            BigDecimal totalWeight = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < items.size(); i++)
            {
                LogisticsBillItem item = items.get(i);
                if (item.getSortOrder() == null)
                {
                    item.setSortOrder(i);
                }
                // 自动计算金额
                if (item.getWeight() != null && item.getUnitPrice() != null)
                {
                    item.setAmount(item.getWeight().multiply(item.getUnitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                totalWeight = totalWeight.add(item.getWeight() != null ? item.getWeight() : BigDecimal.ZERO);
                totalAmount = totalAmount.add(item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO);
            }
            bill.setTotalWeight(totalWeight);
            bill.setTotalAmount(totalAmount);
        }
    }

    /**
     * 生成提单号
     * 格式：客户编码 + 提单日期(yyyyMMdd) + 流水号(4位)
     *
     * @param bill 提单
     */
    private void generateBillNo(LogisticsBill bill)
    {
        if (StringUtils.isEmpty(bill.getBillNo()))
        {
            LogisticsCustomer customer = customerMapper.selectCustomerById(bill.getCustomerId());
            if (customer == null)
            {
                throw new ServiceException("客户信息不存在");
            }

            // 提单号格式：客户编码 + 提单日期(yyyyMMdd) + 流水号
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(bill.getBillDate());
            String prefix = customer.getCustomerCode() + dateStr;

            // 查询当天该客户的提单数量作为流水号
            LogisticsBill query = new LogisticsBill();
            query.setCustomerId(bill.getCustomerId());
            List<LogisticsBill> bills = logisticsBillMapper.selectLogisticsBillList(query);
            int count = 0;
            for (LogisticsBill b : bills)
            {
                if (b.getBillNo() != null && b.getBillNo().startsWith(prefix))
                {
                    count++;
                }
            }

            // 生成提单号并检查数据库中是否存在（包括已删除的记录）
            String serialNo;
            String candidateBillNo;
            int attempt = 0;
            do
            {
                serialNo = String.format("%04d", count + 1 + attempt);
                candidateBillNo = prefix + serialNo;
                attempt++;
            }
            while (logisticsBillMapper.checkBillNoExistsInDb(candidateBillNo) != null);

            bill.setBillNo(candidateBillNo);
        }
    }
}
