<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { listAppVoByPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { useRouter } from 'vue-router'

// 表格数据
const tableData = ref<API.App[]>([])
const loading = ref(false)
const total = ref(0)

// 搜索表单
const searchForm = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
  appName: '',
  userId: undefined
})

// 表格列配置
const columns: TableColumnsType<API.App> = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    key: 'appName',
    width: 200,
    ellipsis: true,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    key: 'cover',
    width: 100,
    ellipsis: true,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    key: 'initPrompt',
    ellipsis: true,
  },
  {
    title: '代码生成类型',
    dataIndex: 'codeGenType',
    key: 'codeGenType',
    width: 120,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    key: 'priority',
    width: 80,
    sorter: true,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    key: 'userId',
    width: 80,
  },
  {
    title: '部署密钥',
    dataIndex: 'deployKey',
    key: 'deployKey',
    width: 120,
    ellipsis: true,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    key: 'deployedTime',
    width: 180,
    sorter: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    sorter: true,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    key: 'updateTime',
    width: 180,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 加载应用数据
const loadAppData = async () => {
  try {
    loading.value = true
    const response = await listAppVoByPageByAdmin(searchForm)

    if (response.data.code === 0 && response.data.data) {
      tableData.value = response.data.data.records || []
      total.value = response.data.data.totalRow || 0
    } else {
      message.error(response.data.message || '获取应用列表失败')
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载应用数据失败:', error)
    message.error('加载应用数据失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索应用
const handleSearch = () => {
  searchForm.pageNum = 1 // 重置到第一页
  loadAppData()
}

// 重置搜索
const handleReset = () => {
  searchForm.appName = ''
  searchForm.userId = undefined
  searchForm.pageNum = 1
  loadAppData()
}

// 分页变化
const handlePageChange = (page: number, pageSize: number) => {
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
  loadAppData()
}

// 删除应用
const handleDelete = async (app: API.App) => {
  if (!app.id) {
    message.error('应用ID不存在')
    return
  }

  try {
    const response = await deleteAppByAdmin({ id: app.id })

    if (response.data.code === 0) {
      message.success('删除应用成功')
      loadAppData() // 重新加载数据
    } else {
      message.error(response.data.message || '删除应用失败')
    }
  } catch (error) {
    console.error('删除应用失败:', error)
    message.error('删除应用失败')
  }
}

// 设置为精选应用
const handleSetFeatured = async (app: API.App) => {
  if (!app.id) {
    message.error('应用ID不存在')
    return
  }

  try {
    const response = await updateAppByAdmin({
      id: app.id,
      priority: 99 // 设置为精选应用的优先级
    })

    if (response.data.code === 0) {
      message.success('设置精选应用成功')
      loadAppData() // 重新加载数据
    } else {
      message.error(response.data.message || '设置精选应用失败')
    }
  } catch (error) {
    console.error('设置精选应用失败:', error)
    message.error('设置精选应用失败')
  }
}

// 编辑应用
const router = useRouter()
const handleEdit = (app: API.App) => {
  if (!app.id) {
    message.error('应用ID不存在')
    return
  }

  router.push(`/app/edit/${app.id}`)
}

// 表格变化（排序等）
const handleTableChange = (pagination: any, filters: any, sorter: any) => {
  if (sorter.field) {
    searchForm.sortField = sorter.field
    searchForm.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc'
  }
  loadAppData()
}

// 组件挂载时加载数据
onMounted(() => {
  loadAppData()
})
</script>

<template>
  <div class="app-manage-page">
    <!-- 搜索栏 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="应用名称">
          <a-input
            v-model:value="searchForm.appName"
            placeholder="请输入应用名称"
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </a-form-item>

        <a-form-item label="用户ID">
          <a-input-number
            v-model:value="searchForm.userId"
            placeholder="请输入用户ID"
            style="width: 120px"
            :min="1"
          />
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading">
            搜索
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 应用表格 -->
    <div class="table-section">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="{
          current: searchForm.pageNum,
          pageSize: searchForm.pageSize,
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total: number, range: [number, number]) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`,
          onChange: handlePageChange,
          onShowSizeChange: handlePageChange,
        }"
        :scroll="{ x: 1500 }"
        row-key="id"
        @change="handleTableChange"
      >
        <!-- 操作列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-button
                type="link"
                size="small"
                :style="{ color: record.priority === 99 ? '#52c41a' : '#1890ff' }"
                @click="handleSetFeatured(record)"
              >
                {{ record.priority === 99 ? '已精选' : '精选' }}
              </a-button>
              <a-popconfirm
                title="确定要删除这个应用吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" danger size="small">
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<style scoped>
.app-manage-page {
  padding: 24px;
  background: #fff;
  height: 100%;
  overflow-y: auto; /* 允许垂直滚动，但隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

/* 隐藏滚动条 */
.app-manage-page::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

.search-section {
  margin-bottom: 24px;
  padding: 24px;
  background: #fafafa;
  border-radius: 8px;
}

.table-section {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-manage-page {
    padding: 16px;
  }

  .search-section {
    padding: 16px;
  }
}
</style>
