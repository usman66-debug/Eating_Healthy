import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user',()=>{
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const menuList = ref([])

  const setLoginInfo = (data) => {
    token.value = data.token
    userInfo.value = data.userInfo
    menuList.value = data.menuList

    localStorage.setItem('token',data.token)
    localStorage.setItem('userInfo',JSON.stringify(data.userInfo))
    localStorage.setItem('menuList',JSON.stringify(data.menuList))
  }

  const saveInfo = localStorage.getItem('userInfo')
    if(saveInfo){
      userInfo.value = JSON.parse(saveInfo)
    }

  const saveMenuList = localStorage.getItem('menuList')
  if(saveMenuList){
    menuList.value = JSON.parse(saveMenuList)
  }

  const isAdmin = ()=>{
    return userInfo.value?.roles?.includes('ADMIN') || false
  }

  const logout = ()=>{
    token.value = ''
    userInfo.value = null
    menuList.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('menuList')
  }

  return {
    token,
    userInfo,
    menuList,
    setLoginInfo,
    isAdmin,
    logout
  }
})
