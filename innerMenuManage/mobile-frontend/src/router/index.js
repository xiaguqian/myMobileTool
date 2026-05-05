import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/init',
    name: 'Init',
    component: () => import('@/views/Init.vue'),
    meta: { title: '初始化' }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/DynamicPage.vue'),
    meta: { title: '首页', slotCode: 'HOME_TOP' }
  },
  {
    path: '/member',
    name: 'Member',
    component: () => import('@/views/DynamicPage.vue'),
    meta: { title: '会员中心', slotCode: 'MEMBER_CENTER' }
  },
  {
    path: '/dynamic/:routePath(.*)',
    name: 'DynamicPage',
    component: () => import('@/views/DynamicPage.vue'),
    meta: { title: '动态页面' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export function addDynamicRoutes(routeData) {
  if (!routeData || !Array.isArray(routeData)) return
  
  routeData.forEach(item => {
    if (item.route_path) {
      const route = {
        path: item.route_path,
        name: item.page_code,
        component: () => import('@/views/DynamicPage.vue'),
        meta: { title: item.page_name, pageId: item.id, pageData: item }
      }
      router.addRoute(route)
    }
  })
}

export default router
