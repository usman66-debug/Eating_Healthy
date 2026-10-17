import request from '@/utils/request'

export const loginApi = (data) => {
  return request({
    url:'/auth/login',
    method:'post',
    data
  })
}

export const profileApi = (data) => {
  return request({
    url:'/auth/profile',
    method:'put',
    data
  })
}

export const changePasswordApi = (data) => {
  return request({
    url:'/auth/change-password',
    method:'post',
    data
  })
}
