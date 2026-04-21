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
import com.scm.logistics.domain.LogisticsOrderLog;
import com.scm.logistics.service.ILogisticsOrderLogService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 订单操作日志Controller
 *
 * @author scm
 * @date 2026-04-21
 */
@RestController
@RequestMapping("/logistics/orderLog")
public class LogisticsOrderLogController extends BaseController
{
    @Autowired
    private ILogisticsOrderLogService orderLogService;

    /**
     * 查询订单操作日志列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsOrderLog orderLog)
    {
        startPage();
        List<LogisticsOrderLog> list = orderLogService.selectOrderLogList(orderLog);
        return getDataTable(list);
    }

    /**
     * 根据订单ID查询操作日志列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:list')")
    @GetMapping("/order/{orderId}")
    public AjaxResult getByOrderId(@PathVariable("orderId") Long orderId)
    {
        List<LogisticsOrderLog> list = orderLogService.selectOrderLogByOrderId(orderId);
        return success(list);
    }

    /**
     * 导出订单操作日志列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:export')")
    @Log(title = "订单操作日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsOrderLog orderLog)
    {
        List<LogisticsOrderLog> list = orderLogService.selectOrderLogList(orderLog);
        ExcelUtil<LogisticsOrderLog> util = new ExcelUtil<LogisticsOrderLog>(LogisticsOrderLog.class);
        util.exportExcel(response, list, "订单操作日志数据");
    }

    /**
     * 获取订单操作日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(orderLogService.selectOrderLogById(logId));
    }

    /**
     * 新增订单操作日志
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:add')")
    @Log(title = "订单操作日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsOrderLog orderLog)
    {
        return toAjax(orderLogService.insertOrderLog(orderLog));
    }

    /**
     * 修改订单操作日志
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:edit')")
    @Log(title = "订单操作日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsOrderLog orderLog)
    {
        return toAjax(orderLogService.insertOrderLog(orderLog));
    }

    /**
     * 删除订单操作日志
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:remove')")
    @Log(title = "订单操作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logId}")
    public AjaxResult remove(@PathVariable Long logId)
    {
        return toAjax(orderLogService.deleteOrderLogById(logId));
    }
}
