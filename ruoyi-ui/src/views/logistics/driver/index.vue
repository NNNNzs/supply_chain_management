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
      <el-form-item label="司机类型" prop="driverType">
        <el-select v-model="queryParams.driverType" placeholder="请选择司机类型" clearable>
          <el-option label="个人司机" value="individual" />
          <el-option label="车队司机" value="fleet" />
        </el-select>
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

    <el-table
      v-loading="loading"
      :data="driverList"
      row-key="driverId"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :default-expand-all="false"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="司机编码" align="center" prop="driverCode" width="120" />
      <el-table-column label="司机姓名" align="center" prop="driverName" width="120" />
      <el-table-column label="司机电话" align="center" prop="driverPhone" width="130" />
      <el-table-column label="司机类型" align="center" prop="driverType" width="100">
        <template #default="scope">
          <el-tag v-if="!scope.row.isGroup" :type="scope.row.driverType === 'individual' ? 'success' : 'warning'">
            {{ scope.row.driverType === 'individual' ? '个人司机' : '车队司机' }}
          </el-tag>
          <el-tag v-else type="info">{{ scope.row.label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="常用车牌号" align="center" prop="vehiclePlate" width="120" />
      <el-table-column label="车队老板" align="center" prop="fleetOwnerName" width="100">
        <template #default="scope">
          <span v-if="scope.row.driverType === 'fleet'">{{ scope.row.fleetOwnerName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="银行信息" align="center" width="200">
        <template #default="scope">
          <div v-if="scope.row.driverType === 'individual' && !scope.row.isGroup">
            <div>{{ scope.row.bankName }}</div>
            <div class="text-xs text-gray-500">{{ scope.row.bankAccount }}</div>
          </div>
          <div v-else-if="scope.row.driverType === 'fleet' && !scope.row.isGroup">
            <div>{{ scope.row.fleetBankName }}</div>
            <div class="text-xs text-gray-500">{{ scope.row.fleetAccountNumber }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag v-if="!scope.row.isGroup && scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else-if="!scope.row.isGroup" type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180" fixed="right">
        <template #default="scope">
          <template v-if="!scope.row.isGroup">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:driver:edit']">修改</el-button>
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:driver:remove']">删除</el-button>
          </template>
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
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form ref="driverRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="司机类型" prop="driverType">
              <el-radio-group v-model="form.driverType" @change="handleDriverTypeChange">
                <el-radio label="individual">个人司机</el-radio>
                <el-radio label="fleet">车队司机</el-radio>
              </el-radio-group>
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

          <!-- 个人司机字段 -->
          <template v-if="form.driverType === 'individual'">
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
          </template>

          <!-- 车队司机字段 -->
          <template v-if="form.driverType === 'fleet'">
            <el-col :span="12">
              <el-form-item label="车队老板姓名" prop="fleetOwnerName">
                <el-input v-model="form.fleetOwnerName" placeholder="请输入车队老板姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="老板联系电话" prop="fleetOwnerPhone">
                <el-input v-model="form.fleetOwnerPhone" placeholder="请输入老板联系电话" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="车队开票账户" prop="fleetAccountName">
                <el-input v-model="form.fleetAccountName" placeholder="请输入车队开票账户名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="车队开票账号" prop="fleetAccountNumber">
                <el-input v-model="form.fleetAccountNumber" placeholder="请输入车队开票账号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="车队开户行" prop="fleetBankName">
                <el-input v-model="form.fleetBankName" placeholder="请输入车队开户行" />
              </el-form-item>
            </el-col>
          </template>

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
    driverType: undefined,
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
    ],
    driverType: [
      { required: true, message: "请选择司机类型", trigger: "change" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询司机信息列表 */
function getList() {
  loading.value = true
  listDriver(queryParams.value).then(response => {
    const list = response.rows || []
    // 构建树形数据结构
    driverList.value = buildTreeData(list)
    total.value = response.total
    loading.value = false
  })
}

/** 构建树形数据结构 */
function buildTreeData(list) {
  // 分组数据
  const individualDrivers = list.filter(item => item.driverType === 'individual')
  const fleetDrivers = list.filter(item => item.driverType === 'fleet')

  const tree = []

  // 添加个人司机分组
  if (individualDrivers.length > 0) {
    tree.push({
      driverId: 'individual',
      label: '个人司机',
      isGroup: true,
      children: individualDrivers
    })
  }

  // 添加车队司机分组
  if (fleetDrivers.length > 0) {
    tree.push({
      driverId: 'fleet',
      label: '车队司机',
      isGroup: true,
      children: fleetDrivers
    })
  }

  return tree
}

/** 司机类型切换 */
function handleDriverTypeChange(value) {
  // 清空相关字段
  if (value === 'individual') {
    form.value.fleetOwnerName = undefined
    form.value.fleetOwnerPhone = undefined
    form.value.fleetAccountName = undefined
    form.value.fleetAccountNumber = undefined
    form.value.fleetBankName = undefined
  } else {
    form.value.bankAccount = undefined
    form.value.bankName = undefined
    form.value.accountName = undefined
  }
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
    driverType: 'individual',
    fleetOwnerName: null,
    fleetOwnerPhone: null,
    vehiclePlate: null,
    bankAccount: null,
    bankName: null,
    accountName: null,
    fleetAccountName: null,
    fleetAccountNumber: null,
    fleetBankName: null,
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
  // 过滤掉分组节点，只选择实际司机数据
  const realSelection = selection.filter(item => !item.isGroup)
  ids.value = realSelection.map(item => item.driverId)
  single.value = realSelection.length != 1
  multiple.value = !realSelection.length
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
