import { useLoginUserStore } from '@/stores/loginUser'
import { message } from 'ant-design-vue'
import router from '@/router'

// 是否已获取登录用户（避免热重载时的重复调用）
let hasFetchedLoginUser = false

/**
 * 全局权限校验
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser

  // 在登录和注册页面不需要获取用户信息，避免重复请求
  const isAuthPage = to.path === '/user/login' || to.path === '/user/register'

  // 只在首次访问且未获取过用户信息时调用，避免重复请求
  if (!hasFetchedLoginUser && !isAuthPage && loginUser.userName === '未登录') {
    console.log('路由守卫：首次获取用户信息')
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    hasFetchedLoginUser = true
  }
  const toUrl = to.fullPath

  // 首页可以公开访问，不需要登录也能查看精选应用等内容
  // 只有在创建应用等操作时才需要登录

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
