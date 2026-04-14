<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="驾驶员单号" prop="driverOrderNo">
        <el-input v-model="queryParams.driverOrderNo" placeholder="请输入驾驶员单号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="司机" prop="driverId">
        <el-select v-model="queryParams.driverId" placeholder="请选择司机" clearable style="width: 150px">
          <el-option v-for="item in driverOptions" :key="item.driverId" :label="item.driverName" :value="item.driverId" />
        </el-select>
      </el-form-item>
      <el-form-item label="车牌号" prop="vehiclePlate">
        <el-input v-model="queryParams.vehiclePlate" placeholder="请输入车牌号" clearable style="width: 150px" />
      </el-form-item>
      <el-form-item label="派车日期" prop="dispatchDate">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 240px"></el-date-picker>
      </el-form-item>
      <el-form-item label="派车状态" prop="dispatchStatus">
        <el-select v-model="queryParams.dispatchStatus" placeholder="派车状态" clearable style="width: 130px">
          <el-option label="待出车" value="pending" />
          <el-option label="运输中" value="transporting" />
          <el-option label="已完成" value="completed" />
        </el-select>
      </el-form-item>
      <el-form-item label="结算状态" prop="settlementStatus">
        <el-select v-model="queryParams.settlementStatus" placeholder="结算状态" clearable style="width: 130px">
          <el-option label="未结算" value="unsettled" />
          <el-option label="已结算" value="settled" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['logistics:driverOrder:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['logistics:driverOrder:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['logistics:driverOrder:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['logistics:driverOrder:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="driverOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="驾驶员单号" align="center" prop="driverOrderNo" width="160" :show-overflow-tooltip="true" />
      <el-table-column label="运单号" align="center" prop="orderNo" width="160" :show-overflow-tooltip="true" />
      <el-table-column label="司机" align="center" prop="driverName" width="100" />
      <el-table-column label="司机电话" align="center" prop="driverPhone" width="120" />
      <el-table-column label="车牌号" align="center" prop="vehiclePlate" width="100" />
      <el-table-column label="载重(吨)" align="center" prop="loadCapacity" width="80" />
      <el-table-column label="实载(吨)" align="center" prop="actualWeight" width="80">
        <template #default="scope">
          {{ scope.row.actualWeight ? scope.row.actualWeight.toFixed(3) : '0.000' }}
        </template>
      </el-table-column>
      <el-table-column label="派车日期" align="center" prop="dispatchDate" width="110" />
      <el-table-column label="派车状态" align="center" prop="dispatchStatus" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.dispatchStatus === 'pending'" type="info">待出车</el-tag>
          <el-tag v-else-if="scope.row.dispatchStatus === 'transporting'" type="primary">运输中</el-tag>
          <el-tag v-else-if="scope.row.dispatchStatus === 'completed'" type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="结算状态" align="center" prop="settlementStatus" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.settlementStatus === 'unsettled'" type="warning">未结算</el-tag>
          <el-tag v-else-if="scope.row.settlementStatus === 'settled'" type="success">已结算</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="回单状态" align="center" prop="receiptStatus" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.receiptStatus === 'not_received'" type="info">未回单</el-tag>
          <el-tag v-else-if="scope.row.receiptStatus === 'received'" type="success">已回单</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:driverOrder:edit']">修改</el-button>
          <el-dropdown v-if="scope.row.dispatchStatus !== 'completed'" v-hasPermi="['logistics:driverOrder:edit']">
            <el-button link type="primary" icon="Refresh">
              更改状态
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleChangeStatus(scope.row, 'transporting')" v-if="scope.row.dispatchStatus === 'pending'">设为运输中</el-dropdown-item>
                <el-dropdown-item @click="handleChangeStatus(scope.row, 'completed')" v-if="scope.row.dispatchStatus === 'transporting'">设为已完成</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:driverOrder:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改驾驶员单对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form ref="driverOrderRef" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="运单" prop="orderId">
              <el-select v-model="form.orderId" placeholder="请选择运单" style="width: 100%" filterable>
                <el-option v-for="item in orderOptions" :key="item.orderId" :label="item.orderNo" :value="item.orderId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机" prop="driverId">
              <el-select v-model="form.driverId" placeholder="请选择司机" style="width: 100%" filterable @change="handleDriverChange">
                <el-option v-for="item in driverOptions" :key="item.driverId" :label="item.driverName" :value="item.driverId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机电话" prop="driverPhone">
              <el-input v-model="form.driverPhone" placeholder="选择司机后自动填充" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车辆" prop="vehicleId">
              <el-select v-model="form.vehicleId" placeholder="请选择车辆" style="width: 100%" filterable @change="handleVehicleChange">
                <el-option v-for="item in vehicleOptions" :key="item.vehicleId" :label="item.vehiclePlate" :value="item.vehicleId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车牌号" prop="vehiclePlate">
              <el-input v-model="form.vehiclePlate" placeholder="选择车辆后自动填充" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车辆载重(吨)" prop="loadCapacity">
              <el-input-number v-model="form.loadCapacity" :min="0" :precision="2" style="width: 100%" :disabled="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际装车重量" prop="actualWeight">
              <el-input-number v-model="form.actualWeight" :min="0" :precision="3" placeholder="请输入实际装车重量" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="派车日期" prop="dispatchDate">
              <el-date-picker v-model="form.dispatchDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="派车状态" prop="dispatchStatus">
              <el-select v-model="form.dispatchStatus" placeholder="请选择派车状态" style="width: 100%">
                <el-option label="待出车" value="pending" />
                <el-option label="运输中" value="transporting" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结算状态" prop="settlementStatus">
              <el-select v-model="form.settlementStatus" placeholder="请选择结算状态" style="width: 100%">
                <el-option label="未结算" value="unsettled" />
                <el-option label="已结算" value="settled" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="回单状态" prop="receiptStatus">
              <el-select v-model="form.receiptStatus" placeholder="请选择回单状态" style="width: 100%">
                <el-option label="未回单" value="not_received" />
                <el-option label="已回单" value="received" />
              </el-select>
            </el-form-item>
          </el-col>
          <!-- 财务信息 -->
          <el-col :span="24">
            <el-divider>财务信息</el-divider>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配载单价" prop="loadingUnitPrice">
              <el-input-number v-model="form.loadingUnitPrice" :min="0" :precision="2" placeholder="请输入配载单价" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运费支出" prop="freightCost">
              <el-input-number v-model="form.freightCost" :min="0" :precision="2" placeholder="请输入运费支出" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代垫付金额" prop="advancePayment">
              <el-input-number v-model="form.advancePayment" :min="0" :precision="2" placeholder="请输入代垫付金额" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款方式" prop="paymentMethod">
              <el-select v-model="form.paymentMethod" placeholder="请选择付款方式" style="width: 100%">
                <el-option label="现金" value="cash" />
                <el-option label="银行转账" value="bank_transfer" />
                <el-option label="微信" value="wechat" />
                <el-option label="支付宝" value="alipay" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收款人" prop="payee">
              <el-input v-model="form.payee" placeholder="请输入收款人" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="DriverOrder">
import { listDriverOrder, getDriverOrder, delDriverOrder, addDriverOrder, updateDriverOrder, exportDriverOrder, updateDriverOrderStatus } from "@/api/logistics/driver-order"
import { listDriver } from "@/api/logistics/driver"
import { listVehicle } from "@/api/logistics/vehicle"
import { listOrder } from "@/api/logistics/order"

const { proxy } = getCurrentInstance()

const driverOrderList = ref([])
const driverOptions = ref([])
const vehicleOptions = ref([])
const orderOptions = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dateRange = ref([])
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    driverOrderNo: null,
    driverId: null,
    vehiclePlate: null,
    dispatchStatus: null,
    settlementStatus: null
  },
  rules: {
    orderId: [
      { required: true, message: "运单不能为空", trigger: "change" }
    ],
    driverId: [
      { required: true, message: "司机不能为空", trigger: "change" }
    ],
    vehicleId: [
      { required: true, message: "车辆不能为空", trigger: "change" }
    ],
    actualWeight: [
      { required: true, message: "实际装车重量不能为空", trigger: "blur" }
    ],
    dispatchDate: [
      { required: true, message: "派车日期不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  const params = proxy.addDateRange(queryParams.value, dateRange.value, 'dispatchDate')
  listDriverOrder(params).then(response => {
    driverOrderList.value = response.rows
    total.value = response.total
    loading.value = false
  })
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

function getOrderList() {
  listOrder({ pageNum: 1, pageSize: 1000 }).then(response => {
    orderOptions.value = response.rows
  })
}

function handleDriverChange(driverId) {
  const driver = driverOptions.value.find(d => d.driverId === driverId)
  if (driver) {
    form.value.driverName = driver.driverName
    form.value.driverPhone = driver.driverPhone
  }
}

function handleVehicleChange(vehicleId) {
  const vehicle = vehicleOptions.value.find(v => v.vehicleId === vehicleId)
  if (vehicle) {
    form.value.vehiclePlate = vehicle.vehiclePlate
    form.value.loadCapacity = vehicle.loadCapacity
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加驾驶员单"
}

function handleUpdate(row) {
  reset()
  const driverOrderId = row.driverOrderId || ids.value[0]
  getDriverOrder(driverOrderId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改驾驶员单"
  })
}

function handleChangeStatus(row, status) {
  const statusMap = {
    'transporting': '运输中',
    'completed': '已完成'
  }
  const statusText = statusMap[status]
  proxy.$modal.confirm(`确认要将驾驶员单"${row.driverOrderNo}"的状态更改为"${statusText}"吗？`).then(function() {
    return updateDriverOrderStatus(row.driverOrderId, status)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("状态更改成功")
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs["driverOrderRef"].validate(valid => {
    if (valid) {
      if (form.value.driverOrderId != null) {
        updateDriverOrder(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addDriverOrder(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const driverOrderIds = row.driverOrderId ? [row.driverOrderId] : ids.value
  proxy.$modal.confirm('是否确认删除驾驶员单编号为"' + driverOrderIds + '"的数据项？').then(function() {
    return delDriverOrder(driverOrderIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleExport() {
  proxy.download('logistics/driverOrder/export', {
    ...queryParams.value
  }, `driver_order_${new Date().getTime()}.xlsx`)
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    driverOrderId: null,
    orderId: null,
    driverId: null,
    driverName: null,
    driverPhone: null,
    vehicleId: null,
    vehiclePlate: null,
    loadCapacity: null,
    actualWeight: null,
    dispatchDate: null,
    dispatchStatus: 'pending',
    settlementStatus: 'unsettled',
    receiptStatus: 'not_received',
    loadingUnitPrice: 0,
    freightCost: 0,
    advancePayment: 0,
    paymentMethod: null,
    payee: null,
    remark: null
  }
  proxy.resetForm("driverOrderRef")
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.driverOrderId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

getList()
getDriverList()
getVehicleList()
getOrderList()
</script>
