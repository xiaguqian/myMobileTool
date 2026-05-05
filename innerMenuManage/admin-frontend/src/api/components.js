import request from './request'

export function getComponents(params) {
  return request({
    url: '/components',
    method: 'get',
    params
  })
}

export function getComponentById(id) {
  return request({
    url: `/components/${id}`,
    method: 'get'
  })
}

export function createComponent(data) {
  return request({
    url: '/components',
    method: 'post',
    data
  })
}

export function updateComponent(id, data) {
  return request({
    url: `/components/${id}`,
    method: 'put',
    data
  })
}

export function deleteComponent(id) {
  return request({
    url: `/components/${id}`,
    method: 'delete'
  })
}

export function getComponentTypes() {
  return request({
    url: '/components/types',
    method: 'get'
  })
}
