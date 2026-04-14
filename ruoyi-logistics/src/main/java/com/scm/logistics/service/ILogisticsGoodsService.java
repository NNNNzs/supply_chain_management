package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsGoods;

/**
 * 货物信息Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsGoodsService
{
    /**
     * 查询货物信息
     *
     * @param goodsId 货物信息主键
     * @return 货物信息
     */
    public LogisticsGoods selectLogisticsGoodsByGoodsId(Long goodsId);

    /**
     * 查询货物信息列表
     *
     * @param logisticsGoods 货物信息
     * @return 货物信息集合
     */
    public List<LogisticsGoods> selectLogisticsGoodsList(LogisticsGoods logisticsGoods);

    /**
     * 新增货物信息
     *
     * @param logisticsGoods 货物信息
     * @return 结果
     */
    public int insertLogisticsGoods(LogisticsGoods logisticsGoods);

    /**
     * 修改货物信息
     *
     * @param logisticsGoods 货物信息
     * @return 结果
     */
    public int updateLogisticsGoods(LogisticsGoods logisticsGoods);

    /**
     * 批量删除货物信息
     *
     * @param goodsIds 需要删除的货物信息主键集合
     * @return 结果
     */
    public int deleteLogisticsGoodsByGoodsIds(Long[] goodsIds);

    /**
     * 删除货物信息信息
     *
     * @param goodsId 货物信息主键
     * @return 结果
     */
    public int deleteLogisticsGoodsByGoodsId(Long goodsId);
}
