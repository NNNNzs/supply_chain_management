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
import com.scm.logistics.domain.LogisticsSettlement;
import com.scm.logistics.service.ILogisticsSettlementService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 财务结算Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/settlement")
public class LogisticsSettlementController extends BaseController
{
    @Autowired
    private ILogisticsSettlementService logisticsSettlementService;

    /**
     * 查询财务结算列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:settlement:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsSettlement logisticsSettlement)
    {
        startPage();
        List<LogisticsSettlement> list = logisticsSettlementService.selectLogisticsSettlementList(logisticsSettlement);
        return getDataTable(list);
    }

    /**
     * 导出财务结算列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:settlement:export')")
    @Log(title = "财务结算", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsSettlement logisticsSettlement)
    {
        List<LogisticsSettlement> list = logisticsSettlementService.selectLogisticsSettlementList(logisticsSettlement);
        ExcelUtil<LogisticsSettlement> util = new ExcelUtil<LogisticsSettlement>(LogisticsSettlement.class);
        util.exportExcel(response, list, "财务结算数据");
    }

    /**
     * 获取财务结算详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:settlement:query')")
    @GetMapping(value = "/{settlementId}")
    public AjaxResult getInfo(@PathVariable("settlementId") Long settlementId)
    {
        return success(logisticsSettlementService.selectLogisticsSettlementBySettlementId(settlementId));
    }

    /**
     * 新增财务结算
     */
    @PreAuthorize("@ss.hasPermi('logistics:settlement:add')")
    @Log(title = "财务结算", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsSettlement logisticsSettlement)
    {
        return toAjax(logisticsSettlementService.insertLogisticsSettlement(logisticsSettlement));
    }

    /**
     * 修改财务结算
     */
    @PreAuthorize("@ss.hasPermi('logistics:settlement:edit')")
    @Log(title = "财务结算", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsSettlement logisticsSettlement)
    {
        return toAjax(logisticsSettlementService.updateLogisticsSettlement(logisticsSettlement));
    }

    /**
     * 删除财务结算
     */
    @PreAuthorize("@ss.hasPermi('logistics:settlement:remove')")
    @Log(title = "财务结算", businessType = BusinessType.DELETE)
	@DeleteMapping("/{settlementIds}")
    public AjaxResult remove(@PathVariable Long[] settlementIds)
    {
        return toAjax(logisticsSettlementService.deleteLogisticsSettlementBySettlementIds(settlementIds));
    }
}
