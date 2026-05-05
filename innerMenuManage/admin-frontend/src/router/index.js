import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/slots'
  },
  {
    path: '/slots',
    name: 'Slots',
    component: () => import('@/views/slots/index.vue')
  },
  {
    path: '/pages',
    name: 'Pages',
    component: () => import('@/views/pages/index.vue')
  },
  {
    path: '/pages/:id/config',
    name: 'PageConfig',
    component: () => import('@/views/pages/PageConfig.vue')
  },
  {
    path: '/components',
    name: 'Components',
    component: () => import('@/views/components/index.vue')
  },
  {
    path: '/menus',
    name: 'Menus',
    component: () => import('@/views/menus/index.vue')
  },
  {
    path: '/menu-trees',
    name: 'MenuTrees',
    component: () => import('@/views/menuTrees/index.vue')
  },
  {
    path: '/menu-trees/:id/config',
    name: 'MenuTreeConfig',
    component: () => import('@/views/menuTrees/TreeConfig.vue')
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('@/views/publish/index.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
