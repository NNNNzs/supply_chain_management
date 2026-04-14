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
import com.scm.logistics.domain.LogisticsCustomer;
import com.scm.logistics.domain.LogisticsOrder;
import com.scm.logistics.mapper.LogisticsBillMapper;
import com.scm.logistics.mapper.LogisticsBillOrderDetailMapper;
import com.scm.logistics.mapper.LogisticsCustomerMapper;
import com.scm.logistics.mapper.LogisticsOrderMapper;
import com.scm.logistics.service.ILogisticsOrderService;

/**
 * 运输订单Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsOrderServiceImpl implements ILogisticsOrderService
{
    @Autowired
    private LogisticsOrderMapper orderMapper;

    @Autowired
    private LogisticsCustomerMapper customerMapper;

    @Autowired
    private LogisticsBillOrderDetailMapper billOrderDetailMapper;

    @Autowired
    private LogisticsBillMapper billMapper;

    /**
     * 查询运输订单
     *
     * @param orderId 运输订单主键
     * @return 运输订单
     */
    @Override
    public LogisticsOrder selectOrderById(Long orderId)
    {
        return orderMapper.selectOrderById(orderId);
    }

    /**
     * 查询运输订单列表
     *
     * @param logisticsOrder 运输订单
     * @return 运输订单
     */
    @Override
    public List<LogisticsOrder> selectOrderList(LogisticsOrder logisticsOrder)
    {
        return orderMapper.selectOrderList(logisticsOrder);
    }

    /**
     * 新增运输订单
     *
     * @param logisticsOrder 运输订单
     * @return 结果
     */
    @Override
    public int insertOrder(LogisticsOrder logisticsOrder)
    {
        // 生成订单号
        generateOrderNo(logisticsOrder);
        // 计算金额
        calculateAmount(logisticsOrder);
        return orderMapper.insertOrder(logisticsOrder);
    }

    /**
     * 修改运输订单
     *
     * @param logisticsOrder 运输订单
     * @return 结果
     */
    @Override
    public int updateOrder(LogisticsOrder logisticsOrder)
    {
        // 如果订单号为空，生成订单号
        if (StringUtils.isEmpty(logisticsOrder.getOrderNo()))
        {
            generateOrderNo(logisticsOrder);
        }
        else
        {
            // 校验订单号是否唯一
            if (!checkOrderNoUnique(logisticsOrder))
            {
                throw new ServiceException("修改订单失败，订单号已存在");
            }
        }
        // 重新计算金额
        calculateAmount(logisticsOrder);
        return orderMapper.updateOrder(logisticsOrder);
    }

    /**
     * 批量删除运输订单
     *
     * @param orderIds 需要删除的运输订单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrderByIds(Long[] orderIds)
    {
        for (Long orderId : orderIds)
        {
            deleteOrderById(orderId);
        }
        return orderIds.length;
    }

    /**
     * 删除运输订单信息
     *
     * @param orderId 运输订单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrderById(Long orderId)
    {
        // 检查订单状态，已完成的订单不能删除
        LogisticsOrder order = orderMapper.selectOrderById(orderId);
        if (order == null)
        {
            throw new ServiceException("订单不存在");
        }
        if ("completed".equals(order.getOrderStatus()))
        {
            throw new ServiceException("已完成的订单不能删除");
        }
        if ("settled".equals(order.getSettlementStatus()))
        {
            throw new ServiceException("已结算的订单不能删除");
        }

        // 获取订单关联的提单ID列表
        List<Long> billIds = billOrderDetailMapper.selectBillIdsByOrderId(orderId);

        // 删除运单的提单关联明细
        billOrderDetailMapper.deleteDetailsByOrderId(orderId);

        // 更新关联提单的状态和已分配重量
        for (Long billId : billIds)
        {
            updateBillStatusAfterOrderDelete(billId);
        }

        // 逻辑删除订单
        return orderMapper.deleteOrderById(orderId);
    }

    /**
     * 删除订单后更新提单状态和已分配重量
     *
     * @param billId 提单ID
     */
    private void updateBillStatusAfterOrderDelete(Long billId)
    {
        // 重新计算提单的已分配重量
        Double allocatedWeight = billOrderDetailMapper.sumAllocatedWeightByBillId(billId);
        LogisticsBill bill = billMapper.selectLogisticsBillByBillId(billId);

        if (bill != null)
        {
            // 更新已分配重量
            bill.setAllocatedWeight(allocatedWeight != null ? BigDecimal.valueOf(allocatedWeight) : BigDecimal.ZERO);

            // 根据已分配重量更新状态
            if (bill.getTotalWeight() != null)
            {
                if (bill.getAllocatedWeight().compareTo(BigDecimal.ZERO) == 0)
                {
                    // 没有分配，恢复为待配载
                    bill.setBillStatus("pending");
                }
                else if (bill.getAllocatedWeight().compareTo(bill.getTotalWeight()) < 0)
                {
                    // 部分分配
                    bill.setBillStatus("partial");
                }
                else
                {
                    // 全部分配
                    bill.setBillStatus("allocated");
                }
            }

            billMapper.updateBillStatusAndWeight(bill);
        }
    }

    /**
     * 生成订单号
     * 格式：类型(2位) + 客户编码 + 年月日 + 流水号(4位)
     *
     * @param logisticsOrder 运输订单
     */
    @Override
    public void generateOrderNo(LogisticsOrder logisticsOrder)
    {
        if (StringUtils.isEmpty(logisticsOrder.getOrderNo()))
        {
            LogisticsCustomer customer = customerMapper.selectCustomerById(logisticsOrder.getCustomerId());
            if (customer == null)
            {
                throw new ServiceException("客户信息不存在");
            }

            // 订单号格式：YS(运输) + 客户编码 + 年月日 + 流水号
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(logisticsOrder.getOrderDate());
            String prefix = "YS" + customer.getCustomerCode() + dateStr;

            // 查询当天该客户的订单数量作为流水号
            List<LogisticsOrder> orders = orderMapper.selectOrdersByCustomerId(logisticsOrder.getCustomerId());
            int count = 0;
            for (LogisticsOrder order : orders)
            {
                if (order.getOrderNo() != null && order.getOrderNo().startsWith(prefix))
                {
                    count++;
                }
            }

            // 生成订单号并检查数据库中是否存在（包括已删除的记录）
            String serialNo;
            String candidateOrderNo;
            int attempt = 0;
            do
            {
                serialNo = String.format("%04d", count + 1 + attempt);
                candidateOrderNo = prefix + serialNo;
                attempt++;
            }
            while (orderMapper.checkOrderNoExistsInDb(candidateOrderNo) != null);

            logisticsOrder.setOrderNo(candidateOrderNo);
        }
    }

    /**
     * 计算订单金额
     *
     * @param logisticsOrder 运输订单
     */
    @Override
    public void calculateAmount(LogisticsOrder logisticsOrder)
    {
        if (logisticsOrder.getWeight() != null && logisticsOrder.getUnitPrice() != null)
        {
            BigDecimal totalAmount = logisticsOrder.getWeight().multiply(logisticsOrder.getUnitPrice());
            logisticsOrder.setTotalAmount(totalAmount);
        }
    }

    /**
     * 校验订单号是否唯一
     *
     * @param logisticsOrder 运输订单
     * @return 结果
     */
    @Override
    public boolean checkOrderNoUnique(LogisticsOrder logisticsOrder)
    {
        Long orderId = StringUtils.isNull(logisticsOrder.getOrderId()) ? -1L : logisticsOrder.getOrderId();
        LogisticsOrder info = orderMapper.checkOrderNoUnique(logisticsOrder.getOrderNo());
        if (StringUtils.isNotNull(info) && info.getOrderId().longValue() != orderId.longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 更改订单状态
     *
     * @param orderId 订单ID
     * @param orderStatus 订单状态
     * @return 结果
     */
    @Override
    public int changeOrderStatus(Long orderId, String orderStatus)
    {
        LogisticsOrder order = new LogisticsOrder();
        order.setOrderId(orderId);
        order.setOrderStatus(orderStatus);
        return orderMapper.updateOrder(order);
    }
}
