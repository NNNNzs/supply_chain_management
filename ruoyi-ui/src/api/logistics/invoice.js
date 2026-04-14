import request from '@/utils/request'

// 查询发票批次列表
export function listInvoiceBatch(query) {
  return request({
    url: '/logistics/invoice/list',
    method: 'get',
    params: query
  })
}

// 查询发票批次详细
export function getInvoiceBatch(batchId) {
  return request({
    url: '/logistics/invoice/' + batchId,
    method: 'get'
  })
}

// 新增发票批次
export function addInvoiceBatch(data) {
  return request({
    url: '/logistics/invoice',
    method: 'post',
    data: data
  })
}

// 修改发票批次
export function updateInvoiceBatch(data) {
  return request({
    url: '/logistics/invoice',
    method: 'put',
    data: data
  })
}

// 删除发票批次
export function delInvoiceBatch(batchId) {
  return request({
    url: '/logistics/invoice/' + batchId,
    method: 'delete'
  })
}

// 合并开票
export function mergeInvoice(data) {
  return request({
    url: '/logistics/invoice/merge',
    method: 'post',
    data: data
  })
}

// 发票开具
export function issueInvoice(batchId) {
  return request({
    url: '/logistics/invoice/issue/' + batchId,
    method: 'put'
  })
}

// 发票作废
export function cancelInvoice(batchId) {
  return request({
    url: '/logistics/invoice/cancel/' + batchId,
    method: 'put'
  })
}

// 取消合并
export function cancelMerge(batchId) {
  return request({
    url: '/logistics/invoice/cancelMerge/' + batchId,
    method: 'put'
  })
}

// 查询可开票订单列表
export function listInvoiceableOrders(query) {
  return request({
    url: '/logistics/invoice/orders',
    method: 'get',
    params: query
  })
}
