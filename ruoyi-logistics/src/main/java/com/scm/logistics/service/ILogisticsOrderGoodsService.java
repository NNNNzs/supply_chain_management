package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsOrderGoods;

/**
 * 订单货物明细Service接口
 *
 * @author scm
 * @date 2026-04-15
 */
public interface ILogisticsOrderGoodsService
{
    /**
     * 查询订单货物明细
     *
     * @param detailId 订单货物明细主键
     * @return 订单货物明细
     */
    public LogisticsOrderGoods selectLogisticsOrderGoodsByDetailId(Long detailId);

    /**
     * 查询订单货物明细列表
     *
     * @param logisticsOrderGoods 订单货物明细
     * @return 订单货物明细集合
     */
    public List<LogisticsOrderGoods> selectLogisticsOrderGoodsList(LogisticsOrderGoods logisticsOrderGoods);

    /**
     * 根据订单ID查询货物明细列表
     *
     * @param orderId 订单ID
     * @return 订单货物明细集合
     */
    public List<LogisticsOrderGoods> selectLogisticsOrderGoodsByOrderId(Long orderId);

    /**
     * 新增订单货物明细
     *
     * @param logisticsOrderGoods 订单货物明细
     * @return 结果
     */
    public int insertLogisticsOrderGoods(LogisticsOrderGoods logisticsOrderGoods);

    /**
     * 修改订单货物明细
     *
     * @param logisticsOrderGoods 订单货物明细
     * @return 结果
     */
    public int updateLogisticsOrderGoods(LogisticsOrderGoods logisticsOrderGoods);

    /**
     * 批量删除订单货物明细
     *
     * @param detailIds 需要删除的订单货物明细主键集合
     * @return 结果
     */
    public int deleteLogisticsOrderGoodsByDetailIds(Long[] detailIds);

    /**
     * 删除订单货物明细信息
     *
     * @param detailId 订单货物明细主键
     * @return 结果
     */
    public int deleteLogisticsOrderGoodsByDetailId(Long detailId);

    /**
     * 根据订单ID删除货物明细
     *
     * @param orderId 订单ID
     * @return 结果
     */
    public int deleteLogisticsOrderGoodsByOrderId(Long orderId);

    /**
     * 批量保存订单货物明细
     *
     * @param orderId 订单ID
     * @param goodsList 货物明细列表
     * @return 结果
     */
    public int batchSaveOrderGoods(Long orderId, List<LogisticsOrderGoods> goodsList);
}
