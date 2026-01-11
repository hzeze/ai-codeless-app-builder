<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined, LockOutlined, CheckCircleOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { userRegister } from '@/api/userController'

// 密码强度枚举
enum PasswordStrength {
  WEAK = 'weak',
  MEDIUM = 'medium',
  STRONG = 'strong'
}

const router = useRouter()

// 表单引用
const formRef = ref()

// 表单数据
const formData = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
})

// 表单验证规则
const rules = {
  userAccount: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, message: '用户名长度不能少于4位', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8位', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string) => {
        if (value !== formData.userPassword) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ]
}

// 加载状态
const loading = ref(false)

// 计算密码强度
const passwordStrength = computed(() => {
  const password = formData.userPassword
  if (!password) return null

  let score = 0

  // 长度检查
  if (password.length >= 8) score += 1
  if (password.length >= 12) score += 1

  // 包含数字
  if (/\d/.test(password)) score += 1

  // 包含小写字母
  if (/[a-z]/.test(password)) score += 1

  // 包含大写字母
  if (/[A-Z]/.test(password)) score += 1

  // 包含特殊字符
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score += 1

  if (score < 3) return PasswordStrength.WEAK
  if (score < 5) return PasswordStrength.MEDIUM
  return PasswordStrength.STRONG
})

// 密码强度文本
const strengthText = computed(() => {
  switch (passwordStrength.value) {
    case PasswordStrength.WEAK:
      return '弱'
    case PasswordStrength.MEDIUM:
      return '中等'
    case PasswordStrength.STRONG:
      return '强'
    default:
      return ''
  }
})

// 密码强度颜色
const strengthColor = computed(() => {
  switch (passwordStrength.value) {
    case PasswordStrength.WEAK:
      return '#ff4d4f'
    case PasswordStrength.MEDIUM:
      return '#faad14'
    case PasswordStrength.STRONG:
      return '#52c41a'
    default:
      return '#d9d9d9'
  }
})

// 提交表单
const handleSubmit = async () => {
  try {
    // 验证表单
    await formRef.value.validate()

    loading.value = true

    // 调用注册API
    const response = await userRegister({
      userAccount: formData.userAccount,
      userPassword: formData.userPassword,
      checkPassword: formData.checkPassword
    })

    if (response.data.code === 0) {
      // 注册成功
      message.success('注册成功！请登录')

      // 清空表单
      formRef.value.resetFields()

      // 跳转到登录页面
      router.push('/user/login')
    } else {
      // 注册失败
      message.error(response.data.message || '注册失败，请重试')
    }
  } catch (error) {
    console.error('注册失败:', error)
    message.error('注册失败，请检查输入信息')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/user/login')
}
</script>

<template>
  <div class="register-container">
    <a-row justify="center" align="middle" class="register-row">
      <a-col :xs="24" :sm="16" :md="12" :lg="8" :xl="6">
        <a-card class="register-card" title="用户注册">
          <template #title>
            <div class="register-title">
              <CheckCircleOutlined class="register-icon" />
              用户注册
            </div>
          </template>

          <a-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            @finish="handleSubmit"
            class="register-form"
          >
            <!-- 用户名输入框 -->
            <a-form-item name="userAccount">
              <a-input
                v-model:value="formData.userAccount"
                placeholder="请输入用户名（至少4位）"
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
                placeholder="请输入密码（至少8位）"
                size="large"
                :disabled="loading"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>

              <!-- 密码强度提示 -->
              <div v-if="formData.userPassword" class="password-strength">
                <span>密码强度：</span>
                <span :style="{ color: strengthColor }">{{ strengthText }}</span>
                <div class="strength-bar">
                  <div
                    class="strength-fill"
                    :class="`strength-${passwordStrength}`"
                  ></div>
                </div>
              </div>
            </a-form-item>

            <!-- 确认密码输入框 -->
            <a-form-item name="checkPassword">
              <a-input-password
                v-model:value="formData.checkPassword"
                placeholder="请确认密码"
                size="large"
                :disabled="loading"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <!-- 注册按钮 -->
            <a-form-item>
              <a-button
                type="primary"
                size="large"
                block
                :loading="loading"
                @click="handleSubmit"
              >
                {{ loading ? '注册中...' : '注册' }}
              </a-button>
            </a-form-item>

            <!-- 登录链接 -->
            <a-form-item>
              <div class="login-link">
                已有账号？
                <a @click="goToLogin">立即登录</a>
              </div>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
.register-container {
  height: 100vh;
  padding: 24px;
  overflow: hidden;
}

.register-row {
  height: calc(100vh - 48px);
}

.register-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  border: none;
}

.register-title {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.register-icon {
  margin-right: 8px;
  font-size: 24px;
}

.register-form {
  margin-top: 24px;
}

.password-strength {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.strength-bar {
  width: 100%;
  height: 4px;
  background-color: #f0f0f0;
  border-radius: 2px;
  margin-top: 4px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.strength-weak {
  width: 33%;
  background-color: #ff4d4f;
}

.strength-medium {
  width: 66%;
  background-color: #faad14;
}

.strength-strong {
  width: 100%;
  background-color: #52c41a;
}

.login-link {
  text-align: center;
  color: #666;
}

.login-link a {
  color: #1890ff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 576px) {
  .register-container {
    padding: 16px;
  }

  .register-card {
    margin: 0 8px;
  }

  .register-title {
    font-size: 18px;
  }
}
</style>
