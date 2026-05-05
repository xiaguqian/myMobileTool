import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/page/:pageCode',
    name: 'DynamicPage',
    component: () => import('@/views/DynamicPage.vue'),
    meta: { title: '动态页面' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 专业管理系统` : '专业管理系统'
  next()
})

export default router