<template>
  <div id="app" class="app-container">
    <router-view v-slot="{ Component, route }">
      <keep-alive>
        <component :is="Component" :key="route.path" />
      </keep-alive>
    </router-view>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from './stores/app'
import { addDynamicRoutes } from './router'

const router = useRouter()
const appStore = useAppStore()

onMounted(async () => {
  const result = await appStore.initApp()
  if (result && result.homePages) {
    addDynamicRoutes(result.homePages)
  }
  if (router.currentRoute.value.path === '/init') {
    router.replace('/home')
  }
})
</script>

<style lang="scss">
#app {
  width: 100%;
  height: 100%;
}

.app-container {
  width: 100%;
  height: 100%;
  background-color: var(--van-background-color-light);
  overflow: hidden;
}
</style>
