import request from '@/utils/request'

// 查询订单操作日志列表
export function listOrderLog(query) {
  return request({
    url: '/logistics/orderLog/list',
    method: 'get',
    params: query
  })
}

// 查询订单操作日志详细
export function getOrderLog(logId) {
  return request({
    url: '/logistics/orderLog/' + logId,
    method: 'get'
  })
}

// 根据订单ID查询操作日志列表
export function getOrderLogByOrderId(orderId) {
  return request({
    url: '/logistics/orderLog/order/' + orderId,
    method: 'get'
  })
}

// 新增订单操作日志
export function addOrderLog(data) {
  return request({
    url: '/logistics/orderLog',
    method: 'post',
    data: data
  })
}

// 修改订单操作日志
export function updateOrderLog(data) {
  return request({
    url: '/logistics/orderLog',
    method: 'put',
    data: data
  })
}

// 删除订单操作日志
export function delOrderLog(logId) {
  return request({
    url: '/logistics/orderLog/' + logId,
    method: 'delete'
  })
}
