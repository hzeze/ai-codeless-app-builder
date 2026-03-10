<template>
  <div class="user-profile-page">
    <div class="profile-container">
      <a-card class="profile-card">
        <a-form
          :model="baseForm"
          layout="vertical"
          @submit.prevent="handleBaseSubmit"
        >
          <div class="avatar-section">
            <a-upload
              :show-upload-list="false"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <a-avatar :size="100" :src="baseForm.userAvatar" class="avatar">
                <template v-if="!baseForm.userAvatar">
                  {{ baseForm.userName?.[0] || '用' }}
                </template>
              </a-avatar>
            </a-upload>
            <div class="avatar-hint">点击头像上传</div>
          </div>

          <a-form-item>
            <template #label>
              <span>
                <UserOutlined style="margin-right: 4px" />
                昵称
              </span>
            </template>
            <a-input
              v-model:value="baseForm.userName"
              placeholder="请输入昵称"
              :max-length="20"
              size="large"
            />
          </a-form-item>

          <a-form-item>
            <template #label>
              <span>
                <FileTextOutlined style="margin-right: 4px" />
                简介
              </span>
            </template>
            <a-textarea
              v-model:value="baseForm.userProfile"
              placeholder="请输入简介"
              :rows="3"
              :max-length="100"
            />
          </a-form-item>

          <a-form-item class="submit-item">
            <a-button type="primary" :loading="savingBase" @click="handleBaseSubmit" size="large">
              保存
            </a-button>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { UploadProps } from 'ant-design-vue'
import { UserOutlined, FileTextOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { updateMyUser, getLoginUser } from '@/api/userController'

const loginUserStore = useLoginUserStore()

const baseForm = reactive<{
  userName: string
  userAvatar: string
  userProfile: string
}>({
  userName: '',
  userAvatar: '',
  userProfile: '',
})

const savingBase = ref(false)

const fillBaseFormFromStore = () => {
  const user = loginUserStore.loginUser
  if (user) {
    baseForm.userName = user.userName || ''
    baseForm.userAvatar = user.userAvatar || ''
    baseForm.userProfile = user.userProfile || ''
  }
}

onMounted(async () => {
  // 首先从本地存储加载用户信息
  loginUserStore.loadFromLocalStorage()
  
  // 只有在本地存储中没有用户信息时，才从后端获取
  if (loginUserStore.loginUser.userName === '未登录') {
    try {
      const res = await getLoginUser()
      if (res.data.code === 0 && res.data.data) {
        loginUserStore.setLoginUser(res.data.data)
      }
    } catch (e) {
      // 忽略错误，在 header 中已有全局获取逻辑
    }
  }
  fillBaseFormFromStore()
})

// 头像上传前处理
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  
  // 读取文件并转换为 base64
  const reader = new FileReader()
  reader.onload = (e) => {
    const result = e.target?.result as string
    if (result) {
      baseForm.userAvatar = result
    }
  }
  reader.readAsDataURL(file)
  
  return false // 阻止自动上传
}

const handleBaseSubmit = async () => {
  if (!baseForm.userName) {
    message.warning('请输入昵称')
    return
  }
  savingBase.value = true
  try {
    const res = await updateMyUser({
      userName: baseForm.userName,
      userAvatar: baseForm.userAvatar,
      userProfile: baseForm.userProfile,
    })
    if (res.data.code === 0) {
    message.success('保存成功')
    // 直接更新 store 中的用户数据，保持表单数据不变
    loginUserStore.setLoginUser({
      ...loginUserStore.loginUser,
      userName: baseForm.userName,
      userAvatar: baseForm.userAvatar,
      userProfile: baseForm.userProfile,
    })
  } else {
    message.error(res.data.message || '保存失败')
  }
  } catch (e) {
    message.error('保存失败，请稍后重试')
  } finally {
    savingBase.value = false
  }
}
</script>

<style scoped>
.user-profile-page {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.profile-container {
  width: 100%;
  max-width: 480px;
}

.profile-card {
  border-radius: 8px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
}

.avatar {
  cursor: pointer;
  transition: opacity 0.3s;
}

.avatar:hover {
  opacity: 0.8;
}

.avatar-hint {
  margin-top: 12px;
  font-size: 12px;
  color: #999;
}

.submit-item {
  text-align: center;
}

.submit-item :deep(.ant-btn) {
  min-width: 120px;
}
</style>

