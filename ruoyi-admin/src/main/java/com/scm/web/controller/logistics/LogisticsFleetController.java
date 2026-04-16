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
import com.scm.logistics.domain.LogisticsFleet;
import com.scm.logistics.service.ILogisticsFleetService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 车队信息Controller
 *
 * @author scm
 * @date 2026-04-15
 */
@RestController
@RequestMapping("/logistics/fleet")
public class LogisticsFleetController extends BaseController
{
    @Autowired
    private ILogisticsFleetService logisticsFleetService;

    /**
     * 查询车队信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:fleet:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsFleet logisticsFleet)
    {
        startPage();
        List<LogisticsFleet> list = logisticsFleetService.selectLogisticsFleetList(logisticsFleet);
        return getDataTable(list);
    }

    /**
     * 导出车队信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:fleet:export')")
    @Log(title = "车队信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsFleet logisticsFleet)
    {
        List<LogisticsFleet> list = logisticsFleetService.selectLogisticsFleetList(logisticsFleet);
        ExcelUtil<LogisticsFleet> util = new ExcelUtil<LogisticsFleet>(LogisticsFleet.class);
        util.exportExcel(response, list, "车队信息数据");
    }

    /**
     * 获取车队信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:fleet:query')")
    @GetMapping(value = "/{fleetId}")
    public AjaxResult getInfo(@PathVariable("fleetId") Long fleetId)
    {
        return success(logisticsFleetService.selectLogisticsFleetByFleetId(fleetId));
    }

    /**
     * 新增车队信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:fleet:add')")
    @Log(title = "车队信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsFleet logisticsFleet)
    {
        return toAjax(logisticsFleetService.insertLogisticsFleet(logisticsFleet));
    }

    /**
     * 修改车队信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:fleet:edit')")
    @Log(title = "车队信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsFleet logisticsFleet)
    {
        return toAjax(logisticsFleetService.updateLogisticsFleet(logisticsFleet));
    }

    /**
     * 删除车队信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:fleet:remove')")
    @Log(title = "车队信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{fleetIds}")
    public AjaxResult remove(@PathVariable Long[] fleetIds)
    {
        return toAjax(logisticsFleetService.deleteLogisticsFleetByFleetIds(fleetIds));
    }

    /**
     * 获取所有可用车队（下拉选择用）
     */
    @GetMapping("/all")
    public AjaxResult getAllFleets()
    {
        LogisticsFleet query = new LogisticsFleet();
        query.setStatus("0");
        List<LogisticsFleet> list = logisticsFleetService.selectLogisticsFleetList(query);
        return success(list);
    }
}
