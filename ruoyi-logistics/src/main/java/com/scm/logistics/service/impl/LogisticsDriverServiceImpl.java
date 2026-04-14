package com.scm.logistics.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsDriverMapper;
import com.scm.logistics.domain.LogisticsDriver;
import com.scm.logistics.service.ILogisticsDriverService;

/**
 * 司机信息Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsDriverServiceImpl implements ILogisticsDriverService
{
    @Autowired
    private LogisticsDriverMapper logisticsDriverMapper;

    /**
     * 查询司机信息
     *
     * @param driverId 司机信息主键
     * @return 司机信息
     */
    @Override
    public LogisticsDriver selectLogisticsDriverByDriverId(Long driverId)
    {
        return logisticsDriverMapper.selectLogisticsDriverByDriverId(driverId);
    }

    /**
     * 查询司机信息列表
     *
     * @param logisticsDriver 司机信息
     * @return 司机信息
     */
    @Override
    public List<LogisticsDriver> selectLogisticsDriverList(LogisticsDriver logisticsDriver)
    {
        return logisticsDriverMapper.selectLogisticsDriverList(logisticsDriver);
    }

    /**
     * 新增司机信息
     *
     * @param logisticsDriver 司机信息
     * @return 结果
     */
    @Override
    public int insertLogisticsDriver(LogisticsDriver logisticsDriver)
    {
        return logisticsDriverMapper.insertLogisticsDriver(logisticsDriver);
    }

    /**
     * 修改司机信息
     *
     * @param logisticsDriver 司机信息
     * @return 结果
     */
    @Override
    public int updateLogisticsDriver(LogisticsDriver logisticsDriver)
    {
        return logisticsDriverMapper.updateLogisticsDriver(logisticsDriver);
    }

    /**
     * 批量删除司机信息
     *
     * @param driverIds 需要删除的司机信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsDriverByDriverIds(Long[] driverIds)
    {
        return logisticsDriverMapper.deleteLogisticsDriverByDriverIds(driverIds);
    }

    /**
     * 删除司机信息信息
     *
     * @param driverId 司机信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsDriverByDriverId(Long driverId)
    {
        return logisticsDriverMapper.deleteLogisticsDriverByDriverId(driverId);
    }
}
