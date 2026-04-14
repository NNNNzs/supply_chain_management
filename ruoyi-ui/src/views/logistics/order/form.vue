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

        <el-divider content-position="left">货物信息</el-divider>

        <el-row>
          <el-col :span="12">
            <el-form-item label="货物" prop="goodsId">
              <el-select v-model="form.goodsId" placeholder="请选择货物" filterable clearable style="width: 100%" @change="handleGoodsChange">
                <el-option v-for="item in goodsOptions" :key="item.goodsId" :label="item.goodsName" :value="item.goodsId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物型号">
              <el-input v-model="form.goodsModel" placeholder="请输入货物型号" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="重量(吨)" prop="weight">
              <el-input-number v-model="form.weight" :min="0" :precision="3" :controls="false" style="width: 100%" @change="calculateAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="运价(元/吨)" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" :controls="false" style="width: 100%" @change="calculateAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总金额(元)">
              <el-input v-model="computedAmount" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
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

const computedAmount = computed(() => {
  const weight = form.value.weight || 0
  const unitPrice = form.value.unitPrice || 0
  return (weight * unitPrice).toFixed(2)
})

const data = reactive({
  form: {
    orderId: null,
    orderDate: new Date().toISOString().split('T')[0],
    customerId: null,
    loadingAddress: null,
    unloadingAddress: null,
    goodsId: null,
    goodsName: null,
    goodsModel: null,
    weight: null,
    unitPrice: null,
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
    ],
    weight: [
      { required: true, message: "重量不能为空", trigger: "blur" }
    ],
    unitPrice: [
      { required: true, message: "运价不能为空", trigger: "blur" }
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
    driverOptions.value = response.rows
  })
}

function calculateAmount() {
  if (form.value.weight && form.value.unitPrice) {
    form.value.totalAmount = form.value.weight * form.value.unitPrice
  }
}

function handleGoodsChange(goodsId) {
  const goods = goodsOptions.value.find(item => item.goodsId === goodsId)
  if (goods) {
    form.value.goodsName = goods.goodsName
    if (goods.unitPrice) {
      form.value.unitPrice = goods.unitPrice
      calculateAmount()
    }
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
      form.value = response.data
    })
  }
}

function submitForm() {
  proxy.$refs.orderRef.validate(valid => {
    if (valid) {
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
