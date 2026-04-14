package com.scm.logistics.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsGoodsMapper;
import com.scm.logistics.domain.LogisticsGoods;
import com.scm.logistics.service.ILogisticsGoodsService;

/**
 * 货物信息Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsGoodsServiceImpl implements ILogisticsGoodsService
{
    @Autowired
    private LogisticsGoodsMapper logisticsGoodsMapper;

    /**
     * 查询货物信息
     *
     * @param goodsId 货物信息主键
     * @return 货物信息
     */
    @Override
    public LogisticsGoods selectLogisticsGoodsByGoodsId(Long goodsId)
    {
        return logisticsGoodsMapper.selectLogisticsGoodsByGoodsId(goodsId);
    }

    /**
     * 查询货物信息列表
     *
     * @param logisticsGoods 货物信息
     * @return 货物信息
     */
    @Override
    public List<LogisticsGoods> selectLogisticsGoodsList(LogisticsGoods logisticsGoods)
    {
        return logisticsGoodsMapper.selectLogisticsGoodsList(logisticsGoods);
    }

    /**
     * 新增货物信息
     *
     * @param logisticsGoods 货物信息
     * @return 结果
     */
    @Override
    public int insertLogisticsGoods(LogisticsGoods logisticsGoods)
    {
        // 自动生成货物编码：GD + 时间戳
        if (logisticsGoods.getGoodsCode() == null || logisticsGoods.getGoodsCode().isEmpty())
        {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            logisticsGoods.setGoodsCode("GD" + timestamp);
        }
        return logisticsGoodsMapper.insertLogisticsGoods(logisticsGoods);
    }

    /**
     * 修改货物信息
     *
     * @param logisticsGoods 货物信息
     * @return 结果
     */
    @Override
    public int updateLogisticsGoods(LogisticsGoods logisticsGoods)
    {
        return logisticsGoodsMapper.updateLogisticsGoods(logisticsGoods);
    }

    /**
     * 批量删除货物信息
     *
     * @param goodsIds 需要删除的货物信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsGoodsByGoodsIds(Long[] goodsIds)
    {
        return logisticsGoodsMapper.deleteLogisticsGoodsByGoodsIds(goodsIds);
    }

    /**
     * 删除货物信息信息
     *
     * @param goodsId 货物信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsGoodsByGoodsId(Long goodsId)
    {
        return logisticsGoodsMapper.deleteLogisticsGoodsByGoodsId(goodsId);
    }
}
