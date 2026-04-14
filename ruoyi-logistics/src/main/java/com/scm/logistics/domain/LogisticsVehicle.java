package com.scm.logistics.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.scm.common.annotation.Excel;
import com.scm.common.core.domain.BaseEntity;

/**
 * 车辆信息对象 logistics_vehicle
 *
 * @author scm
 * @date 2026-04-14
 */
public class LogisticsVehicle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 车辆ID */
    private Long vehicleId;

    /** 车牌号 */
    @Excel(name = "车牌号")
    private String vehiclePlate;

    /** 车辆类型 */
    @Excel(name = "车辆类型")
    private String vehicleType;

    /** 车长（米） */
    @Excel(name = "车长")
    private Double vehicleLength;

    /** 载重（吨） */
    @Excel(name = "载重")
    private Double loadCapacity;

    /** 默认司机ID */
    private Long driverId;

    /** 状态（0正常 1维修 2停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=维修,2=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setVehicleId(Long vehicleId)
    {
        this.vehicleId = vehicleId;
    }

    public Long getVehicleId()
    {
        return vehicleId;
    }

    public void setVehiclePlate(String vehiclePlate)
    {
        this.vehiclePlate = vehiclePlate;
    }

    public String getVehiclePlate()
    {
        return vehiclePlate;
    }

    public void setVehicleType(String vehicleType)
    {
        this.vehicleType = vehicleType;
    }

    public String getVehicleType()
    {
        return vehicleType;
    }

    public void setVehicleLength(Double vehicleLength)
    {
        this.vehicleLength = vehicleLength;
    }

    public Double getVehicleLength()
    {
        return vehicleLength;
    }

    public void setLoadCapacity(Double loadCapacity)
    {
        this.loadCapacity = loadCapacity;
    }

    public Double getLoadCapacity()
    {
        return loadCapacity;
    }

    public void setDriverId(Long driverId)
    {
        this.driverId = driverId;
    }

    public Long getDriverId()
    {
        return driverId;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("vehicleId", getVehicleId())
            .append("vehiclePlate", getVehiclePlate())
            .append("vehicleType", getVehicleType())
            .append("vehicleLength", getVehicleLength())
            .append("loadCapacity", getLoadCapacity())
            .append("driverId", getDriverId())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
