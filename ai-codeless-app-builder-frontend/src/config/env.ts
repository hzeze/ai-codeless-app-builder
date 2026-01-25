/**
 * 环境变量配置
 */
import { CodeGenTypeEnum } from '@/utils/codeGenTypes.ts'

// API基础URL
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'

// 应用部署域名
export const APP_DEPLOY_DOMAIN = import.meta.env.VITE_APP_DEPLOY_DOMAIN || 'http://localhost'

// 应用生成预览域名
export const APP_PREVIEW_DOMAIN = `${API_BASE_URL}/static`

//获取部署应用的完整URL
export const getAppDeployUrl = (deployKey: string) => {
  return `${APP_DEPLOY_DOMAIN}/${deployKey}`
}

//获取应用预览URL
export const getAppPreviewUrl = (codeGenType: string, appId: string) => {
  const baseUrl = `${APP_PREVIEW_DOMAIN}/${codeGenType}_${appId}/`
  // 如果是 Vue 项目，浏览地址需要添加 dist 后缀
  if (codeGenType === CodeGenTypeEnum.VUE_PROJECT) {
    return `${baseUrl}dist/index.html`
  }
  return baseUrl
}
