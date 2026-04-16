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
          <el-option label="未收到" value="not_received" />
          <el-option label="已收到" value="received" />
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
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['logistics:receipt:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['logistics:receipt:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['logistics:receipt:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="receiptList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
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
          <el-tag v-if="scope.row.receiptStatus === 'not_received'" type="warning">未收到</el-tag>
          <el-tag v-else-if="scope.row.receiptStatus === 'received'" type="success">已收到</el-tag>
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

    <!-- 添加或修改回单信息对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="receiptRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="订单号" prop="orderId">
          <el-select v-model="form.orderId" placeholder="请选择订单" clearable filterable style="width: 100%">
            <el-option v-for="item in orderOptions" :key="item.orderId" :label="item.orderNo" :value="item.orderId">
              <span>{{ item.orderNo }}</span>
              <span style="float: right; color: #8492a6; font-size: 12px">{{ item.customerName }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="回单日期" prop="receiptDate">
          <el-date-picker v-model="form.receiptDate" type="date" placeholder="选择回单日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="回单图片" prop="receiptImage">
          <el-upload
            ref="uploadRef"
            :action="upload.url"
            :headers="upload.headers"
            :file-list="upload.fileList"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
            :limit="1"
            :auto-upload="false"
            :on-change="handleUploadChange"
            accept=".jpg,.jpeg,.png,.gif"
            list-type="picture-card">
            <el-icon><Plus /></el-icon>
            <template #file="{ file }">
              <div>
                <el-image
                  v-if="file.url"
                  :src="file.url"
                  fit="cover"
                  style="width: 100%; height: 100%"
                  :preview-src-list="[file.url]"
                  :initial-index="0"
                />
                <div v-else class="el-upload-list__item-thumbnail">
                  <img :src="file.url" alt="" />
                </div>
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="回单状态" prop="receiptStatus">
          <el-radio-group v-model="form.receiptStatus">
            <el-radio label="not_received">未收到</el-radio>
            <el-radio label="received">已收到</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="接收人" prop="receiver">
          <el-input v-model="form.receiver" placeholder="请输入接收人" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

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
import { listReceipt, getReceipt, delReceipt, addReceipt, updateReceipt, confirmReceipt, exportReceipt } from "@/api/logistics/receipt"
import { listOrder } from "@/api/logistics/order"
import { getToken } from "@/utils/auth"

const { proxy } = getCurrentInstance()

const receiptList = ref([])
const orderOptions = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const open = ref(false)
const imagePreviewOpen = ref(false)
const confirmOpen = ref(false)
const previewImage = ref("")

const upload = reactive({
  url: import.meta.env.VITE_APP_BASE_API + "/common/upload",
  headers: { Authorization: "Bearer " + getToken() },
  fileList: []
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    receiptNo: null,
    orderNo: null,
    receiptStatus: null
  },
  rules: {
    orderId: [
      { required: true, message: "订单不能为空", trigger: "change" }
    ]
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

const { queryParams, form, rules, confirmForm, confirmRules } = toRefs(data)

function getList() {
  loading.value = true
  listReceipt(queryParams.value).then(response => {
    receiptList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function getOrderList() {
  // 只显示运输中的订单，因为只有运输中的订单才需要创建回单
  listOrder({ pageNum: 1, pageSize: 1000, orderStatus: "transporting" }).then(response => {
    orderOptions.value = response.rows
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    receiptId: null,
    receiptNo: null,
    orderId: null,
    receiptDate: null,
    receiptImage: null,
    receiptStatus: "not_received",
    receiver: null,
    remark: null
  }
  upload.fileList = []
  proxy.resetForm("receiptRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.receiptId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加回单信息"
  getOrderList()
}

function handleUpdate(row) {
  reset()
  getOrderList()
  const _receiptId = row.receiptId || ids.value[0]
  getReceipt(_receiptId).then(response => {
    form.value = response.data
    // 修复图片回显问题
    if (form.value.receiptImage) {
      upload.fileList = [{
        name: "回单图片",
        url: form.value.receiptImage,
        // 添加这些字段确保el-upload能正确显示
        status: 'success',
        uid: Date.now()
      }]
    }
    open.value = true
    title.value = "修改回单信息"
  })
}

function handleView(row) {
  if (row.receiptImage) {
    previewImage.value = row.receiptImage
    imagePreviewOpen.value = true
  } else {
    proxy.$modal.msgWarning("该回单暂无图片")
  }
}

function submitForm() {
  proxy.$refs["receiptRef"].validate(valid => {
    if (valid) {
      if (upload.fileList.length > 0 && upload.fileList[0].response) {
        form.value.receiptImage = upload.fileList[0].response.url
      }
      if (form.value.receiptId != null) {
        updateReceipt(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addReceipt(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
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

function handleUploadSuccess(response, file, fileList) {
  form.value.receiptImage = response.url
}

function handleUploadRemove(file, fileList) {
  form.value.receiptImage = null
}

function handleUploadChange(file, fileList) {
  // 限制只能上传一张图片
  if (fileList.length > 1) {
    fileList.splice(0, 1)
  }
}

function handleExport() {
  proxy.download('logistics/receipt/export', {
    ...queryParams.value
  }, `receipt_${new Date().getTime()}.xlsx`)
}

getList()
</script>
