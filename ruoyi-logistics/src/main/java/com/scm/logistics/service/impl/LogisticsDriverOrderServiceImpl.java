package com.scm.logistics.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsDriverOrderMapper;
import com.scm.logistics.domain.LogisticsDriverOrder;
import com.scm.logistics.service.ILogisticsDriverOrderService;

/**
 * 驾驶员单Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsDriverOrderServiceImpl implements ILogisticsDriverOrderService
{
    @Autowired
    private LogisticsDriverOrderMapper logisticsDriverOrderMapper;

    /**
     * 查询驾驶员单
     *
     * @param driverOrderId 驾驶员单主键
     * @return 驾驶员单
     */
    @Override
    public LogisticsDriverOrder selectLogisticsDriverOrderByDriverOrderId(Long driverOrderId)
    {
        return logisticsDriverOrderMapper.selectLogisticsDriverOrderByDriverOrderId(driverOrderId);
    }

    /**
     * 查询驾驶员单列表
     *
     * @param logisticsDriverOrder 驾驶员单
     * @return 驾驶员单
     */
    @Override
    public List<LogisticsDriverOrder> selectLogisticsDriverOrderList(LogisticsDriverOrder logisticsDriverOrder)
    {
        return logisticsDriverOrderMapper.selectLogisticsDriverOrderList(logisticsDriverOrder);
    }

    /**
     * 新增驾驶员单
     *
     * @param logisticsDriverOrder 驾驶员单
     * @return 结果
     */
    @Override
    public int insertLogisticsDriverOrder(LogisticsDriverOrder logisticsDriverOrder)
    {
        return logisticsDriverOrderMapper.insertLogisticsDriverOrder(logisticsDriverOrder);
    }

    /**
     * 修改驾驶员单
     *
     * @param logisticsDriverOrder 驾驶员单
     * @return 结果
     */
    @Override
    public int updateLogisticsDriverOrder(LogisticsDriverOrder logisticsDriverOrder)
    {
        return logisticsDriverOrderMapper.updateLogisticsDriverOrder(logisticsDriverOrder);
    }

    /**
     * 批量删除驾驶员单
     *
     * @param driverOrderIds 需要删除的驾驶员单主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsDriverOrderByDriverOrderIds(Long[] driverOrderIds)
    {
        return logisticsDriverOrderMapper.deleteLogisticsDriverOrderByDriverOrderIds(driverOrderIds);
    }

    /**
     * 删除驾驶员单信息
     *
     * @param driverOrderId 驾驶员单主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsDriverOrderByDriverOrderId(Long driverOrderId)
    {
        return logisticsDriverOrderMapper.deleteLogisticsDriverOrderByDriverOrderId(driverOrderId);
    }

    /**
     * 更新驾驶员单状态
     *
     * @param driverOrderId 驾驶员单ID
     * @param status 状态
     * @return 结果
     */
    @Override
    public int updateDriverOrderStatus(Long driverOrderId, String status)
    {
        LogisticsDriverOrder driverOrder = new LogisticsDriverOrder();
        driverOrder.setDriverOrderId(driverOrderId);
        driverOrder.setDispatchStatus(status);
        return logisticsDriverOrderMapper.updateLogisticsDriverOrder(driverOrder);
    }
}
