import request from '@/utils/request'

// 查询订单货物明细列表
export function listOrderGoods(query) {
  return request({
    url: '/logistics/orderGoods/list',
    method: 'get',
    params: query
  })
}

// 查询订单货物明细详细
export function getOrderGoods(detailId) {
  return request({
    url: '/logistics/orderGoods/' + detailId,
    method: 'get'
  })
}

// 根据订单ID查询货物明细
export function getOrderGoodsByOrderId(orderId) {
  return request({
    url: '/logistics/orderGoods/order/' + orderId,
    method: 'get'
  })
}

// 新增订单货物明细
export function addOrderGoods(data) {
  return request({
    url: '/logistics/orderGoods',
    method: 'post',
    data: data
  })
}

// 修改订单货物明细
export function updateOrderGoods(data) {
  return request({
    url: '/logistics/orderGoods',
    method: 'put',
    data: data
  })
}

// 删除订单货物明细
export function delOrderGoods(detailId) {
  return request({
    url: '/logistics/orderGoods/' + detailId,
    method: 'delete'
  })
}
