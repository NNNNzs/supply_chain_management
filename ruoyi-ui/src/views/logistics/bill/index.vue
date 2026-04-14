<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="提单号" prop="billNo">
        <el-input v-model="queryParams.billNo" placeholder="请输入提单号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable style="width: 200px">
          <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="提单日期" prop="billDate">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 240px"></el-date-picker>
      </el-form-item>
      <el-form-item label="提单状态" prop="billStatus">
        <el-select v-model="queryParams.billStatus" placeholder="提单状态" clearable style="width: 150px">
          <el-option label="待配载" value="pending" />
          <el-option label="部分配载" value="partial" />
          <el-option label="已配载" value="allocated" />
          <el-option label="运输中" value="transporting" />
          <el-option label="已完成" value="completed" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['logistics:bill:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['logistics:bill:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['logistics:bill:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Upload" @click="handleImport" v-hasPermi="['logistics:bill:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['logistics:bill:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="billList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="提单号" align="center" prop="billNo" width="180" :show-overflow-tooltip="true" />
      <el-table-column label="提单日期" align="center" prop="billDate" width="110" />
      <el-table-column label="客户" align="center" prop="customerName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="装货地址" align="center" prop="loadingAddress" :show-overflow-tooltip="true" />
      <el-table-column label="卸货地址" align="center" prop="unloadingAddress" :show-overflow-tooltip="true" />
      <el-table-column label="货物" align="center" prop="goodsName" width="100" />
      <el-table-column label="总重量(吨)" align="center" prop="totalWeight" width="100">
        <template #default="scope">
          {{ scope.row.totalWeight ? scope.row.totalWeight.toFixed(3) : '0.000' }}
        </template>
      </el-table-column>
      <el-table-column label="已分配(吨)" align="center" prop="allocatedWeight" width="100">
        <template #default="scope">
          {{ scope.row.allocatedWeight ? scope.row.allocatedWeight.toFixed(3) : '0.000' }}
        </template>
      </el-table-column>
      <el-table-column label="剩余(吨)" align="center" width="90">
        <template #default="scope">
          {{ getRemainWeight(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="运价(元/吨)" align="center" prop="unitPrice" width="100">
        <template #default="scope">
          {{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="总金额(元)" align="center" prop="totalAmount" width="110">
        <template #default="scope">
          {{ scope.row.totalAmount ? scope.row.totalAmount.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="提单状态" align="center" prop="billStatus" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.billStatus === 'pending'" type="info">待配载</el-tag>
          <el-tag v-else-if="scope.row.billStatus === 'partial'" type="warning">部分配载</el-tag>
          <el-tag v-else-if="scope.row.billStatus === 'allocated'" type="primary">已配载</el-tag>
          <el-tag v-else-if="scope.row.billStatus === 'transporting'" type="primary">运输中</el-tag>
          <el-tag v-else-if="scope.row.billStatus === 'completed'" type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:bill:edit']">修改</el-button>
          <el-button link type="primary" icon="View" @click="handleViewAllocations(scope.row)">查看分配</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:bill:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 提单导入对话框 -->
    <excel-import-dialog ref="importRef" title="提单导入" action="/logistics/bill/importData" template-action="/logistics/bill/importTemplate" template-file-name="bill_template" update-support-label="是否更新已经存在的提单数据" @success="getList" />

    <!-- 添加或修改提单对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form ref="billRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="提单号" prop="billNo">
              <el-input v-model="form.billNo" placeholder="请输入提单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提单日期" prop="billDate">
              <el-date-picker v-model="form.billDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" style="width: 100%">
                <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物" prop="goodsId">
              <el-select v-model="form.goodsId" placeholder="请选择货物" style="width: 100%">
                <el-option v-for="item in goodsOptions" :key="item.goodsId" :label="item.goodsName" :value="item.goodsId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="装货地址" prop="loadingAddress">
              <el-input v-model="form.loadingAddress" placeholder="请输入装货地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="卸货地址" prop="unloadingAddress">
              <el-input v-model="form.unloadingAddress" placeholder="请输入卸货地址" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总重量(吨)" prop="totalWeight">
              <el-input-number v-model="form.totalWeight" :min="0" :precision="3" placeholder="请输入总重量" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="运价(元/吨)" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" placeholder="请输入运价" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总金额(元)" prop="totalAmount">
              <el-input-number v-model="form.totalAmount" :min="0" :precision="2" placeholder="自动计算" style="width: 100%" :disabled="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="要求完成日期" prop="requireDate">
              <el-date-picker v-model="form.requireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="普通" value="normal" />
                <el-option label="紧急" value="urgent" />
                <el-option label="非常紧急" value="critical" />
              </el-select>
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

    <!-- 提单分配明细对话框 -->
    <el-dialog title="提单分配明细" v-model="allocationDialogVisible" width="800px" append-to-body>
      <el-table :data="allocationList">
        <el-table-column label="运单号" align="center" prop="orderNo" width="180" />
        <el-table-column label="驾驶员单号" align="center" prop="driverOrderNo" width="180" />
        <el-table-column label="分配重量(吨)" align="center" prop="allocatedWeight" width="120">
          <template #default="scope">
            {{ scope.row.allocatedWeight ? scope.row.allocatedWeight.toFixed(3) : '0.000' }}
          </template>
        </el-table-column>
        <el-table-column label="分配金额(元)" align="center" prop="allocatedAmount" width="120">
          <template #default="scope">
            {{ scope.row.allocatedAmount ? scope.row.allocatedAmount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="分配时间" align="center" prop="createTime" width="160" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Bill">
import { listBill, getBill, delBill, addBill, updateBill, exportBill, getBillAllocations } from "@/api/logistics/bill"
import { listCustomer } from "@/api/logistics/customer"
import { listGoods } from "@/api/logistics/goods"
import ExcelImportDialog from "@/components/ExcelImportDialog"

const { proxy } = getCurrentInstance()

const billList = ref([])
const allocationList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dateRange = ref([])
const customerOptions = ref([])
const goodsOptions = ref([])
const open = ref(false)
const title = ref("")
const allocationDialogVisible = ref(false)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    billNo: null,
    customerId: null,
    billStatus: null
  },
  rules: {
    billNo: [
      { required: true, message: "提单号不能为空", trigger: "blur" }
    ],
    billDate: [
      { required: true, message: "提单日期不能为空", trigger: "blur" }
    ],
    customerId: [
      { required: true, message: "客户不能为空", trigger: "change" }
    ],
    loadingAddress: [
      { required: true, message: "装货地址不能为空", trigger: "blur" }
    ],
    unloadingAddress: [
      { required: true, message: "卸货地址不能为空", trigger: "blur" }
    ],
    totalWeight: [
      { required: true, message: "总重量不能为空", trigger: "blur" }
    ],
    unitPrice: [
      { required: true, message: "运价不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 监听总重量和运价变化，自动计算总金额
watch([() => form.value.totalWeight, () => form.value.unitPrice], ([newWeight, newPrice]) => {
  if (newWeight != null && newPrice != null) {
    form.value.totalAmount = (newWeight * newPrice).toFixed(2)
  }
})

function getList() {
  loading.value = true
  const params = proxy.addDateRange(queryParams.value, dateRange.value, 'billDate')
  listBill(params).then(response => {
    billList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function getCustomerList() {
  listCustomer({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    customerOptions.value = response.rows
  })
}

function getGoodsList() {
  listGoods({ pageNum: 1, pageSize: 1000 }).then(response => {
    goodsOptions.value = response.rows
  })
}

function getRemainWeight(row) {
  if (row.totalWeight != null && row.allocatedWeight != null) {
    return (row.totalWeight - row.allocatedWeight).toFixed(3)
  }
  return row.totalWeight ? row.totalWeight.toFixed(3) : '0.000'
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
  title.value = "添加提单"
}

function handleUpdate(row) {
  reset()
  const billId = row.billId || ids.value[0]
  getBill(billId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改提单"
  })
}

function handleViewAllocations(row) {
  const billId = row.billId
  getBillAllocations(billId).then(response => {
    allocationList.value = response.data
    allocationDialogVisible.value = true
  })
}

function submitForm() {
  proxy.$refs["billRef"].validate(valid => {
    if (valid) {
      if (form.value.billId != null) {
        updateBill(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addBill(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const billIds = row.billId ? [row.billId] : ids.value
  proxy.$modal.confirm('是否确认删除提单编号为"' + billIds + '"的数据项？').then(function() {
    return delBill(billIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleExport() {
  proxy.download('logistics/bill/export', {
    ...queryParams.value
  }, `bill_${new Date().getTime()}.xlsx`)
}

function handleImport() {
  proxy.$refs.importRef.show()
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    billId: null,
    billNo: null,
    billDate: null,
    customerId: null,
    goodsId: null,
    loadingAddress: null,
    unloadingAddress: null,
    totalWeight: null,
    unitPrice: null,
    totalAmount: null,
    priority: 'normal',
    requireDate: null,
    remark: null
  }
  proxy.resetForm("billRef")
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.billId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

getList()
getCustomerList()
getGoodsList()
</script>
