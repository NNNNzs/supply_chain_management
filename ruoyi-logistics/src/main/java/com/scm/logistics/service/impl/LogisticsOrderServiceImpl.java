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
import com.scm.logistics.mapper.LogisticsCustomerMapper;
import com.scm.logistics.mapper.LogisticsOrderMapper;
import com.scm.logistics.service.ILogisticsOrderGoodsService;
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
    private ILogisticsOrderGoodsService orderGoodsService;

    /**
     * 查询运输订单
     *
     * @param orderId 运输订单主键
     * @return 运输订单
     */
    @Override
    public LogisticsOrder selectOrderById(Long orderId)
    {
        LogisticsOrder order = orderMapper.selectOrderById(orderId);
        if (order != null)
        {
            // 加载订单货物明细
            List<com.scm.logistics.domain.LogisticsOrderGoods> goodsList = orderGoodsService.selectLogisticsOrderGoodsByOrderId(orderId);
            order.setGoodsList(goodsList);
        }
        return order;
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
    @Transactional
    public int insertOrder(LogisticsOrder logisticsOrder)
    {
        // 生成订单号
        generateOrderNo(logisticsOrder);
        // 计算金额（从货物明细汇总）
        calculateAmountFromGoods(logisticsOrder);
        // 保存订单
        int result = orderMapper.insertOrder(logisticsOrder);
        // 保存货物明细
        if (logisticsOrder.getGoodsList() != null && !logisticsOrder.getGoodsList().isEmpty())
        {
            for (com.scm.logistics.domain.LogisticsOrderGoods goods : logisticsOrder.getGoodsList())
            {
                goods.setOrderId(logisticsOrder.getOrderId());
                goods.setCreateBy(logisticsOrder.getCreateBy());
            }
            orderGoodsService.batchSaveOrderGoods(logisticsOrder.getOrderId(), logisticsOrder.getGoodsList());
        }
        return result;
    }

    /**
     * 修改运输订单
     *
     * @param logisticsOrder 运输订单
     * @return 结果
     */
    @Override
    @Transactional
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
        // 重新计算金额（从货物明细汇总）
        calculateAmountFromGoods(logisticsOrder);
        // 更新订单
        int result = orderMapper.updateOrder(logisticsOrder);
        // 更新货物明细
        if (logisticsOrder.getGoodsList() != null)
        {
            for (com.scm.logistics.domain.LogisticsOrderGoods goods : logisticsOrder.getGoodsList())
            {
                goods.setOrderId(logisticsOrder.getOrderId());
                goods.setUpdateBy(logisticsOrder.getUpdateBy());
            }
            orderGoodsService.batchSaveOrderGoods(logisticsOrder.getOrderId(), logisticsOrder.getGoodsList());
        }
        return result;
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

        // 删除订单货物明细
        orderGoodsService.deleteLogisticsOrderGoodsByOrderId(orderId);

        // 逻辑删除订单
        return orderMapper.deleteOrderById(orderId);
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
     * 从货物明细汇总计算订单金额和重量
     *
     * @param logisticsOrder 运输订单
     */
    private void calculateAmountFromGoods(LogisticsOrder logisticsOrder)
    {
        if (logisticsOrder.getGoodsList() != null && !logisticsOrder.getGoodsList().isEmpty())
        {
            BigDecimal totalWeight = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (com.scm.logistics.domain.LogisticsOrderGoods goods : logisticsOrder.getGoodsList())
            {
                if (goods.getWeight() != null)
                {
                    totalWeight = totalWeight.add(goods.getWeight());
                }
                if (goods.getAmount() != null)
                {
                    totalAmount = totalAmount.add(goods.getAmount());
                }
            }

            logisticsOrder.setTotalWeight(totalWeight);
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
