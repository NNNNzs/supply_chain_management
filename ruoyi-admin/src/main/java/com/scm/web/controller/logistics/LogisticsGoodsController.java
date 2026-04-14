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
import com.scm.logistics.domain.LogisticsGoods;
import com.scm.logistics.service.ILogisticsGoodsService;
import com.scm.common.utils.poi.ExcelUtil;
import com.scm.common.core.page.TableDataInfo;

/**
 * 货物信息Controller
 *
 * @author scm
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/logistics/goods")
public class LogisticsGoodsController extends BaseController
{
    @Autowired
    private ILogisticsGoodsService logisticsGoodsService;

    /**
     * 查询货物信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(LogisticsGoods logisticsGoods)
    {
        startPage();
        List<LogisticsGoods> list = logisticsGoodsService.selectLogisticsGoodsList(logisticsGoods);
        return getDataTable(list);
    }

    /**
     * 导出货物信息列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:goods:export')")
    @Log(title = "货物信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LogisticsGoods logisticsGoods)
    {
        List<LogisticsGoods> list = logisticsGoodsService.selectLogisticsGoodsList(logisticsGoods);
        ExcelUtil<LogisticsGoods> util = new ExcelUtil<LogisticsGoods>(LogisticsGoods.class);
        util.exportExcel(response, list, "货物信息数据");
    }

    /**
     * 获取货物信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:goods:query')")
    @GetMapping(value = "/{goodsId}")
    public AjaxResult getInfo(@PathVariable("goodsId") Long goodsId)
    {
        return success(logisticsGoodsService.selectLogisticsGoodsByGoodsId(goodsId));
    }

    /**
     * 新增货物信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:goods:add')")
    @Log(title = "货物信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LogisticsGoods logisticsGoods)
    {
        return toAjax(logisticsGoodsService.insertLogisticsGoods(logisticsGoods));
    }

    /**
     * 修改货物信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:goods:edit')")
    @Log(title = "货物信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LogisticsGoods logisticsGoods)
    {
        return toAjax(logisticsGoodsService.updateLogisticsGoods(logisticsGoods));
    }

    /**
     * 删除货物信息
     */
    @PreAuthorize("@ss.hasPermi('logistics:goods:remove')")
    @Log(title = "货物信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds)
    {
        return toAjax(logisticsGoodsService.deleteLogisticsGoodsByGoodsIds(goodsIds));
    }
}
