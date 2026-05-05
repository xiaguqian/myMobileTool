<template>
  <el-container class="home-container">
    <el-header class="home-header">
      <div class="header-left">
        <el-icon class="logo-icon"><Menu /></el-icon>
        <span class="system-name">专业管理系统</span>
      </div>
      <div class="header-right">
        <span v-if="appStore.currentSlotCode" class="slot-info">
          当前栏位: 
          <el-tag type="primary" size="small">{{ appStore.currentSlotCode }}</el-tag>
        </span>
        <el-button v-if="appStore.currentSlotCode" type="primary" link @click="handleBackToHome">
          <el-icon><SwitchButton /></el-icon>
          返回首页
        </el-button>
      </div>
    </el-header>
    
    <el-main class="home-main">
      <el-skeleton v-if="loading" :rows="8" animated />
      
      <template v-else>
        <template v-if="!appStore.currentSlotCode">
          <div class="welcome-section">
            <el-card class="welcome-card">
              <template #header>
                <div class="card-header">
                  <el-icon><HomeFilled /></el-icon>
                  <span>欢迎使用专业管理系统</span>
                </div>
              </template>
              
              <div v-if="hasEntryMenus" class="entry-menus-section">
                <h4 class="section-title">请选择功能入口</h4>
                <el-row :gutter="20">
                  <template v-for="menuTree in appStore.entryMenus" :key="menuTree.id">
                    <el-col :span="8" v-if="menuTree.menus && menuTree.menus.length > 0">
                      <el-card class="menu-tree-card" shadow="hover">
                        <template #header>
                          <div class="menu-tree-header">
                            <el-icon><Folder /></el-icon>
                            <span>{{ menuTree.treeName }}</span>
                          </div>
                        </template>
                        <div class="menu-list">
                          <template v-for="menu in menuTree.menus" :key="menu.id">
                            <div 
                              class="menu-item" 
                              @click="handleMenuClick(menu)"
                              :class="{ 'has-target': menu.targetSlotCode }"
                            >
                              <el-icon><Document /></el-icon>
                              <span class="menu-name">{{ menu.menuName }}</span>
                              <el-tag v-if="menu.targetSlotCode" type="success" size="small">
                                {{ menu.targetSlotCode }}
                              </el-tag>
                            </div>
                            <div 
                              v-for="child in menu.children" 
                              :key="child.id" 
                              class="menu-item child-menu"
                              @click="handleMenuClick(child)"
                              :class="{ 'has-target': child.targetSlotCode }"
                            >
                              <el-icon><Document /></el-icon>
                              <span class="menu-name">{{ child.menuName }}</span>
                              <el-tag v-if="child.targetSlotCode" type="success" size="small">
                                {{ child.targetSlotCode }}
                              </el-tag>
                            </div>
                          </template>
                        </div>
                      </el-card>
                    </el-col>
                  </template>
                </el-row>
              </div>
              
              <div v-else class="empty-entry">
                <el-empty description="暂无入口菜单，请先在管理端配置">
                  <template #description>
                    <div class="empty-tips">
                      <p>暂无入口菜单</p>
                      <p class="text-muted">请在管理端创建栏位号 "entry" 并配置菜单树</p>
                    </div>
                  </template>
                </el-empty>
              </div>
            </el-card>
          </div>
        </template>
        
        <template v-else>
          <div class="content-section">
            <el-card>
              <template #header>
                <div class="card-header">
                  <el-icon><FolderOpened /></el-icon>
                  <span>栏位: {{ appStore.currentSlotCode }}</span>
                </div>
              </template>
              
              <el-descriptions :column="2" border class="mb-4">
                <el-descriptions-item label="栏位号">
                  <el-tag type="primary">{{ appStore.currentSlotCode }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="菜单树数量">
                  {{ appStore.menuTrees.length }} 个
                </el-descriptions-item>
                <el-descriptions-item label="关联页面数">
                  {{ appStore.pages.length }} 个
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag type="success">已加载</el-tag>
                </el-descriptions-item>
              </el-descriptions>
              
              <el-divider>可用页面</el-divider>
              
              <div v-if="appStore.pages.length > 0">
                <el-row :gutter="20">
                  <el-col :span="6" v-for="page in appStore.pages" :key="page.id">
                    <el-card 
                      class="page-card" 
                      shadow="hover"
                      @click="handlePageClick(page)"
                    >
                      <div class="page-card-content">
                        <el-icon class="page-icon"><Document /></el-icon>
                        <span class="page-name">{{ page.pageName }}</span>
                        <span class="page-code">{{ page.pageCode }}</span>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
              </div>
              
              <el-empty v-else description="该栏位暂无关联页面" />
            </el-card>
            
            <el-card v-if="appStore.menuTrees.length > 0" class="mt-4">
              <template #header>
                <div class="card-header">
                  <el-icon><Menu /></el-icon>
                  <span>菜单列表</span>
                </div>
              </template>
              
              <el-menu
                mode="horizontal"
                :default-active="activeMenu"
                background-color="#fff"
                text-color="#303133"
                active-text-color="#409eff"
              >
                <template v-for="menuTree in appStore.menuTrees" :key="menuTree.id">
                  <el-sub-menu v-if="menuTree.menus && menuTree.menus.length > 0" :index="String(menuTree.id)">
                    <template #title>
                      <el-icon><Folder /></el-icon>
                      <span>{{ menuTree.treeName }}</span>
                    </template>
                    <template v-for="menu in menuTree.menus" :key="menu.id">
                      <template v-if="menu.children && menu.children.length > 0">
                        <el-sub-menu :index="String(menu.id)">
                          <template #title>
                            <el-icon><Menu /></el-icon>
                            <span>{{ menu.menuName }}</span>
                          </template>
                          <el-menu-item 
                            v-for="child in menu.children" 
                            :key="child.id" 
                            :index="child.menuUrl || `#${child.id}`"
                            @click="handleMenuNavigate(child)"
                          >
                            <el-icon><Document /></el-icon>
                            <span>{{ child.menuName }}</span>
                          </el-menu-item>
                        </el-sub-menu>
                      </template>
                      <el-menu-item 
                        v-else 
                        :index="menu.menuUrl || `#${menu.id}`"
                        @click="handleMenuNavigate(menu)"
                      >
                        <el-icon><Document /></el-icon>
                        <span>{{ menu.menuName }}</span>
                      </el-menu-item>
                    </template>
                  </el-sub-menu>
                </template>
              </el-menu>
            </el-card>
          </div>
        </template>
      </template>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElLoading } from 'element-plus'
import { 
  Menu, HomeFilled, Folder, Document, FolderOpened, SwitchButton 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const loading = ref(false)
const activeMenu = computed(() => route.path)

const hasEntryMenus = computed(() => {
  return appStore.entryMenus.some(tree => tree.menus && tree.menus.length > 0)
})

async function loadEntryMenus() {
  loading.value = true
  try {
    const res = await appStore.loadEntryMenus()
    if (res.code !== 200) {
      console.log('入口菜单加载失败或不存在，可能是未配置 entry 栏位')
    }
  } catch (error) {
    console.error('加载入口菜单失败:', error)
  } finally {
    loading.value = false
  }
}

async function handleMenuClick(menu) {
  if (menu.targetSlotCode && menu.targetSlotCode.trim()) {
    loading.value = true
    const loadingInstance = ElLoading.service({
      lock: true,
      text: '正在加载栏位配置...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      const res = await appStore.loadSlotData(menu.targetSlotCode.trim())
      if (res.code === 200) {
        ElMessage.success(`已加载栏位: ${menu.targetSlotCode}`)
        
        if (appStore.pages.length === 1) {
          router.push(`/page/${appStore.pages[0].pageCode}`)
        }
      } else {
        ElMessage.error(res.message || '栏位号不存在或无效')
      }
    } catch (error) {
      ElMessage.error('加载栏位配置失败')
    } finally {
      loading.value = false
      loadingInstance.close()
    }
  } else if (menu.menuUrl && menu.menuUrl.trim()) {
    router.push(menu.menuUrl)
  } else {
    ElMessage.info('该菜单未配置目标栏位号或URL')
  }
}

function handlePageClick(page) {
  router.push(`/page/${page.pageCode}`)
}

function handleMenuNavigate(menu) {
  if (menu.menuUrl && menu.menuUrl.trim()) {
    router.push(menu.menuUrl)
  }
}

function handleBackToHome() {
  appStore.clearSlotData()
}

onMounted(() => {
  if (!appStore.entryLoaded) {
    loadEntryMenus()
  }
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  height: 60px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 24px;
  color: #409eff;
}

.system-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.slot-info {
  color: #909399;
  font-size: 14px;
}

.home-main {
  background-color: #f5f7fa;
  padding: 20px;
  flex: 1;
}

.welcome-section {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: bold;
}

.section-title {
  font-size: 18px;
  margin-bottom: 20px;
  color: #303133;
}

.menu-tree-card {
  margin-bottom: 20px;
}

.menu-tree-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}

.menu-list {
  max-height: 300px;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 5px;
}

.menu-item:hover {
  background-color: #ecf5ff;
}

.menu-item.has-target:hover {
  background-color: #f0f9eb;
}

.child-menu {
  padding-left: 35px;
  background-color: #fafafa;
  margin-left: 15px;
}

.menu-name {
  flex: 1;
}

.empty-entry {
  padding: 40px;
}

.empty-tips {
  text-align: center;
}

.empty-tips p {
  margin: 5px 0;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}

.content-section {
  max-width: 1400px;
  margin: 0 auto;
}

.mb-4 {
  margin-bottom: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.page-card {
  cursor: pointer;
  transition: all 0.3s;
}

.page-card:hover {
  transform: translateY(-5px);
}

.page-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.page-icon {
  font-size: 36px;
  color: #409eff;
  margin-bottom: 10px;
}

.page-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.page-code {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>