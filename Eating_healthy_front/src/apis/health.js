import request from '@/utils/request'

export function getHealthProfileList(data) {
  return request({
    url: '/health-profile/list',
    method: 'get',
    params:data
  })
}

export function getHealthProfile() {
  return request({
    url: '/health-profile/mine',
    method: 'get',
  })
}

export function saveHealthProfile(data) {
  return request({
    url: '/health-profile/mine',
    method: 'post',
    data
  })
}