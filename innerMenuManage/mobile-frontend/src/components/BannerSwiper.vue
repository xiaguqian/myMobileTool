<template>
  <div class="banner-swiper">
    <van-swiper
      :autoplay="config?.autoPlay ? config.interval || 3000 : 0"
      :show-indicators="showIndicators"
      :duration="500"
      loop
    >
      <van-swiper-item v-for="(item, index) in images" :key="index">
        <div class="banner-item" @click="handleClick(item)">
          <van-image
            :src="item.url || item"
            :fit="'cover'"
            lazy-load
          />
          <div v-if="item.title" class="banner-title">{{ item.title }}</div>
        </div>
      </van-swiper-item>
    </van-swiper>
  </div>
</template>

<script setup>
import { computed } from 'vue'

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

const emit = defineEmits(['click'])

const images = computed(() => {
  if (props.config?.images) {
    return props.config.images
  }
  if (props.componentData?.default_config?.images) {
    return props.componentData.default_config.images
  }
  return []
})

const showIndicators = computed(() => {
  return props.config?.showIndicators !== false
})

const handleClick = (item) => {
  emit('click', item)
  if (item.url && typeof item.url === 'string' && item.url.startsWith('http')) {
    window.location.href = item.url
  }
}
</script>

<style lang="scss" scoped>
.banner-swiper {
  width: 100%;
  overflow: hidden;
  
  :deep(.van-swipe) {
    width: 100%;
  }
  
  .banner-item {
    position: relative;
    width: 100%;
    height: 180px;
    overflow: hidden;
    
    :deep(.van-image) {
      width: 100%;
      height: 100%;
    }
    
    .banner-title {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding: 10px 15px;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
      color: #fff;
      font-size: 14px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
