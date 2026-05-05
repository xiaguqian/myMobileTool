import request from './request'

export function getSlots(params) {
  return request({
    url: '/slots',
    method: 'get',
    params
  })
}

export function getSlotById(id) {
  return request({
    url: `/slots/${id}`,
    method: 'get'
  })
}

export function createSlot(data) {
  return request({
    url: '/slots',
    method: 'post',
    data
  })
}

export function updateSlot(id, data) {
  return request({
    url: `/slots/${id}`,
    method: 'put',
    data
  })
}

export function deleteSlot(id) {
  return request({
    url: `/slots/${id}`,
    method: 'delete'
  })
}

export function associatePage(data) {
  return request({
    url: '/slots/associate-page',
    method: 'post',
    data
  })
}

export function disassociatePage(data) {
  return request({
    url: '/slots/disassociate-page',
    method: 'post',
    data
  })
}

export function associateMenuTree(data) {
  return request({
    url: '/slots/associate-menu-tree',
    method: 'post',
    data
  })
}

export function disassociateMenuTree(data) {
  return request({
    url: '/slots/disassociate-menu-tree',
    method: 'post',
    data
  })
}
