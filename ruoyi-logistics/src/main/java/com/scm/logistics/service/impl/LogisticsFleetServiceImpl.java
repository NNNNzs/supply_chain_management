package com.scm.logistics.service.impl;

import java.util.List;
import com.scm.common.utils.BaseEntityUtils;
import com.scm.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsFleetMapper;
import com.scm.logistics.domain.LogisticsFleet;
import com.scm.logistics.service.ILogisticsFleetService;

/**
 * 车队信息Service业务层处理
 *
 * @author scm
 * @date 2026-04-15
 */
@Service
public class LogisticsFleetServiceImpl implements ILogisticsFleetService
{
    @Autowired
    private LogisticsFleetMapper logisticsFleetMapper;

    /**
     * 查询车队信息
     *
     * @param fleetId 车队信息主键
     * @return 车队信息
     */
    @Override
    public LogisticsFleet selectLogisticsFleetByFleetId(Long fleetId)
    {
        return logisticsFleetMapper.selectLogisticsFleetByFleetId(fleetId);
    }

    /**
     * 查询车队信息列表
     *
     * @param logisticsFleet 车队信息
     * @return 车队信息
     */
    @Override
    public List<LogisticsFleet> selectLogisticsFleetList(LogisticsFleet logisticsFleet)
    {
        return logisticsFleetMapper.selectLogisticsFleetList(logisticsFleet);
    }

    /**
     * 新增车队信息
     *
     * @param logisticsFleet 车队信息
     * @return 结果
     */
    @Override
    public int insertLogisticsFleet(LogisticsFleet logisticsFleet)
    {
        BaseEntityUtils.fillCreateInfo(logisticsFleet);
        return logisticsFleetMapper.insertLogisticsFleet(logisticsFleet);
    }

    /**
     * 修改车队信息
     *
     * @param logisticsFleet 车队信息
     * @return 结果
     */
    @Override
    public int updateLogisticsFleet(LogisticsFleet logisticsFleet)
    {
        BaseEntityUtils.fillUpdateInfo(logisticsFleet);
        return logisticsFleetMapper.updateLogisticsFleet(logisticsFleet);
    }

    /**
     * 批量删除车队信息
     *
     * @param fleetIds 需要删除的车队信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsFleetByFleetIds(Long[] fleetIds)
    {
        // 检查是否有关联司机
        for (Long fleetId : fleetIds)
        {
            int count = logisticsFleetMapper.countDriversByFleetId(fleetId);
            if (count > 0)
            {
                throw new ServiceException("该车队下还有司机，无法删除");
            }
        }
        return logisticsFleetMapper.deleteLogisticsFleetByFleetIds(fleetIds);
    }

    /**
     * 删除车队信息信息
     *
     * @param fleetId 车队信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsFleetByFleetId(Long fleetId)
    {
        // 检查是否有关联司机
        int count = logisticsFleetMapper.countDriversByFleetId(fleetId);
        if (count > 0)
        {
            throw new ServiceException("该车队下还有司机，无法删除");
        }
        return logisticsFleetMapper.deleteLogisticsFleetByFleetId(fleetId);
    }
}
