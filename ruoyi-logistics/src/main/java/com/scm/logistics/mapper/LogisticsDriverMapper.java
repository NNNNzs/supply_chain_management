package com.scm.logistics.mapper;

import java.util.List;
import com.scm.logistics.domain.LogisticsDriver;

/**
 * 司机信息Mapper接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface LogisticsDriverMapper
{
    /**
     * 查询司机信息
     *
     * @param driverId 司机信息主键
     * @return 司机信息
     */
    public LogisticsDriver selectLogisticsDriverByDriverId(Long driverId);

    /**
     * 查询司机信息列表
     *
     * @param logisticsDriver 司机信息
     * @return 司机信息集合
     */
    public List<LogisticsDriver> selectLogisticsDriverList(LogisticsDriver logisticsDriver);

    /**
     * 新增司机信息
     *
     * @param logisticsDriver 司机信息
     * @return 结果
     */
    public int insertLogisticsDriver(LogisticsDriver logisticsDriver);

    /**
     * 修改司机信息
     *
     * @param logisticsDriver 司机信息
     * @return 结果
     */
    public int updateLogisticsDriver(LogisticsDriver logisticsDriver);

    /**
     * 删除司机信息
     *
     * @param driverId 司机信息主键
     * @return 结果
     */
    public int deleteLogisticsDriverByDriverId(Long driverId);

    /**
     * 批量删除司机信息
     *
     * @param driverIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogisticsDriverByDriverIds(Long[] driverIds);
}
