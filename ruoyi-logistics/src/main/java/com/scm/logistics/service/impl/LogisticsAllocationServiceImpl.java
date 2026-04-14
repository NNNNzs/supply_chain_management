package com.scm.logistics.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scm.common.utils.DateUtils;
import com.scm.common.utils.StringUtils;
import com.scm.logistics.domain.LogisticsBill;
import com.scm.logistics.domain.LogisticsBillItem;
import com.scm.logistics.domain.LogisticsBillOrderDetail;
import com.scm.logistics.domain.LogisticsDriver;
import com.scm.logistics.domain.LogisticsOrder;
import com.scm.logistics.domain.LogisticsVehicle;
import com.scm.logistics.domain.vo.AllocationResultVO;
import com.scm.logistics.domain.vo.BillAllocationItem;
import com.scm.logistics.mapper.LogisticsBillItemMapper;
import com.scm.logistics.mapper.LogisticsBillMapper;
import com.scm.logistics.mapper.LogisticsBillOrderDetailMapper;
import com.scm.logistics.mapper.LogisticsDriverMapper;
import com.scm.logistics.mapper.LogisticsOrderMapper;
import com.scm.logistics.mapper.LogisticsVehicleMapper;
import com.scm.logistics.service.ILogisticsAllocationService;

/**
 * 配载管理Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsAllocationServiceImpl implements ILogisticsAllocationService
{
    @Autowired
    private LogisticsOrderMapper orderMapper;

    @Autowired
    private LogisticsBillMapper billMapper;

    @Autowired
    private LogisticsBillItemMapper billItemMapper;

    @Autowired
    private LogisticsBillOrderDetailMapper billOrderDetailMapper;

    @Autowired
    private LogisticsDriverMapper driverMapper;

    @Autowired
    private LogisticsVehicleMapper vehicleMapper;

    /**
     * 创建运单并分配提单（核心功能）
     * 按货物明细级别分配，支持提单拆分和配载
     *
     * @param allocationItems 提单分配项列表（每项对应一个 bill_item）
     * @param driverId 司机ID
     * @param vehicleId 车辆ID
     * @param loadingDate 装车日期
     * @return 配载结果
     */
    @Override
    @Transactional
    public AllocationResultVO createOrderWithBills(List<BillAllocationItem> allocationItems, Long driverId, Long vehicleId, String loadingDate)
    {
        if (allocationItems == null || allocationItems.isEmpty())
        {
            throw new RuntimeException("请选择要配载的货物明细");
        }

        // 1. 获取司机和车辆信息
        LogisticsDriver driver = driverMapper.selectLogisticsDriverByDriverId(driverId);
        if (driver == null)
        {
            throw new RuntimeException("司机信息不存在");
        }

        LogisticsVehicle vehicle = vehicleMapper.selectLogisticsVehicleByVehicleId(vehicleId);
        if (vehicle == null)
        {
            throw new RuntimeException("车辆信息不存在");
        }

        // 2. 创建运单（含司机、车辆信息）
        LogisticsOrder order = buildOrderFromBills(allocationItems);
        order.setSourceType("bill");
        order.setOrderStatus("pending");
        order.setDriverId(driverId);
        order.setDriverPhone(driver.getDriverPhone());
        order.setVehicleId(vehicleId);
        order.setVehiclePlate(vehicle.getVehiclePlate());
        order.setLoadCapacity(vehicle.getLoadCapacity() != null ? new BigDecimal(vehicle.getLoadCapacity().toString()) : null);
        order.setDispatchDate(DateUtils.parseDate(loadingDate));
        order.setCreateBy(driver.getDriverName());
        orderMapper.insertOrder(order);

        // 3. 生成提单运单明细并更新提单货物明细和提单状态
        List<LogisticsBillOrderDetail> details = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 按提单ID分组，用于更新提单状态
        java.util.Map<Long, BigDecimal> billAllocatedMap = new java.util.HashMap<>();

        for (BillAllocationItem item : allocationItems)
        {
            LogisticsBillOrderDetail detail = new LogisticsBillOrderDetail();
            detail.setBillId(item.getBillId());
            detail.setBillItemId(item.getBillItemId());
            detail.setOrderId(order.getOrderId());
            detail.setAllocatedWeight(item.getAllocWeight());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setAllocatedAmount(item.getAllocWeight().multiply(item.getUnitPrice()).setScale(2, RoundingMode.HALF_UP));
            detail.setCreateBy(driver.getDriverName());
            details.add(detail);

            totalAmount = totalAmount.add(detail.getAllocatedAmount());

            // 更新提单货物明细的已分配重量
            if (item.getBillItemId() != null)
            {
                LogisticsBillItem billItem = billItemMapper.selectLogisticsBillItemByItemId(item.getBillItemId());
                if (billItem != null)
                {
                    BigDecimal newAllocated = billItem.getAllocatedWeight() != null
                        ? billItem.getAllocatedWeight().add(item.getAllocWeight())
                        : item.getAllocWeight();
                    billItem.setAllocatedWeight(newAllocated);
                    billItemMapper.updateLogisticsBillItem(billItem);
                }
            }

            billAllocatedMap.merge(item.getBillId(), item.getAllocWeight(), BigDecimal::add);
        }

        // 批量插入明细
        billOrderDetailMapper.batchInsertLogisticsBillOrderDetail(details);

        // 更新运单汇总数据
        order.setTotalAmount(totalAmount);
        order.setFreightCost(totalAmount); // 运费支出 = 分配总金额
        orderMapper.updateOrder(order);

        // 更新每个提单的状态
        for (java.util.Map.Entry<Long, BigDecimal> entry : billAllocatedMap.entrySet())
        {
            Long billId = entry.getKey();
            BigDecimal allocatedSum = billItemMapper.sumAllocatedWeightByBillId(billId);
            LogisticsBill bill = billMapper.selectLogisticsBillByBillId(billId);
            if (bill != null)
            {
                bill.setAllocatedWeight(allocatedSum);
                if (isBillFullyAllocated(billId))
                {
                    bill.setBillStatus("allocated");
                }
                else
                {
                    bill.setBillStatus("partial");
                }
                billMapper.updateLogisticsBill(bill);
            }
        }

        // 4. 返回配载结果
        AllocationResultVO result = new AllocationResultVO();
        result.setOrderId(order.getOrderId());
        result.setOrderNo(order.getOrderNo());
        result.setTotalWeight(order.getWeight());
        result.setTotalAmount(totalAmount);
        result.setDetailCount(allocationItems.size());

        return result;
    }

    /**
     * 推荐可配载的提单（按货物明细级别）
     *
     * @param loadCapacity 车辆载重
     * @return 提单货物明细列表
     */
    @Override
    public List<BillAllocationItem> recommendBills(Double loadCapacity)
    {
        // 查询待配载的提单货物明细
        List<LogisticsBillItem> pendingItems = billItemMapper.selectPendingBillItems();

        List<BillAllocationItem> result = new ArrayList<>();

        // 转换为 BillAllocationItem
        for (LogisticsBillItem item : pendingItems)
        {
            LogisticsBill bill = billMapper.selectLogisticsBillByBillId(item.getBillId());
            if (bill == null)
            {
                continue;
            }

            BillAllocationItem allocationItem = convertItemToAllocationItem(item, bill);
            result.add(allocationItem);
        }

        return result;
    }

    /**
     * 检查提单的所有货物明细是否都已完全分配
     */
    private boolean isBillFullyAllocated(Long billId)
    {
        List<LogisticsBillItem> items = billItemMapper.selectItemsByBillId(billId);
        if (items == null || items.isEmpty())
        {
            return false;
        }
        for (LogisticsBillItem item : items)
        {
            if (item.getWeight() == null)
            {
                return false;
            }
            BigDecimal remain = item.getWeight().subtract(
                item.getAllocatedWeight() != null ? item.getAllocatedWeight() : BigDecimal.ZERO
            );
            if (remain.compareTo(BigDecimal.ZERO) > 0)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据提单分配项构建运单
     */
    private LogisticsOrder buildOrderFromBills(List<BillAllocationItem> allocationItems)
    {
        if (allocationItems.isEmpty())
        {
            throw new RuntimeException("货物明细列表不能为空");
        }

        LogisticsOrder order = new LogisticsOrder();

        // 使用第一个分配项的信息作为基础
        BillAllocationItem firstItem = allocationItems.get(0);
        order.setOrderDate(new Date());
        order.setCustomerId(getCustomerIdByBillId(firstItem.getBillId()));
        order.setLoadingAddress(firstItem.getLoadingAddress());
        order.setUnloadingAddress(firstItem.getUnloadingAddress());

        // 汇总重量和金额
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BillAllocationItem item : allocationItems)
        {
            totalWeight = totalWeight.add(item.getAllocWeight() != null ? item.getAllocWeight() : BigDecimal.ZERO);
            if (item.getAllocWeight() != null && item.getUnitPrice() != null)
            {
                totalAmount = totalAmount.add(item.getAllocWeight().multiply(item.getUnitPrice()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        order.setWeight(totalWeight);
        order.setUnitPrice(BigDecimal.ZERO); // 多货物运单单价无意义，设为0
        order.setTotalAmount(totalAmount);

        // 拼接货物名称（多种货物用逗号分隔）
        String goodsNames = allocationItems.stream()
            .map(BillAllocationItem::getGoodsName)
            .distinct()
            .collect(java.util.stream.Collectors.joining(", "));
        order.setGoodsName(goodsNames);

        // 生成订单号
        order.setOrderNo(generateOrderNo());

        return order;
    }

    /**
     * 生成运单号
     */
    private String generateOrderNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        return "YS" + dateStr + String.format("%04d", 1); // 简化实现，实际需要查询流水号
    }

    /**
     * 转换提单货物明细为分配项
     */
    private BillAllocationItem convertItemToAllocationItem(LogisticsBillItem item, LogisticsBill bill)
    {
        BillAllocationItem allocationItem = new BillAllocationItem();
        allocationItem.setBillId(bill.getBillId());
        allocationItem.setBillNo(bill.getBillNo());
        allocationItem.setBillItemId(item.getItemId());
        allocationItem.setCustomerName(bill.getCustomerName());
        allocationItem.setGoodsName(item.getGoodsName());
        allocationItem.setGoodsModel(item.getGoodsModel());
        allocationItem.setLoadingAddress(bill.getLoadingAddress());
        allocationItem.setUnloadingAddress(bill.getUnloadingAddress());
        allocationItem.setTotalWeight(item.getWeight());
        allocationItem.setAllocatedWeight(item.getAllocatedWeight());
        allocationItem.setUnitPrice(item.getUnitPrice());
        return allocationItem;
    }

    // 辅助方法
    private Long getCustomerIdByBillId(Long billId)
    {
        LogisticsBill bill = billMapper.selectLogisticsBillByBillId(billId);
        return bill != null ? bill.getCustomerId() : null;
    }
}
