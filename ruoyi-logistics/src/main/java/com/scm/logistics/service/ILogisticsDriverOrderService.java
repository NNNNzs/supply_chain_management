package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsDriverOrder;

/**
 * 驾驶员单Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsDriverOrderService
{
    /**
     * 查询驾驶员单
     *
     * @param driverOrderId 驾驶员单主键
     * @return 驾驶员单
     */
    public LogisticsDriverOrder selectLogisticsDriverOrderByDriverOrderId(Long driverOrderId);

    /**
     * 查询驾驶员单列表
     *
     * @param logisticsDriverOrder 驾驶员单
     * @return 驾驶员单集合
     */
    public List<LogisticsDriverOrder> selectLogisticsDriverOrderList(LogisticsDriverOrder logisticsDriverOrder);

    /**
     * 新增驾驶员单
     *
     * @param logisticsDriverOrder 驾驶员单
     * @return 结果
     */
    public int insertLogisticsDriverOrder(LogisticsDriverOrder logisticsDriverOrder);

    /**
     * 修改驾驶员单
     *
     * @param logisticsDriverOrder 驾驶员单
     * @return 结果
     */
    public int updateLogisticsDriverOrder(LogisticsDriverOrder logisticsDriverOrder);

    /**
     * 批量删除驾驶员单
     *
     * @param driverOrderIds 需要删除的驾驶员单主键集合
     * @return 结果
     */
    public int deleteLogisticsDriverOrderByDriverOrderIds(Long[] driverOrderIds);

    /**
     * 删除驾驶员单信息
     *
     * @param driverOrderId 驾驶员单主键
     * @return 结果
     */
    public int deleteLogisticsDriverOrderByDriverOrderId(Long driverOrderId);

    /**
     * 更新驾驶员单状态
     *
     * @param driverOrderId 驾驶员单ID
     * @param status 状态
     * @return 结果
     */
    public int updateDriverOrderStatus(Long driverOrderId, String status);
}
