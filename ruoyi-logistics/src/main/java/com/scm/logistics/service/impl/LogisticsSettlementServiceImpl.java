package com.scm.logistics.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsSettlementMapper;
import com.scm.logistics.domain.LogisticsSettlement;
import com.scm.logistics.service.ILogisticsSettlementService;

/**
 * 财务结算Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsSettlementServiceImpl implements ILogisticsSettlementService
{
    @Autowired
    private LogisticsSettlementMapper logisticsSettlementMapper;

    /**
     * 查询财务结算
     *
     * @param settlementId 财务结算主键
     * @return 财务结算
     */
    @Override
    public LogisticsSettlement selectLogisticsSettlementBySettlementId(Long settlementId)
    {
        return logisticsSettlementMapper.selectLogisticsSettlementBySettlementId(settlementId);
    }

    /**
     * 查询财务结算列表
     *
     * @param logisticsSettlement 财务结算
     * @return 财务结算
     */
    @Override
    public List<LogisticsSettlement> selectLogisticsSettlementList(LogisticsSettlement logisticsSettlement)
    {
        return logisticsSettlementMapper.selectLogisticsSettlementList(logisticsSettlement);
    }

    /**
     * 新增财务结算
     *
     * @param logisticsSettlement 财务结算
     * @return 结果
     */
    @Override
    public int insertLogisticsSettlement(LogisticsSettlement logisticsSettlement)
    {
        return logisticsSettlementMapper.insertLogisticsSettlement(logisticsSettlement);
    }

    /**
     * 修改财务结算
     *
     * @param logisticsSettlement 财务结算
     * @return 结果
     */
    @Override
    public int updateLogisticsSettlement(LogisticsSettlement logisticsSettlement)
    {
        return logisticsSettlementMapper.updateLogisticsSettlement(logisticsSettlement);
    }

    /**
     * 批量删除财务结算
     *
     * @param settlementIds 需要删除的财务结算主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsSettlementBySettlementIds(Long[] settlementIds)
    {
        return logisticsSettlementMapper.deleteLogisticsSettlementBySettlementIds(settlementIds);
    }

    /**
     * 删除财务结算信息
     *
     * @param settlementId 财务结算主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsSettlementBySettlementId(Long settlementId)
    {
        return logisticsSettlementMapper.deleteLogisticsSettlementBySettlementId(settlementId);
    }
}
