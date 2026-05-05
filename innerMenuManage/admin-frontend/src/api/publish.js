import request from './request'

export function publish(data) {
  return request({
    url: '/publish',
    method: 'post',
    data
  })
}

export function getLatestPublish() {
  return request({
    url: '/publish/latest',
    method: 'get'
  })
}

export function getPublishHistory(params) {
  return request({
    url: '/publish/history',
    method: 'get',
    params
  })
}

export function getPublishedData() {
  return request({
    url: '/publish/data',
    method: 'get'
  })
}
