import request from '@/utils/request'

export const getUserListApi = (params) => {
    return request({
        url:'/user/list',
        method:'get',
        params
    })
}

export const addUserApi = (data) => {
    return request({
        url:'/user',
        method:'post',
        data
    })
}

