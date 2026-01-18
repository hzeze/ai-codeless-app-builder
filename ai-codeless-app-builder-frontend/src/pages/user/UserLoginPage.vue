<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { userLogin } from '@/api/userController'
import { useLoginUserStore } from '@/stores/loginUser'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 表单引用
const formRef = ref()

// 表单数据
const formData = reactive({
  userAccount: '',
  userPassword: ''
})

// 表单验证规则
const rules = {
  userAccount: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8位', trigger: 'blur' }
  ]
}

// 加载状态
const loading = ref(false)

// 提交表单
const handleSubmit = async () => {
  try {
    // 验证表单
    await formRef.value.validate()

    loading.value = true

    // 调用登录API
    const response = await userLogin(formData)

    if (response.data.code === 0 && response.data.data) {
      // 登录成功
      message.success('登录成功！')

      // 更新登录用户信息
      loginUserStore.setLoginUser(response.data.data)

      // 根据用户角色跳转
      if (response.data.data.userRole === 'admin') {
        // 管理员跳转到用户管理页面
        router.push('/admin/userManage')
      } else {
        // 普通用户跳转到主页
        router.push('/')
      }
    } else {
      // 登录失败
      message.error(response.data.message || '登录失败，请重试')
    }
  } catch (error) {
    console.error('登录失败:', error)
    message.error('登录失败，请检查输入信息')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/user/register')
}
</script>

<template>
  <div class="login-container">
    <a-row justify="center" align="middle" class="login-row">
      <a-col :xs="24" :sm="16" :md="12" :lg="8" :xl="6">
        <a-card class="login-card" title="用户登录">
          <template #title>
            <div class="login-title">
              <UserOutlined class="login-icon" />
              用户登录
            </div>
          </template>

          <a-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            @finish="handleSubmit"
            class="login-form"
          >
            <!-- 用户名输入框 -->
            <a-form-item name="userAccount">
              <a-input
                v-model:value="formData.userAccount"
                placeholder="请输入用户名"
                size="large"
                :disabled="loading"
              >
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>

            <!-- 密码输入框 -->
            <a-form-item name="userPassword">
              <a-input-password
                v-model:value="formData.userPassword"
                placeholder="请输入密码"
                size="large"
                :disabled="loading"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <!-- 登录按钮 -->
            <a-form-item>
              <a-button
                type="primary"
                size="large"
                block
                :loading="loading"
                @click="handleSubmit"
              >
                {{ loading ? '登录中...' : '登录' }}
              </a-button>
            </a-form-item>

            <!-- 注册链接 -->
            <a-form-item>
              <div class="register-link">
                还没有账号？
                <a @click="goToRegister">立即注册</a>
              </div>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  padding: 24px;
  overflow: hidden;
}

.login-row {
  height: calc(100vh - 48px);
}

.login-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  border: none;
}

.login-title {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.login-icon {
  margin-right: 8px;
  font-size: 24px;
}

.login-form {
  margin-top: 24px;
}

.register-link {
  text-align: center;
  color: #666;
}

.register-link a {
  color: #1890ff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 576px) {
  .login-container {
    padding: 16px;
  }

  .login-card {
    margin: 0 8px;
  }

  .login-title {
    font-size: 18px;
  }
}
</style>
