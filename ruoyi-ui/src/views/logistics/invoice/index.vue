<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="批次号" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable style="width: 200px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable style="width: 200px">
          <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName"
            :value="item.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="发票状态" prop="invoiceStatus">
        <el-select v-model="queryParams.invoiceStatus" placeholder="发票状态" clearable style="width: 150px">
          <el-option
            v-for="dict in logistics_batch_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleMerge"
          v-hasPermi="['logistics:invoice:merge']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Check" :disabled="single" @click="handleIssue"
          v-hasPermi="['logistics:invoice:issue']">开具</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Close" :disabled="single" @click="handleCancel"
          v-hasPermi="['logistics:invoice:cancel']">作废</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="RefreshLeft" :disabled="single" @click="handleCancelMerge"
          v-hasPermi="['logistics:invoice:merge']">取消合并</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="invoiceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="批次号" align="center" prop="batchNo" min-width="180" />
      <el-table-column label="客户" align="center" prop="customerName" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="开票日期" align="center" prop="invoiceDate" width="110" />
      <el-table-column label="订单数量" align="center" prop="orderCount" width="90" />
      <el-table-column label="开票金额(元)" align="center" prop="totalAmount" width="120">
        <template #default="scope">
          {{ scope.row.totalAmount ? scope.row.totalAmount.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="税额(元)" align="center" prop="taxAmount" width="100">
        <template #default="scope">
          {{ scope.row.taxAmount ? scope.row.taxAmount.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="发票类型" align="center" prop="invoiceType" width="100">
        <template #default="scope">
          <dict-tag :options="logistics_invoice_type" :value="scope.row.invoiceType" />
        </template>
      </el-table-column>
      <el-table-column label="发票状态" align="center" prop="invoiceStatus" width="90">
        <template #default="scope">
          <dict-tag :options="logistics_batch_status" :value="scope.row.invoiceStatus" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['logistics:invoice:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增发票对话框 -->
    <el-dialog title="新增发票" v-model="mergeOpen" width="900px" append-to-body>
      <el-form :model="mergeForm" ref="mergeRef" :rules="mergeRules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="mergeForm.customerId" placeholder="请选择客户" filterable style="width: 100%"
                @change="handleCustomerChange">
                <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName"
                  :value="item.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开票日期" prop="invoiceDate">
              <el-date-picker v-model="mergeForm.invoiceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
                style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="发票类型" prop="invoiceType">
              <el-select v-model="mergeForm.invoiceType" placeholder="请选择发票类型" style="width: 100%">
                <el-option
                  v-for="dict in logistics_invoice_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="税率(%)" prop="taxRate">
              <el-input-number v-model="mergeForm.taxRate" :min="0" :max="100" :precision="2" :controls="false"
                style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">选择订单</el-divider>

      <el-table ref="orderTableRef" :data="orderListForMerge" @selection-change="handleOrderSelectionChange"
        max-height="300">
        <el-table-column type="selection" width="55" align="center" :selectable="checkSelectable" />
        <el-table-column label="订单号" align="center" prop="orderNo" width="180" :show-overflow-tooltip="true" />
        <el-table-column label="订单日期" align="center" prop="orderDate" width="110" />
        <el-table-column label="货物" align="center" prop="goodsName" width="120" />
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
        <el-table-column label="开票状态" align="center" prop="invoiceStatus" width="90">
          <template #default="scope">
            <dict-tag :options="logistics_invoice_status" :value="scope.row.invoiceStatus" />
          </template>
        </el-table-column>
      </el-table>

      <el-descriptions :column="3" border style="margin-top: 20px">
        <el-descriptions-item label="已选订单数">{{ selectedOrders.length }}</el-descriptions-item>
        <el-descriptions-item label="开票金额(元)">{{ selectedTotalAmount.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="税额(元)">{{ selectedTaxAmount.toFixed(2) }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="mergeOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitMerge" :disabled="selectedOrders.length === 0">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 发票详情对话框 -->
    <el-dialog title="发票详情" v-model="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="批次号">{{ invoiceDetail.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ invoiceDetail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="开票日期">{{ invoiceDetail.invoiceDate }}</el-descriptions-item>
        <el-descriptions-item label="发票类型">
          <dict-tag :options="logistics_invoice_type" :value="invoiceDetail.invoiceType" />
        </el-descriptions-item>
        <el-descriptions-item label="订单数量">{{ invoiceDetail.orderCount }}</el-descriptions-item>
        <el-descriptions-item label="开票金额(元)">{{ invoiceDetail.totalAmount ? invoiceDetail.totalAmount.toFixed(2) :
          '0.00'
        }}</el-descriptions-item>
        <el-descriptions-item label="税率(%)">{{ invoiceDetail.taxRate ? invoiceDetail.taxRate.toFixed(2) : '0.00'
        }}</el-descriptions-item>
        <el-descriptions-item label="税额(元)">{{ invoiceDetail.taxAmount ? invoiceDetail.taxAmount.toFixed(2) : '0.00'
        }}</el-descriptions-item>
        <el-descriptions-item label="发票状态">
          <dict-tag :options="logistics_batch_status" :value="invoiceDetail.invoiceStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ invoiceDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ invoiceDetail.remark }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">订单明细</el-divider>

      <el-table :data="invoiceDetail.orderList" max-height="300">
        <el-table-column label="订单号" align="center" prop="orderNo" width="180" :show-overflow-tooltip="true" />
        <el-table-column label="订单日期" align="center" prop="orderDate" width="110" />
        <el-table-column label="货物" align="center" prop="goodsName" width="120" />
        <el-table-column label="重量(吨)" align="center" prop="weight" width="90">
          <template #default="scope">
            {{ scope.row.weight ? scope.row.weight.toFixed(3) : '0.000' }}
          </template>
        </el-table-column>
        <el-table-column label="金额(元)" align="center" prop="amount" width="100">
          <template #default="scope">
            {{ scope.row.amount ? scope.row.amount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Invoice">
import { listInvoiceBatch, getInvoiceBatch, mergeInvoice, issueInvoice, cancelInvoice, cancelMerge, listInvoiceableOrders, delInvoiceBatch } from "@/api/logistics/invoice"
import { listCustomer } from "@/api/logistics/customer"
import { useDict } from '@/utils/dict'

const { proxy } = getCurrentInstance()
const { logistics_batch_status, logistics_invoice_type, logistics_invoice_status } = useDict('logistics_batch_status', 'logistics_invoice_type', 'logistics_invoice_status')

const invoiceList = ref([])
const orderListForMerge = ref([])
const customerOptions = ref([])
const selectedOrders = ref([])
const open = ref(false)
const mergeOpen = ref(false)
const detailOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const invoiceDetail = ref({})
const route = useRoute()

const selectedTotalAmount = computed(() => {
  return selectedOrders.value.reduce((sum, item) => sum + (item.totalAmount || 0), 0)
})

const selectedTaxAmount = computed(() => {
  return selectedTotalAmount.value * (mergeForm.taxRate / 100)
})

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    batchNo: null,
    customerId: null,
    invoiceStatus: null
  },
  mergeForm: {
    customerId: null,
    invoiceDate: new Date().toISOString().split('T')[0],
    invoiceType: 'ordinary',
    taxRate: 0
  },
  mergeRules: {
    customerId: [
      { required: true, message: "请选择客户", trigger: "change" }
    ],
    invoiceDate: [
      { required: true, message: "请选择开票日期", trigger: "change" }
    ],
    invoiceType: [
      { required: true, message: "请选择发票类型", trigger: "change" }
    ]
  }
})

const { queryParams, mergeForm, mergeRules } = toRefs(data)

function getList() {
  loading.value = true
  listInvoiceBatch(queryParams.value).then(response => {
    invoiceList.value = response.rows
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
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleMerge() {
  selectedOrders.value = []
  orderListForMerge.value = []
  mergeForm.value = {
    customerId: null,
    invoiceDate: new Date().toISOString().split('T')[0],
    invoiceType: 'ordinary',
    taxRate: 0
  }
  mergeOpen.value = true
}

function handleCustomerChange(customerId) {
  if (customerId) {
    listInvoiceableOrders({ customerId: customerId, invoiceStatus: 'not_invoiced', orderStatus: 'completed', pageNum: 1, pageSize: 1000 }).then(response => {
      orderListForMerge.value = response.rows || []
    })
  } else {
    orderListForMerge.value = []
  }
  selectedOrders.value = []
}

function handleOrderSelectionChange(selection) {
  selectedOrders.value = selection
}

function checkSelectable(row) {
  return row.invoiceStatus === 'not_invoiced'
}

function calculateTax() {
  // 税额会在 computed 中自动计算
}

function submitMerge() {
  proxy.$refs.mergeRef.validate(valid => {
    if (valid) {
      if (selectedOrders.value.length === 0) {
        proxy.$modal.msgWarning("请至少选择一个订单")
        return
      }
      const orderIds = selectedOrders.value.map(item => item.orderId)
      const data = {
        ...mergeForm.value,
        orderIds: orderIds
      }
      mergeInvoice(data).then(response => {
        proxy.$modal.msgSuccess("合并开票成功")
        mergeOpen.value = false
        getList()
      })
    }
  })
}

function handleIssue() {
  const batchId = ids.value[0]
  proxy.$modal.confirm('确认要开具该发票吗？').then(function () {
    return issueInvoice(batchId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("发票开具成功")
  }).catch(() => { })
}

function handleCancel() {
  const batchId = ids.value[0]
  proxy.$modal.confirm('确认要作废该发票吗？').then(function () {
    return cancelInvoice(batchId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("发票作废成功")
  }).catch(() => { })
}

function handleCancelMerge() {
  const batchId = ids.value[0]
  proxy.$modal.confirm('确认要取消该发票合并吗？取消后订单将恢复为未开票状态。').then(function () {
    return cancelMerge(batchId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("取消合并成功")
  }).catch(() => { })
}

function handleView(row) {
  getInvoiceBatch(row.batchId).then(response => {
    invoiceDetail.value = response.data
    detailOpen.value = true
  })
}

function handleDelete(row) {
  const batchId = row.batchId
  proxy.$modal.confirm('确认要删除该发票批次吗？').then(function () {
    return delInvoiceBatch(batchId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.batchId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

getList()
getCustomerList()
</script>
