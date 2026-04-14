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
import com.scm.logistics.domain.LogisticsDriverOrder;
import com.scm.logistics.service.ILogisticsDriverOrderService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;
import com.scm.common.utils.SecurityUtils;

/**
 * 驾驶员单Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/driverOrder")
public class LogisticsDriverOrderController extends BaseController
{
    @Autowired
    private ILogisticsDriverOrderService logisticsDriverOrderService;

    /**
     * 查询驾驶员单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:driverOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsDriverOrder logisticsDriverOrder)
    {
        startPage();
        List<LogisticsDriverOrder> list = logisticsDriverOrderService.selectLogisticsDriverOrderList(logisticsDriverOrder);
        return getDataTable(list);
    }

    /**
     * 导出驾驶员单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:driverOrder:export')")
    @Log(title = "驾驶员单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsDriverOrder logisticsDriverOrder)
    {
        List<LogisticsDriverOrder> list = logisticsDriverOrderService.selectLogisticsDriverOrderList(logisticsDriverOrder);
        ExcelUtil<LogisticsDriverOrder> util = new ExcelUtil<LogisticsDriverOrder>(LogisticsDriverOrder.class);
        util.exportExcel(response, list, "驾驶员单数据");
    }

    /**
     * 获取驾驶员单详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:driverOrder:query')")
    @GetMapping(value = "/{driverOrderId}")
    public AjaxResult getInfo(@PathVariable("driverOrderId") Long driverOrderId)
    {
        return success(logisticsDriverOrderService.selectLogisticsDriverOrderByDriverOrderId(driverOrderId));
    }

    /**
     * 新增驾驶员单
     */
    @PreAuthorize("@ss.hasPermi('logistics:driverOrder:add')")
    @Log(title = "驾驶员单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsDriverOrder logisticsDriverOrder)
    {
        logisticsDriverOrder.setCreateBy(SecurityUtils.getUsername());
        return toAjax(logisticsDriverOrderService.insertLogisticsDriverOrder(logisticsDriverOrder));
    }

    /**
     * 修改驾驶员单
     */
    @PreAuthorize("@ss.hasPermi('logistics:driverOrder:edit')")
    @Log(title = "驾驶员单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsDriverOrder logisticsDriverOrder)
    {
        logisticsDriverOrder.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(logisticsDriverOrderService.updateLogisticsDriverOrder(logisticsDriverOrder));
    }

    /**
     * 删除驾驶员单
     */
    @PreAuthorize("@ss.hasPermi('logistics:driverOrder:remove')")
    @Log(title = "驾驶员单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{driverOrderIds}")
    public AjaxResult remove(@PathVariable Long[] driverOrderIds)
    {
        return toAjax(logisticsDriverOrderService.deleteLogisticsDriverOrderByDriverOrderIds(driverOrderIds));
    }

    /**
     * 更新驾驶员单状态
     */
    @PreAuthorize("@ss.hasPermi('logistics:driverOrder:edit')")
    @Log(title = "驾驶员单", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{driverOrderId}")
    public AjaxResult changeStatus(@PathVariable("driverOrderId") Long driverOrderId, @RequestBody LogisticsDriverOrder driverOrder)
    {
        LogisticsDriverOrder updateDriverOrder = new LogisticsDriverOrder();
        updateDriverOrder.setDriverOrderId(driverOrderId);
        updateDriverOrder.setDispatchStatus(driverOrder.getDispatchStatus());
        return toAjax(logisticsDriverOrderService.updateDriverOrderStatus(driverOrderId, driverOrder.getDispatchStatus()));
    }
}
