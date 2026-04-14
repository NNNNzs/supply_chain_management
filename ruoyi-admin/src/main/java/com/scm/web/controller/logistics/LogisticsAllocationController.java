package com.scm.web.controller.logistics;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.scm.common.annotation.Log;
import com.scm.common.core.controller.BaseController;
import com.scm.common.core.domain.AjaxResult;
import com.scm.common.enums.BusinessType;
import com.scm.logistics.domain.vo.AllocationResultVO;
import com.scm.logistics.domain.vo.BillAllocationItem;
import com.scm.logistics.service.ILogisticsAllocationService;

/**
 * 配载管理Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/allocation")
public class LogisticsAllocationController extends BaseController
{
    @Autowired
    private ILogisticsAllocationService allocationService;

    /**
     * 查询待配载提单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:allocation:list')")
    @GetMapping("/pendingBills")
    public AjaxResult getPendingBills()
    {
        List<BillAllocationItem> bills = allocationService.recommendBills(null);
        return success(bills);
    }

    /**
     * 推荐可配载提单
     */
    @PreAuthorize("@ss.hasPermi('logistics:allocation:list')")
    @GetMapping("/recommendBills")
    public AjaxResult recommendBills(Double loadCapacity)
    {
        List<BillAllocationItem> bills = allocationService.recommendBills(loadCapacity);
        return success(bills);
    }

    /**
     * 创建运单并分配提单（核心功能）
     */
    @PreAuthorize("@ss.hasPermi('logistics:allocation:operate')")
    @Log(title = "配载管理", businessType = BusinessType.INSERT)
    @PostMapping("/createOrder")
    public AjaxResult createOrderWithBills(@Validated @RequestBody AllocationRequest request)
    {
        AllocationResultVO result = allocationService.createOrderWithBills(
            request.getAllocationItems(),
            request.getDriverId(),
            request.getVehicleId(),
            request.getLoadingDate()
        );
        return success(result);
    }

    /**
     * 配载请求DTO
     */
    public static class AllocationRequest
    {
        private List<BillAllocationItem> allocationItems;
        private Long driverId;
        private Long vehicleId;
        private String loadingDate;

        public List<BillAllocationItem> getAllocationItems()
        {
            return allocationItems;
        }

        public void setAllocationItems(List<BillAllocationItem> allocationItems)
        {
            this.allocationItems = allocationItems;
        }

        public Long getDriverId()
        {
            return driverId;
        }

        public void setDriverId(Long driverId)
        {
            this.driverId = driverId;
        }

        public Long getVehicleId()
        {
            return vehicleId;
        }

        public void setVehicleId(Long vehicleId)
        {
            this.vehicleId = vehicleId;
        }

        public String getLoadingDate()
        {
            return loadingDate;
        }

        public void setLoadingDate(String loadingDate)
        {
            this.loadingDate = loadingDate;
        }
    }
}
