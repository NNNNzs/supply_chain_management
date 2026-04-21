<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable style="width: 200px">
          <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="订单日期" prop="orderDate">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 240px"></el-date-picker>
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="订单状态" clearable style="width: 150px">
          <el-option label="待运输" value="pending" />
          <el-option label="运输中" value="transporting" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['logistics:order:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['logistics:order:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['logistics:order:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Upload" @click="handleImport" v-hasPermi="['logistics:order:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['logistics:order:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单号" align="center" prop="orderNo" width="180">
        <template #default="scope">
          <el-link type="primary" @click="handleViewDetail(scope.row)" :underline="false">
            {{ scope.row.orderNo }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="订单日期" align="center" prop="orderDate" width="110" />
      <el-table-column label="客户" align="center" prop="customerName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="装货地址" align="center" prop="loadingAddress" :show-overflow-tooltip="true" />
      <el-table-column label="卸货地址" align="center" prop="unloadingAddress" :show-overflow-tooltip="true" />
      <el-table-column label="货物" align="center" prop="goodsName" width="100" />
      <el-table-column label="重量(吨)" align="center" prop="weight" width="90">
        <template #default="scope">
          {{ scope.row.weight ? scope.row.weight.toFixed(3) : '0.000' }}
        </template>
      </el-table-column>
      <el-table-column label="金额(元)" align="center" prop="totalAmount" width="100">
        <template #default="scope">
          {{ scope.row.totalAmount ? scope.row.totalAmount.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="车牌号" align="center" prop="vehiclePlate" width="100" />
      <el-table-column label="订单状态" align="center" prop="orderStatus" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.orderStatus === 'pending'" type="info">待运输</el-tag>
          <el-tag v-else-if="scope.row.orderStatus === 'transporting'" type="primary">运输中</el-tag>
          <el-tag v-else-if="scope.row.orderStatus === 'completed'" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.orderStatus === 'cancelled'" type="danger">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="结算状态" align="center" prop="settlementStatus" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.settlementStatus === 'unsettled'" type="warning">未结算</el-tag>
          <el-tag v-else-if="scope.row.settlementStatus === 'partial'" type="primary">部分结算</el-tag>
          <el-tag v-else-if="scope.row.settlementStatus === 'settled'" type="success">已结算</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开票状态" align="center" prop="invoiceStatus" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.invoiceStatus === 'not_invoiced'" type="info">未开票</el-tag>
          <el-tag v-else-if="scope.row.invoiceStatus === 'invoiced'" type="success">已开票</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:order:edit']">修改</el-button>
          <el-dropdown v-if="scope.row.orderStatus !== 'completed' && scope.row.orderStatus !== 'cancelled'" v-hasPermi="['logistics:order:edit']">
            <el-button link type="primary" icon="MoreFilled" style="margin-left: 8px; margin-right: 8px;">
              状态
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleChangeStatus(scope.row, 'transporting')" v-if="scope.row.orderStatus === 'pending'">设为运输中</el-dropdown-item>
                <el-dropdown-item @click="handleChangeStatus(scope.row, 'completed')" v-if="scope.row.orderStatus === 'transporting'">设为已完成</el-dropdown-item>
                <el-dropdown-item @click="handleChangeStatus(scope.row, 'cancelled')" v-if="scope.row.orderStatus === 'pending' || scope.row.orderStatus === 'transporting'">取消订单</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:order:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 订单导入对话框 -->
    <excel-import-dialog ref="importRef" title="订单导入" action="/logistics/order/importData" template-action="/logistics/order/importTemplate" template-file-name="order_template" update-support-label="是否更新已经存在的订单数据" @success="getList" />
  </div>
</template>

<script setup name="Order">
import { listOrder, delOrder, exportOrder, changeOrderStatus } from "@/api/logistics/order"
import { listCustomer } from "@/api/logistics/customer"
import ExcelImportDialog from "@/components/ExcelImportDialog"
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'

const router = useRouter()
const { proxy } = getCurrentInstance()

const orderList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dateRange = ref([])
const customerOptions = ref([])

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: null,
    customerId: null,
    orderStatus: null
  }
})

const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  const params = proxy.addDateRange(queryParams.value, dateRange.value, 'orderDate')
  listOrder(params).then(response => {
    orderList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function getCustomerList() {
  listCustomer({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    customerOptions.value = response.rows
  })
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
  router.push('/logistics/order/form')
}

function handleUpdate(row) {
  const orderId = row.orderId || ids.value[0]
  router.push({
    path: '/logistics/order/form',
    query: { orderId }
  })
}

function handleDelete(row) {
  const orderIds = row.orderId ? [row.orderId] : ids.value
  proxy.$modal.confirm('是否确认删除订单编号为"' + orderIds + '"的数据项？').then(function() {
    return delOrder(orderIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleExport() {
  proxy.download('logistics/order/export', {
    ...queryParams.value
  }, `order_${new Date().getTime()}.xlsx`)
}

function handleImport() {
  proxy.$refs.importRef.show()
}

function handleChangeStatus(row, status) {
  const statusMap = {
    'transporting': '运输中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  const statusText = statusMap[status]
  proxy.$modal.confirm(`确认要将订单"${row.orderNo}"的状态更改为"${statusText}"吗？`).then(function() {
    return changeOrderStatus(row.orderId, status)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("状态更改成功")
  }).catch(() => {})
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.orderId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 查看订单详情
function handleViewDetail(row) {
  router.push({
    path: '/logistics/order/detail',
    query: { orderId: row.orderId }
  })
}

onMounted(() => {
  getCustomerList()
  getList()
})
</script>
