<template>
  <div class="user-card" @click="handleClick">
    <div class="user-info">
      <div class="avatar">
        <van-image
          v-if="userInfo.avatar"
          :src="userInfo.avatar"
          round
          :width="64"
          :height="64"
        />
        <van-icon v-else name="user-o" size="32" />
      </div>
      <div class="user-details">
        <div class="user-name">{{ userInfo.name || '登录/注册' }}</div>
        <div class="user-points" v-if="showPoints">
          <van-icon name="gold-coin-o" />
          <span>{{ userInfo.points || 0 }} 积分</span>
        </div>
      </div>
      <van-icon name="arrow" class="arrow" />
    </div>
    
    <div class="user-stats" v-if="showStats && userInfo.stats">
      <div class="stat-item" v-for="(stat, index) in statsList" :key="index">
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  config: {
    type: Object,
    default: () => ({})
  },
  componentData: {
    type: Object,
    default: () => ({})
  }
})

const router = useRouter()

const showPoints = computed(() => {
  return props.config?.showPoints !== false && props.componentData?.default_config?.showPoints !== false
})

const showStats = computed(() => {
  return props.config?.showStats !== false
})

const userInfo = reactive({
  avatar: '',
  name: '',
  points: 0,
  stats: null
})

const statsList = computed(() => {
  if (props.config?.stats) {
    return props.config.stats
  }
  if (userInfo.stats) {
    return userInfo.stats
  }
  return [
    { value: 0, label: '订单' },
    { value: 0, label: '优惠券' },
    { value: 0, label: '收藏' }
  ]
})

const handleClick = () => {
  if (props.config?.url) {
    if (props.config.url.startsWith('http')) {
      window.location.href = props.config.url
    } else {
      router.push(props.config.url)
    }
  } else {
    router.push('/member')
  }
}
</script>

<style lang="scss" scoped>
.user-card {
  background: linear-gradient(135deg, $primary-color, #69b1ff);
  padding: $spacing-xl;
  color: #fff;
  margin-bottom: $spacing-md;
  
  .user-info {
    display: flex;
    align-items: center;
    position: relative;
    
    .avatar {
      width: 64px;
      height: 64px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: $spacing-md;
    }
    
    .user-details {
      flex: 1;
      
      .user-name {
        font-size: $font-size-lg;
        font-weight: bold;
        margin-bottom: $spacing-xs;
      }
      
      .user-points {
        font-size: $font-size-sm;
        opacity: 0.9;
        display: flex;
        align-items: center;
        gap: $spacing-xs;
      }
    }
    
    .arrow {
      opacity: 0.7;
      font-size: $font-size-lg;
    }
  }
  
  .user-stats {
    display: flex;
    margin-top: $spacing-xl;
    padding-top: $spacing-lg;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    
    .stat-item {
      flex: 1;
      text-align: center;
      
      .stat-value {
        font-size: $font-size-xl;
        font-weight: bold;
        margin-bottom: $spacing-xs;
      }
      
      .stat-label {
        font-size: $font-size-sm;
        opacity: 0.8;
      }
    }
  }
}
</style>
