package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsOrder;

/**
 * 运输订单Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsOrderService
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
     * 批量删除运输订单
     *
     * @param orderIds 需要删除的运输订单主键集合
     * @return 结果
     */
    public int deleteOrderByIds(Long[] orderIds);

    /**
     * 删除运输订单信息
     *
     * @param orderId 运输订单主键
     * @return 结果
     */
    public int deleteOrderById(Long orderId);

    /**
     * 生成订单号
     *
     * @param logisticsOrder 运输订单
     */
    public void generateOrderNo(LogisticsOrder logisticsOrder);

    /**
     * 计算订单金额
     *
     * @param logisticsOrder 运输订单
     */
    public void calculateAmount(LogisticsOrder logisticsOrder);

    /**
     * 校验订单号是否唯一
     *
     * @param logisticsOrder 运输订单
     * @return 结果
     */
    public boolean checkOrderNoUnique(LogisticsOrder logisticsOrder);

    /**
     * 更改订单状态
     *
     * @param orderId 订单ID
     * @param orderStatus 订单状态
     * @return 结果
     */
    public int changeOrderStatus(Long orderId, String orderStatus);

    /**
     * 查询装货地址列表（用于自动完成）
     *
     * @param keyword 关键词
     * @return 地址列表
     */
    public java.util.List<java.util.Map<String, Object>> selectLoadingAddressList(String keyword);

    /**
     * 查询卸货地址列表（用于自动完成）
     *
     * @param keyword 关键词
     * @return 地址列表
     */
    public java.util.List<java.util.Map<String, Object>> selectUnloadingAddressList(String keyword);

    /**
     * 查询所有地址列表（装货和卸货合并，用于自动完成）
     *
     * @param keyword 关键词
     * @return 地址列表
     */
    public java.util.List<java.util.Map<String, Object>> selectAllAddressList(String keyword);
}
