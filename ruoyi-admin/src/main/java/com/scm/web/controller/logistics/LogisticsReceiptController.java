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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.scm.common.annotation.Log;
import com.scm.common.core.controller.BaseController;
import com.scm.common.core.domain.AjaxResult;
import com.scm.common.enums.BusinessType;
import com.scm.logistics.domain.LogisticsReceipt;
import com.scm.logistics.service.ILogisticsReceiptService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 回单信息Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/receipt")
public class LogisticsReceiptController extends BaseController
{
    @Autowired
    private ILogisticsReceiptService logisticsReceiptService;

    /**
     * 查询回单信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsReceipt logisticsReceipt)
    {
        startPage();
        List<LogisticsReceipt> list = logisticsReceiptService.selectLogisticsReceiptList(logisticsReceipt);
        return getDataTable(list);
    }

    /**
     * 导出回单信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:export')")
    @Log(title = "回单信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsReceipt logisticsReceipt)
    {
        List<LogisticsReceipt> list = logisticsReceiptService.selectLogisticsReceiptList(logisticsReceipt);
        ExcelUtil<LogisticsReceipt> util = new ExcelUtil<LogisticsReceipt>(LogisticsReceipt.class);
        util.exportExcel(response, list, "回单信息数据");
    }

    /**
     * 获取回单信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:query')")
    @GetMapping(value = "/{receiptId}")
    public AjaxResult getInfo(@PathVariable("receiptId") Long receiptId)
    {
        return success(logisticsReceiptService.selectLogisticsReceiptByReceiptId(receiptId));
    }

    /**
     * 根据订单ID查询回单信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:query')")
    @GetMapping(value = "/order/{orderId}")
    public AjaxResult getInfoByOrderId(@PathVariable("orderId") Long orderId)
    {
        return success(logisticsReceiptService.selectLogisticsReceiptByOrderId(orderId));
    }

    /**
     * 新增回单信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:add')")
    @Log(title = "回单信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsReceipt logisticsReceipt)
    {
        return toAjax(logisticsReceiptService.insertLogisticsReceipt(logisticsReceipt));
    }

    /**
     * 修改回单信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:edit')")
    @Log(title = "回单信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsReceipt logisticsReceipt)
    {
        return toAjax(logisticsReceiptService.updateLogisticsReceipt(logisticsReceipt));
    }

    /**
     * 确认回单
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:edit')")
    @Log(title = "回单信息", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{receiptId}")
    public AjaxResult confirm(@PathVariable("receiptId") Long receiptId, @RequestParam("receiver") String receiver)
    {
        return toAjax(logisticsReceiptService.confirmReceipt(receiptId, receiver));
    }

    /**
     * 删除回单信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:receipt:remove')")
    @Log(title = "回单信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{receiptIds}")
    public AjaxResult remove(@PathVariable Long[] receiptIds)
    {
        return toAjax(logisticsReceiptService.deleteLogisticsReceiptByReceiptIds(receiptIds));
    }
}
