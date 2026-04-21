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
        <el-button type="info" plain icon="Upload" @click="handleImport" v-hasPermi="['logistics:order:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['logistics:order:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Document" :disabled="multiple" @click="handleMergeInvoice" v-hasPermi="['logistics:invoice:merge']">合并开票</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="DocumentAdd" :disabled="multiple" @click="handleAddReceipt" v-hasPermi="['logistics:receipt:add']">新增回单</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 合并开票对话框 -->
    <el-dialog title="合并开票" v-model="invoiceOpen" width="900px" append-to-body>
      <el-form :model="invoiceForm" ref="invoiceRef" :rules="invoiceRules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="开票日期" prop="invoiceDate">
              <el-date-picker v-model="invoiceForm.invoiceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发票类型" prop="invoiceType">
              <el-select v-model="invoiceForm.invoiceType" placeholder="请选择发票类型" style="width: 100%">
                <el-option label="普通发票" value="ordinary" />
                <el-option label="增值税发票" value="vat" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="税率(%)" prop="taxRate">
              <el-input-number v-model="invoiceForm.taxRate" :min="0" :max="100" :precision="2" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">已选订单</el-divider>

      <el-table :data="selectedOrders" max-height="300">
        <el-table-column label="订单号" align="center" prop="orderNo" width="180" :show-overflow-tooltip="true" />
        <el-table-column label="订单日期" align="center" prop="orderDate" width="110" />
        <el-table-column label="客户" align="center" prop="customerName" width="120" />
        <el-table-column label="货物" align="center" prop="goodsName" width="120" />
        <el-table-column label="金额(元)" align="center" prop="totalAmount" width="100">
          <template #default="scope">
            {{ scope.row.totalAmount ? scope.row.totalAmount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
      </el-table>

      <el-descriptions :column="3" border style="margin-top: 20px">
        <el-descriptions-item label="已选订单数">{{ selectedOrders.length }}</el-descriptions-item>
        <el-descriptions-item label="开票金额(元)">{{ invoiceTotalAmount.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="税额(元)">{{ invoiceTaxAmount.toFixed(2) }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="invoiceOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitMergeInvoice">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" fixed />
      <el-table-column label="订单号" align="center" prop="orderNo" width="180" fixed>
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

    <!-- 新增回单对话框 -->
    <receipt-dialog v-model="receiptDialogVisible" :order-id="selectedOrderId" @success="handleReceiptSuccess" />
  </div>
</template>

<script setup name="Order">
import { listOrder, delOrder, exportOrder, changeOrderStatus } from "@/api/logistics/order"
import { listCustomer } from "@/api/logistics/customer"
import { mergeInvoice } from "@/api/logistics/invoice"
import ExcelImportDialog from "@/components/ExcelImportDialog"
import ReceiptDialog from "@/components/ReceiptDialog/index.vue"
import { useRouter } from 'vue-router'
import { onMounted, computed } from 'vue'

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
const selectedOrders = ref([])
const invoiceOpen = ref(false)
const receiptDialogVisible = ref(false)
const selectedOrderId = ref(null)

const invoiceTotalAmount = computed(() => {
  return selectedOrders.value.reduce((sum, item) => sum + (item.totalAmount || 0), 0)
})

const invoiceTaxAmount = computed(() => {
  return invoiceTotalAmount.value * (invoiceForm.taxRate / 100)
})

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: null,
    customerId: null,
    orderStatus: null
  },
  invoiceForm: {
    customerId: null,
    invoiceDate: new Date().toISOString().split('T')[0],
    invoiceType: 'ordinary',
    taxRate: 0
  },
  invoiceRules: {
    invoiceDate: [
      { required: true, message: "请选择开票日期", trigger: "change" }
    ],
    invoiceType: [
      { required: true, message: "请选择发票类型", trigger: "change" }
    ]
  }
})

const { queryParams, invoiceForm, invoiceRules } = toRefs(data)

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
  selectedOrders.value = selection
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 合并开票
function handleMergeInvoice() {
  if (selectedOrders.value.length === 0) {
    proxy.$modal.msgWarning("请选择要开票的订单")
    return
  }

  // 验证订单状态
  const invalidOrders = selectedOrders.value.filter(order => order.orderStatus !== 'completed')
  if (invalidOrders.length > 0) {
    proxy.$modal.msgWarning("只有已完成的订单才能开票")
    return
  }

  const alreadyInvoiced = selectedOrders.value.filter(order => order.invoiceStatus === 'invoiced')
  if (alreadyInvoiced.length > 0) {
    proxy.$modal.msgWarning("所选订单中存在已开票的订单")
    return
  }

  // 检查是否是同一客户
  const customers = [...new Set(selectedOrders.value.map(order => order.customerId))]
  if (customers.length > 1) {
    proxy.$modal.msgWarning("所选订单必须属于同一客户")
    return
  }

  // 设置客户ID并打开弹窗
  invoiceForm.value.customerId = customers[0]
  invoiceOpen.value = true
}

// 提交合并开票
function submitMergeInvoice() {
  proxy.$refs.invoiceRef.validate(valid => {
    if (valid) {
      const orderIds = selectedOrders.value.map(item => item.orderId)
      const data = {
        ...invoiceForm.value,
        orderIds: orderIds
      }
      mergeInvoice(data).then(response => {
        proxy.$modal.msgSuccess("合并开票成功")
        invoiceOpen.value = false
        getList()
      })
    }
  })
}

// 查看订单详情
function handleViewDetail(row) {
  router.push({
    path: '/logistics/order/detail',
    query: { orderId: row.orderId }
  })
}

// 新增回单
function handleAddReceipt() {
  if (selectedOrders.value.length !== 1) {
    proxy.$modal.msgWarning("请选择一个订单")
    return
  }

  const order = selectedOrders.value[0]
  if (order.orderStatus !== 'transporting') {
    proxy.$modal.msgWarning("只有运输中的订单才能新增回单")
    return
  }

  selectedOrderId.value = order.orderId
  receiptDialogVisible.value = true
}

// 回单操作成功回调
function handleReceiptSuccess() {
  getList()
}

onMounted(() => {
  getCustomerList()
  getList()
})
</script>
