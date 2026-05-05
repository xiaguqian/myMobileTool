import request from './request'

export function getPages(params) {
  return request({
    url: '/pages',
    method: 'get',
    params
  })
}

export function getPageById(id) {
  return request({
    url: `/pages/${id}`,
    method: 'get'
  })
}

export function createPage(data) {
  return request({
    url: '/pages',
    method: 'post',
    data
  })
}

export function updatePage(id, data) {
  return request({
    url: `/pages/${id}`,
    method: 'put',
    data
  })
}

export function deletePage(id) {
  return request({
    url: `/pages/${id}`,
    method: 'delete'
  })
}

export function addPageComponent(data) {
  return request({
    url: '/pages/add-component',
    method: 'post',
    data
  })
}

export function removePageComponent(data) {
  return request({
    url: '/pages/remove-component',
    method: 'post',
    data
  })
}

export function updateComponentConfig(data) {
  return request({
    url: '/pages/update-component-config',
    method: 'put',
    data
  })
}

export function batchConfigureComponents(data) {
  return request({
    url: '/pages/batch-configure-components',
    method: 'post',
    data
  })
}
