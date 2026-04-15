import request from '@/utils/request'

// 查询司机/车队树形列表
export function treeList(query) {
  return request({
    url: '/logistics/driver/treeList',
    method: 'get',
    params: query
  })
}

// 查询司机列表（平铺）
export function listDriver(query) {
  return request({
    url: '/logistics/driver/list',
    method: 'get',
    params: query
  })
}

// 查询司机详细
export function getDriver(driverId) {
  return request({
    url: '/logistics/driver/driver/' + driverId,
    method: 'get'
  })
}

// 查询车队详细
export function getFleet(fleetId) {
  return request({
    url: '/logistics/driver/fleet/' + fleetId,
    method: 'get'
  })
}

// 新增司机
export function addDriver(data) {
  return request({
    url: '/logistics/driver/driver',
    method: 'post',
    data: data
  })
}

// 新增车队
export function addFleet(data) {
  return request({
    url: '/logistics/driver/fleet',
    method: 'post',
    data: data
  })
}

// 修改司机
export function updateDriver(data) {
  return request({
    url: '/logistics/driver/driver',
    method: 'put',
    data: data
  })
}

// 修改车队
export function updateFleet(data) {
  return request({
    url: '/logistics/driver/fleet',
    method: 'put',
    data: data
  })
}

// 删除司机
export function delDriver(driverId) {
  return request({
    url: '/logistics/driver/driver/' + driverId,
    method: 'delete'
  })
}

// 删除车队
export function delFleet(fleetId) {
  return request({
    url: '/logistics/driver/fleet/' + fleetId,
    method: 'delete'
  })
}

// 导出司机
export function exportDriver(query) {
  return request({
    url: '/logistics/driver/export',
    method: 'post',
    params: query
  })
}

// 获取所有可用车队
export function getAllFleets() {
  return request({
    url: '/logistics/driver/fleet/all',
    method: 'get'
  })
}

// 获取所有可用司机
export function getAllDrivers() {
  return request({
    url: '/logistics/driver/all',
    method: 'get'
  })
}
