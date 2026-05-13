<template>
  <div class="app-container dashboard-container" v-loading="loading">
    <!-- Row 1: 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: #409EFF">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todayCount }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: #E6A23C">
              <el-icon :size="28"><Van /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ transportingCount }}</div>
              <div class="stat-label">运输中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: #67C23A">
              <el-icon :size="28"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatAmount(monthRevenue) }}</div>
              <div class="stat-label">本月营收</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: #F56C6C">
              <el-icon :size="28"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingInvoiceCount }}</div>
              <div class="stat-label">待开票</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 2: 月度订单金额图表 + 订单状态饼图 -->
    <div class="grid-row grid-row-2">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>月度订单金额趋势</span>
            <el-date-picker
              v-model="selectedMonth"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
              :clearable="false"
              style="width: 160px"
              @change="onMonthChange"
            />
          </div>
        </template>
        <div ref="monthlyChartRef" style="height: 380px" v-loading="chartLoading" />
      </el-card>
      <el-card shadow="hover">
        <template #header><span>订单状态分布</span></template>
        <div ref="statusPieRef" style="height: 380px" />
      </el-card>
    </div>

    <!-- Row 3: 客户排名 + 订单查询 -->
    <div class="grid-row grid-row-3">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>客户营收排名</span>
            <span class="card-header-sub">当月 Top 5</span>
          </div>
        </template>
        <div class="customer-ranking">
          <div v-for="(item, index) in customerRanking" :key="item.name" class="ranking-item">
            <div class="ranking-left">
              <span :class="['ranking-badge', `ranking-badge-${index + 1}`]">{{ index + 1 }}</span>
              <span class="ranking-name">{{ item.name }}</span>
            </div>
            <div class="ranking-right">
              <span class="ranking-count">{{ item.count }}单</span>
              <span class="ranking-amount">¥{{ formatAmount(item.amount) }}</span>
            </div>
          </div>
          <el-empty v-if="customerRanking.length === 0" description="暂无数据" :image-size="60" />
        </div>
      </el-card>
      <el-card shadow="hover">
        <template #header><span>订单查询</span></template>
        <!-- 搜索栏 -->
        <el-form :inline="true" size="default" class="order-search-form">
          <el-form-item label="日期类型">
            <el-radio-group v-model="orderDateType" @change="onDateTypeChange">
              <el-radio-button value="month">按月</el-radio-button>
              <el-radio-button value="daterange">日期区间</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="日期">
            <el-date-picker
              v-if="orderDateType === 'month'"
              v-model="orderDateValue"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
              :clearable="false"
              style="width: 150px"
            />
            <el-date-picker
              v-else
              v-model="orderDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              :shortcuts="dateRangeShortcuts"
              style="width: 280px"
            />
          </el-form-item>
          <el-form-item label="客户">
            <el-select
              v-model="orderCustomerId"
              placeholder="全部客户"
              clearable
              filterable
              style="width: 180px"
            >
              <el-option
                v-for="c in customerOptions"
                :key="c.customerId"
                :label="c.customerName"
                :value="c.customerId"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onOrderSearch">搜索</el-button>
          </el-form-item>
        </el-form>
        <!-- 表格 -->
        <el-table :data="orderList" size="small" stripe style="width: 100%" v-loading="orderListLoading"
          show-summary :summary-method="getOrderSummary"
        >
          <el-table-column prop="orderNo" label="订单号" min-width="160" show-overflow-tooltip />
          <el-table-column prop="orderDate" label="日期" width="110" />
          <el-table-column prop="customerName" label="客户" min-width="120" show-overflow-tooltip />
          <el-table-column prop="totalAmount" label="金额" width="120" align="right">
            <template #default="{ row }">
              <span>¥{{ formatAmount(row.totalAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="orderStatus" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.orderStatus)" size="small">
                {{ statusLabel(row.orderStatus) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="order-pagination">
          <el-pagination
            small
            layout="total, prev, pager, next"
            :total="orderListTotal"
            :page-size="orderListQuery.pageSize"
            :current-page="orderListQuery.pageNum"
            @current-change="onOrderPageChange"
          />
        </div>
      </el-card>
    </div>

    <!-- Row 4: 发票/回单概览 -->
    <div class="grid-row grid-row-4">
      <el-card shadow="hover">
        <template #header><span>发票概览</span></template>
        <div class="overview-stats">
          <div class="overview-item">
            <span class="overview-label">草稿</span>
            <span class="overview-value">{{ invoiceStats.draft }}</span>
          </div>
          <div class="overview-item">
            <span class="overview-label">已开具</span>
            <span class="overview-value" style="color: #67C23A">{{ invoiceStats.issued }}</span>
          </div>
          <div class="overview-item">
            <span class="overview-label">已作废</span>
            <span class="overview-value" style="color: #909399">{{ invoiceStats.cancelled }}</span>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <template #header><span>回单概览</span></template>
        <div class="overview-stats">
          <div class="overview-item">
            <span class="overview-label">未签收</span>
            <span class="overview-value" style="color: #E6A23C">{{ receiptStats.not_received }}</span>
          </div>
          <div class="overview-item">
            <span class="overview-label">已签收</span>
            <span class="overview-value" style="color: #67C23A">{{ receiptStats.received }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Document, Van, Money, Tickets } from '@element-plus/icons-vue'
import { useDashboardData } from './dashboard/useDashboardData'

const {
  loading,
  chartLoading,
  selectedMonth,
  statusCounts,
  todayCount,
  transportingCount,
  monthRevenue,
  pendingInvoiceCount,
  invoiceStats,
  receiptStats,
  chartData,
  customerRanking,
  loadAll,
  onMonthChange,
  // 订单列表
  orderListLoading,
  orderDateType,
  orderDateValue,
  orderDateRange,
  orderCustomerId,
  orderList,
  orderListTotal,
  orderListQuery,
  orderListAmountSum,
  customerOptions,
  onDateTypeChange,
  onOrderSearch,
  onOrderPageChange,
} = useDashboardData()

// 图表 DOM ref
const monthlyChartRef = ref<HTMLElement>()
const statusPieRef = ref<HTMLElement>()
let monthlyChart: echarts.ECharts | null = null
let statusPie: echarts.ECharts | null = null

function formatAmount(val: number) {
  if (val === undefined || val === null) return '0.00'
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const statusMap: Record<string, string> = {
  pending: '待发车',
  transporting: '运输中',
  completed: '已完成',
  cancelled: '已取消',
}

const statusTagTypeMap: Record<string, string> = {
  pending: 'warning',
  transporting: '',
  completed: 'success',
  cancelled: 'info',
}

function statusLabel(status: string) {
  return statusMap[status] || status
}

function statusTagType(status: string) {
  return statusTagTypeMap[status] || ''
}

// 订单表格合计行
function getOrderSummary({ columns, data }: any) {
  const sums: string[] = []
  columns.forEach((col: any, index: number) => {
    if (index === 0) {
      sums[index] = `合计（${data.length}条）`
      return
    }
    if (index === 1 || index === 2) {
      sums[index] = ''
      return
    }
    if (col.property === 'totalAmount') {
      sums[index] = `¥${formatAmount(orderListAmountSum.value)}`
    } else {
      sums[index] = ''
    }
  })
  return sums
}

// 日期区间快捷选项：最近3个自然月
const dateRangeShortcuts = (() => {
  function getRecentMonths(n: number) {
    const end = new Date()
    const start = new Date(end.getFullYear(), end.getMonth() - n + 1, 1)
    return [start, end]
  }
  return [
    { text: '最近1个月', value: getRecentMonths(1) },
    { text: '最近2个月', value: getRecentMonths(2) },
    { text: '最近3个月', value: getRecentMonths(3) },
  ]
})()

function renderMonthlyChart() {
  if (!monthlyChartRef.value) return
  if (!monthlyChart) {
    monthlyChart = echarts.init(monthlyChartRef.value)
  }
  const { days, amounts, cumulative } = chartData.value
  monthlyChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter(params: any) {
        let tip = params[0].name + '<br/>'
        params.forEach((p: any) => {
          tip += `${p.marker} ${p.seriesName}: ¥${Number(p.value).toLocaleString('zh-CN', { minimumFractionDigits: 2 })}<br/>`
        })
        return tip
      },
    },
    legend: { data: ['日金额', '累计金额'] },
    grid: { left: 80, right: 70, top: 40, bottom: 30 },
    xAxis: {
      type: 'category',
      data: days,
      axisLabel: { interval: 'auto' },
    },
    yAxis: [
      {
        type: 'value',
        name: '金额(元)',
        axisLabel: {
          formatter(val: number) {
            if (val >= 10000) return (val / 10000).toFixed(1) + '万'
            return val.toString()
          },
        },
      },
      {
        type: 'value',
        name: '累计(元)',
        axisLabel: {
          formatter(val: number) {
            if (val >= 10000) return (val / 10000).toFixed(1) + '万'
            return val.toString()
          },
        },
      },
    ],
    series: [
      {
        name: '日金额',
        type: 'bar',
        data: amounts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#79bbff' },
          ]),
          borderRadius: [3, 3, 0, 0],
        },
      },
      {
        name: '累计金额',
        type: 'line',
        yAxisIndex: 1,
        data: cumulative,
        smooth: true,
        itemStyle: { color: '#67C23A' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103,194,58,0.25)' },
            { offset: 1, color: 'rgba(103,194,58,0.02)' },
          ]),
        },
      },
    ],
  }, true)
}

function renderStatusPie() {
  if (!statusPieRef.value) return
  if (!statusPie) {
    statusPie = echarts.init(statusPieRef.value)
  }
  const colors = ['#E6A23C', '#409EFF', '#67C23A', '#909399']
  const data = statusCounts.value.map((item, index) => ({
    ...item,
    itemStyle: { color: colors[index % colors.length] },
  }))
  const total = data.reduce((sum, d) => sum + d.value, 0)
  statusPie.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
    },
    title: {
      text: `${total}`,
      subtext: '总订单',
      left: 'center',
      top: '42%',
      textAlign: 'center',
      textStyle: {
        fontSize: 22,
        fontWeight: 'bold',
        color: '#303133',
      },
      subtextStyle: {
        fontSize: 12,
        color: '#909399',
      },
    },
    series: [
      {
        type: 'pie',
        radius: ['50%', '72%'],
        center: ['50%', '50%'],
        data,
        label: {
          show: true,
          formatter: '{b}\n{c}单',
          lineHeight: 18,
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.2)',
          },
        },
      },
    ],
  }, true)
}

function handleResize() {
  monthlyChart?.resize()
  statusPie?.resize()
}

// 监听月度数据变化，更新图表
watch(chartData, () => {
  nextTick(renderMonthlyChart)
})

// 监听状态数据变化，更新饼图
watch(statusCounts, () => {
  nextTick(renderStatusPie)
})

onMounted(async () => {
  await loadAll()
  nextTick(() => {
    renderMonthlyChart()
    renderStatusPie()
  })
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  monthlyChart?.dispose()
  statusPie?.dispose()
  monthlyChart = null
  statusPie = null
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 16px;
  background-color: var(--el-bg-color-page);
}

// CSS Grid 行布局，保证同行卡片等高
.grid-row {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.grid-row-2 {
  grid-template-columns: 2fr 1fr;
}

.grid-row-3 {
  grid-template-columns: 1fr 2fr;
}

.grid-row-4 {
  grid-template-columns: 1fr 1fr;
}

.grid-row > .el-card {
  // 让 el-card 填满 grid 单元格高度
  height: 100%;
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    flex: 1;
  }
}

// 统计卡片
.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  :deep(.el-card__body) {
    padding: 20px;
  }

  .stat-card-inner {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;
  }

  .stat-info {
    flex: 1;
    min-width: 0;
  }

  .stat-value {
    font-size: 22px;
    font-weight: 700;
    color: var(--el-text-color-primary);
    line-height: 1.3;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .stat-label {
    font-size: 14px;
    color: var(--el-text-color-secondary);
    margin-top: 4px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .card-header-sub {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
}

// 客户排名
.customer-ranking {
  .ranking-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);

    &:last-child {
      border-bottom: none;
    }
  }

  .ranking-left {
    display: flex;
    align-items: center;
    gap: 10px;
    min-width: 0;
  }

  .ranking-badge {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: bold;
    color: #fff;
    background-color: #909399;
    flex-shrink: 0;

    &-1 { background-color: #F56C6C; }
    &-2 { background-color: #E6A23C; }
    &-3 { background-color: #409EFF; }
  }

  .ranking-name {
    font-size: 14px;
    color: var(--el-text-color-primary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .ranking-right {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-shrink: 0;
  }

  .ranking-count {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .ranking-amount {
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }
}

// 订单搜索表单
.order-search-form {
  margin-bottom: 12px;

  :deep(.el-form-item) {
    margin-bottom: 8px;
  }
}

.order-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

// 发票/回单概览
.overview-stats {
  display: flex;
  gap: 32px;
  padding: 8px 0;

  .overview-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }

  .overview-label {
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  .overview-value {
    font-size: 24px;
    font-weight: 700;
    color: var(--el-text-color-primary);
  }
}
</style>
