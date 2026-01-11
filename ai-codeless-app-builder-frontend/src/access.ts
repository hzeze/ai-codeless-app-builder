import { useLoginUserStore } from '@/stores/loginUser'
import { message } from 'ant-design-vue'
import router from '@/router'

// 是否为首次获取登录用户
let firstFetchLoginUser = true

/**
 * 全局权限校验
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser

  // 在登录和注册页面不需要获取用户信息，避免重复请求
  const isAuthPage = to.path === '/user/login' || to.path === '/user/register'

  // 确保页面刷新，首次加载时，能够等后端返回用户信息后再校验权限
  if (firstFetchLoginUser && !isAuthPage) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }
  const toUrl = to.fullPath

  // 首页重定向到登录页面（如果未登录）
  if (to.path === '/') {
    if (!loginUser || loginUser.userName === '未登录') {
      next('/user/login')
      return
    }
  }

  // 管理员权限校验
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('没有权限')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }

  next()
})
