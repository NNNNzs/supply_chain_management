import request from '@/utils/request'

// 查询驾驶员单列表
export function listDriverOrder(query) {
  return request({
    url: '/logistics/driverOrder/list',
    method: 'get',
    params: query
  })
}

// 查询驾驶员单详细
export function getDriverOrder(driverOrderId) {
  return request({
    url: '/logistics/driverOrder/' + driverOrderId,
    method: 'get'
  })
}

// 新增驾驶员单
export function addDriverOrder(data) {
  return request({
    url: '/logistics/driverOrder',
    method: 'post',
    data: data
  })
}

// 修改驾驶员单
export function updateDriverOrder(data) {
  return request({
    url: '/logistics/driverOrder',
    method: 'put',
    data: data
  })
}

// 删除驾驶员单
export function delDriverOrder(driverOrderId) {
  return request({
    url: '/logistics/driverOrder/' + driverOrderId,
    method: 'delete'
  })
}

// 导出驾驶员单
export function exportDriverOrder(query) {
  return request({
    url: '/logistics/driverOrder/export',
    method: 'get',
    params: query
  })
}

// 更新驾驶员单状态
export function updateDriverOrderStatus(driverOrderId, status) {
  return request({
    url: '/logistics/driverOrder/status/' + driverOrderId,
    method: 'put',
    data: { status }
  })
}
