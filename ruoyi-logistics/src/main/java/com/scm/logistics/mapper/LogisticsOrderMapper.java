package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsOrder;

/**
 * 运输订单Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsOrderMapper
{
    /**
     * 查询运输订单
     *
     * @param orderId 运输订单主键
     * @return 运输订单
     */
    public LogisticsOrder selectOrderById(Long orderId);

    /**
     * 查询运输订单列表
     *
     * @param logisticsOrder 运输订单
     * @return 运输订单集合
     */
    public List<LogisticsOrder> selectOrderList(LogisticsOrder logisticsOrder);

    /**
     * 新增运输订单
     *
     * @param logisticsOrder 运输订单
     * @return 结果
     */
    public int insertOrder(LogisticsOrder logisticsOrder);

    /**
     * 修改运输订单
     *
     * @param logisticsOrder 运输订单
     * @return 结果
     */
    public int updateOrder(LogisticsOrder logisticsOrder);

    /**
     * 删除运输订单
     *
     * @param orderId 运输订单主键
     * @return 结果
     */
    public int deleteOrderById(Long orderId);

    /**
     * 批量删除运输订单
     *
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrderByIds(Long[] orderIds);

    /**
     * 校验订单号是否唯一
     *
     * @param orderNo 订单号
     * @return 结果
     */
    public LogisticsOrder checkOrderNoUnique(String orderNo);

    /**
     * 根据客户ID查询订单列表
     *
     * @param customerId 客户ID
     * @return 订单列表
     */
    public List<LogisticsOrder> selectOrdersByCustomerId(Long customerId);

    /**
     * 检查订单号是否存在于数据库（包括已删除的记录）
     *
     * @param orderNo 订单号
     * @return 结果
     */
    public LogisticsOrder checkOrderNoExistsInDb(String orderNo);

    /**
     * 查询装货地址列表（去重并统计使用次数）
     *
     * @param keyword 关键词
     * @return 地址列表
     */
    public List<java.util.Map<String, Object>> selectLoadingAddressList(String keyword);

    /**
     * 查询卸货地址列表（去重并统计使用次数）
     *
     * @param keyword 关键词
     * @return 地址列表
     */
    public List<java.util.Map<String, Object>> selectUnloadingAddressList(String keyword);

    /**
     * 查询所有地址列表（装货和卸货合并，去重并统计使用次数）
     *
     * @param keyword 关键词
     * @return 地址列表
     */
    public List<java.util.Map<String, Object>> selectAllAddressList(String keyword);

    /**
     * 根据订单号前缀查询最大订单号（用于生成流水号）
     *
     * @param orderNoPrefix 订单号前缀
     * @return 最大订单号
     */
    public String selectMaxOrderNoByPrefix(String orderNoPrefix);
}
