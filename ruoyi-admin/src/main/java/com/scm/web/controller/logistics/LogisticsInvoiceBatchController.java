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
import com.scm.logistics.domain.LogisticsInvoiceBatch;
import com.scm.logistics.service.ILogisticsInvoiceBatchService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 发票批次Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/invoiceBatch")
public class LogisticsInvoiceBatchController extends BaseController
{
    @Autowired
    private ILogisticsInvoiceBatchService logisticsInvoiceBatchService;

    /**
     * 查询发票批次列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        startPage();
        List<LogisticsInvoiceBatch> list = logisticsInvoiceBatchService.selectLogisticsInvoiceBatchList(logisticsInvoiceBatch);
        return getDataTable(list);
    }

    /**
     * 导出发票批次列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:export')")
    @Log(title = "发票批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        List<LogisticsInvoiceBatch> list = logisticsInvoiceBatchService.selectLogisticsInvoiceBatchList(logisticsInvoiceBatch);
        ExcelUtil<LogisticsInvoiceBatch> util = new ExcelUtil<LogisticsInvoiceBatch>(LogisticsInvoiceBatch.class);
        util.exportExcel(response, list, "发票批次数据");
    }

    /**
     * 获取发票批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId)
    {
        return success(logisticsInvoiceBatchService.selectLogisticsInvoiceBatchByBatchId(batchId));
    }

    /**
     * 新增发票批次
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:merge')")
    @Log(title = "发票批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        return toAjax(logisticsInvoiceBatchService.insertLogisticsInvoiceBatch(logisticsInvoiceBatch));
    }

    /**
     * 修改发票批次
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:edit')")
    @Log(title = "发票批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        return toAjax(logisticsInvoiceBatchService.updateLogisticsInvoiceBatch(logisticsInvoiceBatch));
    }

    /**
     * 删除发票批次
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:remove')")
    @Log(title = "发票批次", businessType = BusinessType.DELETE)
	@DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds)
    {
        return toAjax(logisticsInvoiceBatchService.deleteLogisticsInvoiceBatchByBatchIds(batchIds));
    }
}
