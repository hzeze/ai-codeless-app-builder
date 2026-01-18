<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import type { CardProps } from 'ant-design-vue'
import { listMyAppVoByPage, deleteApp } from '@/api/appController'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 应用列表
const apps = ref<API.AppVO[]>([])
const loading = ref(false)
const total = ref(0)

// 查询参数
const queryParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 20,
  sortField: 'createTime',
  sortOrder: 'desc'
})

// 判断是否已登录
const isLoggedIn = computed(() => {
  return loginUserStore.loginUser.userName !== '未登录'
})

// 加载我的应用列表
const loadMyApps = async () => {
  if (!isLoggedIn.value) return

  try {
    loading.value = true
    const response = await listMyAppVoByPage(queryParams)

    if (response.data.code === 0 && response.data.data) {
      apps.value = response.data.data.records || []
      total.value = response.data.data.totalRow || 0
    } else {
      apps.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载我的应用失败:', error)
    apps.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 删除应用
const handleDelete = async (app: API.AppVO) => {
  if (!app.id) {
    message.error('应用ID不存在')
    return
  }

  try {
    const response = await deleteApp({ id: app.id })

    if (response.data.code === 0) {
      message.success('删除应用成功')
      loadMyApps() // 重新加载数据
    } else {
      message.error(response.data.message || '删除应用失败')
    }
  } catch (error) {
    console.error('删除应用失败:', error)
    message.error('删除应用失败')
  }
}

// 编辑应用
const handleEdit = (app: API.AppVO) => {
  if (!app.id) return
  router.push(`/app/edit/${app.id}`)
}

// 查看应用详情（进入对话页面）
const handleView = (app: API.AppVO) => {
  if (!app.id) return
  router.push(`/app/chat/${app.id}`)
}

// 分页变化处理
const handlePageChange = (page: number, pageSize: number) => {
  queryParams.pageNum = page
  queryParams.pageSize = pageSize
  loadMyApps()
}

// 应用卡片配置
const appCardProps = computed<CardProps>(() => ({
  size: 'small',
  hoverable: true,
  style: { width: '100%', cursor: 'pointer' }
}))

// 组件挂载时加载数据
onMounted(() => {
  if (isLoggedIn.value) {
    loadMyApps()
  }
})
</script>

<template>
  <div class="app-manage-page">
    <div class="page-header">
      <h1>我的应用</h1>
      <p>管理您的应用，编辑信息或删除不需要的应用</p>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!isLoggedIn" class="not-logged-in">
      <a-empty description="请先登录查看您的应用">
        <template #image>
          <UserOutlined style="font-size: 64px; color: #d9d9d9;" />
        </template>
        <a-button type="primary" @click="router.push('/user/login')">
          去登录
        </a-button>
      </a-empty>
    </div>

    <!-- 应用列表 -->
    <div v-else>
      <div v-if="loading" class="loading-container">
        <a-spin size="large" />
      </div>

      <div v-else-if="apps.length === 0" class="empty-container">
        <a-empty description="暂无应用，前往首页创建应用吧！">
          <a-button type="primary" @click="router.push('/')">
            前往首页
          </a-button>
        </a-empty>
      </div>

      <div v-else class="apps-grid">
        <a-card
          v-for="app in apps"
          :key="app.id"
          v-bind="appCardProps"
          @click="handleView(app)"
        >
          <template #cover>
            <div class="app-cover" :style="{ backgroundImage: `url(${app.cover || '/default-app-cover.png'})` }" />
          </template>
          <a-card-meta :title="app.appName || '未命名应用'" :description="app.initPrompt">
            <template #description>
              <div class="app-meta">
                <span class="app-time">{{ new Date(app.createTime || '').toLocaleDateString() }}</span>
                <div class="app-actions">
                  <a-button type="link" size="small" @click.stop="handleView(app)">
                    查看
                  </a-button>
                  <a-button type="link" size="small" @click.stop="handleEdit(app)">
                    编辑
                  </a-button>
                  <a-popconfirm
                    title="确定要删除这个应用吗？"
                    ok-text="确定"
                    cancel-text="取消"
                    @confirm="handleDelete(app)"
                  >
                    <a-button type="link" danger size="small" @click.stop>
                      删除
                    </a-button>
                  </a-popconfirm>
                </div>
              </div>
            </template>
          </a-card-meta>
        </a-card>
      </div>

      <!-- 分页 -->
        <div v-if="total > (queryParams.pageSize || 20)" class="pagination-container">
        <a-pagination
          :current="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :total="total"
          show-size-changer
          show-quick-jumper
            :show-total="(total: number, range: [number, number]) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`"
          @change="handlePageChange"
          @showSizeChange="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { UserOutlined } from '@ant-design/icons-vue'
</script>

<style scoped>
.app-manage-page {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.page-header p {
  font-size: 16px;
  color: #6b7280;
}

.not-logged-in {
  background: white;
  border-radius: 8px;
  padding: 60px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.app-cover {
  height: 160px;
  background-size: cover;
  background-position: center;
  background-color: #f0f0f0;
  border-radius: 8px 8px 0 0;
}

.app-meta {
  margin-top: 8px;
}

.app-time {
  font-size: 12px;
  color: #6b7280;
  display: block;
  margin-bottom: 8px;
}

.app-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.loading-container,
.empty-container {
  background: white;
  border-radius: 8px;
  padding: 60px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-manage-page {
    padding: 16px;
  }

  .apps-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .app-actions {
    flex-direction: column;
    align-items: flex-end;
  }

  .not-logged-in,
  .loading-container,
  .empty-container {
    padding: 40px 20px;
  }
}
</style>