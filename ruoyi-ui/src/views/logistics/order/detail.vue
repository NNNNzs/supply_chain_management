<template>
  <div class="app-container" style="padding: 20px;">
    <el-page-header @back="goBack" content="订单详情">
      <template #extra>
        <el-button type="primary" icon="Edit" @click="handleEdit" v-hasPermi="['logistics:order:edit']">编辑</el-button>
        <el-button @click="goBack">返回</el-button>
      </template>
    </el-page-header>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-tag v-if="orderDetail.orderStatus === 'pending'" type="info">待运输</el-tag>
          <el-tag v-else-if="orderDetail.orderStatus === 'transporting'" type="primary">运输中</el-tag>
          <el-tag v-else-if="orderDetail.orderStatus === 'completed'" type="success">已完成</el-tag>
          <el-tag v-else-if="orderDetail.orderStatus === 'cancelled'" type="danger">已取消</el-tag>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单日期">{{ orderDetail.orderDate }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ orderDetail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="装货地址" :span="3">{{ orderDetail.loadingAddress }}</el-descriptions-item>
        <el-descriptions-item label="卸货地址" :span="3">{{ orderDetail.unloadingAddress }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>货物明细</span>
      </template>

      <el-table :data="orderDetail.goodsList" border style="width: 100%">
        <el-table-column type="index" width="50" label="序号" align="center" />
        <el-table-column label="货物名称" prop="goodsName" min-width="120" />
        <el-table-column label="货物型号" prop="goodsModel" width="120" />
        <el-table-column label="单位" prop="goodsUnit" width="80" align="center" />
        <el-table-column label="重量(吨)" prop="weight" width="100" align="center">
          <template #default="scope">
            {{ scope.row.weight ? scope.row.weight.toFixed(3) : '0.000' }}
          </template>
        </el-table-column>
        <el-table-column label="单价(元/吨)" prop="unitPrice" width="110" align="center">
          <template #default="scope">
            {{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="金额(元)" prop="amount" width="100" align="center">
          <template #default="scope">
            {{ scope.row.amount ? scope.row.amount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
      </el-table>

      <el-row style="margin-top: 15px" :gutter="20">
        <el-col :span="8">
          <div class="summary-item">
            <span class="summary-label">总重量：</span>
            <span class="summary-value">{{ totalWeight }} 吨</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="summary-item">
            <span class="summary-label">总金额：</span>
            <span class="summary-value">{{ totalAmount }} 元</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="summary-item">
            <span class="summary-label">代垫付金额：</span>
            <span class="summary-value">{{ orderDetail.advancePayment ? orderDetail.advancePayment.toFixed(2) : '0.00' }} 元</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>车辆司机信息</span>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="司机姓名">
          <template v-if="orderDetail.driverName">
            <el-icon color="#67C23A" :size="16" style="margin-right: 6px; vertical-align: -2px;">
              <User />
            </el-icon>
            {{ orderDetail.driverName }}
          </template>
          <span v-else class="text-placeholder">未分配</span>
        </el-descriptions-item>
        <el-descriptions-item label="司机电话">
          <template v-if="orderDetail.driverPhone">
            {{ orderDetail.driverPhone }}
          </template>
          <span v-else class="text-placeholder">未分配</span>
        </el-descriptions-item>
        <el-descriptions-item label="车牌号">
          <template v-if="orderDetail.vehiclePlate">
            {{ orderDetail.vehiclePlate }}
          </template>
          <span v-else class="text-placeholder">未分配</span>
        </el-descriptions-item>
        <el-descriptions-item label="配载单价">
          {{ orderDetail.loadingUnitPrice ? orderDetail.loadingUnitPrice.toFixed(2) : '0.00' }} 元
        </el-descriptions-item>
        <el-descriptions-item label="运费支出">
          {{ orderDetail.freightCost ? orderDetail.freightCost.toFixed(2) : '0.00' }} 元
        </el-descriptions-item>
        <el-descriptions-item label="司机类型">
          <el-tag v-if="orderDetail.driverType === 'individual'" type="success" size="small">个人司机</el-tag>
          <el-tag v-else-if="orderDetail.driverType === 'fleet'" type="primary" size="small">车队司机</el-tag>
          <span v-else class="text-placeholder">未分配</span>
        </el-descriptions-item>
        <el-descriptions-item label="所属车队" v-if="orderDetail.driverType === 'fleet'">
          {{ orderDetail.fleetName || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>财务状态</span>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="结算状态">
          <el-tag v-if="orderDetail.settlementStatus === 'unsettled'" type="warning">未结算</el-tag>
          <el-tag v-else-if="orderDetail.settlementStatus === 'partial'" type="primary">部分结算</el-tag>
          <el-tag v-else-if="orderDetail.settlementStatus === 'settled'" type="success">已结算</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开票状态">
          <el-tag v-if="orderDetail.invoiceStatus === 'not_invoiced'" type="info">未开票</el-tag>
          <el-tag v-else-if="orderDetail.invoiceStatus === 'invoiced'" type="success">已开票</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 20px;" v-if="orderDetail.remark">
      <template #header>
        <span>备注信息</span>
      </template>

      <div class="remark-content">{{ orderDetail.remark }}</div>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>操作记录</span>
      </template>

      <el-timeline>
        <el-timeline-item
          v-if="orderDetail.orderDate"
          :timestamp="orderDetail.orderDate"
          placement="top"
        >
          <el-card>
            <h4>订单创建</h4>
            <p>订单编号：{{ orderDetail.orderNo }}</p>
          </el-card>
        </el-timeline-item>
        <el-timeline-item
          v-if="orderDetail.orderStatus === 'transporting'"
          timestamp="运输中"
          placement="top"
          icon="el-icon-truck"
          color="#409EFF"
        >
          <el-card>
            <h4>订单开始运输</h4>
            <p>司机：{{ orderDetail.driverName }}（{{ orderDetail.vehiclePlate }}）</p>
          </el-card>
        </el-timeline-item>
        <el-timeline-item
          v-if="orderDetail.orderStatus === 'completed'"
          timestamp="已完成"
          placement="top"
          icon="el-icon-circle-check"
          color="#67C23A"
        >
          <el-card>
            <h4>订单已完成</h4>
            <p>订单金额：{{ totalAmount }} 元</p>
          </el-card>
        </el-timeline-item>
        <el-timeline-item
          v-if="orderDetail.orderStatus === 'cancelled'"
          timestamp="已取消"
          placement="top"
          icon="el-icon-circle-close"
          color="#F56C6C"
        >
          <el-card>
            <h4>订单已取消</h4>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup name="OrderDetail" lang="ts">
import { getOrder } from "@/api/logistics/order"
import { useRoute, useRouter } from 'vue-router'
import { User } from '@element-plus/icons-vue'
import { ref, computed, onMounted } from 'vue'

const route = useRoute()
const router = useRouter()

const orderId = ref(route.params.id || route.query.orderId)
const orderDetail = ref({
  orderNo: '',
  orderDate: '',
  customerId: null,
  customerName: '',
  loadingAddress: '',
  unloadingAddress: '',
  goodsList: [],
  totalWeight: 0,
  totalAmount: 0,
  advancePayment: 0,
  vehiclePlate: '',
  driverId: null,
  driverName: '',
  driverPhone: '',
  driverType: '',
  fleetName: '',
  loadingUnitPrice: 0,
  freightCost: 0,
  orderStatus: 'pending',
  settlementStatus: 'unsettled',
  invoiceStatus: 'not_invoiced',
  remark: ''
})

const totalWeight = computed(() => {
  let total = 0
  if (orderDetail.value.goodsList && orderDetail.value.goodsList.length > 0) {
    orderDetail.value.goodsList.forEach((item: any) => {
      total += (item.weight || 0)
    })
  }
  return total.toFixed(3)
})

const totalAmount = computed(() => {
  let total = 0
  if (orderDetail.value.goodsList && orderDetail.value.goodsList.length > 0) {
    orderDetail.value.goodsList.forEach((item: any) => {
      total += (item.amount || 0)
    })
  }
  return total.toFixed(2)
})

function loadOrderData() {
  if (orderId.value) {
    getOrder(orderId.value).then(response => {
      orderDetail.value = response.data
    }).catch(() => {
      router.back()
    })
  }
}

function handleEdit() {
  router.push({
    path: '/logistics/order/form',
    query: { orderId: orderId.value }
  })
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadOrderData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-item {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
}

.summary-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.text-placeholder {
  color: #909399;
  font-style: italic;
}

.remark-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.el-timeline {
  padding-left: 10px;
}

.el-timeline-item__content h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: bold;
}

.el-timeline-item__content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
}
</style>
