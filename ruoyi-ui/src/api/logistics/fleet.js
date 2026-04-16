import request from '@/utils/request'

// 查询车队列表
export function listFleet(query) {
  return request({
    url: '/logistics/fleet/list',
    method: 'get',
    params: query
  })
}

// 查询车队详细
export function getFleet(fleetId) {
  return request({
    url: '/logistics/fleet/' + fleetId,
    method: 'get'
  })
}

// 新增车队
export function addFleet(data) {
  return request({
    url: '/logistics/fleet',
    method: 'post',
    data: data
  })
}

// 修改车队
export function updateFleet(data) {
  return request({
    url: '/logistics/fleet',
    method: 'put',
    data: data
  })
}

// 删除车队
export function delFleet(fleetId) {
  return request({
    url: '/logistics/fleet/' + fleetId,
    method: 'delete'
  })
}

// 导出车队
export function exportFleet(query) {
  return request({
    url: '/logistics/fleet/export',
    method: 'post',
    params: query
  })
}

// 获取所有可用车队
export function getAllFleets() {
  return request({
    url: '/logistics/fleet/all',
    method: 'get'
  })
}
