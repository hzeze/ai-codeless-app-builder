<template>
  <a-layout class="basic-layout">
    <!-- 头部导航栏 - 登录和注册页面不显示 -->
    <a-layout-header v-if="!isAuthPage" class="layout-header">
      <GlobalHeader />
    </a-layout-header>

    <!-- 内容区域 -->
    <a-layout-content class="layout-content" :style="{ height: contentHeight }">
      <RouterView />
    </a-layout-content>

    <!-- 底部版权信息 - 登录和注册页面不显示 -->
    <a-layout-footer v-if="!isAuthPage" class="layout-footer">
      <GlobalFooter />
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

const route = useRoute()

// 判断是否是认证页面（登录/注册）
const isAuthPage = computed(() => {
  return route.path === '/user/login' || route.path === '/user/register'
})

// 内容区域高度 - 对于非认证页面，设置最小高度但允许滚动
const contentHeight = computed(() => {
  return isAuthPage.value ? '100vh' : 'auto'
})
</script>

<style scoped>
.basic-layout {
  height: 100vh;
  overflow: hidden;
}

.layout-header {
  background: #fff;
  padding: 0;
  height: 64px;
  line-height: 64px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.layout-content {
  flex: 1;
  padding: 0; /* 移除内边距，由页面自己控制 */
  background: #fff;
  overflow: auto;
}

.layout-footer {
  background: #fff;
  padding: 0;
  text-align: center;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-content {
    padding: 16px;
    min-height: calc(100vh - 128px);
  }
}
</style>

