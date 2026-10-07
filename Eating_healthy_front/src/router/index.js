import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/admin',
    name: 'AdminLayout',
    meta: {
      requiredAuth: true,
      adminOnly: true,
    },
    component: () => import('@/layouts/AdminMainLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/Dashboard/DashboardPage.vue'),
        meta: {
          title: '首页',
        },
      },
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('@/views/User/UserList.vue'),
        meta: {
          title: '用户列表',
        },
      },
    ],
  },
  {
    path:'/login',
    name:'Login',
    component: () => import('@/views/Login/LoginPage.vue'),
  },
  {
    path:'/403',
    name:'Forbidden',
    component: () => import('@/views/Error/Forbidden.vue'),
  },
  {
    path:'/:pathMatch(.*)*',
    name:'NotFound',
    component: () => import('@/views/Error/NotFound.vue'),
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const loggedIn = !!token
  const admin = loggedIn && isAdminUser()

  // 当前路由是否需要登录
  const requiresAuth = to.matched.some(
    record => record.meta.requiresAuth
  )

  // 当前路由是否仅管理员可访问
  const adminOnly = to.matched.some(
    record => record.meta.adminOnly
  )

  // 1. 未登录用户
  if (!loggedIn) {
    // 需要登录的页面，跳登录页
    if (requiresAuth || adminOnly) {
      return next({
        path: '/login',
        query: {
          redirect: to.fullPath
        }
      })
    }

    // 其他公开页面正常访问
    return next()
  }

  // 2. 已登录用户访问登录页或根路径
  if (to.path === '/login' || to.path === '/') {
    return next(
      admin
        ? '/admin/dashboard'
        : '/home/index'
    )
  }

  // 3. 普通用户访问管理员页面
  if (adminOnly && !admin) {
    return next('/403')
  }

  // 4. 其他情况正常放行
  return next()
})

function isAdminUser(){
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return Array.isArray(userInfo.roles) && userInfo.roles.includes('ADMIN')
}

export default router