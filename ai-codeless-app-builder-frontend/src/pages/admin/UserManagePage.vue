<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { listUserVoByPage, deleteUser } from '@/api/userController'

// 表格数据
const tableData = ref<API.UserVO[]>([])
const loading = ref(false)
const total = ref(0)

// 搜索表单
const searchForm = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
  userName: ''
})

// 表格列配置
const columns: TableColumnsType<API.UserVO> = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '用户名',
    dataIndex: 'userAccount',
    key: 'userAccount',
    width: 150,
  },
  {
    title: '用户昵称',
    dataIndex: 'userName',
    key: 'userName',
    width: 150,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    key: 'userRole',
    width: 100,
  },
  {
    title: '用户简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
    ellipsis: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    fixed: 'right',
  },
]

// 加载用户数据
const loadUserData = async () => {
  try {
    loading.value = true
    const response = await listUserVoByPage(searchForm)

    if (response.data.code === 0 && response.data.data) {
      tableData.value = response.data.data.records || []
      total.value = response.data.data.totalRow || 0
    } else {
      message.error(response.data.message || '获取用户列表失败')
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载用户数据失败:', error)
    message.error('加载用户数据失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索用户
const handleSearch = () => {
  searchForm.pageNum = 1 // 重置到第一页
  loadUserData()
}

// 重置搜索
const handleReset = () => {
  searchForm.userName = ''
  searchForm.pageNum = 1
  loadUserData()
}

// 分页变化
const handlePageChange = (page: number, pageSize: number) => {
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
  loadUserData()
}

// 删除用户
const handleDelete = async (user: API.UserVO) => {
  if (!user.id) {
    message.error('用户ID不存在')
    return
  }

  try {
    const response = await deleteUser({ id: user.id })

    if (response.data.code === 0) {
      message.success('删除用户成功')
      loadUserData() // 重新加载数据
    } else {
      message.error(response.data.message || '删除用户失败')
    }
  } catch (error) {
    console.error('删除用户失败:', error)
    message.error('删除用户失败')
  }
}

// 表格变化（排序等）
const handleTableChange = (pagination: any, filters: any, sorter: any) => {
  if (sorter.field) {
    searchForm.sortField = sorter.field
    searchForm.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc'
  }
  loadUserData()
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserData()
})
</script>

<template>
  <div class="user-manage-page">
    <!-- 搜索栏 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="用户名">
          <a-input
            v-model:value="searchForm.userName"
            placeholder="请输入用户名"
            style="width: 200px"
            @pressEnter="handleSearch"
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

    <!-- 用户表格 -->
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
        :scroll="{ x: 800 }"
        row-key="id"
        @change="handleTableChange"
      >
        <!-- 操作列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-popconfirm
              title="确定要删除这个用户吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete(record)"
            >
              <a-button type="link" danger size="small">
                删除
              </a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<style scoped>
.user-manage-page {
  padding: 24px;
  background: #fff;
  height: 100%;
  overflow-y: auto; /* 允许垂直滚动，但隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

/* 隐藏滚动条 */
.user-manage-page::-webkit-scrollbar {
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
  .user-manage-page {
    padding: 16px;
  }

  .search-section {
    padding: 16px;
  }
}
</style>
