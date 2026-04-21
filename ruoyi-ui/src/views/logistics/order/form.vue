<template>
  <div class="app-container" style="padding: 20px;">
    <el-page-header @back="goBack" :content="pageTitle">
      <template #extra>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="goBack">取消</el-button>
      </template>
    </el-page-header>

    <el-card style="margin-top: 20px;">
      <el-form ref="orderRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单日期" prop="orderDate">
              <el-date-picker v-model="form.orderDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
                style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
                <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName"
                  :value="item.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">地址信息</el-divider>

        <el-row>
          <el-col :span="24">
            <el-form-item label="装货地址" prop="loadingAddress">
              <el-autocomplete v-model="form.loadingAddress" :fetch-suggestions="queryAddresses" placeholder="请输入装货地址"
                style="width: 100%" @select="handleLoadingAddressSelect">
                <template #default="{ item }">
                  <div class="address-item">
                    <span class="address-name">{{ item.address }}</span>
                    <span class="address-count">使用 {{ item.count }} 次</span>
                  </div>
                </template>
              </el-autocomplete>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="卸货地址" prop="unloadingAddress">
              <el-autocomplete v-model="form.unloadingAddress" :fetch-suggestions="queryAddresses" placeholder="请输入卸货地址"
                style="width: 100%" @select="handleUnloadingAddressSelect">
                <template #default="{ item }">
                  <div class="address-item">
                    <span class="address-name">{{ item.address }}</span>
                    <span class="address-count">使用 {{ item.count }} 次</span>
                  </div>
                </template>
              </el-autocomplete>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">
          货物明细
          <el-button type="primary" size="small" icon="Plus" style="margin-left: 10px"
            @click="addGoodsItem">添加货物</el-button>
          <el-button type="success" size="small" icon="Plus" style="margin-left: 5px"
            @click="showAddGoodsDialog">新增货物</el-button>
        </el-divider>

        <el-table :data="form.goodsList" border style="width: 100%">
          <el-table-column type="index" width="50" label="序号" align="center" />
          <el-table-column label="货物" min-width="150">
            <template #default="scope">
              <el-select v-model="scope.row.goodsId" placeholder="请选择货物" filterable style="width: 100%"
                @change="(val) => handleGoodsItemChange(scope.$index, val)">
                <el-option v-for="item in uniqueGoodsOptions" :key="item.goodsId" :label="item.goodsName"
                  :value="item.goodsId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="货物型号" min-width="120">
            <template #default="scope">
              <el-select v-model="scope.row.goodsModel" placeholder="请选择货物型号" filterable allow-create
                style="width: 100%">
                <el-option v-for="(model, idx) in getGoodsModels(scope.row.goodsId)" :key="idx" :label="model"
                  :value="model" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="计量单位" width="100">
            <template #default="scope">
              <el-input v-model="scope.row.goodsUnit" placeholder="单位" />
            </template>
          </el-table-column>
          <el-table-column label="重量(吨)" width="130">
            <template #default="scope">
              <el-input-number v-model="scope.row.weight" :min="0" :precision="3" :controls="false" style="width: 100%"
                @change="calculateGoodsItemAmount(scope.$index)" />
            </template>
          </el-table-column>
          <el-table-column label="单价(元/吨)" width="140">
            <template #default="scope">
              <el-input-number v-model="scope.row.unitPrice" :min="0" :precision="2" :controls="false"
                style="width: 100%" @change="calculateGoodsItemAmount(scope.$index)" />
            </template>
          </el-table-column>
          <el-table-column label="金额(元)" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.amount" disabled />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="scope">
              <el-button type="danger" icon="Delete" circle size="small" @click="removeGoodsItem(scope.$index)"
                :disabled="form.goodsList.length === 1" />
            </template>
          </el-table-column>
        </el-table>

        <el-row style="margin-top: 15px">
          <el-col :span="8">
            <el-form-item label="总重量(吨)">
              <el-input v-model="totalWeight" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总金额(元)">
              <el-input v-model="totalAmount" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="代垫付金额">
              <el-input-number v-model="form.advancePayment" :min="0" :precision="2" :controls="false"
                style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">车辆司机</el-divider>

        <el-row>
          <el-col :span="12">
            <el-form-item label="司机" prop="driverId">
              <el-tree-select v-model="form.driverId" :data="driverTreeData" placeholder="请选择司机"
                :props="{ label: 'driverName', value: 'driverId', disabled: 'disabled' }" check-strictly
                :render-after-expand="false" filterable :filter-node-method="filterDriverNode" default-expand-all
                style="width: 100%" @change="handleDriverChange" ref="treeSelectRef">
                <template #default="{ node, data }">
                  <span v-if="data.type === 'fleet' || data.type === 'group'">
                    <el-icon v-if="data.type === 'fleet'" color="#409EFF" :size="16"
                      style="margin-right: 6px; vertical-align: -2px;">
                      <OfficeBuilding />
                    </el-icon>
                    <span>{{ data.label }}</span>
                  </span>
                  <span v-else>
                    <el-icon color="#67C23A" :size="16" style="margin-right: 6px; vertical-align: -2px;">
                      <User />
                    </el-icon>
                    <span>{{ data.driverName }}</span>
                    <span style="color: #999; font-size: 12px; margin-left: 8px">{{ data.driverPhone }}</span>
                  </span>
                </template>
              </el-tree-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车牌号">
              <el-input v-model="form.vehiclePlate" placeholder="选择司机后自动带出" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="司机电话">
              <el-input v-model="form.driverPhone" placeholder="选择司机后自动带出" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配载单价">
              <el-input-number v-model="form.loadingUnitPrice" :min="0" :precision="2" :controls="false"
                style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="运费支出">
              <el-input-number v-model="form.freightCost" :min="0" :precision="2" :controls="false"
                style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">其他信息</el-divider>

        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="500" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 新增货物对话框 -->
    <GoodsFormDialog v-model="showGoodsDialog" @success="handleGoodsAdded" />
  </div>
</template>

<script setup name="OrderForm" lang="ts">
import { getOrder, addOrder, updateOrder, getAddresses } from "@/api/logistics/order"
import { listCustomer } from "@/api/logistics/customer"
import { listGoods } from "@/api/logistics/goods"
import { getDriverTree } from "@/api/logistics/driver"
import { useRoute, useRouter } from 'vue-router'
import GoodsFormDialog from "@/views/logistics/goods/components/GoodsFormDialog.vue"
import { OfficeBuilding, User } from '@element-plus/icons-vue'
import type { TreeSelectInstance } from 'element-plus'
import { ref, reactive, computed, onMounted, getCurrentInstance } from 'vue'
import type { FormInstance } from 'element-plus'

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance() as any

const orderId = ref(route.params.id || route.query.orderId)
const orderRef = ref<FormInstance>()
const isEdit = ref(!!orderId.value)
const pageTitle = computed(() => isEdit.value ? '修改订单' : '新增订单')

const customerOptions = ref([])
const goodsOptions = ref([])
const goodsModelMap = ref(new Map()) // 货物ID -> 型号列表
const driverTreeData = ref([])
const showGoodsDialog = ref(false)

const treeSelectRef = ref<TreeSelectInstance>(null)

// 货物去重选项（按货物名称去重）
const uniqueGoodsOptions = computed(() => {
  const map = new Map()
  goodsOptions.value.forEach(item => {
    if (!map.has(item.goodsName)) {
      map.set(item.goodsName, item)
    }
  })
  return Array.from(map.values()).sort((a, b) => a.goodsName.localeCompare(b.goodsName))
})

const totalWeight = computed(() => {
  let total = 0
  form.goodsList.forEach(item => {
    total += (item.weight || 0)
  })
  return total.toFixed(3)
})

const totalAmount = computed(() => {
  let total = 0
  form.goodsList.forEach(item => {
    total += (item.amount || 0)
  })
  return total.toFixed(2)
})

// 表单数据
const form = reactive({
  orderId: null,
  orderDate: new Date().toISOString().split('T')[0],
  customerId: null,
  loadingAddress: null,
  unloadingAddress: null,
  goodsList: [
    { goodsId: null, goodsName: null, goodsModel: null, goodsUnit: null, weight: null, unitPrice: null, amount: 0 }
  ],
  totalWeight: 0,
  totalAmount: 0,
  advancePayment: 0,
  vehiclePlate: null,
  driverId: null,
  driverPhone: null,
  loadingUnitPrice: 0,
  freightCost: 0,
  orderStatus: "pending",
  remark: null
})

// 表单验证规则
const rules = {
  orderDate: [
    { required: true, message: "订单日期不能为空", trigger: "change" }
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

// 地址自动完成查询（装货和卸货使用同一个接口）
async function queryAddresses(queryString, cb) {
  if (!queryString || queryString.length < 1) {
    cb([])
    return
  }
  try {
    const response = await getAddresses(queryString)
    const results = response.data.map(item => ({
      address: item.address,
      count: item.total_count || item.count,
      value: item.address
    }))
    cb(results)
  } catch (error) {
    cb([])
  }
}

function handleLoadingAddressSelect(item) {
  form.loadingAddress = item.address
}

function handleUnloadingAddressSelect(item) {
  form.unloadingAddress = item.address
}

function getCustomerList() {
  listCustomer({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    customerOptions.value = response.rows
  })
}

function getGoodsList() {
  listGoods({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    goodsOptions.value = response.rows
    // 构建货物型号映射表
    buildGoodsModelMap()
  })
}

function buildGoodsModelMap() {
  const modelMap = new Map()
  goodsOptions.value.forEach(goods => {
    if (!modelMap.has(goods.goodsId)) {
      modelMap.set(goods.goodsId, new Set())
    }
    if (goods.goodsModel) {
      modelMap.get(goods.goodsId).add(goods.goodsModel)
    }
  })
  // 将Set转换为数组
  goodsModelMap.value = new Map()
  modelMap.forEach((set, key) => {
    goodsModelMap.value.set(key, Array.from(set).sort())
  })
}

function getGoodsModels(goodsId) {
  if (!goodsId) return []
  return goodsModelMap.value.get(goodsId) || []
}

function getDriverTreeData() {
  getDriverTree().then(response => {
    driverTreeData.value = response.data || []
  }).catch(() => {
    driverTreeData.value = []
  })
}

function addGoodsItem() {
  form.goodsList.push({
    goodsId: null,
    goodsName: null,
    goodsModel: null,
    goodsUnit: null,
    weight: null,
    unitPrice: null,
    amount: 0
  })
}

function removeGoodsItem(index) {
  if (form.goodsList.length > 1) {
    form.goodsList.splice(index, 1)
  }
}

function handleGoodsItemChange(index, goodsId) {
  const goods = goodsOptions.value.find(item => item.goodsId === goodsId)
  if (goods) {
    form.goodsList[index].goodsName = goods.goodsName
    form.goodsList[index].goodsUnit = goods.goodsUnit
    if (goods.unitPrice) {
      form.goodsList[index].unitPrice = goods.unitPrice
      calculateGoodsItemAmount(index)
    }
    // 如果该货物有型号，默认选择第一个
    const models = getGoodsModels(goodsId)
    if (models.length > 0) {
      form.goodsList[index].goodsModel = models[0]
    }
  }
}

function calculateGoodsItemAmount(index) {
  const item = form.goodsList[index]
  if (item.weight && item.unitPrice) {
    // 保留2位小数，避免浮点数精度问题
    item.amount = Math.round(item.weight * item.unitPrice * 100) / 100
  } else {
    item.amount = 0
  }
}

function handleDriverChange(driverId) {
  // 从树形数据中查找选中的司机
  function findDriver(data, id) {
    for (const item of data) {
      if (item.driverId === id) {
        return item
      }
      if (item.children) {
        const found = findDriver(item.children, id)
        if (found) return found
      }
    }
    return null
  }

  const driver = findDriver(driverTreeData.value, driverId)
  if (driver) {
    form.vehiclePlate = driver.vehiclePlate
    form.driverPhone = driver.driverPhone
  }
}

// 司机树形搜索过滤函数
function filterDriverNode(value, data) {
  if (!value) return true
  // 如果是司机节点，搜索司机姓名和电话
  if (data.driverName) {
    return data.driverName.toLowerCase().includes(value.toLowerCase()) ||
      (data.driverPhone && data.driverPhone.includes(value))
  }
  // 如果是分组节点，搜索分组名称
  if (data.label) {
    return data.label.toLowerCase().includes(value.toLowerCase())
  }
  return false
}

function showAddGoodsDialog() {
  showGoodsDialog.value = true
}

function handleGoodsAdded(newGoods) {
  // 刷新货物列表
  getGoodsList()
}

function loadOrderData() {
  if (orderId.value) {
    getOrder(orderId.value).then(response => {
      const data = response.data
      // 确保货物明细列表存在
      if (!data.goodsList || data.goodsList.length === 0) {
        data.goodsList = [
          { goodsId: null, goodsName: null, goodsModel: null, goodsUnit: null, weight: null, unitPrice: null, amount: 0 }
        ]
      }
      // 使用 Object.assign 更新 reactive 对象的属性
      Object.assign(form, data)
    })
  }
}

function submitForm() {
  orderRef.value?.validate(valid => {
    if (valid) {
      // 验证货物明细
      const hasValidGoods = form.goodsList.some(item => item.goodsId && item.weight && item.unitPrice)
      if (!hasValidGoods) {
        proxy.$modal.msgWarning("请至少添加一个有效的货物明细")
        return
      }

      // 计算总重量和总金额
      form.totalWeight = parseFloat(totalWeight.value)
      form.totalAmount = parseFloat(totalAmount.value)

      const apiCall = isEdit.value ? updateOrder(form) : addOrder(form)
      apiCall.then(response => {
        proxy.$modal.msgSuccess(isEdit.value ? "修改成功" : "新增成功")
        goBack()
      })
    }
  })
}

function goBack() {
  router.back()
}

onMounted(() => {
  getCustomerList()
  getGoodsList()
  getDriverTreeData()
  loadOrderData()
})
</script>

<style scoped>
.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.address-name {
  flex: 1;
}

.address-count {
  color: #999;
  font-size: 12px;
  margin-left: 10px;
}
</style>
