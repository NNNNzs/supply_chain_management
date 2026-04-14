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
import com.scm.logistics.domain.LogisticsVehicle;
import com.scm.logistics.service.ILogisticsVehicleService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 车辆信息Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/vehicle")
public class LogisticsVehicleController extends BaseController
{
    @Autowired
    private ILogisticsVehicleService logisticsVehicleService;

    /**
     * 查询车辆信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:vehicle:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsVehicle logisticsVehicle)
    {
        startPage();
        List<LogisticsVehicle> list = logisticsVehicleService.selectLogisticsVehicleList(logisticsVehicle);
        return getDataTable(list);
    }

    /**
     * 导出车辆信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:vehicle:export')")
    @Log(title = "车辆信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsVehicle logisticsVehicle)
    {
        List<LogisticsVehicle> list = logisticsVehicleService.selectLogisticsVehicleList(logisticsVehicle);
        ExcelUtil<LogisticsVehicle> util = new ExcelUtil<LogisticsVehicle>(LogisticsVehicle.class);
        util.exportExcel(response, list, "车辆信息数据");
    }

    /**
     * 获取车辆信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:vehicle:query')")
    @GetMapping(value = "/{vehicleId}")
    public AjaxResult getInfo(@PathVariable("vehicleId") Long vehicleId)
    {
        return success(logisticsVehicleService.selectLogisticsVehicleByVehicleId(vehicleId));
    }

    /**
     * 新增车辆信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:vehicle:add')")
    @Log(title = "车辆信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsVehicle logisticsVehicle)
    {
        return toAjax(logisticsVehicleService.insertLogisticsVehicle(logisticsVehicle));
    }

    /**
     * 修改车辆信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:vehicle:edit')")
    @Log(title = "车辆信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsVehicle logisticsVehicle)
    {
        return toAjax(logisticsVehicleService.updateLogisticsVehicle(logisticsVehicle));
    }

    /**
     * 删除车辆信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:vehicle:remove')")
    @Log(title = "车辆信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{vehicleIds}")
    public AjaxResult remove(@PathVariable Long[] vehicleIds)
    {
        return toAjax(logisticsVehicleService.deleteLogisticsVehicleByVehicleIds(vehicleIds));
    }
}
