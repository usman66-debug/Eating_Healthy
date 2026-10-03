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
    path:'/:pathMatch(.*)*',
    name:'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router