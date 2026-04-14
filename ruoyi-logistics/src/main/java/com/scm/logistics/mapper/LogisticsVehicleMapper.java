package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsVehicle;

/**
 * 车辆信息Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsVehicleMapper
{
    /**
     * 查询车辆信息
     *
     * @param vehicleId 车辆信息主键
     * @return 车辆信息
     */
    public LogisticsVehicle selectLogisticsVehicleByVehicleId(Long vehicleId);

    /**
     * 查询车辆信息列表
     *
     * @param logisticsVehicle 车辆信息
     * @return 车辆信息集合
     */
    public List<LogisticsVehicle> selectLogisticsVehicleList(LogisticsVehicle logisticsVehicle);

    /**
     * 新增车辆信息
     *
     * @param logisticsVehicle 车辆信息
     * @return 结果
     */
    public int insertLogisticsVehicle(LogisticsVehicle logisticsVehicle);

    /**
     * 修改车辆信息
     *
     * @param logisticsVehicle 车辆信息
     * @return 结果
     */
    public int updateLogisticsVehicle(LogisticsVehicle logisticsVehicle);

    /**
     * 删除车辆信息
     *
     * @param vehicleId 车辆信息主键
     * @return 结果
     */
    public int deleteLogisticsVehicleByVehicleId(Long vehicleId);

    /**
     * 批量删除车辆信息
     *
     * @param vehicleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsVehicleByVehicleIds(Long[] vehicleIds);
}
