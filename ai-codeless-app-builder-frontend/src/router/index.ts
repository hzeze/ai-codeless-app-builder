import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/pages/HomePage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import HomePage from '@/pages/HomePage.vue'
import AppChatPage from '@/pages/AppChatPage.vue'
import AppManagePage from '@/pages/admin/AppManagePage.vue'
import AppEditPage from '@/pages/AppEditPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterPage,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManagePage,
    },
    {
      path: '/admin/appManage',
      name: '应用管理',
      component: AppManagePage,
    },
    {
      path: '/app/chat/:appId',
      name: '应用生成对话',
      component: AppChatPage,
    },
    {
      path: '/app/edit/:appId',
      name: '编辑应用',
      component: AppEditPage,
    },
    {
      path: '/app/manage',
      name: '我的应用',
      component: () => import('@/pages/AppManagePage.vue'),
    },
  ],
})

export default router
