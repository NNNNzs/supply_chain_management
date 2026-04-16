<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="车队名称" prop="fleetName">
        <el-input
          v-model="queryParams.fleetName"
          placeholder="请输入车队名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="老板姓名" prop="ownerName">
        <el-input
          v-model="queryParams.ownerName"
          placeholder="请输入老板姓名"
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
          v-hasPermi="['logistics:fleet:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['logistics:fleet:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['logistics:fleet:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['logistics:fleet:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="fleetList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="车队名称" align="center" prop="fleetName" width="150" />
      <el-table-column label="老板姓名" align="center" prop="ownerName" width="120" />
      <el-table-column label="老板电话" align="center" prop="ownerPhone" width="130" />
      <el-table-column label="开票账户" align="center" width="200">
        <template #default="scope">
          <div v-if="scope.row.accountName || scope.row.accountNumber">
            <div>{{ scope.row.accountName }}</div>
            <div class="text-xs text-gray-500">{{ scope.row.accountNumber }}</div>
          </div>
          <span v-else class="text-gray-400">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="开户行" align="center" prop="bankName" width="150" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:fleet:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:fleet:remove']">删除</el-button>
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

    <!-- 添加或修改车队信息对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="fleetRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="车队名称" prop="fleetName">
              <el-input v-model="form.fleetName" placeholder="请输入车队名称" />
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
          <el-col :span="12">
            <el-form-item label="车队老板姓名" prop="ownerName">
              <el-input v-model="form.ownerName" placeholder="请输入车队老板姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="老板联系电话" prop="ownerPhone">
              <el-input v-model="form.ownerPhone" placeholder="请输入老板联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-divider content-position="left">结算账户信息</el-divider>
          </el-col>
          <el-col :span="24">
            <el-form-item label="车队开票账户" prop="accountName">
              <el-input v-model="form.accountName" placeholder="请输入车队开票账户名称（公司名称或个体户名称）" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开票账号" prop="accountNumber">
              <el-input v-model="form.accountNumber" placeholder="请输入车队开票账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户行" prop="bankName">
              <el-input v-model="form.bankName" placeholder="请输入车队开户行" />
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

<script setup name="Fleet">
import { listFleet, getFleet, delFleet, addFleet, updateFleet } from "@/api/logistics/fleet"

const { proxy } = getCurrentInstance()

const fleetList = ref([])
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
    fleetName: undefined,
    ownerName: undefined,
    ownerPhone: undefined,
    status: undefined
  },
  rules: {
    fleetName: [
      { required: true, message: "车队名称不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询车队列表 */
function getList() {
  loading.value = true
  listFleet(queryParams.value).then(response => {
    fleetList.value = response.rows
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
    fleetId: null,
    fleetName: null,
    ownerName: null,
    ownerPhone: null,
    accountName: null,
    accountNumber: null,
    bankName: null,
    status: "0",
    remark: null
  }
  proxy.resetForm("fleetRef")
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
  ids.value = selection.map(item => item.fleetId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加车队"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _fleetId = row.fleetId || ids.value
  getFleet(_fleetId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改车队"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["fleetRef"].validate(valid => {
    if (valid) {
      if (form.value.fleetId != null) {
        updateFleet(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addFleet(form.value).then(() => {
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
  const _fleetIds = row.fleetId || ids.value
  proxy.$modal.confirm('是否确认删除车队"' + (row.fleetName || '') + '"？').then(function() {
    return delFleet(_fleetIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('logistics/fleet/export', {
    ...queryParams.value
  }, `fleet_${new Date().getTime()}.xlsx`)
}

getList()
</script>
