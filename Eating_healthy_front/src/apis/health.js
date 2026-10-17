import request from '@/utils/request'

export function getHealthProfileList(data) {
  return request({
    url: '/health-profile/list',
    method: 'get',
    params:data
  })
}