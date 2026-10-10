import request from '@/utils/request'

export const getCategoryTree = () => {
  return request({
    url: '/food/category/tree',
    method: 'get',
  })
}

export const addCategory = (data) => {
  return request({
    url: '/food/category',
    method: 'post',
    data,
  })
}

export const updateCategory = (data) => {
  return request({
    url: '/food/category',
    method: 'put',
    data,
  })
}

export const deleteCategory = (id) => {
  return request({
    url: `/food/category/${id}`,
    method: 'delete',
  })
}