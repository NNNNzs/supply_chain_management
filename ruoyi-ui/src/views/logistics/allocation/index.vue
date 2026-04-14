<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：待配载货物明细列表 -->
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>待配载货物明细</span>
              <el-button type="primary" size="small" icon="Refresh" @click="getPendingBillsData">刷新</el-button>
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
            size="small"
            :row-key="(row) => row.billItemId"
          >
            <el-table-column type="selection" width="45" align="center" :reserve-selection="true" />
            <el-table-column label="提单号" align="center" prop="billNo" width="130" :show-overflow-tooltip="true" />
            <el-table-column label="客户" align="center" prop="customerName" width="80" :show-overflow-tooltip="true" />
            <el-table-column label="货物" align="center" prop="goodsName" width="80" :show-overflow-tooltip="true" />
            <el-table-column label="总重量" align="center" width="70">
              <template #default="scope">
                {{ scope.row.totalWeight ? scope.row.totalWeight.toFixed(3) : '0' }}
              </template>
            </el-table-column>
            <el-table-column label="已分配" align="center" width="70">
              <template #default="scope">
                {{ scope.row.allocatedWeight ? scope.row.allocatedWeight.toFixed(3) : '0' }}
              </template>
            </el-table-column>
            <el-table-column label="剩余" align="center" width="65">
              <template #default="scope">
                {{ getRemainWeight(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="运价" align="center" width="65">
              <template #default="scope">
                {{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(0) : '-' }}
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

            <el-table :data="selectedBills" max-height="300" size="small" border>
              <el-table-column label="提单/货物" min-width="140" :show-overflow-tooltip="true">
                <template #default="scope">
                  <div>{{ scope.row.billNo }}</div>
                  <div style="font-size: 11px; color: #909399;">{{ scope.row.goodsName }}</div>
                </template>
              </el-table-column>
              <el-table-column label="剩余" align="center" width="70">
                <template #default="scope">
                  {{ getRemainWeight(scope.row) }}
                </template>
              </el-table-column>
              <el-table-column label="本次分配" align="center" width="100">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.allocWeight"
                    :min="0.001"
                    :max="getRemainWeightNum(scope.row)"
                    :precision="3"
                    size="small"
                    controls-position="right"
                    style="width: 90px"
                    @change="handleAllocWeightChange"
                  />
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

      <!-- 右侧：已选汇总 -->
      <el-col :span="6">
        <el-card shadow="never">
          <template #header>
            <span>已选货物汇总</span>
          </template>

          <el-empty v-if="selectedBills.length === 0" description="请从左侧选择货物" :image-size="80" />

          <div v-else>
            <div v-for="group in groupedByBill" :key="group.billId" class="bill-group">
              <div class="bill-group-title">{{ group.billNo }}</div>
              <el-tag
                v-for="item in group.items"
                :key="item.billItemId"
                class="bill-tag"
                closable
                @close="handleRemoveItem(item)"
              >
                <div>{{ item.goodsName }}</div>
                <div class="bill-info">分配: {{ (item.allocWeight || 0).toFixed(3) }}吨</div>
              </el-tag>
            </div>
          </div>

          <el-divider />

          <div class="summary-section">
            <h4>配载汇总</h4>
            <el-statistic title="货物明细数" :value="selectedBills.length" />
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
            <el-descriptions-item label="装车重量">{{ allocationResult.totalWeight }} 吨</el-descriptions-item>
            <el-descriptions-item label="分配金额">{{ allocationResult.totalAmount }} 元</el-descriptions-item>
            <el-descriptions-item label="货物明细数">{{ allocationResult.detailCount }} 条</el-descriptions-item>
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
    { required: true, message: "请选择装车日期", trigger: "blur" }
  ]
}

const totalAllocWeight = computed(() => {
  return selectedBills.value.reduce((sum, item) => {
    return sum + (item.allocWeight || 0)
  }, 0).toFixed(3)
})

const totalAllocAmount = computed(() => {
  return selectedBills.value.reduce((sum, item) => {
    const weight = item.allocWeight || 0
    const price = item.unitPrice || 0
    return sum + (weight * price)
  }, 0).toFixed(2)
})

const loadUtilization = computed(() => {
  if (vehicleCapacity.value <= 0) return '0.00'
  const utilization = (parseFloat(totalAllocWeight.value) / vehicleCapacity.value * 100).toFixed(2)
  return utilization
})

// 按提单号分组
const groupedByBill = computed(() => {
  const groups = {}
  selectedBills.value.forEach(item => {
    if (!groups[item.billId]) {
      groups[item.billId] = {
        billId: item.billId,
        billNo: item.billNo,
        items: []
      }
    }
    groups[item.billId].items.push(item)
  })
  return Object.values(groups)
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
    pendingBills.value.forEach(item => {
      item.allocWeight = getRemainWeightNum(item)
    })
  })
}

function handleRecommendBills() {
  recommendBills(loadCapacity.value).then(response => {
    pendingBills.value = response.data
    pendingBills.value.forEach(item => {
      item.allocWeight = getRemainWeightNum(item)
    })
    proxy.$modal.msgSuccess("已根据载重 " + loadCapacity.value + " 吨推荐合适的货物")
  })
}

function handlePendingSelectionChange(selection) {
  // 添加新选中的
  selection.forEach(item => {
    const existing = selectedBills.value.find(b => b.billItemId === item.billItemId)
    if (!existing) {
      selectedBills.value.push({
        ...item,
        allocWeight: getRemainWeightNum(item)
      })
    }
  })

  // 移除取消选中的
  const selectedIds = selection.map(b => b.billItemId)
  selectedBills.value = selectedBills.value.filter(b => selectedIds.includes(b.billItemId))
}

function handleRemoveItem(item) {
  selectedBills.value = selectedBills.value.filter(b => b.billItemId !== item.billItemId)
  proxy.$refs.pendingBillTable.toggleRowSelection(
    pendingBills.value.find(b => b.billItemId === item.billItemId),
    false
  )
}

function handleAllocWeightChange() {
  // 重量变化时自动更新统计
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
      const invalidItems = selectedBills.value.filter(b => !b.allocWeight || b.allocWeight <= 0)
      if (invalidItems.length > 0) {
        proxy.$modal.msgWarning("请为所有货物设置分配重量")
        return
      }

      // 验证载重
      if (parseFloat(totalAllocWeight.value) > vehicleCapacity.value) {
        proxy.$modal.msgWarning(`分配重量 ${totalAllocWeight.value} 吨超过车辆载重 ${vehicleCapacity.value} 吨`)
        return
      }

      const allocationItems = selectedBills.value.map(item => ({
        billId: item.billId,
        billNo: item.billNo,
        billItemId: item.billItemId,
        customerName: item.customerName,
        goodsName: item.goodsName,
        goodsModel: item.goodsModel,
        loadingAddress: item.loadingAddress,
        unloadingAddress: item.unloadingAddress,
        totalWeight: item.totalWeight,
        allocatedWeight: item.allocatedWeight,
        unitPrice: item.unitPrice,
        allocWeight: item.allocWeight
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
        selectedBills.value = []
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

.bill-group {
  margin-bottom: 15px;
}

.bill-group-title {
  font-size: 13px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  padding-left: 4px;
  border-left: 3px solid #409eff;
}

.bill-tag {
  display: block;
  width: 100%;
  margin-bottom: 6px;
  padding: 8px;
  height: auto;
  white-space: normal;
}

.bill-info {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
}

.summary-section h4 {
  margin-bottom: 15px;
  color: #606266;
}

.summary-section .el-statistic {
  margin-bottom: 15px;
}
</style>
