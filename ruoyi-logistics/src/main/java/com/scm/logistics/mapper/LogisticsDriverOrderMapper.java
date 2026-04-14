package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsDriverOrder;

/**
 * 驾驶员单Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsDriverOrderMapper
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
     * 删除驾驶员单
     *
     * @param driverOrderId 驾驶员单主键
     * @return 结果
     */
    public int deleteLogisticsDriverOrderByDriverOrderId(Long driverOrderId);

    /**
     * 批量删除驾驶员单
     *
     * @param driverOrderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsDriverOrderByDriverOrderIds(Long[] driverOrderIds);

    /**
     * 校验驾驶员单号是否唯一
     *
     * @param driverOrderNo 驾驶员单号
     * @return 结果
     */
    public LogisticsDriverOrder checkDriverOrderNoUnique(String driverOrderNo);

    /**
     * 根据运单ID查询驾驶员单列表
     *
     * @param orderId 运单ID
     * @return 驾驶员单集合
     */
    public List<LogisticsDriverOrder> selectDriverOrdersByOrderId(Long orderId);

    /**
     * 查询司机的驾驶员单列表（用于结算）
     *
     * @param driverId 司机ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 驾驶员单集合
     */
    public List<LogisticsDriverOrder> selectDriverOrdersForSettlement(Long driverId, String startDate, String endDate);
}
