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

  return {
    token,
    userInfo,
    menuList,
    setLoginInfo
  }
})
