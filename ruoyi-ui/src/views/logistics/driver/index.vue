<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="搜索" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索车队名称或司机姓名"
          clearable
          @keyup.enter="handleQuery"
          style="width: 200px"
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
          @click="handleAddFleet"
          v-hasPermi="['logistics:driver:add']"
        >新增车队</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Plus"
          @click="handleAddDriver"
          v-hasPermi="['logistics:driver:add']"
        >新增司机</el-button>
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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getTreeList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="treeData"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :default-expand-all="false"
      style="width: 100%"
    >
      <el-table-column label="名称" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <span class="table-cell">
            <el-icon v-if="scope.row.type === 'fleet'" color="#409EFF" :size="16" style="margin-right: 6px; vertical-align: -2px;"><OfficeBuilding /></el-icon>
            <el-icon v-else color="#67C23A" :size="16" style="margin-right: 6px; vertical-align: -2px;"><User /></el-icon>
            <span>{{ scope.row.type === 'fleet' ? scope.row.fleetName : scope.row.driverName }}</span>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 'fleet'" type="warning" size="small">车队</el-tag>
          <el-tag v-else-if="scope.row.driverType === 'individual'" type="success" size="small">个人</el-tag>
          <el-tag v-else type="info" size="small">车队</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" width="125">
        <template #default="scope">
          <span v-if="scope.row.type === 'fleet'">{{ scope.row.ownerPhone || '-' }}</span>
          <span v-else>{{ scope.row.driverPhone || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车牌号" align="center" width="100">
        <template #default="scope">
          <span v-if="scope.row.type === 'driver'">{{ scope.row.vehiclePlate || '-' }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="结算账户" align="center" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.type === 'fleet' && scope.row.accountName">{{ scope.row.accountName }}</span>
          <span v-else-if="scope.row.type === 'driver' && scope.row.driverType === 'individual' && scope.row.bankName">{{ scope.row.bankName }}</span>
          <span v-else-if="scope.row.type === 'driver' && scope.row.driverType === 'fleet'">车队结算</span>
          <span v-else class="text-gray-400">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="司机数" align="center" width="80">
        <template #default="scope">
          <span v-if="scope.row.type === 'fleet'">{{ scope.row.driverCount || 0 }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="75">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="130" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              link
              type="primary"
              @click="scope.row.type === 'fleet' ? handleEditFleet(scope.row) : handleEditDriver(scope.row)"
              v-hasPermi="['logistics:driver:edit']"
            >编辑</el-button>
            <el-button
              link
              type="danger"
              @click="scope.row.type === 'fleet' ? handleDeleteFleet(scope.row) : handleDeleteDriver(scope.row)"
              v-hasPermi="['logistics:driver:remove']"
            >删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 车队表单对话框 -->
    <el-dialog :title="fleetTitle" v-model="fleetOpen" width="700px" append-to-body>
      <el-form ref="fleetRef" :model="fleetForm" :rules="fleetRules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="车队名称" prop="fleetName">
              <el-input v-model="fleetForm.fleetName" placeholder="请输入车队名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="fleetForm.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车队老板姓名" prop="ownerName">
              <el-input v-model="fleetForm.ownerName" placeholder="请输入车队老板姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="老板联系电话" prop="ownerPhone">
              <el-input v-model="fleetForm.ownerPhone" placeholder="请输入老板联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-divider content-position="left">结算账户信息</el-divider>
          </el-col>
          <el-col :span="24">
            <el-form-item label="车队开票账户" prop="accountName">
              <el-input v-model="fleetForm.accountName" placeholder="请输入车队开票账户名称（公司名称或个体户名称）" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开票账号" prop="accountNumber">
              <el-input v-model="fleetForm.accountNumber" placeholder="请输入车队开票账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户行" prop="bankName">
              <el-input v-model="fleetForm.bankName" placeholder="请输入车队开户行" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="fleetForm.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFleetForm">确 定</el-button>
          <el-button @click="cancelFleet">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 司机表单对话框 -->
    <el-dialog :title="driverTitle" v-model="driverOpen" width="800px" append-to-body>
      <el-form ref="driverRef" :model="driverForm" :rules="driverRules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="司机类型" prop="driverType">
              <el-radio-group v-model="driverForm.driverType" @change="handleDriverTypeChange">
                <el-radio label="individual">个人司机</el-radio>
                <el-radio label="fleet">车队司机</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="driverForm.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机姓名" prop="driverName">
              <el-input v-model="driverForm.driverName" placeholder="请输入司机姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机电话" prop="driverPhone">
              <el-input v-model="driverForm.driverPhone" placeholder="请输入司机电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="driverForm.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="驾驶证号" prop="driverLicense">
              <el-input v-model="driverForm.driverLicense" placeholder="请输入驾驶证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="常用车牌号" prop="vehiclePlate">
              <el-input v-model="driverForm.vehiclePlate" placeholder="请输入常用车牌号" />
            </el-form-item>
          </el-col>

          <!-- 车队司机：选择车队 -->
          <template v-if="driverForm.driverType === 'fleet'">
            <el-col :span="12">
              <el-form-item label="所属车队" prop="fleetId">
                <el-select v-model="driverForm.fleetId" placeholder="请选择车队" clearable filterable>
                  <el-option
                    v-for="fleet in fleetList"
                    :key="fleet.fleetId"
                    :label="fleet.fleetName"
                    :value="fleet.fleetId"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </template>

          <!-- 个人司机：银行账户信息 -->
          <template v-if="driverForm.driverType === 'individual'">
            <el-col :span="24">
              <el-divider content-position="left">结算账户信息</el-divider>
            </el-col>
            <el-col :span="12">
              <el-form-item label="银行账号" prop="bankAccount">
                <el-input v-model="driverForm.bankAccount" placeholder="请输入银行账号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="开户行" prop="bankName">
                <el-input v-model="driverForm.bankName" placeholder="请输入开户行" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="账户姓名" prop="accountName">
                <el-input v-model="driverForm.accountName" placeholder="请输入账户姓名" />
              </el-form-item>
            </el-col>
          </template>

          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="driverForm.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitDriverForm">确 定</el-button>
          <el-button @click="cancelDriver">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Driver">
import { treeList, getDriver, getFleet, addDriver, addFleet, updateDriver, updateFleet, delDriver, delFleet, exportDriver, getAllFleets } from "@/api/logistics/driver"
import { OfficeBuilding, User } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()

const treeData = ref([])
const fleetList = ref([])
const loading = ref(true)
const showSearch = ref(true)

// 车队表单
const fleetOpen = ref(false)
const fleetTitle = ref("")
const fleetForm = ref({})
const fleetRules = {
  fleetName: [{ required: true, message: "车队名称不能为空", trigger: "blur" }]
}

// 司机表单
const driverOpen = ref(false)
const driverTitle = ref("")
const driverForm = ref({})
const driverRules = {
  driverName: [{ required: true, message: "司机姓名不能为空", trigger: "blur" }],
  driverPhone: [{ required: true, message: "司机电话不能为空", trigger: "blur" }],
  driverType: [{ required: true, message: "请选择司机类型", trigger: "change" }],
  fleetId: [{ required: true, message: "请选择所属车队", trigger: "change" }]
}

const queryParams = ref({
  keyword: undefined,
  status: undefined
})

/** 查询树形列表 */
function getTreeList() {
  loading.value = true
  const params = {}

  // 处理关键词搜索
  if (queryParams.value.keyword) {
    params.keyword = queryParams.value.keyword
  }

  if (queryParams.value.status) {
    params.status = queryParams.value.status
  }

  // 查询树形数据
  treeList(params).then(response => {
    treeData.value = response.data || []
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 查询车队列表（下拉选择用） */
function getFleetList() {
  getAllFleets().then(response => {
    fleetList.value = response.data || []
  })
}

/** 司机类型切换 */
function handleDriverTypeChange(value) {
  if (value === 'individual') {
    driverForm.value.fleetId = undefined
  } else {
    driverForm.value.bankAccount = undefined
    driverForm.value.bankName = undefined
    driverForm.value.accountName = undefined
  }
  proxy.$refs["driverRef"]?.clearValidate()
}

// ========== 车队操作 ==========

/** 新增车队 */
function handleAddFleet() {
  resetFleetForm()
  fleetOpen.value = true
  fleetTitle.value = "新增车队"
}

/** 编辑车队 */
function handleEditFleet(row) {
  resetFleetForm()
  const fleetId = row.fleetId
  getFleet(fleetId).then(response => {
    fleetForm.value = response.data
    fleetOpen.value = true
    fleetTitle.value = "编辑车队"
  })
}

/** 提交车队表单 */
function submitFleetForm() {
  proxy.$refs["fleetRef"].validate(valid => {
    if (valid) {
      if (fleetForm.value.fleetId != null) {
        updateFleet(fleetForm.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          fleetOpen.value = false
          getTreeList()
        })
      } else {
        addFleet(fleetForm.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          fleetOpen.value = false
          getTreeList()
        })
      }
    }
  })
}

/** 删除车队 */
function handleDeleteFleet(row) {
  const fleetName = row.fleetName || ''
  const driverCount = row.driverCount || 0
  let message = `是否确认删除车队"${fleetName}"？`
  if (driverCount > 0) {
    message = `车队"${fleetName}"下还有 ${driverCount} 个司机，是否确认删除？`
  }
  proxy.$modal.confirm(message).then(function() {
    return delFleet(row.fleetId)
  }).then(() => {
    getTreeList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 取消车队表单 */
function cancelFleet() {
  fleetOpen.value = false
  resetFleetForm()
}

/** 重置车队表单 */
function resetFleetForm() {
  fleetForm.value = {
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

// ========== 司机操作 ==========

/** 新增司机 */
function handleAddDriver() {
  resetDriverForm()
  getFleetList()
  driverOpen.value = true
  driverTitle.value = "新增司机"
}

/** 编辑司机 */
function handleEditDriver(row) {
  resetDriverForm()
  getFleetList()
  getDriver(row.driverId).then(response => {
    driverForm.value = response.data
    driverOpen.value = true
    driverTitle.value = "编辑司机"
  })
}

/** 提交司机表单 */
function submitDriverForm() {
  proxy.$refs["driverRef"].validate(valid => {
    if (valid) {
      if (driverForm.value.driverId != null) {
        updateDriver(driverForm.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          driverOpen.value = false
          getTreeList()
        })
      } else {
        addDriver(driverForm.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          driverOpen.value = false
          getTreeList()
        })
      }
    }
  })
}

/** 删除司机 */
function handleDeleteDriver(row) {
  const driverName = row.driverName || ''
  proxy.$modal.confirm('是否确认删除司机"' + driverName + '"？').then(function() {
    return delDriver(row.driverId)
  }).then(() => {
    getTreeList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 取消司机表单 */
function cancelDriver() {
  driverOpen.value = false
  resetDriverForm()
}

/** 重置司机表单 */
function resetDriverForm() {
  driverForm.value = {
    driverId: null,
    driverName: null,
    driverPhone: null,
    idCard: null,
    driverLicense: null,
    driverType: 'individual',
    fleetId: null,
    vehiclePlate: null,
    bankAccount: null,
    bankName: null,
    accountName: null,
    status: "0",
    remark: null
  }
  proxy.resetForm("driverRef")
}

// ========== 通用操作 ==========

/** 搜索按钮操作 */
function handleQuery() {
  getTreeList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  queryParams.value = {
    keyword: undefined,
    status: undefined
  }
  handleQuery()
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('logistics/driver/export', {}, `driver_${new Date().getTime()}.xlsx`)
}

getTreeList()
</script>

<style scoped>
.table-cell {
  display: inline-block;
}

.table-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.table-actions .el-button {
  margin: 0;
  padding: 0;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__expand-icon) {
  margin-right: 4px;
}

:deep(.el-table__indent) {
  padding-left: 8px;
}

.text-gray-400 {
  color: #9ca3af;
}
</style>
