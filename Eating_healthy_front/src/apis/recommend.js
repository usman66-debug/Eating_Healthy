import request from '@/utils/request'

export function getAllRecords(params){
  return request({
    url:'/recommend/record/list',
    method:'get',
    params,
  })
}
