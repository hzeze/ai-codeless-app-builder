<script setup lang="ts">
  import { ref, reactive, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { message } from 'ant-design-vue'
  import { useLoginUserStore } from '@/stores/loginUser'
  import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
  import { getAppDeployUrl } from '@/config/env.ts'
  import dayjs from 'dayjs'

  const router = useRouter()
  const loginUserStore = useLoginUserStore()

  // 用户提示词
  const userPrompt = ref('')
  const creating = ref(false)

  // 我的应用数据
  const myApps = ref<API.AppVO[]>([])
  const myAppsPage = reactive({
    current: 1,
    pageSize: 6,
    total: 0,
  })

  // 精选应用数据
  const featuredApps = ref<API.AppVO[]>([])
  const featuredAppsPage = reactive({
    current: 1,
    pageSize: 6,
    total: 0,
  })

  // 设置提示词
  const setPrompt = (prompt: string) => {
    userPrompt.value = prompt
  }

  // 优化提示词
  const optimizePrompt = () => {
    if (!userPrompt.value.trim()) {
      message.warning('请先输入提示词')
      return
    }
    // 这里可以调用优化接口，暂时简单处理
    message.info('优化功能开发中...')
  }

  // 创建应用
  const createApp = async () => {
    if (!userPrompt.value.trim()) {
      message.warning('请输入应用描述')
      return
    }

    if (!loginUserStore.loginUser.id) {
      message.warning('请先登录')
      await router.push('/user/login')
      return
    }

    creating.value = true
    try {
      const res = await addApp({
        initPrompt: userPrompt.value.trim(),
      })

      if (res.data.code === 0 && res.data.data) {
        message.success('应用创建成功')
        // 跳转到对话页面
        await router.push(`/app/chat/${res.data.data}`)
      } else {
        message.error('创建失败：' + res.data.message)
      }
    } catch (error) {
      console.error('创建应用失败：', error)
      message.error('创建失败，请重试')
    } finally {
      creating.value = false
    }
  }

  // 加载我的应用
  const loadMyApps = async () => {
    if (!loginUserStore.loginUser.id) {
      return
    }

    try {
      const res = await listMyAppVoByPage({
        pageNum: myAppsPage.current,
        pageSize: myAppsPage.pageSize,
        sortField: 'createTime',
        sortOrder: 'desc',
      })

      if (res.data.code === 0 && res.data.data) {
        myApps.value = res.data.data.records || []
        myAppsPage.total = res.data.data.totalRow || 0
      }
    } catch (error) {
      console.error('加载我的应用失败：', error)
    }
  }

  // 加载精选应用
  const loadFeaturedApps = async () => {
    try {
      const res = await listGoodAppVoByPage({
        pageNum: featuredAppsPage.current,
        pageSize: featuredAppsPage.pageSize,
        sortField: 'createTime',
        sortOrder: 'desc',
      })

      if (res.data.code === 0 && res.data.data) {
        featuredApps.value = res.data.data.records || []
        featuredAppsPage.total = res.data.data.totalRow || 0
      }
    } catch (error) {
      console.error('加载精选应用失败：', error)
    }
  }


  // 查看应用
  const viewApp = (appId: number | undefined) => {
    if (appId) {
      router.push(`/app/chat/${appId}?view=1`)
    }
  }

  // 查看作品
  const viewWork = (deployKey: string | undefined) => {
    if (deployKey) {
      window.open(getAppDeployUrl(deployKey), '_blank')
    }
  }

  // 格式化时间
  const formatTime = (time: string | undefined) => {
    if (!time) return ''
    return dayjs(time).format('YYYY-MM-DD')
  }

  // 页面加载时获取数据
  onMounted(() => {
    loadMyApps()
    loadFeaturedApps()
  })
  </script>

  <template>
    <div id="homePage">
      <!-- 网站标题和描述 -->
      <div class="hero-section">
        <h1 class="hero-title">一句话 呈所想</h1>
        <p class="hero-description">与 AI 对话轻松创建应用和网站</p>
      </div>

      <!-- 用户提示词输入框 -->
      <div class="input-section">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="使用 AI 创建一个高效的小工具，帮我计算......"
          :rows="4"
          :maxlength="1000"
          class="prompt-input"
        />
        <a-button type="primary" shape="circle" size="large" class="submit-button" @click="createApp" :loading="creating">
          <template #icon>
            <span>↑</span>
          </template>
        </a-button>
      </div>

      <!-- 快捷按钮 -->
      <div class="quick-actions">
        <a-button type="default" @click="setPrompt('波普风电商页面')">波普风电商页面</a-button>
        <a-button type="default" @click="setPrompt('企业网站')">企业网站</a-button>
        <a-button type="default" @click="setPrompt('电商运营后台')">电商运营后台</a-button>
        <a-button type="default" @click="setPrompt('暗黑话题社区')">暗黑话题社区</a-button>
      </div>

      <!-- 我的作品 -->
      <div class="section">
        <h2 class="section-title">我的作品</h2>
        <div class="app-grid">
          <div v-for="app in myApps" :key="app.id" class="app-card">
            <div class="app-preview">
              <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
              <div v-else class="app-placeholder">
                <span>🤖</span>
              </div>
              <div class="app-overlay">
                <div class="overlay-buttons">
                  <a-button type="primary" @click="viewApp(app.id)">查看对话</a-button>
                  <a-button v-if="app.deployKey" type="default" @click="viewWork(app.deployKey)">查看作品</a-button>
                </div>
              </div>
            </div>
            <div class="app-info">
              <h3 class="app-title">{{ app.appName || '未命名应用' }}</h3>
              <p class="app-time">创建于 {{ formatTime(app.createTime) }}</p>
            </div>
          </div>
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps"
          />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <h2 class="section-title">精选案例</h2>
        <div class="featured-grid">
          <div v-for="app in featuredApps" :key="app.id" class="featured-card">
            <div class="featured-preview">
              <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
              <div v-else class="featured-placeholder">
                <span>🤖</span>
              </div>
              <div class="featured-overlay">
                <div class="overlay-buttons">
                  <a-button type="primary" @click="viewApp(app.id)">查看对话</a-button>
                  <a-button v-if="app.deployKey" type="default" @click="viewWork(app.deployKey)">查看作品</a-button>
                </div>
              </div>
            </div>
            <div class="featured-info">
              <div class="user-avatar">
                <img v-if="app.userVO?.userAvatar" :src="app.userVO.userAvatar" :alt="app.userVO?.userName" />
                <div v-else class="avatar-placeholder">
                  <span>{{ (app.userVO?.userName || 'NoCode 官方').charAt(0) }}</span>
                </div>
              </div>
              <div class="app-details">
                <h3 class="app-title">{{ app.appName || '未命名应用' }}</h3>
                <p class="user-name">{{ app.userVO?.userName || 'NoCode 官方' }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps"
          />
        </div>
      </div>
    </div>
  </template>

  <style scoped>
  #homePage {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    min-height: calc(100vh - 128px);
  }

  /* 英雄区域 */
  .hero-section {
    text-align: center;
    padding: 60px 0 40px;
    margin-bottom: 40px;
    color: #1a1a1a;
  }

  .hero-title {
    font-size: 48px;
    font-weight: 700;
    margin: 0 0 16px;
    line-height: 1.2;
  }

  .hero-description {
    font-size: 18px;
    margin: 0;
    color: #666;
  }

  /* 输入区域 */
  .input-section {
    position: relative;
    margin-bottom: 24px;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
  }

  .prompt-input {
    border-radius: 12px;
    border: 2px solid #e8e8e8;
    font-size: 16px;
    padding: 16px 60px 16px 16px;
    transition: all 0.3s;
    resize: none;
  }

  .prompt-input:focus {
    border-color: #1890ff;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  }

  .submit-button {
    position: absolute;
    bottom: 12px;
    right: 12px;
  }

  /* 快捷按钮 */
  .quick-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
    margin-bottom: 60px;
    flex-wrap: wrap;
  }

  .quick-actions .ant-btn {
    border-radius: 20px;
    padding: 4px 16px;
    height: auto;
  }

  /* 区域标题 */
  .section {
    margin-bottom: 60px;
  }

  .section-title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 32px;
    color: #1a1a1a;
  }

  /* 我的作品网格 */
  .app-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
  }

  .app-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition:
      transform 0.3s,
      box-shadow 0.3s;
    cursor: pointer;
  }

  .app-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  .app-preview {
    height: 180px;
    background: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
  }

  .app-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .app-placeholder {
    font-size: 48px;
    color: #d9d9d9;
  }

  .app-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
  }

  .app-card:hover .app-overlay {
    opacity: 1;
  }

  .overlay-buttons {
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }

  .app-info {
    padding: 16px;
  }

  .app-title {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 8px;
    color: #1a1a1a;
  }

  .app-time {
    font-size: 14px;
    color: #666;
    margin: 0;
  }

  /* 精选案例网格 */
  .featured-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
  }

  .featured-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition:
      transform 0.3s,
      box-shadow 0.3s;
  }

  .featured-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  .featured-preview {
    height: 200px;
    background: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
  }

  .featured-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .featured-placeholder {
    font-size: 48px;
    color: #d9d9d9;
  }

  .featured-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
  }

  .featured-overlay .overlay-buttons {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .featured-card:hover .featured-overlay {
    opacity: 1;
  }

  .featured-info {
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
  }

  .user-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-placeholder {
    width: 100%;
    height: 100%;
    background: #1890ff;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    font-size: 16px;
  }

  .app-details {
    flex: 1;
    min-width: 0;
  }

  .app-title {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 4px;
    color: #1a1a1a;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .user-name {
    font-size: 14px;
    color: #666;
    margin: 0;
  }

  /* 分页 */
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 32px;
  }

  /* 响应式设计 */
  @media (max-width: 768px) {
    .hero-title {
      font-size: 32px;
    }

    .hero-description {
      font-size: 16px;
    }

    .app-grid,
    .featured-grid {
      grid-template-columns: 1fr;
    }

    .quick-actions {
      justify-content: center;
    }
  }
  </style>
