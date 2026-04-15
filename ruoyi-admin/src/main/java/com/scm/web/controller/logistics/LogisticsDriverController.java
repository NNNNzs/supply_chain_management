package com.scm.web.controller.logistics;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.scm.common.annotation.Log;
import com.scm.common.core.controller.BaseController;
import com.scm.common.core.domain.AjaxResult;
import com.scm.common.enums.BusinessType;
import com.scm.logistics.domain.LogisticsDriver;
import com.scm.logistics.domain.LogisticsFleet;
import com.scm.logistics.service.ILogisticsDriverService;
import com.scm.logistics.service.ILogisticsFleetService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 司机/车队管理Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/driver")
public class LogisticsDriverController extends BaseController
{
    @Autowired
    private ILogisticsDriverService logisticsDriverService;

    @Autowired
    private ILogisticsFleetService logisticsFleetService;

    /**
     * 查询司机/车队树形列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:list')")
    @GetMapping("/treeList")
    public AjaxResult treeList(LogisticsDriver driverQuery, LogisticsFleet fleetQuery)
    {
        List<LogisticsFleet> fleetList = logisticsFleetService.selectLogisticsFleetList(fleetQuery);
        List<LogisticsDriver> driverList = logisticsDriverService.selectLogisticsDriverList(driverQuery);

        // 构建树形结构
        List<Object> treeData = buildTreeData(fleetList, driverList);
        return success(treeData);
    }

    /**
     * 查询司机信息列表（平铺列表，用于下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsDriver logisticsDriver)
    {
        startPage();
        List<LogisticsDriver> list = logisticsDriverService.selectLogisticsDriverList(logisticsDriver);
        return getDataTable(list);
    }

    /**
     * 导出司机信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:export')")
    @Log(title = "司机信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsDriver logisticsDriver)
    {
        List<LogisticsDriver> list = logisticsDriverService.selectLogisticsDriverList(logisticsDriver);
        ExcelUtil<LogisticsDriver> util = new ExcelUtil<LogisticsDriver>(LogisticsDriver.class);
        util.exportExcel(response, list, "司机信息数据");
    }

    /**
     * 获取司机详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:query')")
    @GetMapping(value = "/driver/{driverId}")
    public AjaxResult getDriverInfo(@PathVariable("driverId") Long driverId)
    {
        return success(logisticsDriverService.selectLogisticsDriverByDriverId(driverId));
    }

    /**
     * 获取车队详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:query')")
    @GetMapping(value = "/fleet/{fleetId}")
    public AjaxResult getFleetInfo(@PathVariable("fleetId") Long fleetId)
    {
        return success(logisticsFleetService.selectLogisticsFleetByFleetId(fleetId));
    }

    /**
     * 新增司机
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:add')")
    @Log(title = "司机信息", businessType = BusinessType.INSERT)
    @PostMapping("/driver")
    public AjaxResult addDriver(@RequestBody LogisticsDriver logisticsDriver)
    {
        return toAjax(logisticsDriverService.insertLogisticsDriver(logisticsDriver));
    }

    /**
     * 新增车队
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:add')")
    @Log(title = "车队信息", businessType = BusinessType.INSERT)
    @PostMapping("/fleet")
    public AjaxResult addFleet(@RequestBody LogisticsFleet logisticsFleet)
    {
        return toAjax(logisticsFleetService.insertLogisticsFleet(logisticsFleet));
    }

    /**
     * 修改司机
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:edit')")
    @Log(title = "司机信息", businessType = BusinessType.UPDATE)
    @PutMapping("/driver")
    public AjaxResult editDriver(@RequestBody LogisticsDriver logisticsDriver)
    {
        return toAjax(logisticsDriverService.updateLogisticsDriver(logisticsDriver));
    }

    /**
     * 修改车队
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:edit')")
    @Log(title = "车队信息", businessType = BusinessType.UPDATE)
    @PutMapping("/fleet")
    public AjaxResult editFleet(@RequestBody LogisticsFleet logisticsFleet)
    {
        return toAjax(logisticsFleetService.updateLogisticsFleet(logisticsFleet));
    }

    /**
     * 删除司机
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:remove')")
    @Log(title = "司机信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/driver/{driverIds}")
    public AjaxResult removeDriver(@PathVariable Long[] driverIds)
    {
        return toAjax(logisticsDriverService.deleteLogisticsDriverByDriverIds(driverIds));
    }

    /**
     * 删除车队
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:remove')")
    @Log(title = "车队信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/fleet/{fleetIds}")
    public AjaxResult removeFleet(@PathVariable Long[] fleetIds)
    {
        return toAjax(logisticsFleetService.deleteLogisticsFleetByFleetIds(fleetIds));
    }

    /**
     * 获取所有可用车队（下拉选择用）
     */
    @GetMapping("/fleet/all")
    public AjaxResult getAllFleets()
    {
        LogisticsFleet query = new LogisticsFleet();
        query.setStatus("0");
        List<LogisticsFleet> list = logisticsFleetService.selectLogisticsFleetList(query);
        return success(list);
    }

    /**
     * 获取所有可用司机（下拉选择用）
     */
    @GetMapping("/all")
    public AjaxResult getAllDrivers()
    {
        LogisticsDriver query = new LogisticsDriver();
        query.setStatus("0");
        List<LogisticsDriver> list = logisticsDriverService.selectLogisticsDriverList(query);
        return success(list);
    }

    /**
     * 构建树形数据
     */
    private List<Object> buildTreeData(List<LogisticsFleet> fleetList, List<LogisticsDriver> driverList)
    {
        List<Object> result = new java.util.ArrayList<>();

        // 添加车队及其司机
        for (LogisticsFleet fleet : fleetList)
        {
            java.util.Map<String, Object> fleetNode = new java.util.HashMap<>();
            fleetNode.put("id", "F" + fleet.getFleetId());
            fleetNode.put("type", "fleet");
            fleetNode.put("fleetId", fleet.getFleetId());
            fleetNode.put("fleetName", fleet.getFleetName());
            fleetNode.put("ownerName", fleet.getOwnerName());
            fleetNode.put("ownerPhone", fleet.getOwnerPhone());
            fleetNode.put("accountName", fleet.getAccountName());
            fleetNode.put("accountNumber", fleet.getAccountNumber());
            fleetNode.put("bankName", fleet.getBankName());
            fleetNode.put("status", fleet.getStatus());
            fleetNode.put("remark", fleet.getRemark());

            // 查找该车队的司机
            List<LogisticsDriver> fleetDrivers = new java.util.ArrayList<>();
            for (LogisticsDriver driver : driverList)
            {
                if ("fleet".equals(driver.getDriverType()) && fleet.getFleetId().equals(driver.getFleetId()))
                {
                    fleetDrivers.add(driver);
                }
            }
            fleetNode.put("children", fleetDrivers);
            fleetNode.put("driverCount", fleetDrivers.size());

            result.add(fleetNode);
        }

        // 添加个人司机
        for (LogisticsDriver driver : driverList)
        {
            if ("individual".equals(driver.getDriverType()))
            {
                java.util.Map<String, Object> driverNode = new java.util.HashMap<>();
                driverNode.put("id", "D" + driver.getDriverId());
                driverNode.put("type", "driver");
                driverNode.put("driverId", driver.getDriverId());
                driverNode.put("driverName", driver.getDriverName());
                driverNode.put("driverPhone", driver.getDriverPhone());
                driverNode.put("vehiclePlate", driver.getVehiclePlate());
                driverNode.put("bankAccount", driver.getBankAccount());
                driverNode.put("bankName", driver.getBankName());
                driverNode.put("accountName", driver.getAccountName());
                driverNode.put("status", driver.getStatus());
                driverNode.put("remark", driver.getRemark());
                result.add(driverNode);
            }
        }

        return result;
    }
}
