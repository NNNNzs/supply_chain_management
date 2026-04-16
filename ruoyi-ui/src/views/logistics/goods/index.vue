<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="货物编码" prop="goodsCode">
        <el-input v-model="queryParams.goodsCode" placeholder="请输入货物编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="货物名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入货物名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['logistics:goods:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['logistics:goods:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['logistics:goods:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="货物编码" align="center" prop="goodsCode" min-width="120" />
      <el-table-column label="货物名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="货物型号" align="center" prop="goodsModel" width="120" />
      <el-table-column label="货物分类" align="center" prop="goodsCategory" width="100" />
      <el-table-column label="计量单位" align="center" prop="goodsUnit" width="80" />
      <el-table-column label="参考单价" align="center" prop="unitPrice" width="100">
        <template #default="scope">
          {{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['logistics:goods:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['logistics:goods:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改货物对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="goodsRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="货物编码" prop="goodsCode">
              <el-input v-model="form.goodsCode" placeholder="请输入货物编码" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物名称" prop="goodsName">
              <el-input v-model="form.goodsName" placeholder="请输入货物名称" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="货物型号" prop="goodsModel">
              <el-input v-model="form.goodsModel" placeholder="请输入货物型号" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物分类" prop="goodsCategory">
              <el-input v-model="form.goodsCategory" placeholder="请输入货物分类" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="计量单位" prop="goodsUnit">
              <el-select v-model="form.goodsUnit" placeholder="请选择计量单位">
                <el-option label="吨" value="吨" />
                <el-option label="千克" value="千克" />
                <el-option label="立方米" value="立方米" />
                <el-option label="件" value="件" />
                <el-option label="箱" value="箱" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参考单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="500" />
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

<script setup name="Goods">
import { listGoods, getGoods, addGoods, updateGoods, delGoods } from "@/api/logistics/goods"

const { proxy } = getCurrentInstance()

const goodsList = ref([])
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
    goodsCode: null,
    goodsName: null
  },
  rules: {
    goodsCode: [
      { required: true, message: "货物编码不能为空", trigger: "blur" }
    ],
    goodsName: [
      { required: true, message: "货物名称不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listGoods(queryParams.value).then(response => {
    goodsList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    goodsId: null,
    goodsCode: null,
    goodsName: null,
    goodsModel: null,
    goodsUnit: "吨",
    goodsCategory: null,
    unitPrice: 0,
    status: "0",
    remark: null
  }
  proxy.resetForm("goodsRef")
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
  reset()
  open.value = true
  title.value = "添加货物"
}

function handleUpdate(row) {
  reset()
  const goodsId = row.goodsId || ids.value[0]
  getGoods(goodsId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改货物"
  })
}

function submitForm() {
  proxy.$refs.goodsRef.validate(valid => {
    if (valid) {
      if (form.value.goodsId != null) {
        updateGoods(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addGoods(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const goodsIds = row.goodsId ? [row.goodsId] : ids.value
  proxy.$modal.confirm('是否确认删除货物编号为"' + goodsIds + '"的数据项？').then(function() {
    return delGoods(goodsIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleStatusChange(row) {
  let text = row.status === "0" ? "启用" : "停用"
  proxy.$modal.confirm('确认要"' + text + '""' + row.goodsName + '"货物吗？').then(function() {
    return updateGoods(row)
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功")
  }).catch(function() {
    row.status = row.status === "0" ? "1" : "0"
  })
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.goodsId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

getList()
</script>
