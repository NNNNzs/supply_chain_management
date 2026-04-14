package com.scm.logistics.service;

import com.scm.logistics.domain.vo.AllocationResultVO;
import com.scm.logistics.domain.vo.BillAllocationItem;
import java.util.List;

/**
 * 配载管理Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsAllocationService
{
    /**
     * 创建运单并分配提单（核心功能）
     * 支持提单拆分和配载
     *
     * @param allocationItems 提单分配项列表
     * @param driverId 司机ID
     * @param vehicleId 车辆ID
     * @param loadingDate 装车日期
     * @return 配载结果
     */
    public AllocationResultVO createOrderWithBills(List<BillAllocationItem> allocationItems, Long driverId, Long vehicleId, String loadingDate);

    /**
     * 推荐可配载的提单
     * 根据车辆载重推荐合适的提单组合
     *
     * @param loadCapacity 车辆载重
     * @return 提单列表
     */
    public List<BillAllocationItem> recommendBills(Double loadCapacity);
}
