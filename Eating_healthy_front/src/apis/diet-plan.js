import request from '@/utils/request'


export function getDietPlanList(params){
  return request({
    url:'/diet-plan/list',
    method:'get',
    params,
  })
}

export function getDietPlanDetail(id){
  return request({
    url:`/diet-plan/${id}`,
    method:'get',
  })
}

export function deleteDietPlanDetail(id){
  return request({
    url:`/diet-plan/${id}`,
    method:'delete',
  })
}