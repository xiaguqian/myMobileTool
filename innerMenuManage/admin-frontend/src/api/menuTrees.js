import request from './request'

export function getMenuTrees(params) {
  return request({
    url: '/menu-trees',
    method: 'get',
    params
  })
}

export function getMenuTreeById(id) {
  return request({
    url: `/menu-trees/${id}`,
    method: 'get'
  })
}

export function getMenuTreeStructure(id) {
  return request({
    url: `/menu-trees/${id}/structure`,
    method: 'get'
  })
}

export function createMenuTree(data) {
  return request({
    url: '/menu-trees',
    method: 'post',
    data
  })
}

export function updateMenuTree(id, data) {
  return request({
    url: `/menu-trees/${id}`,
    method: 'put',
    data
  })
}

export function deleteMenuTree(id) {
  return request({
    url: `/menu-trees/${id}`,
    method: 'delete'
  })
}

export function addTreeNode(data) {
  return request({
    url: '/menu-trees/node',
    method: 'post',
    data
  })
}

export function updateTreeNode(data) {
  return request({
    url: '/menu-trees/node',
    method: 'put',
    data
  })
}

export function deleteTreeNode(id) {
  return request({
    url: `/menu-trees/node/${id}`,
    method: 'delete'
  })
}

export function batchUpdateTreeNodes(data) {
  return request({
    url: '/menu-trees/batch-update',
    method: 'post',
    data
  })
}
