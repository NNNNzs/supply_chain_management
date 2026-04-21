package com.scm.logistics.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scm.common.core.domain.AjaxResult;
import com.scm.common.utils.BaseEntityUtils;
import com.scm.common.utils.SecurityUtils;
import com.scm.logistics.domain.LogisticsInvoiceDetail;
import com.scm.logistics.domain.LogisticsOrder;
import com.scm.logistics.mapper.LogisticsInvoiceBatchMapper;
import com.scm.logistics.mapper.LogisticsInvoiceDetailMapper;
import com.scm.logistics.mapper.LogisticsOrderMapper;
import com.scm.logistics.service.ILogisticsInvoiceBatchService;
import com.scm.logistics.service.ILogisticsOrderLogService;

/**
 * 发票批次Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsInvoiceBatchServiceImpl implements ILogisticsInvoiceBatchService
{
    @Autowired
    private LogisticsInvoiceBatchMapper logisticsInvoiceBatchMapper;

    @Autowired
    private LogisticsInvoiceDetailMapper logisticsInvoiceDetailMapper;

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    @Autowired
    private ILogisticsOrderLogService logisticsOrderLogService;

    /**
     * 查询发票批次
     *
     * @param batchId 发票批次主键
     * @return 发票批次
     */
    @Override
    public LogisticsInvoiceBatch selectLogisticsInvoiceBatchByBatchId(Long batchId)
    {
        LogisticsInvoiceBatch batch = logisticsInvoiceBatchMapper.selectLogisticsInvoiceBatchByBatchId(batchId);
        if (batch != null) {
            List<LogisticsInvoiceDetail> details = logisticsInvoiceDetailMapper.selectDetailsByBatchId(batchId);
            batch.setOrderList(details);
        }
        return batch;
    }

    /**
     * 查询发票批次列表
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 发票批次
     */
    @Override
    public List<LogisticsInvoiceBatch> selectLogisticsInvoiceBatchList(LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        return logisticsInvoiceBatchMapper.selectLogisticsInvoiceBatchList(logisticsInvoiceBatch);
    }

    /**
     * 新增发票批次
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 结果
     */
    @Override
    public int insertLogisticsInvoiceBatch(LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        BaseEntityUtils.fillCreateInfo(logisticsInvoiceBatch);
        return logisticsInvoiceBatchMapper.insertLogisticsInvoiceBatch(logisticsInvoiceBatch);
    }

    /**
     * 修改发票批次
     *
     * @param logisticsInvoiceBatch 发票批次
     * @return 结果
     */
    @Override
    public int updateLogisticsInvoiceBatch(LogisticsInvoiceBatch logisticsInvoiceBatch)
    {
        BaseEntityUtils.fillUpdateInfo(logisticsInvoiceBatch);
        return logisticsInvoiceBatchMapper.updateLogisticsInvoiceBatch(logisticsInvoiceBatch);
    }

    /**
     * 批量删除发票批次
     *
     * @param batchIds 需要删除的发票批次主键
     * @return 结果
     */
    @Override
    public int deleteLogisticsInvoiceBatchByBatchIds(Long[] batchIds)
    {
        return logisticsInvoiceBatchMapper.deleteLogisticsInvoiceBatchByBatchIds(batchIds);
    }

    /**
     * 删除发票批次信息
     *
     * @param batchId 发票批次主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteLogisticsInvoiceBatchByBatchId(Long batchId)
    {
        // 恢复订单为未开票状态
        List<LogisticsInvoiceDetail> details = logisticsInvoiceDetailMapper.selectDetailsByBatchId(batchId);
        for (LogisticsInvoiceDetail detail : details) {
            LogisticsOrder order = new LogisticsOrder();
            order.setOrderId(detail.getOrderId());
            order.setInvoiceStatus("not_invoiced");
            BaseEntityUtils.fillUpdateInfo(order);
            logisticsOrderMapper.updateOrder(order);

            // 记录订单取消开票日志
            logisticsOrderLogService.logOrderUpdate(
                detail.getOrderId(),
                detail.getOrderNo(),
                SecurityUtils.getUserId(),
                SecurityUtils.getUsername(),
                "删除发票批次，恢复为未开票状态"
            );
        }

        // 删除明细
        logisticsInvoiceDetailMapper.deleteDetailsByBatchId(batchId);

        // 删除批次
        return logisticsInvoiceBatchMapper.deleteLogisticsInvoiceBatchByBatchId(batchId);
    }

    /**
     * 查询可开票订单列表
     *
     * @param logisticsOrder 订单查询条件
     * @return 订单集合
     */
    @Override
    public List<LogisticsOrder> selectInvoiceableOrderList(LogisticsOrder logisticsOrder)
    {
        return logisticsOrderMapper.selectOrderList(logisticsOrder);
    }

    /**
     * 合并开票
     *
     * @param customerId 客户ID
     * @param invoiceDate 开票日期
     * @param invoiceType 发票类型
     * @param taxRate 税率
     * @param orderIds 订单ID列表
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult mergeInvoice(Long customerId, String invoiceDate, String invoiceType, Double taxRate, List<Long> orderIds)
    {
        if (orderIds == null || orderIds.isEmpty()) {
            return AjaxResult.error("请选择要开票的订单");
        }

        // 验证订单状态
        List<LogisticsOrder> orders = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Long orderId : orderIds) {
            LogisticsOrder order = logisticsOrderMapper.selectOrderById(orderId);
            if (order == null) {
                return AjaxResult.error("订单不存在：" + orderId);
            }
            if (!"completed".equals(order.getOrderStatus())) {
                return AjaxResult.error("订单未完成，无法开票：" + order.getOrderNo());
            }
            if ("invoiced".equals(order.getInvoiceStatus())) {
                return AjaxResult.error("订单已开票：" + order.getOrderNo());
            }
            if (!customerId.equals(order.getCustomerId())) {
                return AjaxResult.error("订单客户不一致：" + order.getOrderNo());
            }
            orders.add(order);
            totalAmount = totalAmount.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
        }

        // 计算税额 - 从 Double 转换为 BigDecimal
        BigDecimal taxRateBD = taxRate != null ? BigDecimal.valueOf(taxRate) : BigDecimal.ZERO;
        BigDecimal taxAmount = totalAmount.multiply(taxRateBD).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

        // 生成批次号
        String batchNo = generateBatchNo();

        // 创建发票批次 - 将 String 日期转换为 Date
        Date parsedInvoiceDate = null;
        try {
            if (invoiceDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                parsedInvoiceDate = sdf.parse(invoiceDate);
            }
        } catch (Exception e) {
            // 使用当前日期
        }

        LogisticsInvoiceBatch batch = new LogisticsInvoiceBatch();
        batch.setBatchNo(batchNo);
        batch.setCustomerId(customerId);
        batch.setInvoiceDate(parsedInvoiceDate);
        batch.setInvoiceType(invoiceType);
        batch.setTaxRate(taxRateBD);
        batch.setTotalAmount(totalAmount);
        batch.setTaxAmount(taxAmount);
        batch.setOrderCount(orders.size());
        batch.setInvoiceStatus("draft");
        BaseEntityUtils.fillCreateInfo(batch);
        logisticsInvoiceBatchMapper.insertLogisticsInvoiceBatch(batch);

        // 创建明细记录
        for (LogisticsOrder order : orders) {
            LogisticsInvoiceDetail detail = new LogisticsInvoiceDetail();
            detail.setBatchId(batch.getBatchId());
            detail.setOrderId(order.getOrderId());
            detail.setOrderNo(order.getOrderNo());
            detail.setAmount(order.getTotalAmount());
            logisticsInvoiceDetailMapper.insertLogisticsInvoiceDetail(detail);

            // 更新订单开票状态
            order.setInvoiceStatus("invoiced");
            BaseEntityUtils.fillUpdateInfo(order);
            logisticsOrderMapper.updateOrder(order);

            // 记录订单开票日志
            logisticsOrderLogService.logOrderInvoice(
                order.getOrderId(),
                order.getOrderNo(),
                SecurityUtils.getUserId(),
                SecurityUtils.getUsername(),
                batchNo
            );
        }

        return AjaxResult.success("合并开票成功", batch);
    }

    /**
     * 取消合并（恢复订单为未开票状态）
     *
     * @param batchId 批次ID
     * @return 结果
     */
    @Override
    @Transactional
    public int cancelMerge(Long batchId)
    {
        // 恢复订单为未开票状态
        List<LogisticsInvoiceDetail> details = logisticsInvoiceDetailMapper.selectDetailsByBatchId(batchId);
        for (LogisticsInvoiceDetail detail : details) {
            LogisticsOrder order = new LogisticsOrder();
            order.setOrderId(detail.getOrderId());
            order.setInvoiceStatus("not_invoiced");
            BaseEntityUtils.fillUpdateInfo(order);
            logisticsOrderMapper.updateOrder(order);

            // 记录订单取消开票日志
            logisticsOrderLogService.logOrderUpdate(
                detail.getOrderId(),
                detail.getOrderNo(),
                SecurityUtils.getUserId(),
                SecurityUtils.getUsername(),
                "取消开票，恢复为未开票状态"
            );
        }

        // 删除明细
        logisticsInvoiceDetailMapper.deleteDetailsByBatchId(batchId);

        // 删除批次
        return logisticsInvoiceBatchMapper.deleteLogisticsInvoiceBatchByBatchId(batchId);
    }

    /**
     * 生成批次号
     *
     * @return 批次号
     */
    private String generateBatchNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "FP" + dateStr;

        // 查询当天最大序号
        Integer maxSeq = logisticsInvoiceBatchMapper.selectMaxSeqByBatchNoPrefix(prefix);
        int seq = (maxSeq == null ? 0 : maxSeq) + 1;

        return prefix + String.format("%04d", seq);
    }
}
