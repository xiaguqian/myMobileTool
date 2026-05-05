import request from './request'

export function getPagesBySlotCode(slotCode) {
  return request({
    url: `/slots/${encodeURIComponent(slotCode)}/pages`,
    method: 'get'
  })
}

export function getMenuTreesBySlotCode(slotCode) {
  return request({
    url: `/slots/${encodeURIComponent(slotCode)}/menu-trees`,
    method: 'get'
  })
}

export function getSlotData(slotCode) {
  return request({
    url: `/slots/${encodeURIComponent(slotCode)}`,
    method: 'get'
  })
}

export function getMenusWithPermissions(permissionCodes) {
  return request({
    url: '/menus/with-permissions',
    method: 'post',
    data: { permissionCodes }
  })
}

export function getAllMenus() {
  return request({
    url: '/menus/all',
    method: 'get'
  })
}

export function getPageByRoute(routePath) {
  return request({
    url: `/pages/route/${encodeURIComponent(routePath)}`,
    method: 'get'
  })
}
