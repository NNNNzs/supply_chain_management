import request from '@/utils/request'

// 查询货物列表
export function listGoods(query) {
  return request({
    url: '/logistics/goods/list',
    method: 'get',
    params: query
  })
}

// 查询货物详细
export function getGoods(goodsId) {
  return request({
    url: '/logistics/goods/' + goodsId,
    method: 'get'
  })
}

// 新增货物
export function addGoods(data) {
  return request({
    url: '/logistics/goods',
    method: 'post',
    data: data
  })
}

// 修改货物
export function updateGoods(data) {
  return request({
    url: '/logistics/goods',
    method: 'put',
    data: data
  })
}

// 删除货物
export function delGoods(goodsId) {
  return request({
    url: '/logistics/goods/' + goodsId,
    method: 'delete'
  })
}
