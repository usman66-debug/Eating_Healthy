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
      {
        path: 'user/profile',
        name: 'UserProfile',
        component: () => import('@/views/User/Profile.vue'),
        meta: {
          title: '用户信息',
        },
      },
      {
        path: 'food/category',
        name: 'FoodCategory',
        component: () => import('@/views/food/CategoryList.vue'),
        meta: {
          title: '食材分类',
        },
      },
      {
        path: 'food/list',
        name: 'FoodList',
        component: () => import('@/views/food/FoodList.vue'),
        meta: {
          title: '食材信息',
        },
      },
      {
        path: 'food/recipe',
        name: 'RecipeList',
        component: () => import('@/views/food/RecipeList.vue'),
        meta: {
          title: '食谱管理',
        },
      },
      {
        path:'recommend/daily',
        name:'DailyRecommend',
        component: () => import('@/views/Recommend/DailyRecommend.vue'),
        meta: {
          title: '每日推荐',
        },
      },
      {
        path:'recommend/record',
        name:'RecommendRecord',
        component: () => import('@/views/Recommend/RecommendRecord.vue'),
        meta: {
          title: '推荐记录',
        },
      },
      {
        path:'recommend/ai',
        name:'AIRecommend',
        component: () => import('@/views/Recommend/AIRecommend.vue'),
        meta: {
          title: 'AI推荐',
        },
      },
      {
        path:'diet-plan/list',
        name:'DietPlanList',
        component: () => import('@/views/Diet-plan/DietPlanList.vue'),
        meta: {
          title: '饮食计划列表',
        },
      },
      {
        path:'health/list',
        name:'HealthProfileList',
        component: () => import('@/views/Health/HealthProfileList.vue'),
        meta: {
          title: '健康档案',
        },
      },
      {
        path:'notice/list',
        name:'NoticeList',
        component: () => import('@/views/notice/NoticeList.vue'),
        meta: {
          title: '系统公告',
        },
      }
    ],
  },
  {
    path:'/home',
    name:'Home',
    redirect:'/home/index',
    component: () => import('@/layouts/UserLayout.vue'),
    meta: {
      title: '用户首页',
    },
    children: [
      {
        path:'index',
        name:'UserHome',
        component: () => import('@/views/home/UserHome.vue'),
        meta: {
          title: '用户首页',
        },
      },
      {
        path: 'food',
        name: 'UserFood',
        component: () => import('@/views/food/FoodList.vue'),
        meta: {
          title: '用户食材',
        },
      },
      {
        path: 'recipe',
        name: 'UserRecipe',
        component: () => import('@/views/food/RecipeList.vue'),
        meta: {
          title: '用户食谱',
        },
      },
      {
        path: 'health',
        name: 'UserHealth',
        component: () => import('@/views/Health/HealthProfile.vue'),
        meta: {
          title: '用户健康档案',
        },
      },
      {
        path:'recommend',
        name:'UserRecommend',
        component: () => import('@/views/Recommend/DailyRecommend.vue'),
        meta: {
          title: '用户推荐',
        },
      },
      {
        path:'diet-plan',
        name:'UserDietPlan',
        component: () => import('@/views/Diet-plan/MyDietPlan.vue'),
        meta: {
          title: '用户饮食计划',
        },
      }
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