package com.scm.logistics.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.scm.logistics.domain.LogisticsOrderGoods;
import com.scm.logistics.service.ILogisticsOrderGoodsService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 订单货物明细Controller
 *
 * @author scm
 * @date 2026-04-15
 */
@RestController
@RequestMapping("/logistics/orderGoods")
public class LogisticsOrderGoodsController extends BaseController
{
    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    /**
     * 查询订单货物明细列表
     */
    @GetMapping("/list")
    public TableDataInfo list(LogisticsOrderGoods logisticsOrderGoods)
    {
        startPage();
        List<LogisticsOrderGoods> list = logisticsOrderGoodsService.selectLogisticsOrderGoodsList(logisticsOrderGoods);
        return getDataTable(list);
    }

    /**
     * 根据订单ID查询货物明细列表
     */
    @GetMapping("/order/{orderId}")
    public AjaxResult getByOrderId(@PathVariable("orderId") Long orderId)
    {
        List<LogisticsOrderGoods> list = logisticsOrderGoodsService.selectLogisticsOrderGoodsByOrderId(orderId);
        return AjaxResult.success(list);
    }

    /**
     * 导出订单货物明细列表
     */
    @Log(title = "订单货物明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsOrderGoods logisticsOrderGoods)
    {
        List<LogisticsOrderGoods> list = logisticsOrderGoodsService.selectLogisticsOrderGoodsList(logisticsOrderGoods);
        ExcelUtil<LogisticsOrderGoods> util = new ExcelUtil<LogisticsOrderGoods>(LogisticsOrderGoods.class);
        util.exportExcel(response, list, "订单货物明细数据");
    }

    /**
     * 获取订单货物明细详细信息
     */
    @GetMapping(value = "/{detailId}")
    public AjaxResult getInfo(@PathVariable("detailId") Long detailId)
    {
        return AjaxResult.success(logisticsOrderGoodsService.selectLogisticsOrderGoodsByDetailId(detailId));
    }

    /**
     * 新增订单货物明细
     */
    @Log(title = "订单货物明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsOrderGoods logisticsOrderGoods)
    {
        return toAjax(logisticsOrderGoodsService.insertLogisticsOrderGoods(logisticsOrderGoods));
    }

    /**
     * 修改订单货物明细
     */
    @Log(title = "订单货物明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsOrderGoods logisticsOrderGoods)
    {
        return toAjax(logisticsOrderGoodsService.updateLogisticsOrderGoods(logisticsOrderGoods));
    }

    /**
     * 删除订单货物明细
     */
    @Log(title = "订单货物明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{detailIds}")
    public AjaxResult remove(@PathVariable Long[] detailIds)
    {
        return toAjax(logisticsOrderGoodsService.deleteLogisticsOrderGoodsByDetailIds(detailIds));
    }
}
