<template>
  <div class="init-page">
    <div class="init-content">
      <div class="logo">
        <van-icon name="apps-o" size="80" />
      </div>
      <div class="title">千人千面菜单系统</div>
      <div class="subtitle">正在加载配置数据...</div>
      <div class="loading">
        <van-loading type="spinner" color="#1989fa" />
        <span>{{ loadingText }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { showToast } from 'vant'
import router from '@/router'

const loadingText = ref('正在初始化应用...')
const appStore = useAppStore()

onMounted(async () => {
  try {
    loadingText.value = '正在获取配置数据...'
    await appStore.initApp()
  } catch (error) {
    console.error('初始化失败:', error)
    showToast('加载配置失败，请重试')
    setTimeout(() => {
      router.replace('/home')
    }, 1500)
  }
})
</script>

<style lang="scss" scoped>
.init-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .init-content {
    text-align: center;
    color: #fff;
  }
  
  .logo {
    margin-bottom: 30px;
    opacity: 0.9;
  }
  
  .title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 15px;
  }
  
  .subtitle {
    font-size: 14px;
    opacity: 0.8;
    margin-bottom: 40px;
  }
  
  .loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    
    span {
      font-size: 14px;
      opacity: 0.8;
    }
  }
}
</style>
