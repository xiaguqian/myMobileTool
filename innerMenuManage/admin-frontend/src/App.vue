<template>
  <div id="app">
    <el-container class="app-container">
      <el-aside width="220px" class="app-aside">
        <div class="logo">
          <i class="el-icon-menu"></i>
          <span>菜单管理系统</span>
        </div>
        <el-menu
          :default-active="$route.path"
          router
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/slots">
            <i class="el-icon-s-grid"></i>
            <span>栏位管理</span>
          </el-menu-item>
          <el-menu-item index="/pages">
            <i class="el-icon-document"></i>
            <span>页面管理</span>
          </el-menu-item>
          <el-menu-item index="/components">
            <i class="el-icon-s-platform"></i>
            <span>组件管理</span>
          </el-menu-item>
          <el-menu-item index="/menus">
            <i class="el-icon-s-flag"></i>
            <span>菜单管理</span>
          </el-menu-item>
          <el-menu-item index="/menu-trees">
            <i class="el-icon-share"></i>
            <span>菜单树管理</span>
          </el-menu-item>
          <el-menu-item index="/publish">
            <i class="el-icon-upload2"></i>
            <span>发布管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style lang="scss">
#app {
  width: 100%;
  height: 100vh;
}

.app-container {
  height: 100%;
}

.app-aside {
  background-color: #304156;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #263445;
    color: #fff;
    font-size: 18px;
    font-weight: bold;

    i {
      margin-right: 8px;
      font-size: 24px;
    }
  }
}

.sidebar-menu {
  border-right: none;
}

.app-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
