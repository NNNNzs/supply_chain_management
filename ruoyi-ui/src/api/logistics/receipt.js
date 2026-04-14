import request from '@/utils/request'

// 查询回单信息列表
export function listReceipt(query) {
  return request({
    url: '/logistics/receipt/list',
    method: 'get',
    params: query
  })
}

// 查询回单信息详细
export function getReceipt(receiptId) {
  return request({
    url: '/logistics/receipt/' + receiptId,
    method: 'get'
  })
}

// 新增回单信息
export function addReceipt(data) {
  return request({
    url: '/logistics/receipt',
    method: 'post',
    data: data
  })
}

// 修改回单信息
export function updateReceipt(data) {
  return request({
    url: '/logistics/receipt',
    method: 'put',
    data: data
  })
}

// 删除回单信息
export function delReceipt(receiptId) {
  return request({
    url: '/logistics/receipt/' + receiptId,
    method: 'delete'
  })
}

// 根据订单ID查询回单信息
export function getReceiptByOrderId(orderId) {
  return request({
    url: '/logistics/receipt/order/' + orderId,
    method: 'get'
  })
}

// 确认回单
export function confirmReceipt(receiptId, receiver) {
  return request({
    url: '/logistics/receipt/confirm/' + receiptId,
    method: 'put',
    params: { receiver }
  })
}

// 导出回单信息
export function exportReceipt(query) {
  return request({
    url: '/logistics/receipt/export',
    method: 'post',
    params: query
  })
}
