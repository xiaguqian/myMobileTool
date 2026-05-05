<template>
  <div class="dynamic-page page-container">
    <van-nav-bar
      :title="pageTitle"
      left-arrow
      @click-left="handleBack"
    />
    
    <div class="page-content" v-if="pageData">
      <template v-for="(component, index) in pageComponents" :key="component.id || index">
        <component
          :is="getComponent(component.componentType)"
          :config="component.config || component.default_config"
          :component-data="component"
          class="component-wrapper"
        />
      </template>
      
      <van-empty v-if="pageComponents.length === 0" description="暂无组件" />
    </div>
    
    <div class="loading-state" v-else-if="loading">
      <van-loading type="spinner" size="24" />
      <span>加载中...</span>
    </div>
    
    <van-empty v-else description="页面不存在" />
    
    <van-tabbar v-if="showBottomNav" v-model="currentRoute" @change="onRouteChange">
      <van-tabbar-item
        v-for="(item, index) in bottomNavItems"
        :key="index"
        :to="item.path"
      >
        <template #icon="{ active }">
          <van-icon :name="item.icon || (active ? 'home' : 'home-o')" />
        </template>
        {{ item.name }}
      </van-tabbar-item>
    </van-tabbar>
    
    <div class="bottom-nav-placeholder" v-if="showBottomNav"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { getPageByRoute, getSlotData } from '@/api/menu'
import { showToast } from 'vant'
import BannerSwiper from '@/components/BannerSwiper.vue'
import GridNav from '@/components/GridNav.vue'
import ProductList from '@/components/ProductList.vue'
import UserInfoCard from '@/components/UserInfoCard.vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const pageTitle = ref('页面')
const pageData = ref(null)
const loading = ref(false)
const pageComponents = ref([])

const currentRoute = ref(0)
const bottomNavItems = computed(() => appStore.bottomNavItems)
const showBottomNav = computed(() => bottomNavItems.value && bottomNavItems.value.length > 0)

const componentMap = {
  'banner': BannerSwiper,
  'grid': GridNav,
  'list': ProductList,
  'card': UserInfoCard
}

const getComponent = (type) => {
  return componentMap[type] || 'div'
}

const loadPageData = async () => {
  loading.value = true
  
  try {
    const slotCode = route.meta.slotCode
    const pageId = route.meta.pageId
    const routePath = route.params.routePath || route.path
    
    if (slotCode) {
      pageTitle.value = route.meta.title || '页面'
      
      let slotData = appStore.getSlotDataByCode(slotCode)
      if (!slotData) {
        const res = await getSlotData(slotCode)
        if (res.success) {
          slotData = res.data
        }
      }
      
      if (slotData && slotData.pages && slotData.pages.length > 0) {
        pageData.value = slotData.pages[0]
        pageComponents.value = slotData.pages[0].components || []
        if (slotData.pages[0].page_name) {
          pageTitle.value = slotData.pages[0].page_name
        }
      }
    } else if (pageId && route.meta.pageData) {
      pageData.value = route.meta.pageData
      pageComponents.value = route.meta.pageData.components || []
      pageTitle.value = route.meta.title || route.meta.pageData.page_name
    } else {
      const res = await getPageByRoute(routePath)
      if (res.success && res.data) {
        pageData.value = res.data
        pageComponents.value = res.data.components || []
        pageTitle.value = res.data.page_name
      }
    }
  } catch (error) {
    console.error('加载页面数据失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}

const onRouteChange = (index) => {
  if (bottomNavItems.value[index]) {
    appStore.setCurrentRoute(bottomNavItems.value[index].path)
  }
}

onMounted(() => {
  loadPageData()
  
  if (bottomNavItems.value.length > 0) {
    const currentIndex = bottomNavItems.value.findIndex(item => item.path === route.path)
    if (currentIndex >= 0) {
      currentRoute.value = currentIndex
    }
  }
})

watch(() => route.path, () => {
  loadPageData()
})
</script>

<style lang="scss" scoped>
.dynamic-page {
  position: relative;
}

.page-content {
  flex: 1;
  overflow-y: auto;
}

.loading-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 15px;
  color: $text-color-2;
}

:deep(.van-tabbar) {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
</style>
