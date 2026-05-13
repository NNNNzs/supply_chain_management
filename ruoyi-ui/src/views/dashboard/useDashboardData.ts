import { ref, computed } from 'vue'
import { listOrder } from '@/api/logistics/order'
import { listCustomer } from '@/api/logistics/customer'
import { listInvoiceBatch } from '@/api/logistics/invoice'
import { listReceipt } from '@/api/logistics/receipt'

function getMonthRange(yearMonth: string) {
  const [year, month] = yearMonth.split('-').map(Number)
  const startDate = `${year}-${String(month).padStart(2, '0')}-01`
  const lastDay = new Date(year, month, 0).getDate()
  const endDate = `${year}-${String(month).padStart(2, '0')}-${lastDay}`
  return { startDate, endDate, daysInMonth: lastDay }
}

function getToday() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

function getCurrentMonth() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

export function useDashboardData() {
  const loading = ref(false)
  const chartLoading = ref(false)
  const selectedMonth = ref(getCurrentMonth())

  // 原始数据
  const monthlyOrders = ref<any[]>([])
  const statusCounts = ref<{ name: string; value: number }[]>([])
  const todayCount = ref(0)
  const transportingCount = ref(0)
  const pendingInvoiceCount = ref(0)
  const invoiceStats = ref<{ draft: number; issued: number; cancelled: number }>({ draft: 0, issued: 0, cancelled: 0 })
  const receiptStats = ref<{ not_received: number; received: number }>({ not_received: 0, received: 0 })

  // 订单列表搜索
  const orderListLoading = ref(false)
  const orderDateType = ref<'month' | 'daterange'>('month')
  const orderDateValue = ref(getCurrentMonth())
  const orderDateRange = ref<[string, string] | null>(null)
  const orderCustomerId = ref<number | undefined>(undefined)
  const orderList = ref<any[]>([])
  const orderListTotal = ref(0)
  const orderListQuery = ref({ pageNum: 1, pageSize: 20 })
  const customerOptions = ref<{ customerId: number; customerName: string }[]>([])

  // 订单列表金额合计
  const orderListAmountSum = computed(() => {
    return orderList.value.reduce((sum, o) => sum + (Number(o.totalAmount) || 0), 0)
  })

  // 获取月度全量订单（分页）
  async function fetchAllMonthlyOrders(yearMonth: string) {
    const { startDate, endDate } = getMonthRange(yearMonth)
    let allOrders: any[] = []
    let pageNum = 1
    let total = Infinity
    while (allOrders.length < total) {
      const res = await listOrder({
        pageNum,
        pageSize: 500,
        params: { beginOrderDate: startDate, endOrderDate: endDate }
      }) as any
      allOrders = allOrders.concat(res.rows || [])
      total = res.total || 0
      pageNum++
    }
    return allOrders
  }

  // 轻量获取某个条件的 total 数量
  async function fetchCount(params: any) {
    const res = await listOrder({ pageNum: 1, pageSize: 1, ...params }) as any
    return res.total || 0
  }

  // 统计卡片数据
  async function fetchStatCards() {
    const today = getToday()
    const [todayTotal, transportingTotal, pendingInvTotal] = await Promise.all([
      fetchCount({ params: { beginOrderDate: today, endOrderDate: today } }),
      fetchCount({ orderStatus: 'transporting' }),
      fetchCount({ invoiceStatus: 'not_invoiced' }),
    ])
    todayCount.value = todayTotal
    transportingCount.value = transportingTotal
    pendingInvoiceCount.value = pendingInvTotal
  }

  // 订单状态分布
  async function fetchStatusCounts() {
    const statuses = [
      { key: 'pending', label: '待发车' },
      { key: 'transporting', label: '运输中' },
      { key: 'completed', label: '已完成' },
      { key: 'cancelled', label: '已取消' },
    ]
    const results = await Promise.all(
      statuses.map(s => fetchCount({ orderStatus: s.key }).then(count => ({ name: s.label, value: count })))
    )
    statusCounts.value = results
  }

  // 获取客户下拉选项
  async function fetchCustomerOptions() {
    const res = await listCustomer({ pageNum: 1, pageSize: 1000 }) as any
    customerOptions.value = (res.rows || []).map((c: any) => ({
      customerId: c.customerId,
      customerName: c.customerName,
    }))
  }

  // 订单列表搜索
  async function searchOrderList() {
    orderListLoading.value = true
    try {
      const params: any = {
        ...orderListQuery.value,
        orderByColumn: 'o.order_id',
        isAsc: 'descending',
      }
      if (orderCustomerId.value) {
        params.customerId = orderCustomerId.value
      }
      if (orderDateType.value === 'month' && orderDateValue.value) {
        const [year, month] = orderDateValue.value.split('-').map(Number)
        const lastDay = new Date(year, month, 0).getDate()
        params.params = {
          beginOrderDate: `${orderDateValue.value}-01`,
          endOrderDate: `${orderDateValue.value}-${lastDay}`,
        }
      } else if (orderDateType.value === 'daterange' && orderDateRange.value) {
        params.params = {
          beginOrderDate: orderDateRange.value[0],
          endOrderDate: orderDateRange.value[1],
        }
      }
      const res = await listOrder(params) as any
      orderList.value = res.rows || []
      orderListTotal.value = res.total || 0
    } finally {
      orderListLoading.value = false
    }
  }

  // 日期类型切换时重置值
  function onDateTypeChange() {
    if (orderDateType.value === 'month') {
      orderDateValue.value = getCurrentMonth()
      orderDateRange.value = null
    } else {
      orderDateValue.value = ''
      // 默认最近1个月
      const end = new Date()
      const start = new Date(end.getFullYear(), end.getMonth(), 1)
      const fmt = (d: Date) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
      orderDateRange.value = [fmt(start), fmt(end)]
    }
    orderListQuery.value.pageNum = 1
    searchOrderList()
  }

  // 搜索
  function onOrderSearch() {
    orderListQuery.value.pageNum = 1
    searchOrderList()
  }

  // 翻页
  function onOrderPageChange(page: number) {
    orderListQuery.value.pageNum = page
    searchOrderList()
  }

  // 发票/回单状态统计
  async function fetchInvoiceReceiptStats() {
    const [draftRes, issuedRes, cancelledRes, notReceivedRes, receivedRes] = await Promise.all([
      listInvoiceBatch({ pageNum: 1, pageSize: 1, invoiceStatus: 'draft' }),
      listInvoiceBatch({ pageNum: 1, pageSize: 1, invoiceStatus: 'issued' }),
      listInvoiceBatch({ pageNum: 1, pageSize: 1, invoiceStatus: 'cancelled' }),
      listReceipt({ pageNum: 1, pageSize: 1, receiptStatus: 'not_received' }),
      listReceipt({ pageNum: 1, pageSize: 1, receiptStatus: 'received' }),
    ] as any[])
    invoiceStats.value = {
      draft: draftRes.total || 0,
      issued: issuedRes.total || 0,
      cancelled: cancelledRes.total || 0,
    }
    receiptStats.value = {
      not_received: notReceivedRes.total || 0,
      received: receivedRes.total || 0,
    }
  }

  // 月度图表数据
  const chartData = computed(() => {
    const { daysInMonth } = getMonthRange(selectedMonth.value)
    const dailyMap: Record<string, number> = {}
    const dailyCountMap: Record<string, number> = {}
    for (let i = 1; i <= daysInMonth; i++) {
      const day = String(i).padStart(2, '0')
      dailyMap[day] = 0
      dailyCountMap[day] = 0
    }
    monthlyOrders.value.forEach(order => {
      const dateStr = order.orderDate || ''
      const day = dateStr.substring(8, 10)
      if (day && dailyMap[day] !== undefined) {
        dailyMap[day] += Number(order.totalAmount) || 0
        dailyCountMap[day]++
      }
    })
    const days: string[] = []
    const amounts: number[] = []
    const counts: number[] = []
    const cumulative: number[] = []
    let runningTotal = 0
    for (let i = 1; i <= daysInMonth; i++) {
      const day = String(i).padStart(2, '0')
      days.push(`${i}日`)
      const amount = dailyMap[day] || 0
      amounts.push(Math.round(amount * 100) / 100)
      counts.push(dailyCountMap[day] || 0)
      runningTotal += amount
      cumulative.push(Math.round(runningTotal * 100) / 100)
    }
    return { days, amounts, counts, cumulative }
  })

  // 本月营收（从 monthlyOrders 聚合）
  const monthRevenue = computed(() => {
    return monthlyOrders.value.reduce((sum, o) => sum + (Number(o.totalAmount) || 0), 0)
  })

  // 客户排名 Top 5
  const customerRanking = computed(() => {
    const map: Record<string, { name: string; count: number; amount: number }> = {}
    monthlyOrders.value.forEach(o => {
      const name = o.customerName || '未知'
      if (!map[name]) map[name] = { name, count: 0, amount: 0 }
      map[name].count++
      map[name].amount += Number(o.totalAmount) || 0
    })
    return Object.values(map)
      .sort((a, b) => b.amount - a.amount)
      .slice(0, 5)
      .map(item => ({
        ...item,
        amount: Math.round(item.amount * 100) / 100,
      }))
  })

  // 主加载函数
  async function loadAll() {
    loading.value = true
    try {
      const [orders] = await Promise.all([
        fetchAllMonthlyOrders(selectedMonth.value),
        fetchStatCards(),
        fetchStatusCounts(),
        fetchCustomerOptions(),
        fetchInvoiceReceiptStats(),
      ])
      monthlyOrders.value = orders
    } finally {
      loading.value = false
    }
    searchOrderList()
  }

  // 切换月份
  async function onMonthChange() {
    chartLoading.value = true
    try {
      monthlyOrders.value = await fetchAllMonthlyOrders(selectedMonth.value)
    } finally {
      chartLoading.value = false
    }
  }

  return {
    loading,
    chartLoading,
    selectedMonth,
    monthlyOrders,
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
    searchOrderList,
    onDateTypeChange,
    onOrderSearch,
    onOrderPageChange,
  }
}
