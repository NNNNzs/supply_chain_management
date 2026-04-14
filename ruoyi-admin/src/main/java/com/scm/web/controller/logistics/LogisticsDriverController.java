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
import com.scm.logistics.service.ILogisticsDriverService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 司机信息Controller
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

    /**
     * 查询司机信息列表
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
     * 获取司机信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:query')")
    @GetMapping(value = "/{driverId}")
    public AjaxResult getInfo(@PathVariable("driverId") Long driverId)
    {
        return success(logisticsDriverService.selectLogisticsDriverByDriverId(driverId));
    }

    /**
     * 新增司机信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:add')")
    @Log(title = "司机信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsDriver logisticsDriver)
    {
        return toAjax(logisticsDriverService.insertLogisticsDriver(logisticsDriver));
    }

    /**
     * 修改司机信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:edit')")
    @Log(title = "司机信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsDriver logisticsDriver)
    {
        return toAjax(logisticsDriverService.updateLogisticsDriver(logisticsDriver));
    }

    /**
     * 删除司机信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:driver:remove')")
    @Log(title = "司机信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{driverIds}")
    public AjaxResult remove(@PathVariable Long[] driverIds)
    {
        return toAjax(logisticsDriverService.deleteLogisticsDriverByDriverIds(driverIds));
    }
}
