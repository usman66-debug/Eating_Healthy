import request from '@/utils/request'

export const getCategoryTree = () => {
  return request({
    url: '/food/category/tree',
    method: 'get',
  })
}

export const getCategoryList = () => {
  return request({
    url: '/food/category/list',
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

export const getFoodList = (params) => {
  return request({
    url: '/food/info/list',
    method: 'get',
    params,
  })
}

export const addFood = (data) => {
  return request({
    url: '/food/info',
    method: 'post',
    data,
  })
}

export const updateFood = (data) => {
  return request({
    url: '/food/info',
    method: 'put',
    data,
  })
}

export const getFoodDetail = (id) => {
  return request({
    url: `/food/info/${id}`,
    method: 'get',
  })
}

export const deleteFood = (id) => {
  return request({
    url: `/food/info/${id}`,
    method: 'delete',
  })
}
