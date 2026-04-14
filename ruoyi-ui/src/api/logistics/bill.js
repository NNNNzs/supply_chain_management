import request from '@/utils/request'

// 查询提单列表
export function listBill(query) {
  return request({
    url: '/logistics/bill/list',
    method: 'get',
    params: query
  })
}

// 查询提单详细
export function getBill(billId) {
  return request({
    url: '/logistics/bill/' + billId,
    method: 'get'
  })
}

// 新增提单
export function addBill(data) {
  return request({
    url: '/logistics/bill',
    method: 'post',
    data: data
  })
}

// 修改提单
export function updateBill(data) {
  return request({
    url: '/logistics/bill',
    method: 'put',
    data: data
  })
}

// 删除提单
export function delBill(billId) {
  return request({
    url: '/logistics/bill/' + billId,
    method: 'delete'
  })
}

// 导出提单
export function exportBill(query) {
  return request({
    url: '/logistics/bill/export',
    method: 'get',
    params: query
  })
}

// 导入提单
export function importBill(data) {
  return request({
    url: '/logistics/bill/importData',
    method: 'post',
    data: data
  })
}

// 下载导入模板
export function importTemplate() {
  return request({
    url: '/logistics/bill/importTemplate',
    method: 'get'
  })
}

// 查询待配载提单
export function getPendingBills() {
  return request({
    url: '/logistics/bill/pending',
    method: 'get'
  })
}

// 查询提单分配明细
export function getBillAllocations(billId) {
  return request({
    url: '/logistics/bill/' + billId + '/allocations',
    method: 'get'
  })
}
