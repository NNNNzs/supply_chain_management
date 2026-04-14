package com.scm.logistics.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.logistics.mapper.LogisticsVehicleMapper;
import com.scm.logistics.domain.LogisticsVehicle;
import com.scm.logistics.service.ILogisticsVehicleService;

/**
 * 车辆信息Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsVehicleServiceImpl implements ILogisticsVehicleService
{
    @Autowired
    private LogisticsVehicleMapper logisticsVehicleMapper;

    /**
     * 查询车辆信息
     *
     * @param vehicleId 车辆信息主键
     * @return 车辆信息
     */
    @Override
    public LogisticsVehicle selectLogisticsVehicleByVehicleId(Long vehicleId)
    {
        return logisticsVehicleMapper.selectLogisticsVehicleByVehicleId(vehicleId);
    }

    /**
     * 查询车辆信息列表
     *
     * @param logisticsVehicle 车辆信息
     * @return 车辆信息
     */
    @Override
    public List<LogisticsVehicle> selectLogisticsVehicleList(LogisticsVehicle logisticsVehicle)
    {
        return logisticsVehicleMapper.selectLogisticsVehicleList(logisticsVehicle);
    }

    /**
     * 新增车辆信息
     *
     * @param logisticsVehicle 车辆信息
     * @return 结果
     */
    @Override
    public int insertLogisticsVehicle(LogisticsVehicle logisticsVehicle)
    {
        return logisticsVehicleMapper.insertLogisticsVehicle(logisticsVehicle);
    }

    /**
     * 修改车辆信息
     *
     * @param logisticsVehicle 车辆信息
     * @return 结果
     */
    @Override
    public int updateLogisticsVehicle(LogisticsVehicle logisticsVehicle)
    {
        return logisticsVehicleMapper.updateLogisticsVehicle(logisticsVehicle);
    }

    /**
     * 批量删除车辆信息
     *
     * @param vehicleIds 需要删除的车辆信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsVehicleByVehicleIds(Long[] vehicleIds)
    {
        return logisticsVehicleMapper.deleteLogisticsVehicleByVehicleIds(vehicleIds);
    }

    /**
     * 删除车辆信息信息
     *
     * @param vehicleId 车辆信息主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsVehicleByVehicleId(Long vehicleId)
    {
        return logisticsVehicleMapper.deleteLogisticsVehicleByVehicleId(vehicleId);
    }
}
