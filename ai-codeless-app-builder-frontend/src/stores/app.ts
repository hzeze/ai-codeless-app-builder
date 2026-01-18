import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 当前应用信息
  const currentApp = ref<API.AppVO | null>(null)

  // 应用列表缓存
  const myAppsCache = ref<API.AppVO[]>([])
  const featuredAppsCache = ref<API.AppVO[]>([])

  // 设置当前应用
  function setCurrentApp(app: API.AppVO | null) {
    currentApp.value = app
  }

  // 更新我的应用缓存
  function updateMyAppsCache(apps: API.AppVO[]) {
    myAppsCache.value = apps
  }

  // 更新精选应用缓存
  function updateFeaturedAppsCache(apps: API.AppVO[]) {
    featuredAppsCache.value = apps
  }

  // 清空缓存
  function clearCache() {
    currentApp.value = null
    myAppsCache.value = []
    featuredAppsCache.value = []
  }

  return {
    currentApp,
    myAppsCache,
    featuredAppsCache,
    setCurrentApp,
    updateMyAppsCache,
    updateFeaturedAppsCache,
    clearCache
  }
})