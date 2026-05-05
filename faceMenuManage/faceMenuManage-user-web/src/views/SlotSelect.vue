<template>
  <div class="slot-select-container">
    <el-card class="select-card">
      <template #header>
        <div class="card-header">
          <el-icon class="logo-icon"><Menu /></el-icon>
          <span>专业管理系统</span>
        </div>
      </template>
      
      <el-form :model="form" label-width="100px">
        <el-form-item label="栏位号">
          <el-input 
            v-model="form.slotCode" 
            placeholder="请输入栏位号" 
            size="large"
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <el-icon><Position /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            :loading="loading"
            @click="handleSubmit"
            class="submit-btn"
          >
            进入系统
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <p>提示：请输入管理员配置的栏位号进入系统</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { ElMessage } from 'element-plus'
import { Menu, Position } from '@element-plus/icons-vue'

const router = useRouter()
const appStore = useAppStore()

const loading = ref(false)
const form = reactive({
  slotCode: ''
})

async function handleSubmit() {
  if (!form.slotCode.trim()) {
    ElMessage.warning('请输入栏位号')
    return
  }
  
  loading.value = true
  try {
    const res = await appStore.loadSlotData(form.slotCode.trim())
    if (res.code === 200) {
      ElMessage.success('进入系统成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '栏位号不存在或无效')
    }
  } catch (error) {
    ElMessage.error('加载数据失败，请检查栏位号是否正确')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.slot-select-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.select-card {
  width: 450px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
}

.logo-icon {
  font-size: 24px;
  color: #409eff;
}

.submit-btn {
  width: 100%;
}

.tips {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  color: #909399;
  font-size: 13px;
  text-align: center;
}
</style>