import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL:'/api',
  timeout:30000
})

//请求拦截器
request.interceptors.request.use(
  config=>{
    const token = localStorage.getItem('token')
    if(token){
      config.headers.Authorization = 'Bearer ' + token
    }
    return config
  },
  error=>{
    return Promise.reject(error)
  }
)

//响应拦截器
request.interceptors.response.use(
  response=>{
    // 下载或需要响应头/HTTP 状态的接口可以显式保留原始 Axios 响应。
    if (response.config.rawResponse || ['blob', 'arraybuffer'].includes(response.config.responseType)) {
      return response
    }
    const res = response.data
    if(res.code !== 200){
      ElMessage.error(res.message || '请求失败')
      res.handled = true // 页面捕获后避免重复提示
      if(res.code === 401){
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('menuList')
        router.push('/login')
      }
      return Promise.reject(res)
    }
    return res
  },
  error=>{
    ElMessage.error(error.response?.data?.message || error.message || '请求失败')
    error.handled = true
    return Promise.reject(error)
  }
)

export default request
