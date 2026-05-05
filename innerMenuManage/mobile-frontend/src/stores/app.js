import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getSlotData, getMenusWithPermissions, getAllMenus } from '@/api/menu'

export const useAppStore = defineStore('app', () => {
  const isInitialized = ref(false)
  const appConfig = ref(null)
  const slotData = ref({})
  const allMenus = ref([])
  const userMenus = ref([])
  const currentRoute = ref('/home')
  const bottomNavItems = ref([])

  const hasPermission = computed(() => (permissionCode) => {
    return userMenus.value.some(m => m.permission_code === permissionCode)
  })

  const loadData = async () => {
    try {
      const [slotHome, slotMember, menus] = await Promise.all([
        getSlotData('HOME_TOP'),
        getSlotData('MEMBER_CENTER'),
        getAllMenus()
      ])

      if (slotHome.success) {
        slotData.value['HOME_TOP'] = slotHome.data
        if (slotHome.data.menuTrees && slotHome.data.menuTrees.length > 0) {
          buildBottomNav(slotHome.data.menuTrees[0])
        }
      }

      if (slotMember.success) {
        slotData.value['MEMBER_CENTER'] = slotMember.data
      }

      if (menus.success) {
        allMenus.value = menus.data
        userMenus.value = menus.data
      }

      return {
        homePages: slotHome.success ? slotHome.data.pages : null
      }
    } catch (error) {
      console.error('加载数据失败:', error)
      return { homePages: null }
    }
  }

  const buildBottomNav = (menuTree) => {
    if (!menuTree || !menuTree.structure) return

    const findMenuNodes = (nodes) => {
      const menus = []
      nodes.forEach(node => {
        if (node.nodeType === 2 && node.menu) {
          menus.push({
            name: node.menu.menuName,
            path: node.menu.routeUrl,
            icon: node.nodeIcon || node.menu.menuIcon
          })
        }
        if (node.children && node.children.length > 0) {
          menus.push(...findMenuNodes(node.children))
        }
      })
      return menus
    }

    bottomNavItems.value = findMenuNodes(menuTree.structure)
  }

  const getSlotDataByCode = (slotCode) => {
    return slotData.value[slotCode]
  }

  const loadSlotData = async (slotCode) => {
    try {
      const res = await getSlotData(slotCode)
      if (res.success) {
        slotData.value[slotCode] = res.data
        return res.data
      }
    } catch (error) {
      console.error('加载栏位数据失败:', error)
    }
    return null
  }

  const setCurrentRoute = (path) => {
    currentRoute.value = path
  }

  const initApp = async () => {
    const result = await loadData()
    isInitialized.value = true
    return result
  }

  return {
    isInitialized,
    appConfig,
    slotData,
    allMenus,
    userMenus,
    currentRoute,
    bottomNavItems,
    hasPermission,
    initApp,
    loadData,
    getSlotDataByCode,
    loadSlotData,
    setCurrentRoute
  }
})
