<template>
  <a-layout class="basic-layout">
    <!-- 头部导航栏 -->
    <a-layout-header class="layout-header">
      <GlobalHeader />
    </a-layout-header>

    <!-- 内容区域 -->
    <a-layout-content class="layout-content">
      <RouterView />
    </a-layout-content>

    <!-- 底部版权信息 -->
    <a-layout-footer class="layout-footer">
      <GlobalFooter />
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import GlobalHeader from '../components/GlobalHeader.vue'
import GlobalFooter from '../components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
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
  padding: 24px;
  background: #fff;
  min-height: calc(100vh - 128px);
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
