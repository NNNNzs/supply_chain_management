package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsFleet;

/**
 * 车队信息Service接口
 *
 * @author scm
 * @date 2026-04-15
 */
public interface ILogisticsFleetService
{
    /**
     * 查询车队信息
     *
     * @param fleetId 车队信息主键
     * @return 车队信息
     */
    public LogisticsFleet selectLogisticsFleetByFleetId(Long fleetId);

    /**
     * 查询车队信息列表
     *
     * @param logisticsFleet 车队信息
     * @return 车队信息集合
     */
    public List<LogisticsFleet> selectLogisticsFleetList(LogisticsFleet logisticsFleet);

    /**
     * 新增车队信息
     *
     * @param logisticsFleet 车队信息
     * @return 结果
     */
    public int insertLogisticsFleet(LogisticsFleet logisticsFleet);

    /**
     * 修改车队信息
     *
     * @param logisticsFleet 车队信息
     * @return 结果
     */
    public int updateLogisticsFleet(LogisticsFleet logisticsFleet);

    /**
     * 批量删除车队信息
     *
     * @param fleetIds 需要删除的车队信息主键集合
     * @return 结果
     */
    public int deleteLogisticsFleetByFleetIds(Long[] fleetIds);

    /**
     * 删除车队信息信息
     *
     * @param fleetId 车队信息主键
     * @return 结果
     */
    public int deleteLogisticsFleetByFleetId(Long fleetId);
}
