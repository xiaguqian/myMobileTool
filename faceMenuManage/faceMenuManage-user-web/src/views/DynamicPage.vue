<template>
  <el-container class="page-container">
    <el-header class="page-header">
      <div class="header-left">
        <el-icon class="logo-icon"><Menu /></el-icon>
        <span class="system-name">专业管理系统</span>
      </div>
      <div class="header-right">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ currentPage?.pageName || '动态页面' }}</el-breadcrumb-item>
        </el-breadcrumb>
        <el-button type="primary" link @click="handleChangeSlot">
          <el-icon><SwitchButton /></el-icon>
          切换栏位
        </el-button>
      </div>
    </el-header>
    
    <el-container>
      <el-aside width="220px" class="page-aside">
        <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          :router="true"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>返回首页</span>
          </el-menu-item>
          
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
      
      <el-main class="page-main">
        <el-skeleton v-if="loading" :rows="10" animated />
        
        <template v-else>
          <template v-if="currentPage">
            <el-card>
              <template #header>
                <div class="page-title">
                  <el-icon><Document /></el-icon>
                  <span>{{ currentPage.pageName }}</span>
                  <el-tag type="info" size="small">{{ currentPage.pageCode }}</el-tag>
                </div>
              </template>
              
              <div v-if="currentPage.components && currentPage.components.length > 0">
                <component
                  v-for="(comp, index) in currentPage.components"
                  :key="comp.id || index"
                  :is="getComponentType(comp.componentType)"
                  :config="comp.config"
                  :data="comp"
                />
              </div>
              
              <el-empty v-else description="该页面暂无配置组件" />
            </el-card>
          </template>
          
          <el-empty v-else description="页面不存在或未配置" />
        </template>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, markRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { Menu, HomeFilled, Document, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const loading = ref(false)
const activeMenu = computed(() => route.path)

const currentPage = computed(() => {
  const pageCode = route.params.pageCode
  return appStore.pages.find(p => p.pageCode === pageCode)
})

const componentTypes = {
  Header: markRaw({
    name: 'HeaderComponent',
    template: `
      <el-card class="header-component" shadow="never">
        <div class="header-content">
          <h2>{{ config?.title || 'Header组件' }}</h2>
          <p v-if="config?.description">{{ config.description }}</p>
        </div>
      </el-card>
    `,
    props: ['config', 'data']
  }),
  Footer: markRaw({
    name: 'FooterComponent',
    template: `
      <el-card class="footer-component" shadow="never">
        <div class="footer-content">
          <p>{{ config?.copyright || '© 2024 专业管理系统' }}</p>
        </div>
      </el-card>
    `,
    props: ['config', 'data']
  }),
  Banner: markRaw({
    name: 'BannerComponent',
    template: `
      <el-carousel height="200px">
        <el-carousel-item v-for="(item, index) in (config?.items || [])" :key="index">
          <div class="banner-item" :style="{ backgroundColor: item.color || '#409eff' }">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
          </div>
        </el-carousel-item>
        <el-carousel-item v-if="!config?.items || config.items.length === 0">
          <div class="banner-item default-banner">
            <h3>Banner组件</h3>
            <p>请配置轮播内容</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    `,
    props: ['config', 'data']
  }),
  List: markRaw({
    name: 'ListComponent',
    template: `
      <el-card class="list-component">
        <template #header>
          <span>{{ config?.title || '列表组件' }}</span>
        </template>
        <el-table :data="(config?.items || [])" style="width: 100%">
          <el-table-column 
            v-for="(col, index) in (config?.columns || [])" 
            :key="index"
            :prop="col.prop"
            :label="col.label"
            :width="col.width"
          />
          <el-table-column v-if="!config?.columns || config.columns.length === 0" label="数据">
            <template #default="scope">
              <pre>{{ JSON.stringify(scope.row, null, 2) }}</pre>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!config?.items || config.items.length === 0" description="暂无数据" />
      </el-card>
    `,
    props: ['config', 'data']
  }),
  Form: markRaw({
    name: 'FormComponent',
    template: `
      <el-card class="form-component">
        <template #header>
          <span>{{ config?.title || '表单组件' }}</span>
        </template>
        <el-form :model="formData" label-width="100px">
          <el-form-item 
            v-for="(field, index) in (config?.fields || [])" 
            :key="index"
            :label="field.label"
            :prop="field.prop"
          >
            <el-input 
              v-if="field.type === 'input'"
              v-model="formData[field.prop]"
              :placeholder="field.placeholder || '请输入' + field.label"
            />
            <el-select 
              v-else-if="field.type === 'select'"
              v-model="formData[field.prop]"
              :placeholder="field.placeholder || '请选择' + field.label"
              style="width: 100%"
            >
              <el-option 
                v-for="opt in (field.options || [])" 
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
            <el-date-picker
              v-else-if="field.type === 'date'"
              v-model="formData[field.prop]"
              type="date"
              :placeholder="field.placeholder || '请选择日期'"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
            <el-input
              v-else
              v-model="formData[field.prop]"
              type="textarea"
              :rows="3"
              :placeholder="field.placeholder || '请输入' + field.label"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">提交</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-empty v-if="!config?.fields || config.fields.length === 0" description="请配置表单字段" />
      </el-card>
    `,
    props: ['config', 'data'],
    setup(props) {
      const formData = ref({})
      
      if (props.config?.fields) {
        props.config.fields.forEach(field => {
          formData.value[field.prop] = field.defaultValue || ''
        })
      }
      
      function handleSubmit() {
        console.log('表单提交:', formData.value)
      }
      
      function handleReset() {
        if (props.config?.fields) {
          props.config.fields.forEach(field => {
            formData.value[field.prop] = field.defaultValue || ''
          })
        }
      }
      
      return {
        formData,
        handleSubmit,
        handleReset
      }
    }
  }),
  Default: markRaw({
    name: 'DefaultComponent',
    template: `
      <el-card class="default-component">
        <template #header>
          <div class="component-header">
            <el-icon><Document /></el-icon>
            <span>{{ data?.componentName || '组件' }}</span>
            <el-tag type="info" size="small">{{ data?.componentType || '未知类型' }}</el-tag>
          </div>
        </template>
        <div class="component-body">
          <p><strong>组件编码:</strong> {{ data?.componentCode }}</p>
          <p><strong>组件类型:</strong> {{ data?.componentType }}</p>
          <el-divider>配置内容</el-divider>
          <pre class="config-preview">{{ JSON.stringify(config, null, 2) }}</pre>
        </div>
      </el-card>
    `,
    props: ['config', 'data']
  })
}

function getComponentType(type) {
  return componentTypes[type] || componentTypes.Default
}

function handleChangeSlot() {
  appStore.clearSlotData()
  router.push('/slot-select')
}
</script>

<style scoped>
.page-container {
  height: 100vh;
}

.page-header {
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

.page-aside {
  background-color: #304156;
}

.page-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: bold;
}

.component-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.banner-item {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #fff;
}

.banner-item h3 {
  font-size: 24px;
  margin-bottom: 10px;
}

.default-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.config-preview {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  max-height: 300px;
  overflow-y: auto;
}
</style>