import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/admin',
    name: 'AdminLayout',
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
    ],
  },
  {
    path:'/login',
    name:'Login',
    component: () => import('@/views/Login/LoginPage.vue'),
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

export default router