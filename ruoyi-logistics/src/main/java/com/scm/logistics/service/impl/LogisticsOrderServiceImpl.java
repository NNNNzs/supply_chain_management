package com.scm.logistics.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.common.exception.ServiceException;
import com.scm.common.utils.StringUtils;
import com.scm.logistics.domain.LogisticsCustomer;
import com.scm.logistics.domain.LogisticsOrder;
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
    public int deleteOrderByIds(Long[] orderIds)
    {
        return orderMapper.deleteOrderByIds(orderIds);
    }

    /**
     * 删除运输订单信息
     *
     * @param orderId 运输订单主键
     * @return 结果
     */
    @Override
    public int deleteOrderById(Long orderId)
    {
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

            String serialNo = String.format("%04d", count + 1);
            logisticsOrder.setOrderNo(prefix + serialNo);
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
}
