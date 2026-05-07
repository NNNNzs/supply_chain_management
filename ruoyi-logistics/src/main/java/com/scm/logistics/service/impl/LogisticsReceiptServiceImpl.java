package com.scm.logistics.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scm.common.exception.ServiceException;
import com.scm.common.utils.BaseEntityUtils;
import com.scm.common.utils.SecurityUtils;
import com.scm.common.utils.StringUtils;
import com.scm.logistics.mapper.LogisticsReceiptMapper;
import com.scm.logistics.mapper.LogisticsOrderMapper;
import com.scm.logistics.domain.LogisticsReceipt;
import com.scm.logistics.domain.LogisticsOrder;
import com.scm.logistics.service.ILogisticsReceiptService;
import com.scm.logistics.service.ILogisticsOrderLogService;
import com.scm.system.service.ISysConfigService;

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

    @Autowired
    private ILogisticsOrderLogService orderLogService;

    @Autowired
    private ISysConfigService configService;

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
    @Transactional
    public int insertLogisticsReceipt(LogisticsReceipt logisticsReceipt)
    {
        // 获取订单信息用于状态联动
        LogisticsOrder order = null;
        if (logisticsReceipt.getOrderId() != null)
        {
            order = logisticsOrderMapper.selectOrderById(logisticsReceipt.getOrderId());
        }

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
        int result = logisticsReceiptMapper.insertLogisticsReceipt(logisticsReceipt);

        // 记录回单创建日志
        if (result > 0)
        {
            try
            {
                String operatorName = SecurityUtils.getUsername();
                Long operatorId = SecurityUtils.getUserId();
                orderLogService.logOrderUpdate(logisticsReceipt.getOrderId(), order != null ? order.getOrderNo() : null,
                    operatorId, operatorName, "创建回单：" + logisticsReceipt.getReceiptNo());
            }
            catch (Exception e)
            {
                // 日志记录失败不影响主流程
            }
        }

        return result;
    }

    /**
     * 修改回单信息
     *
     * @param logisticsReceipt 回单信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateLogisticsReceipt(LogisticsReceipt logisticsReceipt)
    {
        // 获取原回单信息
        LogisticsReceipt oldReceipt = logisticsReceiptMapper.selectLogisticsReceiptByReceiptId(logisticsReceipt.getReceiptId());
        if (oldReceipt == null)
        {
            throw new ServiceException("回单信息不存在");
        }

        // 获取订单信息
        LogisticsOrder order = null;
        if (logisticsReceipt.getOrderId() != null)
        {
            order = logisticsOrderMapper.selectOrderById(logisticsReceipt.getOrderId());
        }

        BaseEntityUtils.fillUpdateInfo(logisticsReceipt);
        int result = logisticsReceiptMapper.updateLogisticsReceipt(logisticsReceipt);

        // 如果回单状态从未收到变为已收到，更新订单状态为已完成
        if (result > 0 && "not_received".equals(oldReceipt.getReceiptStatus())
            && "received".equals(logisticsReceipt.getReceiptStatus()))
        {
            if (order != null)
            {
                LogisticsOrder updateOrder = new LogisticsOrder();
                updateOrder.setOrderId(order.getOrderId());
                updateOrder.setOrderStatus("completed");
                BaseEntityUtils.fillUpdateInfo(updateOrder);
                logisticsOrderMapper.updateOrder(updateOrder);

                // 记录订单状态变更日志
                try
                {
                    String operatorName = SecurityUtils.getUsername();
                    Long operatorId = SecurityUtils.getUserId();
                    orderLogService.logOrderStatusChange(order.getOrderId(), order.getOrderNo(),
                        order.getOrderStatus(), "completed", operatorId, operatorName);
                }
                catch (Exception e)
                {
                    // 日志记录失败不影响主流程
                }
            }
        }

        return result;
    }

    /**
     * 批量删除回单信息
     *
     * @param receiptIds 需要删除的回单信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteLogisticsReceiptByReceiptIds(Long[] receiptIds)
    {
        int count = 0;
        for (Long receiptId : receiptIds)
        {
            count += deleteLogisticsReceiptByReceiptId(receiptId);
        }
        return count;
    }

    /**
     * 删除回单信息信息
     *
     * @param receiptId 回单信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteLogisticsReceiptByReceiptId(Long receiptId)
    {
        // 获取回单信息
        LogisticsReceipt receipt = logisticsReceiptMapper.selectLogisticsReceiptByReceiptId(receiptId);
        if (receipt == null)
        {
            throw new ServiceException("回单信息不存在");
        }

        // 获取订单信息
        LogisticsOrder order = null;
        if (receipt.getOrderId() != null)
        {
            order = logisticsOrderMapper.selectOrderById(receipt.getOrderId());
        }

        // 删除回单
        int result = logisticsReceiptMapper.deleteLogisticsReceiptByReceiptId(receiptId);

        // 如果订单状态是已完成，恢复为待运输
        if (result > 0 && order != null && "completed".equals(order.getOrderStatus()))
        {
            LogisticsOrder updateOrder = new LogisticsOrder();
            updateOrder.setOrderId(order.getOrderId());
            updateOrder.setOrderStatus("pending");
            BaseEntityUtils.fillUpdateInfo(updateOrder);
            logisticsOrderMapper.updateOrder(updateOrder);

            // 记录订单状态变更日志
            try
            {
                String operatorName = SecurityUtils.getUsername();
                Long operatorId = SecurityUtils.getUserId();
                orderLogService.logOrderStatusChange(order.getOrderId(), order.getOrderNo(),
                    "completed", "pending", operatorId, operatorName);
            }
            catch (Exception e)
            {
                // 日志记录失败不影响主流程
            }
        }

        // 记录删除日志
        if (result > 0)
        {
            try
            {
                String operatorName = SecurityUtils.getUsername();
                Long operatorId = SecurityUtils.getUserId();
                orderLogService.logOrderUpdate(receipt.getOrderId(), order != null ? order.getOrderNo() : null,
                    operatorId, operatorName, "删除回单：" + receipt.getReceiptNo());
            }
            catch (Exception e)
            {
                // 日志记录失败不影响主流程
            }
        }

        return result;
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
            throw new ServiceException("回单信息不存在");
        }

        // 获取订单信息用于日志记录
        LogisticsOrder order = null;
        String oldOrderStatus = null;
        if (receipt.getOrderId() != null)
        {
            order = logisticsOrderMapper.selectOrderById(receipt.getOrderId());
            if (order != null)
            {
                oldOrderStatus = order.getOrderStatus();
            }
        }

        // 2. 更新回单状态
        receipt.setReceiptStatus("received");
        receipt.setReceiver(receiver);
        BaseEntityUtils.fillUpdateInfo(receipt);
        int result = logisticsReceiptMapper.updateLogisticsReceipt(receipt);

        // 3. 更新订单状态为"已完成"
        if (receipt.getOrderId() != null && order != null)
        {
            LogisticsOrder updateOrder = new LogisticsOrder();
            updateOrder.setOrderId(receipt.getOrderId());
            updateOrder.setOrderStatus("completed");
            BaseEntityUtils.fillUpdateInfo(updateOrder);
            logisticsOrderMapper.updateOrder(updateOrder);

            // 记录订单状态变更日志
            if (!"completed".equals(oldOrderStatus))
            {
                try
                {
                    String operatorName = SecurityUtils.getUsername();
                    Long operatorId = SecurityUtils.getUserId();
                    orderLogService.logOrderStatusChange(order.getOrderId(), order.getOrderNo(),
                        oldOrderStatus, "completed", operatorId, operatorName);
                    orderLogService.logOrderUpdate(order.getOrderId(), order.getOrderNo(),
                        operatorId, operatorName, "确认回单：" + receipt.getReceiptNo());
                }
                catch (Exception e)
                {
                    // 日志记录失败不影响主流程
                }
            }
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
