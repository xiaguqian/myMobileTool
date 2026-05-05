import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/request'

export const useAppStore = defineStore('app', () => {
  const currentSlotCode = ref('')
  const menuTrees = ref([])
  const pages = ref([])
  const allMenus = ref([])
  const loading = ref(false)

  const currentMenus = computed(() => {
    const menus = []
    menuTrees.value.forEach(tree => {
      if (tree.menus) {
        menus.push(...tree.menus)
      }
    })
    return menus
  })

  const currentPages = computed(() => pages.value)

  async function loadSlotData(slotCode) {
    loading.value = true
    try {
      const res = await api.get(`/api/slot/${slotCode}`)
      if (res.data.code === 200) {
        currentSlotCode.value = slotCode
        menuTrees.value = res.data.data.menuTrees || []
        pages.value = res.data.data.pages || []
      }
      return res.data
    } finally {
      loading.value = false
    }
  }

  async function loadAllMenus() {
    try {
      const res = await api.get('/api/slot/menus/all')
      if (res.data.code === 200) {
        allMenus.value = res.data.data || []
      }
      return res.data
    } catch (error) {
      console.error('加载全量菜单失败:', error)
    }
  }

  function clearSlotData() {
    currentSlotCode.value = ''
    menuTrees.value = []
    pages.value = []
  }

  return {
    currentSlotCode,
    menuTrees,
    pages,
    allMenus,
    loading,
    currentMenus,
    currentPages,
    loadSlotData,
    loadAllMenus,
    clearSlotData
  }
})