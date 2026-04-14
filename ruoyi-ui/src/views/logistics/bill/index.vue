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

    <el-table v-loading="loading" :data="billList" @selection-change="handleSelectionChange" row-key="billId">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="expand">
        <template #default="{ row }">
          <div style="padding: 10px 20px;">
            <h4 style="margin: 0 0 10px 0; color: #606266;">货物明细</h4>
            <el-table :data="row.billItems || []" size="small" border>
              <el-table-column label="货物名称" prop="goodsName" />
              <el-table-column label="型号" prop="goodsModel" width="120" />
              <el-table-column label="重量(吨)" prop="weight" width="120" align="center">
                <template #default="scope">
                  {{ scope.row.weight ? scope.row.weight.toFixed(3) : '0.000' }}
                </template>
              </el-table-column>
              <el-table-column label="已分配(吨)" prop="allocatedWeight" width="120" align="center">
                <template #default="scope">
                  {{ scope.row.allocatedWeight ? scope.row.allocatedWeight.toFixed(3) : '0.000' }}
                </template>
              </el-table-column>
              <el-table-column label="剩余(吨)" width="110" align="center">
                <template #default="scope">
                  {{ getRemainWeight(scope.row) }}
                </template>
              </el-table-column>
              <el-table-column label="运价(元/吨)" prop="unitPrice" width="120" align="center">
                <template #default="scope">
                  {{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(2) : '0.00' }}
                </template>
              </el-table-column>
              <el-table-column label="金额(元)" prop="amount" width="120" align="center">
                <template #default="scope">
                  {{ scope.row.amount ? scope.row.amount.toFixed(2) : '0.00' }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="提单号" align="center" prop="billNo" width="180" :show-overflow-tooltip="true" />
      <el-table-column label="提单日期" align="center" prop="billDate" width="110" />
      <el-table-column label="客户" align="center" prop="customerName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="装货地址" align="center" prop="loadingAddress" :show-overflow-tooltip="true" />
      <el-table-column label="卸货地址" align="center" prop="unloadingAddress" :show-overflow-tooltip="true" />
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
          {{ getBillRemainWeight(scope.row) }}
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
    <el-dialog :title="title" v-model="open" width="1000px" append-to-body>
      <el-form ref="billRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="提单号" prop="billNo">
              <el-input v-model="form.billNo" placeholder="请输入提单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提单日期" prop="billDate">
              <el-date-picker v-model="form.billDate" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" style="width: 100%">
                <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
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
          <el-col :span="12">
            <el-form-item label="要求完成日期" prop="requireDate">
              <el-date-picker v-model="form.requireDate" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
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
        </el-row>

        <el-divider content-position="left">货物明细</el-divider>

        <el-row style="margin-bottom: 10px;">
          <el-col :span="24">
            <el-button type="primary" plain icon="Plus" size="small" @click="handleAddItem">添加货物</el-button>
          </el-col>
        </el-row>

        <el-table :data="form.billItems" border size="small" style="width: 100%">
          <el-table-column label="货物" min-width="150">
            <template #default="scope">
              <el-select v-model="scope.row.goodsId" placeholder="选择货物" filterable style="width: 100%" @change="(val) => handleGoodsChange(val, scope.$index)">
                <el-option v-for="item in goodsOptions" :key="item.goodsId" :label="item.goodsName" :value="item.goodsId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="型号" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.goodsModel" placeholder="型号" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="重量(吨)" width="140">
            <template #default="scope">
              <el-input-number v-model="scope.row.weight" :min="0.001" :precision="3" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="运价(元/吨)" width="140">
            <template #default="scope">
              <el-input-number v-model="scope.row.unitPrice" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="金额(元)" width="120">
            <template #default="scope">
              {{ getItemAmount(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="scope">
              <el-button link type="danger" icon="Delete" @click="handleRemoveItem(scope.$index)" :disabled="form.billItems.length <= 1" />
            </template>
          </el-table-column>
        </el-table>

        <el-descriptions :column="3" border size="small" style="margin-top: 15px">
          <el-descriptions-item label="合计重量">{{ totalItemWeight }} 吨</el-descriptions-item>
          <el-descriptions-item label="合计金额">{{ totalItemAmount }} 元</el-descriptions-item>
          <el-descriptions-item label="货物种类">{{ form.billItems.length }} 种</el-descriptions-item>
        </el-descriptions>

        <el-col :span="24">
          <el-form-item label="备注" prop="remark" style="margin-top: 15px">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
          </el-form-item>
        </el-col>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 提单分配明细对话框 -->
    <el-dialog title="提单分配明细" v-model="allocationDialogVisible" width="900px" append-to-body>
      <el-table :data="allocationList" border>
        <el-table-column label="运单号" align="center" prop="orderNo" width="160" />
        <el-table-column label="货物名称" align="center" prop="goodsName" width="120" />
        <el-table-column label="驾驶员单号" align="center" prop="driverOrderNo" width="160" />
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
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 货物明细合计
const totalItemWeight = computed(() => {
  return (form.value.billItems || []).reduce((sum, item) => {
    return sum + (item.weight || 0)
  }, 0).toFixed(3)
})

const totalItemAmount = computed(() => {
  return (form.value.billItems || []).reduce((sum, item) => {
    return sum + (item.weight || 0) * (item.unitPrice || 0)
  }, 0).toFixed(2)
})

function getItemAmount(item) {
  if (item.weight != null && item.unitPrice != null) {
    return (item.weight * item.unitPrice).toFixed(2)
  }
  return '0.00'
}

function getRemainWeight(item) {
  if (item.weight != null && item.allocatedWeight != null) {
    return (item.weight - item.allocatedWeight).toFixed(3)
  }
  return item.weight ? item.weight.toFixed(3) : '0.000'
}

function getBillRemainWeight(row) {
  if (row.totalWeight != null && row.allocatedWeight != null) {
    return (row.totalWeight - row.allocatedWeight).toFixed(3)
  }
  return row.totalWeight ? row.totalWeight.toFixed(3) : '0.000'
}

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

function handleGoodsChange(goodsId, index) {
  const goods = goodsOptions.value.find(g => g.goodsId === goodsId)
  if (goods) {
    form.value.billItems[index].goodsName = goods.goodsName
    form.value.billItems[index].unitPrice = goods.unitPrice
  }
}

function handleAddItem() {
  if (!form.value.billItems) {
    form.value.billItems = []
  }
  form.value.billItems.push({
    goodsId: null,
    goodsName: '',
    goodsModel: '',
    weight: null,
    unitPrice: null,
    amount: null
  })
}

function handleRemoveItem(index) {
  form.value.billItems.splice(index, 1)
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
  // 默认添加一行货物明细
  form.value.billItems = [{
    goodsId: null,
    goodsName: '',
    goodsModel: '',
    weight: null,
    unitPrice: null,
    amount: null
  }]
  open.value = true
  title.value = "添加提单"
}

function handleUpdate(row) {
  reset()
  const billId = row.billId || ids.value[0]
  getBill(billId).then(response => {
    form.value = response.data
    // 确保货物明细存在
    if (!form.value.billItems || form.value.billItems.length === 0) {
      form.value.billItems = [{
        goodsId: null,
        goodsName: '',
        goodsModel: '',
        weight: null,
        unitPrice: null,
        amount: null
      }]
    }
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
      // 验证货物明细
      const items = form.value.billItems
      if (!items || items.length === 0) {
        proxy.$modal.msgWarning("请至少添加一种货物")
        return
      }
      const hasValidItem = items.some(item => item.goodsId && item.weight && item.weight > 0)
      if (!hasValidItem) {
        proxy.$modal.msgWarning("请至少填写一种完整货物信息")
        return
      }

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
    loadingAddress: null,
    unloadingAddress: null,
    billItems: [],
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
