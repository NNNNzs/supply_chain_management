package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsSettlement;

/**
 * 财务结算Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsSettlementService
{
    /**
     * 查询财务结算
     *
     * @param settlementId 财务结算主键
     * @return 财务结算
     */
    public LogisticsSettlement selectLogisticsSettlementBySettlementId(Long settlementId);

    /**
     * 查询财务结算列表
     *
     * @param logisticsSettlement 财务结算
     * @return 财务结算集合
     */
    public List<LogisticsSettlement> selectLogisticsSettlementList(LogisticsSettlement logisticsSettlement);

    /**
     * 新增财务结算
     *
     * @param logisticsSettlement 财务结算
     * @return 结果
     */
    public int insertLogisticsSettlement(LogisticsSettlement logisticsSettlement);

    /**
     * 修改财务结算
     *
     * @param logisticsSettlement 财务结算
     * @return 结果
     */
    public int updateLogisticsSettlement(LogisticsSettlement logisticsSettlement);

    /**
     * 批量删除财务结算
     *
     * @param settlementIds 需要删除的财务结算主键集合
     * @return 结果
     */
    public int deleteLogisticsSettlementBySettlementIds(Long[] settlementIds);

    /**
     * 删除财务结算信息
     *
     * @param settlementId 财务结算主键
     * @return 结果
     */
    public int deleteLogisticsSettlementBySettlementId(Long settlementId);
}
