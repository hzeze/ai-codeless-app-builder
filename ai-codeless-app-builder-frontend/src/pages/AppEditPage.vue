<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppVoById, updateApp, getAppVoByIdByAdmin, updateAppByAdmin } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 获取应用ID
const appId = route.params.appId as string

// 表单数据
const formData = reactive<API.AppUpdateRequest | API.AppAdminUpdateRequest>({
  id: appId,
  appName: '',
  cover: '',
  priority: undefined
})

// 应用信息
const appInfo = ref<API.AppVO | null>(null)
const loading = ref(false)
const saving = ref(false)

// 判断是否为管理员
const isAdmin = computed(() => {
  return loginUserStore.loginUser.userRole === 'admin'
})

// 判断是否为应用所有者
const isOwner = computed(() => {
  return appInfo.value?.userId === loginUserStore.loginUser.id
})

// 表单验证规则
const rules = computed(() => ({
  appName: [
    { required: true, message: '请输入应用名称', trigger: 'blur' },
    { max: 50, message: '应用名称不能超过50个字符', trigger: 'blur' }
  ],
  cover: isAdmin.value ? [
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' }
  ] : [],
  priority: isAdmin.value ? [
    { type: 'number', min: 0, max: 100, message: '优先级必须在0-100之间', trigger: 'blur' }
  ] : []
}))

// 获取应用信息
const loadAppInfo = async () => {
  if (!appId) return

  try {
    loading.value = true
    let response

    // 根据用户角色调用不同的接口
    if (isAdmin.value) {
      response = await getAppVoByIdByAdmin({ id: appId })
    } else {
      response = await getAppVoById({ id: appId })
    }

    if (response.data.code === 0 && response.data.data) {
      appInfo.value = response.data.data

      // 如果不是管理员且不是应用所有者，无权访问
      if (!isAdmin.value && !isOwner.value) {
        message.error('无权访问此应用')
        router.push('/')
        return
      }

      // 初始化表单数据
      formData.appName = appInfo.value?.appName || ''
      if (isAdmin.value) {
        (formData as API.AppAdminUpdateRequest).cover = appInfo.value?.cover || ''
        ;(formData as API.AppAdminUpdateRequest).priority = appInfo.value?.priority || 0
      }
    } else {
      message.error(response.data.message || '获取应用信息失败')
      router.push('/')
    }
  } catch (error) {
    console.error('获取应用信息失败:', error)
    message.error('获取应用信息失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 保存应用信息
const handleSave = async () => {
  try {
    saving.value = true

    let response
    if (isAdmin.value) {
      response = await updateAppByAdmin(formData as API.AppAdminUpdateRequest)
    } else {
      response = await updateApp(formData as API.AppUpdateRequest)
    }

    if (response.data.code === 0) {
      message.success('保存成功')
      router.back()
    } else {
      message.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  router.back()
}

// 组件挂载时加载数据
onMounted(() => {
  loadAppInfo()
})
</script>

<template>
  <div class="app-edit-page">
    <div class="edit-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>编辑应用信息</h1>
        <p v-if="appInfo" class="app-info">
          应用ID: {{ appInfo.id }} |
          创建时间: {{ new Date(appInfo.createTime || '').toLocaleString() }}
        </p>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <a-spin size="large" />
        <p>加载中...</p>
      </div>

      <!-- 编辑表单 -->
      <div v-else-if="appInfo" class="edit-form">
        <a-form
          :model="formData"
          :rules="rules"
          layout="vertical"
          @finish="handleSave"
        >
          <!-- 应用名称 -->
          <a-form-item label="应用名称" name="appName">
            <a-input
              v-model:value="formData.appName"
              placeholder="请输入应用名称"
              :maxlength="50"
              show-count
            />
          </a-form-item>

          <!-- 初始提示词（只读） -->
          <a-form-item label="初始提示词">
            <a-textarea
              v-model:value="appInfo.initPrompt"
              :rows="4"
              readonly
              placeholder="无"
            />
          </a-form-item>

          <!-- 管理员专用字段 -->
          <template v-if="isAdmin">
            <!-- 封面URL -->
            <a-form-item label="封面URL" name="cover">
              <a-input
                v-model:value="(formData as API.AppAdminUpdateRequest).cover"
                placeholder="请输入封面图片URL"
              />
            </a-form-item>

            <!-- 优先级 -->
            <a-form-item label="优先级" name="priority">
              <a-input-number
                v-model:value="(formData as API.AppAdminUpdateRequest).priority"
                :min="0"
                :max="100"
                placeholder="请输入优先级 (0-100)"
                style="width: 100%"
              />
              <div class="priority-hint">
                <small>优先级99表示精选应用，将在精选应用列表中显示</small>
              </div>
            </a-form-item>
          </template>

          <!-- 操作按钮 -->
          <a-form-item>
            <a-space>
              <a-button type="primary" html-type="submit" :loading="saving">
                保存
              </a-button>
              <a-button @click="handleCancel">
                取消
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>

      <!-- 权限不足提示 -->
      <div v-else class="no-permission">
        <a-empty description="无权访问此应用或应用不存在" />
        <a-button type="primary" @click="router.push('/')">
          返回首页
        </a-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-edit-page {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

.edit-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.page-header {
  padding: 24px;
  border-bottom: 1px solid #e8e8e8;
  background: #fafafa;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}

.app-info {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: #6b7280;
}

.loading-container {
  padding: 60px;
  text-align: center;
}

.loading-container p {
  margin-top: 16px;
  color: #666;
}

.edit-form {
  padding: 24px;
}

.priority-hint {
  margin-top: 4px;
  color: #666;
}

.no-permission {
  padding: 60px;
  text-align: center;
}

.no-permission .ant-btn {
  margin-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-edit-page {
    padding: 16px;
  }

  .edit-container {
    margin: 0;
  }

  .page-header {
    padding: 16px;
  }

  .edit-form {
    padding: 16px;
  }
}
</style>