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
import org.springframework.web.multipart.MultipartFile;
import com.scm.common.annotation.Log;
import com.scm.common.core.controller.BaseController;
import com.scm.common.core.domain.AjaxResult;
import com.scm.common.core.page.TableDataInfo;
import com.scm.common.enums.BusinessType;
import com.scm.common.exception.ServiceException;
import com.scm.common.utils.StringUtils;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.logistics.domain.LogisticsOrder;
import com.scm.logistics.service.ILogisticsOrderService;

/**
 * 运输订单Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/order")
public class LogisticsOrderController extends BaseController
{
    @Autowired
    private ILogisticsOrderService orderService;

    /**
     * 查询运输订单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsOrder order)
    {
        startPage();
        List<LogisticsOrder> list = orderService.selectOrderList(order);
        return getDataTable(list);
    }

    /**
     * 导出运输订单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:export')")
    @Log(title = "运输订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsOrder order)
    {
        List<LogisticsOrder> list = orderService.selectOrderList(order);
        ExcelUtil<LogisticsOrder> util = new ExcelUtil<LogisticsOrder>(LogisticsOrder.class);
        util.exportExcel(response, list, "运输订单数据");
    }

    /**
     * 获取运输订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(orderService.selectOrderById(orderId));
    }

    /**
     * 新增运输订单
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:add')")
    @Log(title = "运输订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LogisticsOrder order)
    {
        return toAjax(orderService.insertOrder(order));
    }

    /**
     * 修改运输订单
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:edit')")
    @Log(title = "运输订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LogisticsOrder order)
    {
        return toAjax(orderService.updateOrder(order));
    }

    /**
     * 更改订单状态
     * 注意：必须放在 deleteMapping 之前，避免路径冲突
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:edit')")
    @Log(title = "运输订单", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus/{orderId}")
    public AjaxResult changeStatus(@PathVariable("orderId") Long orderId, @RequestBody LogisticsOrder order)
    {
        // 使用专门的更改状态方法，避免触发订单号生成等逻辑
        return toAjax(orderService.changeOrderStatus(orderId, order.getOrderStatus()));
    }

    /**
     * 删除运输订单
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:remove')")
    @Log(title = "运输订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(orderService.deleteOrderByIds(orderIds));
    }

    /**
     * 导入订单
     */
    @PreAuthorize("@ss.hasPermi('logistics:order:import')")
    @Log(title = "运输订单", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<LogisticsOrder> util = new ExcelUtil<LogisticsOrder>(LogisticsOrder.class);
        List<LogisticsOrder> orderList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = "导入成功";
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (LogisticsOrder order : orderList)
        {
            try
            {
                // 生成订单号
                if (StringUtils.isEmpty(order.getOrderNo()))
                {
                    orderService.generateOrderNo(order);
                }
                // 计算金额
                orderService.calculateAmount(order);
                orderService.insertOrder(order);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、订单号 ").append(order.getOrderNo()).append(" 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、订单导入失败：";
                failureMsg.append(msg).append(e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条");
        }
        return success(successMsg.toString());
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<LogisticsOrder> util = new ExcelUtil<LogisticsOrder>(LogisticsOrder.class);
        util.importTemplateExcel(response, "运输订单数据");
    }

    /**
     * 查询装货地址列表（用于自动完成）
     */
    @GetMapping("/loadingAddresses")
    public AjaxResult getLoadingAddresses(String keyword)
    {
        List<java.util.Map<String, Object>> list = orderService.selectLoadingAddressList(keyword);
        return success(list);
    }

    /**
     * 查询卸货地址列表（用于自动完成）
     */
    @GetMapping("/unloadingAddresses")
    public AjaxResult getUnloadingAddresses(String keyword)
    {
        List<java.util.Map<String, Object>> list = orderService.selectUnloadingAddressList(keyword);
        return success(list);
    }

    /**
     * 查询所有地址列表（装货和卸货合并，用于自动完成）
     */
    @GetMapping("/addresses")
    public AjaxResult getAddresses(String keyword)
    {
        List<java.util.Map<String, Object>> list = orderService.selectAllAddressList(keyword);
        return success(list);
    }
}
