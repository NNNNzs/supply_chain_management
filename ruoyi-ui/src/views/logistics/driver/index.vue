<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="司机编码" prop="driverCode">
        <el-input
          v-model="queryParams.driverCode"
          placeholder="请输入司机编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="司机姓名" prop="driverName">
        <el-input
          v-model="queryParams.driverName"
          placeholder="请输入司机姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['logistics:driver:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['logistics:driver:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['logistics:driver:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['logistics:driver:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="driverList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="司机ID" align="center" prop="driverId" />
      <el-table-column label="司机编码" align="center" prop="driverCode" />
      <el-table-column label="司机姓名" align="center" prop="driverName" />
      <el-table-column label="司机电话" align="center" prop="driverPhone" />
      <el-table-column label="常用车牌号" align="center" prop="vehiclePlate" />
      <el-table-column label="银行账号" align="center" prop="bankAccount" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:driver:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:driver:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改司机信息对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="driverRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="司机编码" prop="driverCode">
              <el-input v-model="form.driverCode" placeholder="请输入司机编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机姓名" prop="driverName">
              <el-input v-model="form.driverName" placeholder="请输入司机姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机电话" prop="driverPhone">
              <el-input v-model="form.driverPhone" placeholder="请输入司机电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="驾驶证号" prop="driverLicense">
              <el-input v-model="form.driverLicense" placeholder="请输入驾驶证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="常用车牌号" prop="vehiclePlate">
              <el-input v-model="form.vehiclePlate" placeholder="请输入常用车牌号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行账号" prop="bankAccount">
              <el-input v-model="form.bankAccount" placeholder="请输入银行账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户行" prop="bankName">
              <el-input v-model="form.bankName" placeholder="请输入开户行" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户姓名" prop="accountName">
              <el-input v-model="form.accountName" placeholder="请输入账户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
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

<script setup name="Driver">
import { listDriver, getDriver, delDriver, addDriver, updateDriver } from "@/api/logistics/driver"

const { proxy } = getCurrentInstance()

const driverList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    driverCode: undefined,
    driverName: undefined,
    driverPhone: undefined,
    status: undefined
  },
  rules: {
    driverCode: [
      { required: true, message: "司机编码不能为空", trigger: "blur" }
    ],
    driverName: [
      { required: true, message: "司机姓名不能为空", trigger: "blur" }
    ],
    driverPhone: [
      { required: true, message: "司机电话不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询司机信息列表 */
function getList() {
  loading.value = true
  listDriver(queryParams.value).then(response => {
    driverList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    driverId: null,
    driverCode: null,
    driverName: null,
    driverPhone: null,
    idCard: null,
    driverLicense: null,
    vehiclePlate: null,
    bankAccount: null,
    bankName: null,
    accountName: null,
    status: "0",
    remark: null
  }
  proxy.resetForm("driverRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.driverId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加司机信息"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _driverId = row.driverId || ids.value
  getDriver(_driverId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改司机信息"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["driverRef"].validate(valid => {
    if (valid) {
      if (form.value.driverId != null) {
        updateDriver(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addDriver(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _driverIds = row.driverId || ids.value
  proxy.$modal.confirm('是否确认删除司机信息编号为"' + _driverIds + '"的数据项？').then(function() {
    return delDriver(_driverIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('logistics/driver/export', {
    ...queryParams.value
  }, `driver_${new Date().getTime()}.xlsx`)
}

getList()
</script>
