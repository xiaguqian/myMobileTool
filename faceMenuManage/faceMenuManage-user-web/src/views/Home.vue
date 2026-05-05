<template>
  <el-container class="home-container">
    <el-header class="home-header">
      <div class="header-left">
        <el-icon class="logo-icon"><Menu /></el-icon>
        <span class="system-name">专业管理系统</span>
      </div>
      <div class="header-right">
        <span class="slot-info">当前栏位: {{ appStore.currentSlotCode }}</span>
        <el-button type="primary" link @click="handleChangeSlot">
          <el-icon><SwitchButton /></el-icon>
          切换栏位
        </el-button>
      </div>
    </el-header>
    
    <el-container>
      <el-aside width="220px" class="home-aside">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :router="true"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
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
                    >
                      <el-icon><Document /></el-icon>
                      <span>{{ child.menuName }}</span>
                    </el-menu-item>
                  </el-sub-menu>
                </template>
                <el-menu-item v-else :index="menu.menuUrl || `#${menu.id}`">
                  <el-icon><Document /></el-icon>
                  <span>{{ menu.menuName }}</span>
                </el-menu-item>
              </template>
            </el-sub-menu>
          </template>
          
          <el-menu-item-group v-if="appStore.pages.length > 0">
            <template #title>页面列表</template>
            <el-menu-item 
              v-for="page in appStore.pages" 
              :key="page.id" 
              :index="`/page/${page.pageCode}`"
            >
              <el-icon><Document /></el-icon>
              <span>{{ page.pageName }}</span>
            </el-menu-item>
          </el-menu-item-group>
        </el-menu>
      </el-aside>
      
      <el-main class="home-main">
        <el-card v-if="appStore.loading">
          <el-skeleton :rows="8" animated />
        </el-card>
        
        <template v-else>
          <el-card v-if="appStore.currentSlotCode">
            <template #header>
              <div class="card-header">
                <span>欢迎使用专业管理系统</span>
              </div>
            </template>
            
            <el-descriptions :column="2" border>
              <el-descriptions-item label="当前栏位号">
                <el-tag type="primary">{{ appStore.currentSlotCode }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="菜单树数量">
                {{ appStore.menuTrees.length }} 个
              </el-descriptions-item>
              <el-descriptions-item label="关联页面数">
                {{ appStore.pages.length }} 个
              </el-descriptions-item>
              <el-descriptions-item label="系统状态">
                <el-tag type="success">正常运行</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            
            <el-divider>快速导航</el-divider>
            
            <el-row :gutter="20">
              <el-col :span="6" v-for="page in appStore.pages.slice(0, 4)" :key="page.id">
                <el-card 
                  class="quick-card" 
                  shadow="hover"
                  @click="$router.push(`/page/${page.pageCode}`)"
                >
                  <div class="quick-card-content">
                    <el-icon class="card-icon"><Document /></el-icon>
                    <span class="card-name">{{ page.pageName }}</span>
                    <span class="card-code">{{ page.pageCode }}</span>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-card>
          
          <el-empty v-else description="请选择栏位进入系统" />
        </template>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { 
  Menu, HomeFilled, Folder, Document, SwitchButton, Position 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

function handleChangeSlot() {
  appStore.clearSlotData()
  router.push('/slot-select')
}
</script>

<style scoped>
.home-container {
  height: 100vh;
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 0 20px;
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

.home-aside {
  background-color: #304156;
}

.home-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}

.quick-card {
  cursor: pointer;
  transition: all 0.3s;
}

.quick-card:hover {
  transform: translateY(-5px);
}

.quick-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.card-icon {
  font-size: 36px;
  color: #409eff;
  margin-bottom: 10px;
}

.card-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.card-code {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>