import { defineStore } from 'pinia'
import { ref, onMounted } from 'vue'
import { getLoginUser } from '@/api/userController.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  // 默认值
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  // 从本地存储加载用户信息
  function loadFromLocalStorage() {
    const storedUser = localStorage.getItem('loginUser')
    if (storedUser) {
      try {
        loginUser.value = JSON.parse(storedUser)
      } catch (e) {
        console.error('解析本地存储的用户信息失败:', e)
      }
    }
  }

  // 保存用户信息到本地存储
  function saveToLocalStorage() {
    localStorage.setItem('loginUser', JSON.stringify(loginUser.value))
  }

  // 获取登录用户信息
  async function fetchLoginUser() {
    try {
      const res = await getLoginUser()
      if (res.data.code === 0 && res.data.data) {
        loginUser.value = res.data.data
        saveToLocalStorage()
      }
    } catch (e) {
      console.error('获取登录用户信息失败:', e)
    }
  }

  // 更新登录用户信息
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
    saveToLocalStorage()
  }

  // 组件挂载时从本地存储加载用户信息
  onMounted(() => {
    loadFromLocalStorage()
  })

  return { loginUser, setLoginUser, fetchLoginUser, loadFromLocalStorage, saveToLocalStorage }
})
