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

      <!-- 右侧：登录按钮 -->
      <div class="header-right">
        <a-button type="primary" @click="handleLogin"> 登录</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'

const router = useRouter()

// 当前选中的菜单项
const selectedKeys = ref<string[]>(['home'])

// 菜单配置
const menuItems = computed<MenuProps['items']>(() => [
  {
    key: 'home',
    label: '首页',
  },
])

// 处理菜单点击
const handleMenuClick = (menuInfo: { key: string }) => {
  const { key } = menuInfo
  selectedKeys.value = [key]

  // 根据菜单项跳转路由
  if (key === 'home') {
    router.push('/')
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
  // TODO: 实现登录逻辑
  console.log('登录按钮被点击')
}
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
}

.header-right {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .site-title {
    display: none;
  }

  .header-center {
    display: none;
  }
}
</style>
