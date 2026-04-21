<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="回单编号" prop="receiptNo">
        <el-input v-model="queryParams.receiptNo" placeholder="请输入回单编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="回单状态" prop="receiptStatus">
        <el-select v-model="queryParams.receiptStatus" placeholder="回单状态" clearable style="width: 150px">
          <el-option
            v-for="dict in logistics_receipt_status"
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['logistics:receipt:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['logistics:receipt:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="receiptList">
      <el-table-column label="回单编号" align="center" prop="receiptNo" width="180" />
      <el-table-column label="订单号" align="center" prop="orderNo" width="180" :show-overflow-tooltip="true" />
      <el-table-column label="回单日期" align="center" prop="receiptDate" width="110" />
      <el-table-column label="回单图片" align="center" prop="receiptImage" width="100">
        <template #default="scope">
          <image-preview v-if="scope.row.receiptImage" :src="scope.row.receiptImage" :width="50" :height="50" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="回单状态" align="center" prop="receiptStatus" width="100">
        <template #default="scope">
          <dict-tag :options="logistics_receipt_status" :value="scope.row.receiptStatus" />
        </template>
      </el-table-column>
      <el-table-column label="接收人" align="center" prop="receiver" width="100" />
      <el-table-column label="接收时间" align="center" prop="receiveTime" width="160" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['logistics:receipt:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:receipt:edit']">修改</el-button>
          <el-button link type="primary" icon="Check" @click="handleConfirm(scope.row)" v-hasPermi="['logistics:receipt:edit']" v-if="scope.row.receiptStatus === 'not_received'">确认</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:receipt:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 回单表单对话框 -->
    <receipt-dialog v-model="open" :receipt-id="receiptId" @success="handleDialogSuccess" />

    <!-- 回单图片预览对话框 -->
    <el-dialog title="回单图片" v-model="imagePreviewOpen" width="800px" append-to-body>
      <el-image :src="previewImage" fit="contain" style="width: 100%; height: 500px" />
    </el-dialog>

    <!-- 确认回单对话框 -->
    <el-dialog title="确认回单" v-model="confirmOpen" width="400px" append-to-body>
      <el-form ref="confirmRef" :model="confirmForm" :rules="confirmRules" label-width="80px">
        <el-form-item label="接收人" prop="receiver">
          <el-input v-model="confirmForm.receiver" placeholder="请输入接收人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitConfirm">确 定</el-button>
          <el-button @click="confirmOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Receipt">
import { listReceipt, getReceipt, delReceipt, confirmReceipt, exportReceipt } from "@/api/logistics/receipt"
import { useDict } from '@/utils/dict'
import ReceiptDialog from "@/components/ReceiptDialog/index.vue"

const { proxy } = getCurrentInstance()
const { logistics_receipt_status } = useDict('logistics_receipt_status')

const receiptList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const imagePreviewOpen = ref(false)
const confirmOpen = ref(false)
const previewImage = ref("")
const receiptId = ref(null)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    receiptNo: null,
    orderNo: null,
    receiptStatus: null
  },
  confirmForm: {
    receiptId: null,
    receiver: ""
  },
  confirmRules: {
    receiver: [
      { required: true, message: "接收人不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, confirmForm, confirmRules } = toRefs(data)

function getList() {
  loading.value = true
  listReceipt(queryParams.value).then(response => {
    receiptList.value = response.rows
    total.value = response.total
    loading.value = false
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

function handleAdd() {
  receiptId.value = null
  open.value = true
}

function handleUpdate(row) {
  receiptId.value = row.receiptId
  open.value = true
}

function handleView(row) {
  if (row.receiptImage) {
    previewImage.value = row.receiptImage
    imagePreviewOpen.value = true
  } else {
    proxy.$modal.msgWarning("该回单暂无图片")
  }
}

function handleDialogSuccess() {
  getList()
}

function handleDelete(row) {
  const _receiptIds = row.receiptId || ids.value
  proxy.$modal.confirm('是否确认删除回单信息编号为"' + _receiptIds + '"的数据项？').then(function() {
    return delReceipt(_receiptIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleConfirm(row) {
  confirmForm.value.receiptId = row.receiptId
  confirmForm.value.receiver = ""
  confirmOpen.value = true
}

function submitConfirm() {
  proxy.$refs["confirmRef"].validate(valid => {
    if (valid) {
      confirmReceipt(confirmForm.value.receiptId, confirmForm.value.receiver).then(response => {
        proxy.$modal.msgSuccess("确认成功")
        confirmOpen.value = false
        getList()
      })
    }
  })
}

function handleExport() {
  proxy.download('logistics/receipt/export', {
    ...queryParams.value
  }, `receipt_${new Date().getTime()}.xlsx`)
}

getList()
</script>
