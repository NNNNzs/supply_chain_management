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
import com.scm.logistics.domain.LogisticsBillOrderDetail;
import com.scm.logistics.domain.LogisticsDriver;
import com.scm.logistics.domain.LogisticsDriverOrder;
import com.scm.logistics.domain.LogisticsOrder;
import com.scm.logistics.domain.LogisticsVehicle;
import com.scm.logistics.domain.vo.AllocationResultVO;
import com.scm.logistics.domain.vo.BillAllocationItem;
import com.scm.logistics.mapper.LogisticsBillMapper;
import com.scm.logistics.mapper.LogisticsBillOrderDetailMapper;
import com.scm.logistics.mapper.LogisticsDriverMapper;
import com.scm.logistics.mapper.LogisticsDriverOrderMapper;
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
    private LogisticsBillOrderDetailMapper billOrderDetailMapper;

    @Autowired
    private LogisticsDriverOrderMapper driverOrderMapper;

    @Autowired
    private LogisticsDriverMapper driverMapper;

    @Autowired
    private LogisticsVehicleMapper vehicleMapper;

    /**
     * 创建运单并分配提单（核心功能）
     * 支持提单拆分和配载
     *
     * @param allocationItems 提单分配项列表
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
            throw new RuntimeException("请选择要配载的提单");
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

        // 2. 创建运单
        LogisticsOrder order = buildOrderFromBills(allocationItems);
        order.setSourceType("bill");
        order.setOrderStatus("pending");
        order.setDispatchStatus("dispatched");
        order.setCreateBy(driver.getDriverName()); // 临时设置，后续从登录用户获取
        orderMapper.insertOrder(order);

        // 3. 生成提单运单明细并更新提单状态
        List<LogisticsBillOrderDetail> details = new ArrayList<>();
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BillAllocationItem item : allocationItems)
        {
            // 创建明细记录
            LogisticsBillOrderDetail detail = new LogisticsBillOrderDetail();
            detail.setBillId(item.getBillId());
            detail.setOrderId(order.getOrderId());
            detail.setAllocatedWeight(item.getAllocWeight());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setAllocatedAmount(item.getAllocWeight().multiply(item.getUnitPrice()).setScale(2, RoundingMode.HALF_UP));
            detail.setCreateBy(driver.getDriverName());
            details.add(detail);

            totalWeight = totalWeight.add(item.getAllocWeight());
            totalAmount = totalAmount.add(detail.getAllocatedAmount());

            // 更新提单的已分配重量和状态
            LogisticsBill bill = billMapper.selectLogisticsBillByBillId(item.getBillId());
            BigDecimal newAllocatedWeight = bill.getAllocatedWeight().add(item.getAllocWeight());
            bill.setAllocatedWeight(newAllocatedWeight);

            // 更新提单状态
            if (newAllocatedWeight.compareTo(bill.getTotalWeight()) >= 0)
            {
                bill.setBillStatus("allocated");
            }
            else
            {
                bill.setBillStatus("partial");
            }
            billMapper.updateLogisticsBill(bill);
        }

        // 批量插入明细
        billOrderDetailMapper.batchInsertLogisticsBillOrderDetail(details);

        // 更新运单的汇总数据
        order.setTotalWeight(totalWeight);
        order.setAllocatedAmount(totalAmount);
        orderMapper.updateOrder(order);

        // 4. 创建驾驶员单
        LogisticsDriverOrder driverOrder = new LogisticsDriverOrder();
        driverOrder.setDriverOrderNo(generateDriverOrderNo());
        driverOrder.setOrderId(order.getOrderId());
        driverOrder.setOrderNo(order.getOrderNo());
        driverOrder.setDriverId(driverId);
        driverOrder.setDriverName(driver.getDriverName());
        driverOrder.setDriverPhone(driver.getDriverPhone());
        driverOrder.setVehicleId(vehicleId);
        driverOrder.setVehiclePlate(vehicle.getVehiclePlate());
        driverOrder.setLoadCapacity(vehicle.getLoadCapacity() != null ? new BigDecimal(vehicle.getLoadCapacity().toString()) : null);
        driverOrder.setActualWeight(totalWeight);
        driverOrder.setDispatchDate(DateUtils.parseDate(loadingDate));
        driverOrder.setDispatchStatus("pending");
        driverOrder.setSettlementStatus("unsettled");
        driverOrder.setReceiptStatus("not_received");
        driverOrder.setCreateBy(driver.getDriverName());
        driverOrderMapper.insertLogisticsDriverOrder(driverOrder);

        // 5. 返回配载结果
        AllocationResultVO result = new AllocationResultVO();
        result.setOrderId(order.getOrderId());
        result.setOrderNo(order.getOrderNo());
        result.setDriverOrderId(driverOrder.getDriverOrderId());
        result.setDriverOrderNo(driverOrder.getDriverOrderNo());
        result.setTotalWeight(totalWeight);
        result.setTotalAmount(totalAmount);
        result.setDetailCount(allocationItems.size());

        return result;
    }

    /**
     * 推荐可配载的提单
     * 根据车辆载重推荐合适的提单组合
     *
     * @param loadCapacity 车辆载重
     * @return 提单列表
     */
    @Override
    public List<BillAllocationItem> recommendBills(Double loadCapacity)
    {
        // 查询待配载和部分配载的提单
        LogisticsBill query = new LogisticsBill();
        query.setBillStatus("pending");
        List<LogisticsBill> pendingBills = billMapper.selectPendingBills(query);

        query.setBillStatus("partial");
        List<LogisticsBill> partialBills = billMapper.selectPendingBills(query);

        List<BillAllocationItem> result = new ArrayList<>();

        // 转换为 BillAllocationItem
        for (LogisticsBill bill : pendingBills)
        {
            BillAllocationItem item = convertToAllocationItem(bill);
            result.add(item);
        }

        for (LogisticsBill bill : partialBills)
        {
            BillAllocationItem item = convertToAllocationItem(bill);
            result.add(item);
        }

        return result;
    }

    /**
     * 根据提单列表构建运单
     */
    private LogisticsOrder buildOrderFromBills(List<BillAllocationItem> allocationItems)
    {
        if (allocationItems.isEmpty())
        {
            throw new RuntimeException("提单列表不能为空");
        }

        LogisticsOrder order = new LogisticsOrder();

        // 使用第一个提单的信息作为基础
        BillAllocationItem firstItem = allocationItems.get(0);
        order.setOrderDate(new Date());
        order.setCustomerId(getCustomerIdByBillId(firstItem.getBillId()));
        order.setLoadingAddress(firstItem.getLoadingAddress());
        order.setUnloadingAddress(firstItem.getUnloadingAddress());
        order.setGoodsId(getGoodsIdByBillId(firstItem.getBillId()));
        order.setGoodsName(firstItem.getGoodsName());

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
     * 生成驾驶员单号
     */
    private String generateDriverOrderNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        return "SJ" + dateStr + String.format("%04d", 1); // 简化实现
    }

    /**
     * 转换提单为分配项
     */
    private BillAllocationItem convertToAllocationItem(LogisticsBill bill)
    {
        BillAllocationItem item = new BillAllocationItem();
        item.setBillId(bill.getBillId());
        item.setBillNo(bill.getBillNo());
        item.setCustomerName(bill.getCustomerName());
        item.setGoodsName(bill.getGoodsName());
        item.setLoadingAddress(bill.getLoadingAddress());
        item.setUnloadingAddress(bill.getUnloadingAddress());
        item.setTotalWeight(bill.getTotalWeight());
        item.setAllocatedWeight(bill.getAllocatedWeight());
        item.setUnitPrice(bill.getUnitPrice());
        return item;
    }

    // 辅助方法（需要根据实际情况完善）
    private Long getCustomerIdByBillId(Long billId)
    {
        LogisticsBill bill = billMapper.selectLogisticsBillByBillId(billId);
        return bill != null ? bill.getCustomerId() : null;
    }

    private Long getGoodsIdByBillId(Long billId)
    {
        LogisticsBill bill = billMapper.selectLogisticsBillByBillId(billId);
        return bill != null ? bill.getGoodsId() : null;
    }
}
