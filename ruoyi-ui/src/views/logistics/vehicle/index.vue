<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="车牌号" prop="vehiclePlate">
        <el-input
          v-model="queryParams.vehiclePlate"
          placeholder="请输入车牌号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="车辆类型" prop="vehicleType">
        <el-input
          v-model="queryParams.vehicleType"
          placeholder="请输入车辆类型"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="维修" value="1" />
          <el-option label="停用" value="2" />
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
          v-hasPermi="['logistics:vehicle:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['logistics:vehicle:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['logistics:vehicle:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['logistics:vehicle:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="vehicleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="车辆ID" align="center" prop="vehicleId" />
      <el-table-column label="车牌号" align="center" prop="vehiclePlate" />
      <el-table-column label="车辆类型" align="center" prop="vehicleType" />
      <el-table-column label="车长(米)" align="center" prop="vehicleLength" />
      <el-table-column label="载重(吨)" align="center" prop="loadCapacity" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="warning">维修</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:vehicle:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:vehicle:remove']">删除</el-button>
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

    <!-- 添加或修改车辆信息对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="vehicleRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="车牌号" prop="vehiclePlate">
              <el-input v-model="form.vehiclePlate" placeholder="请输入车牌号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车辆类型" prop="vehicleType">
              <el-input v-model="form.vehicleType" placeholder="请输入车辆类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车长(米)" prop="vehicleLength">
              <el-input-number v-model="form.vehicleLength" :min="0" :precision="2" placeholder="请输入车长" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="载重(吨)" prop="loadCapacity">
              <el-input-number v-model="form.loadCapacity" :min="0" :precision="2" placeholder="请输入载重" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认司机" prop="driverId">
              <el-select v-model="form.driverId" placeholder="请选择默认司机" clearable filterable>
                <el-option
                  v-for="driver in driverList"
                  :key="driver.driverId"
                  :label="driver.driverName"
                  :value="driver.driverId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">维修</el-radio>
                <el-radio label="2">停用</el-radio>
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

<script setup name="Vehicle">
import { listVehicle, getVehicle, delVehicle, addVehicle, updateVehicle } from "@/api/logistics/vehicle"
import { listDriver } from "@/api/logistics/driver"

const { proxy } = getCurrentInstance()

const vehicleList = ref([])
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
    vehiclePlate: undefined,
    vehicleType: undefined,
    driverId: undefined,
    status: undefined
  },
  rules: {
    vehiclePlate: [
      { required: true, message: "车牌号不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询车辆信息列表 */
function getList() {
  loading.value = true
  listVehicle(queryParams.value).then(response => {
    vehicleList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 获取司机列表 */
function getDriverList() {
  listDriver({ status: '0' }).then(response => {
    driverList.value = response.rows
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
    vehicleId: null,
    vehiclePlate: null,
    vehicleType: null,
    vehicleLength: null,
    loadCapacity: null,
    driverId: null,
    status: "0",
    remark: null
  }
  proxy.resetForm("vehicleRef")
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
  ids.value = selection.map(item => item.vehicleId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加车辆信息"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _vehicleId = row.vehicleId || ids.value
  getVehicle(_vehicleId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改车辆信息"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["vehicleRef"].validate(valid => {
    if (valid) {
      if (form.value.vehicleId != null) {
        updateVehicle(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addVehicle(form.value).then(() => {
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
  const _vehicleIds = row.vehicleId || ids.value
  proxy.$modal.confirm('是否确认删除车辆信息编号为"' + _vehicleIds + '"的数据项？').then(function() {
    return delVehicle(_vehicleIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('logistics/vehicle/export', {
    ...queryParams.value
  }, `vehicle_${new Date().getTime()}.xlsx`)
}

getList()
getDriverList()
</script>
