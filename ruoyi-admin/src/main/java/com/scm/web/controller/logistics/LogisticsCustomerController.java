package com.scm.web.controller.logistics;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.scm.common.annotation.Log;
import com.scm.common.core.controller.BaseController;
import com.scm.common.core.domain.AjaxResult;
import com.scm.common.core.page.TableDataInfo;
import com.scm.common.enums.BusinessType;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.logistics.domain.LogisticsCustomer;
import com.scm.logistics.service.ILogisticsCustomerService;

/**
 * 客户信息Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/customer")
public class LogisticsCustomerController extends BaseController
{
    @Autowired
    private ILogisticsCustomerService customerService;

    /**
     * 查询客户信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:customer:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsCustomer customer)
    {
        startPage();
        List<LogisticsCustomer> list = customerService.selectCustomerList(customer);
        return getDataTable(list);
    }

    /**
     * 导出客户信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:customer:export')")
    @Log(title = "客户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsCustomer customer)
    {
        List<LogisticsCustomer> list = customerService.selectCustomerList(customer);
        ExcelUtil<LogisticsCustomer> util = new ExcelUtil<LogisticsCustomer>(LogisticsCustomer.class);
        util.exportExcel(response, list, "客户信息数据");
    }

    /**
     * 获取客户信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:customer:query')")
    @GetMapping(value = "/{customerId}")
    public AjaxResult getInfo(@PathVariable("customerId") Long customerId)
    {
        return success(customerService.selectCustomerById(customerId));
    }

    /**
     * 新增客户信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:customer:add')")
    @Log(title = "客户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LogisticsCustomer customer)
    {
        return toAjax(customerService.insertCustomer(customer));
    }

    /**
     * 修改客户信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:customer:edit')")
    @Log(title = "客户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LogisticsCustomer customer)
    {
        return toAjax(customerService.updateCustomer(customer));
    }

    /**
     * 删除客户信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:customer:remove')")
    @Log(title = "客户信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds)
    {
        return toAjax(customerService.deleteCustomerByIds(customerIds));
    }
}
