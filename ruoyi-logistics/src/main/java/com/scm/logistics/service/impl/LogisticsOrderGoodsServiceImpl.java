package com.scm.logistics.service.impl;

import java.util.List;
import com.scm.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scm.logistics.mapper.LogisticsOrderGoodsMapper;
import com.scm.logistics.domain.LogisticsOrderGoods;
import com.scm.logistics.service.ILogisticsOrderGoodsService;

/**
 * 订单货物明细Service业务层处理
 *
 * @author scm
 * @date 2026-04-15
 */
@Service
public class LogisticsOrderGoodsServiceImpl implements ILogisticsOrderGoodsService
{
    @Autowired
    private LogisticsOrderGoodsMapper logisticsOrderGoodsMapper;

    /**
     * 查询订单货物明细
     *
     * @param detailId 订单货物明细主键
     * @return 订单货物明细
     */
    @Override
    public LogisticsOrderGoods selectLogisticsOrderGoodsByDetailId(Long detailId)
    {
        return logisticsOrderGoodsMapper.selectLogisticsOrderGoodsByDetailId(detailId);
    }

    /**
     * 查询订单货物明细列表
     *
     * @param logisticsOrderGoods 订单货物明细
     * @return 订单货物明细
     */
    @Override
    public List<LogisticsOrderGoods> selectLogisticsOrderGoodsList(LogisticsOrderGoods logisticsOrderGoods)
    {
        return logisticsOrderGoodsMapper.selectLogisticsOrderGoodsList(logisticsOrderGoods);
    }

    /**
     * 根据订单ID查询货物明细列表
     *
     * @param orderId 订单ID
     * @return 订单货物明细集合
     */
    @Override
    public List<LogisticsOrderGoods> selectLogisticsOrderGoodsByOrderId(Long orderId)
    {
        return logisticsOrderGoodsMapper.selectLogisticsOrderGoodsByOrderId(orderId);
    }

    /**
     * 新增订单货物明细
     *
     * @param logisticsOrderGoods 订单货物明细
     * @return 结果
     */
    @Override
    public int insertLogisticsOrderGoods(LogisticsOrderGoods logisticsOrderGoods)
    {
        logisticsOrderGoods.setCreateTime(DateUtils.getNowDate());
        return logisticsOrderGoodsMapper.insertLogisticsOrderGoods(logisticsOrderGoods);
    }

    /**
     * 修改订单货物明细
     *
     * @param logisticsOrderGoods 订单货物明细
     * @return 结果
     */
    @Override
    public int updateLogisticsOrderGoods(LogisticsOrderGoods logisticsOrderGoods)
    {
        logisticsOrderGoods.setUpdateTime(DateUtils.getNowDate());
        return logisticsOrderGoodsMapper.updateLogisticsOrderGoods(logisticsOrderGoods);
    }

    /**
     * 批量删除订单货物明细
     *
     * @param detailIds 需要删除的订单货物明细主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsOrderGoodsByDetailIds(Long[] detailIds)
    {
        return logisticsOrderGoodsMapper.deleteLogisticsOrderGoodsByDetailIds(detailIds);
    }

    /**
     * 删除订单货物明细信息
     *
     * @param detailId 订单货物明细主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsOrderGoodsByDetailId(Long detailId)
    {
        return logisticsOrderGoodsMapper.deleteLogisticsOrderGoodsByDetailId(detailId);
    }

    /**
     * 根据订单ID删除货物明细
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    public int deleteLogisticsOrderGoodsByOrderId(Long orderId)
    {
        return logisticsOrderGoodsMapper.deleteLogisticsOrderGoodsByOrderId(orderId);
    }

    /**
     * 批量保存订单货物明细（先删除后新增）
     *
     * @param orderId 订单ID
     * @param goodsList 货物明细列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchSaveOrderGoods(Long orderId, List<LogisticsOrderGoods> goodsList)
    {
        if (goodsList == null || goodsList.isEmpty())
        {
            return 0;
        }

        // 先删除该订单的所有货物明细
        deleteLogisticsOrderGoodsByOrderId(orderId);

        // 批量插入新的货物明细
        return logisticsOrderGoodsMapper.batchInsertLogisticsOrderGoods(goodsList);
    }
}
