<template>
  <div class="global-header">
    <div class="header-content">
      <!-- 左侧：logo 和网站标题 -->
      <div class="header-left">
        <img src="@/assets/logo.svg" alt="logo" class="logo" @click="handleLogoClick" />
        <span class="site-title">AI零代码应用生成平台</span>
      </div>

      <!-- 中间：菜单 -->
      <div class="header-center">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
          class="header-menu"
        />
      </div>

      <!-- 右侧：用户信息或登录按钮 -->
      <div class="header-right">
        <!-- 已登录状态 -->
        <template v-if="isLoggedIn">
          <a-dropdown :trigger="['click']" placement="bottomRight">
            <a-button type="text" class="user-info">
              <a-avatar
                :src="loginUserStore.loginUser.userAvatar"
                :icon="UserOutlined"
                size="small"
              />
              <span class="user-name">{{ loginUserStore.loginUser.userName }}</span>
            </a-button>
            <template #overlay>
              <a-menu :items="userMenuItems" @click="handleUserMenuClick" />
            </template>
          </a-dropdown>
        </template>

        <!-- 未登录状态 -->
        <template v-else>
          <a-button type="primary" @click="handleLogin">
            登录
          </a-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { MenuProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogout } from '@/api/userController'

const router = useRouter()
const route = useRoute()
const loginUserStore = useLoginUserStore()

  // 当前选中的菜单项 - 根据路由动态设置
const selectedKeys = computed(() => {
  const path = route.path
  if (path === '/') {
    return ['home']
  } else if (path === '/admin/userManage') {
    return ['userManage']
  } else if (path === '/admin/appManage') {
    return ['appManage']
  }
  return ['home'] // 默认选中首页
})

// 菜单配置
const menuItems = computed<MenuProps['items']>(() => {
  const items = [
    {
      key: 'home',
      label: '首页',
    },
  ]

  // 如果是管理员，添加管理菜单
  if (loginUserStore.loginUser.userRole === 'admin') {
    items.push({
      key: 'userManage',
      label: '用户管理',
    })
    items.push({
      key: 'appManage',
      label: '应用管理',
    })
  }

  return items
})

// 计算是否已登录
const isLoggedIn = computed(() => {
  return loginUserStore.loginUser.userName !== '未登录'
})

// 用户菜单项
const userMenuItems = computed<MenuProps['items']>(() => [
  {
    key: 'profile',
    label: '个人资料',
    disabled: true, // 暂时禁用
  },
  {
    key: 'logout',
    label: '退出登录',
  },
])

// 处理菜单点击
const handleMenuClick = (menuInfo: { key: string }) => {
  const { key } = menuInfo

  // 根据菜单项跳转路由
  if (key === 'home') {
    router.push('/')
  } else if (key === 'userManage') {
    router.push('/admin/userManage')
  } else if (key === 'appManage') {
    router.push('/admin/appManage')
  } else if (key === 'about') {
    router.push('/about')
  }
}

// 处理logo点击
const handleLogoClick = () => {
  router.push('/')
}

// 处理登录按钮点击
const handleLogin = () => {
  router.push('/user/login')
}

// 处理用户菜单点击
const handleUserMenuClick = async (menuInfo: { key: string }) => {
  const { key } = menuInfo

  if (key === 'logout') {
    await handleLogout()
  } else if (key === 'profile') {
    // TODO: 跳转到个人资料页面
    message.info('个人资料功能开发中')
  }
}

// 处理登出
const handleLogout = async () => {
  try {
    const response = await userLogout()
    if (response.data.code === 0) {
      message.success('已退出登录')

      // 重置登录用户信息
      loginUserStore.setLoginUser({
        userName: '未登录',
      })

      // 跳转到登录页面
      router.push('/user/login')
    } else {
      message.error(response.data.message || '退出登录失败')
    }
  } catch (error) {
    console.error('登出失败:', error)
    message.error('退出登录失败')
  }
}

// 组件挂载时获取登录用户信息
onMounted(async () => {
  // 在认证页面不获取用户信息，避免重复请求
  const isAuthPage = route.path === '/user/login' || route.path === '/user/register'

  // 如果当前显示为未登录状态，且不是认证页面，尝试获取用户信息
  if (loginUserStore.loginUser.userName === '未登录' && !isAuthPage) {
    await loginUserStore.fetchLoginUser()
  }
})
</script>

<style scoped>
.global-header {
  width: 100%;
  height: 64px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
}

/* 移动端调整布局顺序 */
@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    padding: 0 12px;
  }

  .header-left {
    order: 1;
    flex: 0 0 auto;
    margin-right: 8px;
  }

  .header-center {
    order: 2;
    flex: 1;
    min-width: 0;
    margin: 8px 0;
  }

  .header-right {
    order: 3;
    flex: 0 0 auto;
    margin-left: 8px;
  }

  .header-menu {
    justify-content: flex-start;
    overflow-x: auto; /* 允许水平滚动 */
  }

  .site-title {
    font-size: 12px; /* 进一步缩小标题 */
  }
}

.header-left {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.site-title {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.header-menu {
  border-bottom: none;
  background: transparent;
  flex: 1;
  min-width: 0; /* 允许缩小 */
}

/* 确保菜单项不会被压缩得太小 */
.header-menu .ant-menu-item {
  min-width: auto;
  padding: 0 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  height: auto;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background-color: rgba(24, 144, 255, 0.1);
}

.user-info .ant-avatar {
  margin-right: 8px;
}

.user-name {
  font-size: 14px;
  color: #666;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .site-title {
    font-size: 14px;
  }

  .header-center {
    /* 在移动端显示菜单，但可能需要调整样式 */
    display: block;
    order: 2; /* 调整顺序，让菜单在中间 */
  }

  .header-menu {
    /* 移动端菜单样式调整 */
    font-size: 14px;
  }

  .user-name {
    display: none;
  }

  .user-info .ant-avatar {
    margin-right: 0;
  }
}

/* 更大的屏幕也保持菜单可见 */
@media (max-width: 1024px) {
  .header-center {
    display: block;
  }
}
</style>

