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
              <el-date-picker v-model="form.orderDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
                <el-option v-for="item in customerOptions" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">地址信息</el-divider>

        <el-row>
          <el-col :span="24">
            <el-form-item label="装货地址" prop="loadingAddress">
              <el-input v-model="form.loadingAddress" placeholder="请输入装货地址" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="卸货地址" prop="unloadingAddress">
              <el-input v-model="form.unloadingAddress" placeholder="请输入卸货地址" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">
          货物明细
          <el-button type="primary" size="small" icon="Plus" style="margin-left: 10px" @click="addGoodsItem">添加货物</el-button>
        </el-divider>

        <el-table :data="form.goodsList" border style="width: 100%">
          <el-table-column type="index" width="50" label="序号" align="center" />
          <el-table-column label="货物" min-width="150">
            <template #default="scope">
              <el-select v-model="scope.row.goodsId" placeholder="请选择货物" filterable style="width: 100%" @change="(val) => handleGoodsItemChange(scope.$index, val)">
                <el-option v-for="item in goodsOptions" :key="item.goodsId" :label="item.goodsName" :value="item.goodsId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="货物型号" min-width="120">
            <template #default="scope">
              <el-input v-model="scope.row.goodsModel" placeholder="请输入货物型号" />
            </template>
          </el-table-column>
          <el-table-column label="计量单位" width="100">
            <template #default="scope">
              <el-input v-model="scope.row.goodsUnit" placeholder="单位" />
            </template>
          </el-table-column>
          <el-table-column label="重量(吨)" width="130">
            <template #default="scope">
              <el-input-number v-model="scope.row.weight" :min="0" :precision="3" :controls="false" style="width: 100%" @change="calculateGoodsItemAmount(scope.$index)" />
            </template>
          </el-table-column>
          <el-table-column label="单价(元/吨)" width="140">
            <template #default="scope">
              <el-input-number v-model="scope.row.unitPrice" :min="0" :precision="2" :controls="false" style="width: 100%" @change="calculateGoodsItemAmount(scope.$index)" />
            </template>
          </el-table-column>
          <el-table-column label="金额(元)" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.amount" disabled />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="scope">
              <el-button type="danger" icon="Delete" circle size="small" @click="removeGoodsItem(scope.$index)" :disabled="form.goodsList.length === 1" />
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
              <el-input-number v-model="form.advancePayment" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">车辆司机</el-divider>

        <el-row>
          <el-col :span="12">
            <el-form-item label="司机" prop="driverId">
              <el-select v-model="form.driverId" placeholder="请选择司机" filterable clearable style="width: 100%" @change="handleDriverChange">
                <el-option v-for="item in driverOptions" :key="item.driverId" :label="item.driverName" :value="item.driverId" />
              </el-select>
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
              <el-input-number v-model="form.loadingUnitPrice" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="运费支出">
              <el-input-number v-model="form.freightCost" :min="0" :precision="2" :controls="false" style="width: 100%" />
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
  </div>
</template>

<script setup name="OrderForm">
import { getOrder, addOrder, updateOrder } from "@/api/logistics/order"
import { listCustomer } from "@/api/logistics/customer"
import { listGoods } from "@/api/logistics/goods"
import { listDriver } from "@/api/logistics/driver"
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const orderId = ref(route.params.id || route.query.orderId)
const isEdit = ref(!!orderId.value)
const pageTitle = computed(() => isEdit.value ? '修改订单' : '新增订单')

const customerOptions = ref([])
const goodsOptions = ref([])
const driverOptions = ref([])

const totalWeight = computed(() => {
  let total = 0
  form.value.goodsList.forEach(item => {
    total += (item.weight || 0)
  })
  return total.toFixed(3)
})

const totalAmount = computed(() => {
  let total = 0
  form.value.goodsList.forEach(item => {
    total += (item.amount || 0)
  })
  return total.toFixed(2)
})

const data = reactive({
  form: {
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
  },
  rules: {
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
})

const { form, rules } = toRefs(data)

function getCustomerList() {
  listCustomer({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    customerOptions.value = response.rows
  })
}

function getGoodsList() {
  listGoods({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    goodsOptions.value = response.rows
  })
}

function getDriverList() {
  listDriver({ status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    driverOptions.value = response.rows.filter(item => !item.isGroup)
  })
}

function addGoodsItem() {
  form.value.goodsList.push({
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
  if (form.value.goodsList.length > 1) {
    form.value.goodsList.splice(index, 1)
  }
}

function handleGoodsItemChange(index, goodsId) {
  const goods = goodsOptions.value.find(item => item.goodsId === goodsId)
  if (goods) {
    form.value.goodsList[index].goodsName = goods.goodsName
    form.value.goodsList[index].goodsModel = goods.goodsModel
    form.value.goodsList[index].goodsUnit = goods.goodsUnit
    if (goods.unitPrice) {
      form.value.goodsList[index].unitPrice = goods.unitPrice
      calculateGoodsItemAmount(index)
    }
  }
}

function calculateGoodsItemAmount(index) {
  const item = form.value.goodsList[index]
  if (item.weight && item.unitPrice) {
    item.amount = item.weight * item.unitPrice
  } else {
    item.amount = 0
  }
}

function handleDriverChange(driverId) {
  const driver = driverOptions.value.find(item => item.driverId === driverId)
  if (driver) {
    form.value.vehiclePlate = driver.vehiclePlate
    form.value.driverPhone = driver.driverPhone
  }
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
      form.value = data
    })
  }
}

function submitForm() {
  proxy.$refs.orderRef.validate(valid => {
    if (valid) {
      // 验证货物明细
      const hasValidGoods = form.value.goodsList.some(item => item.goodsId && item.weight && item.unitPrice)
      if (!hasValidGoods) {
        proxy.$modal.msgWarning("请至少添加一个有效的货物明细")
        return
      }

      // 计算总重量和总金额
      form.value.totalWeight = parseFloat(totalWeight.value)
      form.value.totalAmount = parseFloat(totalAmount.value)

      const apiCall = isEdit.value ? updateOrder(form.value) : addOrder(form.value)
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
  getDriverList()
  loadOrderData()
})
</script>
