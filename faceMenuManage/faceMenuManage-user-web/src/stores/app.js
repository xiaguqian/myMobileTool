import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/request'

const DEFAULT_ENTRY_SLOT = 'entry'

export const useAppStore = defineStore('app', () => {
  const currentSlotCode = ref('')
  const menuTrees = ref([])
  const pages = ref([])
  const allMenus = ref([])
  const loading = ref(false)
  
  const entryMenus = ref([])
  const entryLoaded = ref(false)

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

  const flatEntryMenus = computed(() => {
    const result = []
    function extractMenus(menus) {
      if (menus && menus.length) {
        menus.forEach(menu => {
          result.push({
            id: menu.id,
            menuCode: menu.menuCode,
            menuName: menu.menuName,
            menuUrl: menu.menuUrl,
            icon: menu.icon,
            targetSlotCode: menu.targetSlotCode
          })
          if (menu.children && menu.children.length) {
            extractMenus(menu.children)
          }
        })
      }
    }
    
    entryMenus.value.forEach(tree => {
      if (tree.menus) {
        extractMenus(tree.menus)
      }
    })
    
    return result
  })

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

  async function loadEntryMenus() {
    if (entryLoaded.value) {
      return { code: 200 }
    }
    
    try {
      const res = await api.get(`/api/slot/${DEFAULT_ENTRY_SLOT}`)
      if (res.data.code === 200) {
        entryMenus.value = res.data.data.menuTrees || []
        entryLoaded.value = true
      }
      return res.data
    } catch (error) {
      console.error('加载入口菜单失败:', error)
      return { code: 500, message: '加载入口菜单失败' }
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

  function clearAll() {
    clearSlotData()
    entryMenus.value = []
    entryLoaded.value = false
  }

  return {
    currentSlotCode,
    menuTrees,
    pages,
    allMenus,
    loading,
    entryMenus,
    entryLoaded,
    DEFAULT_ENTRY_SLOT,
    currentMenus,
    currentPages,
    flatEntryMenus,
    loadSlotData,
    loadEntryMenus,
    loadAllMenus,
    clearSlotData,
    clearAll
  }
})