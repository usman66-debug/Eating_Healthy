import request from '@/utils/request'

export const getUserListApi = (params) => {
    return request({
        url:'/user/list',
        method:'get',
        params
    })
}

