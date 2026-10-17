import request from '@/utils/request'

export function getAllRecords(params){
  return request({
    url:'/recommend/record/list',
    method:'get',
    params,
  })
}

export function getTodayRecommend(){
  return request({
    url:'/recommend/record/today',
    method:'get',
  })
}

export function generateRecommend(){
  return request({
    url:'/recommend/record/generate',
    method:'post',
  })
}

export function feedbackRecommend(data){
  return request({
    url:'/recommend/record/feedback',
    method:'post',
    data,
  })
}

export function aiRecommend(data){
  return request({
    url:'/recommend/record/ai',
    method:'post',
    data,
  })
}

export function getRecommendHistory(params){
  return request({
    url:'/recommend/record/my',
    method:'get',
    params, 
  })
}
