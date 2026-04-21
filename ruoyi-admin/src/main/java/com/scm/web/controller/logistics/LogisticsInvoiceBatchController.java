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
import com.scm.logistics.domain.LogisticsOrder;
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
@RequestMapping("/logistics/invoice")
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
     * 查询可开票订单列表
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:list')")
    @GetMapping("/orders")
    public TableDataInfo listInvoiceableOrders(LogisticsOrder logisticsOrder)
    {
        startPage();
        List<LogisticsOrder> list = logisticsInvoiceBatchService.selectInvoiceableOrderList(logisticsOrder);
        return getDataTable(list);
    }

    /**
     * 合并开票
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:merge')")
    @Log(title = "发票批次", businessType = BusinessType.INSERT)
    @PostMapping("/merge")
    public AjaxResult merge(@RequestBody MergeInvoiceRequest request)
    {
        return logisticsInvoiceBatchService.mergeInvoice(
            request.getCustomerId(),
            request.getInvoiceDate(),
            request.getInvoiceType(),
            request.getTaxRate(),
            request.getOrderIds()
        );
    }

    /**
     * 发票开具
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:issue')")
    @Log(title = "发票批次", businessType = BusinessType.UPDATE)
    @PutMapping("/issue/{batchId}")
    public AjaxResult issue(@PathVariable Long batchId)
    {
        LogisticsInvoiceBatch batch = logisticsInvoiceBatchService.selectLogisticsInvoiceBatchByBatchId(batchId);
        if (batch == null) {
            return error("发票批次不存在");
        }
        if ("issued".equals(batch.getInvoiceStatus())) {
            return error("发票已开具，请勿重复操作");
        }
        if ("cancelled".equals(batch.getInvoiceStatus())) {
            return error("发票已作废，无法开具");
        }

        batch.setInvoiceStatus("issued");
        return toAjax(logisticsInvoiceBatchService.updateLogisticsInvoiceBatch(batch));
    }

    /**
     * 发票作废
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:cancel')")
    @Log(title = "发票批次", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{batchId}")
    public AjaxResult cancel(@PathVariable Long batchId)
    {
        LogisticsInvoiceBatch batch = logisticsInvoiceBatchService.selectLogisticsInvoiceBatchByBatchId(batchId);
        if (batch == null) {
            return error("发票批次不存在");
        }
        if (!"issued".equals(batch.getInvoiceStatus())) {
            return error("只有已开具的发票才能作废");
        }

        batch.setInvoiceStatus("cancelled");
        return toAjax(logisticsInvoiceBatchService.updateLogisticsInvoiceBatch(batch));
    }

    /**
     * 取消合并（恢复订单为未开票状态）
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:merge')")
    @Log(title = "发票批次", businessType = BusinessType.UPDATE)
    @PutMapping("/cancelMerge/{batchId}")
    public AjaxResult cancelMerge(@PathVariable Long batchId)
    {
        LogisticsInvoiceBatch batch = logisticsInvoiceBatchService.selectLogisticsInvoiceBatchByBatchId(batchId);
        if (batch == null) {
            return error("发票批次不存在");
        }
        if ("issued".equals(batch.getInvoiceStatus())) {
            return error("发票已开具，无法取消合并");
        }

        return toAjax(logisticsInvoiceBatchService.cancelMerge(batchId));
    }

    /**
     * 删除发票批次（恢复订单为未开票状态）
     */
    @PreAuthorize("@ss.hasPermi('logistics:invoice:remove')")
    @Log(title = "发票批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchId}")
    public AjaxResult remove(@PathVariable Long batchId)
    {
        LogisticsInvoiceBatch batch = logisticsInvoiceBatchService.selectLogisticsInvoiceBatchByBatchId(batchId);
        if (batch == null) {
            return error("发票批次不存在");
        }
        if ("issued".equals(batch.getInvoiceStatus())) {
            return error("发票已开具，无法删除");
        }

        return toAjax(logisticsInvoiceBatchService.deleteLogisticsInvoiceBatchByBatchId(batchId));
    }

    /**
     * 合并开票请求对象
     */
    public static class MergeInvoiceRequest {
        private Long customerId;
        private String invoiceDate;
        private String invoiceType;
        private Double taxRate;
        private List<Long> orderIds;

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public String getInvoiceType() {
            return invoiceType;
        }

        public void setInvoiceType(String invoiceType) {
            this.invoiceType = invoiceType;
        }

        public Double getTaxRate() {
            return taxRate;
        }

        public void setTaxRate(Double taxRate) {
            this.taxRate = taxRate;
        }

        public List<Long> getOrderIds() {
            return orderIds;
        }

        public void setOrderIds(List<Long> orderIds) {
            this.orderIds = orderIds;
        }
    }
}
