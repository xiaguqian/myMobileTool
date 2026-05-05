import request from './request'

export function getMenus(params) {
  return request({
    url: '/menus',
    method: 'get',
    params
  })
}

export function getActiveMenus() {
  return request({
    url: '/menus/active',
    method: 'get'
  })
}

export function getMenuById(id) {
  return request({
    url: `/menus/${id}`,
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/menus',
    method: 'post',
    data
  })
}

export function updateMenu(id, data) {
  return request({
    url: `/menus/${id}`,
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/menus/${id}`,
    method: 'delete'
  })
}
