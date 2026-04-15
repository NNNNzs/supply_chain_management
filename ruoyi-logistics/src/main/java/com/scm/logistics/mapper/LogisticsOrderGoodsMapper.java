package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsOrderGoods;

/**
 * 订单货物明细Mapper接口
 *
 * @author scm
 * @date 2026-04-15
 */
public interface LogisticsOrderGoodsMapper
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
     * 删除订单货物明细
     *
     * @param detailId 订单货物明细主键
     * @return 结果
     */
    public int deleteLogisticsOrderGoodsByDetailId(Long detailId);

    /**
     * 批量删除订单货物明细
     *
     * @param detailIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsOrderGoodsByDetailIds(Long[] detailIds);

    /**
     * 根据订单ID删除货物明细
     *
     * @param orderId 订单ID
     * @return 结果
     */
    public int deleteLogisticsOrderGoodsByOrderId(Long orderId);

    /**
     * 批量新增订单货物明细
     *
     * @param logisticsOrderGoodsList 订单货物明细列表
     * @return 结果
     */
    public int batchInsertLogisticsOrderGoods(List<LogisticsOrderGoods> logisticsOrderGoodsList);
}
