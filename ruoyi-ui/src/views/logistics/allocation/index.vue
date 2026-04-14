<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：待配载提单列表 -->
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>待配载提单</span>
              <el-button type="primary" size="small" icon="Refresh" @click="getPendingBills">刷新</el-button>
            </div>
          </template>

          <el-form :inline="true" class="filter-form">
            <el-form-item label="车辆载重:">
              <el-input-number v-model="loadCapacity" :min="0" :precision="2" placeholder="载重(吨)" style="width: 150px" @change="handleRecommendBills" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" icon="MagicStick" @click="handleRecommendBills">智能推荐</el-button>
            </el-form-item>
          </el-form>

          <el-table
            ref="pendingBillTable"
            :data="pendingBills"
            @selection-change="handlePendingSelectionChange"
            max-height="500"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="提单号" align="center" prop="billNo" width="150" :show-overflow-tooltip="true" />
            <el-table-column label="客户" align="center" prop="customerName" width="100" :show-overflow-tooltip="true" />
            <el-table-column label="货物" align="center" prop="goodsName" width="80" />
            <el-table-column label="总重量" align="center" prop="totalWeight" width="80">
              <template #default="scope">
                {{ scope.row.totalWeight ? scope.row.totalWeight.toFixed(3) : '0' }}
              </template>
            </el-table-column>
            <el-table-column label="已分配" align="center" prop="allocatedWeight" width="80">
              <template #default="scope">
                {{ scope.row.allocatedWeight ? scope.row.allocatedWeight.toFixed(3) : '0' }}
              </template>
            </el-table-column>
            <el-table-column label="剩余" align="center" width="70">
              <template #default="scope">
                {{ getRemainWeight(scope.row) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 中间：配载设置 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <span>配载设置</span>
          </template>

          <el-form ref="allocationRef" :model="allocationForm" :rules="allocationRules" label-width="100px">
            <el-form-item label="司机" prop="driverId">
              <el-select v-model="allocationForm.driverId" placeholder="请选择司机" style="width: 100%" filterable>
                <el-option
                  v-for="item in driverOptions"
                  :key="item.driverId"
                  :label="item.driverName + ' - ' + item.driverPhone"
                  :value="item.driverId"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="车辆" prop="vehicleId">
              <el-select v-model="allocationForm.vehicleId" placeholder="请选择车辆" style="width: 100%" filterable @change="handleVehicleChange">
                <el-option
                  v-for="item in vehicleOptions"
                  :key="item.vehicleId"
                  :label="item.vehiclePlate + ' (载重:' + item.loadCapacity + '吨)'"
                  :value="item.vehicleId"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="装车日期" prop="loadingDate">
              <el-date-picker
                v-model="allocationForm.loadingDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>

            <el-divider>重量分配明细</el-divider>

            <el-table :data="selectedBills" max-height="300">
              <el-table-column label="提单号" align="center" prop="billNo" width="120" :show-overflow-tooltip="true" />
              <el-table-column label="剩余重量" align="center" width="90">
                <template #default="scope">
                  {{ getRemainWeight(scope.row) }}
                </template>
              </el-table-column>
              <el-table-column label="本次分配" align="center" width="110">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.allocWeight"
                    :min="0.001"
                    :max="getRemainWeightNum(scope.row)"
                    :precision="3"
                    size="small"
                    @change="handleAllocWeightChange"
                  />
                </template>
              </el-table-column>
              <el-table-column label="运价" align="center" width="80">
                <template #default="scope">
                  {{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(2) : '0' }}
                </template>
              </el-table-column>
            </el-table>

            <el-descriptions :column="1" border size="small" style="margin-top: 15px">
              <el-descriptions-item label="车辆载重">{{ vehicleCapacity }} 吨</el-descriptions-item>
              <el-descriptions-item label="已分配重量">{{ totalAllocWeight }} 吨</el-descriptions-item>
              <el-descriptions-item label="载重利用率">{{ loadUtilization }}%</el-descriptions-item>
              <el-descriptions-item label="分配金额">{{ totalAllocAmount }} 元</el-descriptions-item>
            </el-descriptions>

            <el-form-item style="margin-top: 20px">
              <el-button type="primary" icon="Check" @click="handleCreateOrder" :disabled="selectedBills.length === 0" style="width: 100%">
                创建运单并派车
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：已选提单汇总 -->
      <el-col :span="6">
        <el-card shadow="never">
          <template #header>
            <span>已选提单汇总</span>
          </template>

          <el-empty v-if="selectedBills.length === 0" description="请从左侧选择提单" :image-size="80" />

          <div v-else>
            <el-tag v-for="bill in selectedBills" :key="bill.billId" class="bill-tag" closable @close="handleRemoveBill(bill)">
              <div>{{ bill.billNo }}</div>
              <div class="bill-info">{{ bill.goodsName }} | 分配: {{ bill.allocWeight ? bill.allocWeight.toFixed(3) : '0.000' }}吨</div>
            </el-tag>
          </div>

          <el-divider />

          <div class="summary-section">
            <h4>配载汇总</h4>
            <el-statistic title="提单数量" :value="selectedBills.length" />
            <el-statistic title="总分配重量" :value="totalAllocWeight" suffix="吨" :precision="3" />
            <el-statistic title="分配总金额" :value="totalAllocAmount" suffix="元" :precision="2" />
          </div>
        </el-card>

        <!-- 配载结果 -->
        <el-card shadow="never" style="margin-top: 20px" v-if="allocationResult">
          <template #header>
            <span style="color: #67c23a">配载成功</span>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="运单号">{{ allocationResult.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="驾驶员单号">{{ allocationResult.driverOrderNo }}</el-descriptions-item>
            <el-descriptions-item label="装车重量">{{ allocationResult.totalWeight }} 吨</el-descriptions-item>
            <el-descriptions-item label="分配金额">{{ allocationResult.totalAmount }} 元</el-descriptions-item>
            <el-descriptions-item label="提单明细数">{{ allocationResult.detailCount }} 条</el-descriptions-item>
          </el-descriptions>
          <el-button type="success" icon="View" @click="viewOrder" style="width: 100%; margin-top: 15px">查看运单详情</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Allocation">
import { getPendingBills, recommendBills, createOrderWithBills } from "@/api/logistics/allocation"
import { listDriver } from "@/api/logistics/driver"
import { listVehicle } from "@/api/logistics/vehicle"
import { useRouter } from 'vue-router'

const router = useRouter()
const { proxy } = getCurrentInstance()

const pendingBills = ref([])
const selectedBills = ref([])
const driverOptions = ref([])
const vehicleOptions = ref([])
const loadCapacity = ref(30)
const vehicleCapacity = ref(0)
const allocationResult = ref(null)

const allocationForm = reactive({
  driverId: null,
  vehicleId: null,
  loadingDate: new Date().toISOString().split('T')[0]
})

const allocationRules = {
  driverId: [
    { required: true, message: "请选择司机", trigger: "change" }
  ],
  vehicleId: [
    { required: true, message: "请选择车辆", trigger: "change" }
  ],
  loadingDate: [
    { required: true, message: "请选择装车日期", trigger: "change" }
  ]
}

const totalAllocWeight = computed(() => {
  return selectedBills.value.reduce((sum, bill) => {
    return sum + (bill.allocWeight || 0)
  }, 0).toFixed(3)
})

const totalAllocAmount = computed(() => {
  return selectedBills.value.reduce((sum, bill) => {
    const weight = bill.allocWeight || 0
    const price = bill.unitPrice || 0
    return sum + (weight * price)
  }, 0).toFixed(2)
})

const loadUtilization = computed(() => {
  if (vehicleCapacity.value <= 0) return '0.00'
  const utilization = (parseFloat(totalAllocWeight.value) / vehicleCapacity.value * 100).toFixed(2)
  return utilization
})

function getRemainWeight(row) {
  if (row.totalWeight != null && row.allocatedWeight != null) {
    return (row.totalWeight - row.allocatedWeight).toFixed(3)
  }
  return row.totalWeight ? row.totalWeight.toFixed(3) : '0.000'
}

function getRemainWeightNum(row) {
  if (row.totalWeight != null && row.allocatedWeight != null) {
    return row.totalWeight - row.allocatedWeight
  }
  return row.totalWeight || 0
}

function getPendingBillsData() {
  getPendingBills().then(response => {
    pendingBills.value = response.data
    // 初始化 allocWeight
    pendingBills.value.forEach(bill => {
      bill.allocWeight = getRemainWeightNum(bill)
    })
  })
}

function handleRecommendBills() {
  recommendBills(loadCapacity.value).then(response => {
    pendingBills.value = response.data
    // 初始化 allocWeight
    pendingBills.value.forEach(bill => {
      bill.allocWeight = getRemainWeightNum(bill)
    })
    proxy.$modal.msgSuccess("已根据载重 " + loadCapacity.value + " 吨推荐合适的提单")
  })
}

function handlePendingSelectionChange(selection) {
  // 添加新选中的提单
  selection.forEach(bill => {
    const existing = selectedBills.value.find(b => b.billId === bill.billId)
    if (!existing) {
      selectedBills.value.push({
        ...bill,
        allocWeight: getRemainWeightNum(bill)
      })
    }
  })

  // 移除取消选中的提单
  const selectedIds = selection.map(b => b.billId)
  selectedBills.value = selectedBills.value.filter(b => selectedIds.includes(b.billId))
}

function handleRemoveBill(bill) {
  selectedBills.value = selectedBills.value.filter(b => b.billId !== bill.billId)
  // 取消表格中的选中状态
  proxy.$refs.pendingBillTable.toggleRowSelection(bill, false)
}

function handleAllocWeightChange() {
  // 重量变化时，自动更新统计
}

function handleVehicleChange(vehicleId) {
  const vehicle = vehicleOptions.value.find(v => v.vehicleId === vehicleId)
  if (vehicle) {
    vehicleCapacity.value = vehicle.loadCapacity || 0
  }
}

function handleCreateOrder() {
  proxy.$refs["allocationRef"].validate(valid => {
    if (valid) {
      // 验证分配重量
      const invalidBills = selectedBills.value.filter(b => !b.allocWeight || b.allocWeight <= 0)
      if (invalidBills.length > 0) {
        proxy.$modal.msgWarning("请为所有提单设置分配重量")
        return
      }

      // 验证载重
      if (parseFloat(totalAllocWeight.value) > vehicleCapacity.value) {
        proxy.$modal.msgWarning(`分配重量 ${totalAllocWeight.value} 吨超过车辆载重 ${vehicleCapacity.value} 吨`)
        return
      }

      const allocationItems = selectedBills.value.map(bill => ({
        billId: bill.billId,
        billNo: bill.billNo,
        customerName: bill.customerName,
        goodsName: bill.goodsName,
        loadingAddress: bill.loadingAddress,
        unloadingAddress: bill.unloadingAddress,
        totalWeight: bill.totalWeight,
        allocatedWeight: bill.allocatedWeight,
        unitPrice: bill.unitPrice,
        allocWeight: bill.allocWeight
      }))

      const requestData = {
        allocationItems: allocationItems,
        driverId: allocationForm.driverId,
        vehicleId: allocationForm.vehicleId,
        loadingDate: allocationForm.loadingDate
      }

      createOrderWithBills(requestData).then(response => {
        allocationResult.value = response.data
        proxy.$modal.msgSuccess("配载成功！")
        // 清空已选提单
        selectedBills.value = []
        // 刷新待配载提单列表
        getPendingBillsData()
      })
    }
  })
}

function viewOrder() {
  if (allocationResult.value && allocationResult.value.orderId) {
    router.push({
      path: '/logistics/order/form',
      query: { orderId: allocationResult.value.orderId }
    })
  }
}

function getDriverList() {
  listDriver({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    driverOptions.value = response.rows
  })
}

function getVehicleList() {
  listVehicle({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    vehicleOptions.value = response.rows
  })
}

getPendingBillsData()
getDriverList()
getVehicleList()
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 15px;
}

.bill-tag {
  display: block;
  width: 100%;
  margin-bottom: 10px;
  padding: 10px;
  height: auto;
  white-space: normal;
}

.bill-info {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.summary-section h4 {
  margin-bottom: 15px;
  color: #606266;
}

.summary-section .el-statistic {
  margin-bottom: 15px;
}
</style>
