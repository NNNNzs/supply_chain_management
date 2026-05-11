package com.scm.logistics.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.scm.logistics.domain.LogisticsGoods;

/**
 * 货物信息Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsGoodsMapper
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
     * 删除货物信息
     *
     * @param goodsId 货物信息主键
     * @return 结果
     */
    public int deleteLogisticsGoodsByGoodsId(Long goodsId);

    /**
     * 批量删除货物信息
     *
     * @param goodsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsGoodsByGoodsIds(Long[] goodsIds);

    /**
     * 按编码和型号查询货物（排除指定ID）
     *
     * @param goodsCode 货物编码
     * @param goodsModel 货物型号
     * @param excludeGoodsId 排除的货物ID（编辑时排除自身）
     * @return 货物信息
     */
    public LogisticsGoods selectByCodeAndModel(@Param("goodsCode") String goodsCode, @Param("goodsModel") String goodsModel, @Param("excludeGoodsId") Long excludeGoodsId);
}
