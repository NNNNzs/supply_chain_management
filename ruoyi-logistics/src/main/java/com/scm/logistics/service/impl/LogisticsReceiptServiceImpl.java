package com.scm.logistics.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scm.common.utils.BaseEntityUtils;
import com.scm.common.utils.StringUtils;
import com.scm.logistics.mapper.LogisticsReceiptMapper;
import com.scm.logistics.mapper.LogisticsOrderMapper;
import com.scm.logistics.domain.LogisticsReceipt;
import com.scm.logistics.domain.LogisticsOrder;
import com.scm.logistics.service.ILogisticsReceiptService;

/**
 * 回单信息Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsReceiptServiceImpl implements ILogisticsReceiptService
{
    @Autowired
    private LogisticsReceiptMapper logisticsReceiptMapper;

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    /**
     * 查询回单信息
     *
     * @param receiptId 回单信息主键
     * @return 回单信息
     */
    @Override
    public LogisticsReceipt selectLogisticsReceiptByReceiptId(Long receiptId)
    {
        return logisticsReceiptMapper.selectLogisticsReceiptByReceiptId(receiptId);
    }

    /**
     * 查询回单信息列表
     *
     * @param logisticsReceipt 回单信息
     * @return 回单信息
     */
    @Override
    public List<LogisticsReceipt> selectLogisticsReceiptList(LogisticsReceipt logisticsReceipt)
    {
        return logisticsReceiptMapper.selectLogisticsReceiptList(logisticsReceipt);
    }

    /**
     * 新增回单信息
     *
     * @param logisticsReceipt 回单信息
     * @return 结果
     */
    @Override
    public int insertLogisticsReceipt(LogisticsReceipt logisticsReceipt)
    {
        // 自动生成回单编号
        if (StringUtils.isEmpty(logisticsReceipt.getReceiptNo()))
        {
            logisticsReceipt.setReceiptNo(generateReceiptNo());
        }
        // 默认状态为未收到
        if (StringUtils.isEmpty(logisticsReceipt.getReceiptStatus()))
        {
            logisticsReceipt.setReceiptStatus("not_received");
        }
        BaseEntityUtils.fillCreateInfo(logisticsReceipt);
        return logisticsReceiptMapper.insertLogisticsReceipt(logisticsReceipt);
    }

    /**
     * 修改回单信息
     *
     * @param logisticsReceipt 回单信息
     * @return 结果
     */
    @Override
    public int updateLogisticsReceipt(LogisticsReceipt logisticsReceipt)
    {
        BaseEntityUtils.fillUpdateInfo(logisticsReceipt);
        return logisticsReceiptMapper.updateLogisticsReceipt(logisticsReceipt);
    }

    /**
     * 批量删除回单信息
     *
     * @param receiptIds 需要删除的回单信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsReceiptByReceiptIds(Long[] receiptIds)
    {
        return logisticsReceiptMapper.deleteLogisticsReceiptByReceiptIds(receiptIds);
    }

    /**
     * 删除回单信息信息
     *
     * @param receiptId 回单信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsReceiptByReceiptId(Long receiptId)
    {
        return logisticsReceiptMapper.deleteLogisticsReceiptByReceiptId(receiptId);
    }

    /**
     * 生成回单编号
     * 格式：HD + 年月日 + 流水号(4位)
     *
     * @return 回单编号
     */
    @Override
    public String generateReceiptNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());

        // 查询当天已有的回单数量，生成流水号
        LogisticsReceipt query = new LogisticsReceipt();
        query.setReceiptNo("HD" + dateStr);
        List<LogisticsReceipt> list = logisticsReceiptMapper.selectLogisticsReceiptList(query);

        int serialNo = list.size() + 1;
        return String.format("HD%s%04d", dateStr, serialNo);
    }

    /**
     * 确认回单
     * 确认回单时，同时将关联的订单状态更新为"已完成"
     *
     * @param receiptId 回单ID
     * @param receiver 接收人
     * @return 结果
     */
    @Override
    @Transactional
    public int confirmReceipt(Long receiptId, String receiver)
    {
        // 1. 查询回单信息
        LogisticsReceipt receipt = logisticsReceiptMapper.selectLogisticsReceiptByReceiptId(receiptId);
        if (receipt == null) {
            throw new RuntimeException("回单信息不存在");
        }

        // 2. 更新回单状态
        receipt.setReceiptStatus("received");
        receipt.setReceiver(receiver);
        int result = logisticsReceiptMapper.updateLogisticsReceipt(receipt);

        // 3. 更新订单状态为"已完成"
        if (receipt.getOrderId() != null) {
            LogisticsOrder order = new LogisticsOrder();
            order.setOrderId(receipt.getOrderId());
            order.setOrderStatus("completed");
            logisticsOrderMapper.updateOrder(order);
        }

        return result;
    }

    /**
     * 根据订单ID查询回单信息
     *
     * @param orderId 订单ID
     * @return 回单信息
     */
    @Override
    public LogisticsReceipt selectLogisticsReceiptByOrderId(Long orderId)
    {
        return logisticsReceiptMapper.selectLogisticsReceiptByOrderId(orderId);
    }
}
