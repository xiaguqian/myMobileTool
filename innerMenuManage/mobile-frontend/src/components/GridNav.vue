<template>
  <div class="grid-nav">
    <van-grid
      :column-num="columns"
      :border="false"
      :gutter="8"
    >
      <van-grid-item
        v-for="(item, index) in items"
        :key="index"
        @click="handleClick(item)"
      >
        <div class="grid-icon" :style="getIconStyle(item)">
          <van-icon v-if="item.icon" :name="item.icon" :size="24" />
          <van-image
            v-else-if="item.iconUrl"
            :src="item.iconUrl"
            :width="32"
            :height="32"
          />
          <span v-else class="icon-text">{{ getInitial(item.name) }}</span>
        </div>
        <div class="grid-text">{{ item.name }}</div>
        <van-tag v-if="item.badge" type="danger" class="grid-badge">{{ item.badge }}</van-tag>
      </van-grid-item>
    </van-grid>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

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

const columns = computed(() => {
  return props.config?.columns || props.componentData?.default_config?.columns || 4
})

const items = computed(() => {
  if (props.config?.items) {
    return props.config.items
  }
  if (props.componentData?.default_config?.items) {
    return props.componentData.default_config.items
  }
  return []
})

const getIconStyle = (item) => {
  const style = {}
  if (item.color) {
    style.color = item.color
  }
  if (item.bgColor) {
    style.backgroundColor = item.bgColor
  }
  return style
}

const getInitial = (name) => {
  if (!name) return '?'
  return name.charAt(0)
}

const handleClick = (item) => {
  if (item.url) {
    if (item.url.startsWith('http')) {
      window.location.href = item.url
    } else {
      router.push(item.url)
    }
  } else if (item.action) {
    showToast(`执行动作: ${item.action}`)
  }
}
</script>

<style lang="scss" scoped>
.grid-nav {
  background-color: $bg-color-light;
  padding: $spacing-md;
  margin-bottom: $spacing-md;
  
  .grid-icon {
    width: 48px;
    height: 48px;
    margin: 0 auto $spacing-xs;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba($primary-color, 0.1);
    border-radius: 50%;
    font-size: $font-size-xl;
    color: $primary-color;
    transition: all 0.3s;
    
    .icon-text {
      font-size: $font-size-lg;
      font-weight: bold;
    }
  }
  
  .grid-text {
    font-size: $font-size-sm;
    color: $text-color;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .grid-badge {
    position: absolute;
    top: 2px;
    right: 8px;
    font-size: $font-size-xs;
  }
}
</style>
