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

export const updateUserApi = (data) => {
    return request({
        url:'/user',
        method:'put',
        data
    })
}

export const resetPasswordApi = (id) => {
    return request({
        url:`/user/reset-password/${id}`,
        method:'post',
    })
}

export const deleteUserApi = (id) => {
    return request({
        url:`/user/${id}`,
        method:'delete',
    })
}

