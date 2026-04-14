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
import org.springframework.web.multipart.MultipartFile;
import com.scm.common.annotation.Log;
import com.scm.common.core.controller.BaseController;
import com.scm.common.core.domain.AjaxResult;
import com.scm.common.enums.BusinessType;
import com.scm.logistics.domain.LogisticsBill;
import com.scm.logistics.service.ILogisticsBillService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;
import com.scm.common.utils.SecurityUtils;

/**
 * 提单Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/bill")
public class LogisticsBillController extends BaseController
{
    @Autowired
    private ILogisticsBillService logisticsBillService;

    /**
     * 查询提单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:bill:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsBill logisticsBill)
    {
        startPage();
        List<LogisticsBill> list = logisticsBillService.selectLogisticsBillList(logisticsBill);
        return getDataTable(list);
    }

    /**
     * 导出提单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:bill:export')")
    @Log(title = "提单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsBill logisticsBill)
    {
        List<LogisticsBill> list = logisticsBillService.selectLogisticsBillList(logisticsBill);
        ExcelUtil<LogisticsBill> util = new ExcelUtil<LogisticsBill>(LogisticsBill.class);
        util.exportExcel(response, list, "提单数据");
    }

    /**
     * 获取提单详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:bill:query')")
    @GetMapping(value = "/{billId}")
    public AjaxResult getInfo(@PathVariable("billId") Long billId)
    {
        return success(logisticsBillService.selectLogisticsBillByBillId(billId));
    }

    /**
     * 新增提单
     */
    @PreAuthorize("@ss.hasPermi('logistics:bill:add')")
    @Log(title = "提单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsBill logisticsBill)
    {
        logisticsBill.setCreateBy(SecurityUtils.getUsername());
        return toAjax(logisticsBillService.insertLogisticsBill(logisticsBill));
    }

    /**
     * 修改提单
     */
    @PreAuthorize("@ss.hasPermi('logistics:bill:edit')")
    @Log(title = "提单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsBill logisticsBill)
    {
        logisticsBill.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(logisticsBillService.updateLogisticsBill(logisticsBill));
    }

    /**
     * 删除提单
     */
    @PreAuthorize("@ss.hasPermi('logistics:bill:remove')")
    @Log(title = "提单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{billIds}")
    public AjaxResult remove(@PathVariable Long[] billIds)
    {
        return toAjax(logisticsBillService.deleteLogisticsBillByBillIds(billIds));
    }

    /**
     * 查询待配载提单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:allocation:operate')")
    @GetMapping("/pending")
    public AjaxResult getPendingBills()
    {
        List<LogisticsBill> list = logisticsBillService.selectPendingBills();
        return success(list);
    }

    /**
     * 查询提单分配明细
     */
    @PreAuthorize("@ss.hasPermi('logistics:bill:query')")
    @GetMapping("/{billId}/allocations")
    public AjaxResult getBillAllocations(@PathVariable("billId") Long billId)
    {
        List<Object> list = logisticsBillService.selectBillAllocations(billId);
        return success(list);
    }
}
