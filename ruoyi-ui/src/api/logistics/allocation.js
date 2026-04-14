import request from '@/utils/request'

// 查询待配载提单列表
export function getPendingBills() {
  return request({
    url: '/logistics/allocation/pendingBills',
    method: 'get'
  })
}

// 推荐可配载提单
export function recommendBills(loadCapacity) {
  return request({
    url: '/logistics/allocation/recommendBills',
    method: 'get',
    params: { loadCapacity }
  })
}

// 创建运单并分配提单（核心功能）
export function createOrderWithBills(data) {
  return request({
    url: '/logistics/allocation/createOrder',
    method: 'post',
    data: data
  })
}
