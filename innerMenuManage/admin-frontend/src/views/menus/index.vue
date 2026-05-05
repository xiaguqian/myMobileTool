<template>
  <div class="page-container">
    <div class="page-header">
      <span class="title">菜单管理</span>
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增菜单
      </el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="菜单编码/名称/权限编码" clearable @keyup.enter.native="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="menu_code" label="菜单编码" width="150" />
        <el-table-column prop="menu_name" label="菜单名称" min-width="120" />
        <el-table-column prop="menu_icon" label="菜单图标" width="120">
          <template slot-scope="scope">
            <i :class="scope.row.menu_icon" v-if="scope.row.menu_icon"></i>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="route_url" label="路由URL" min-width="150" />
        <el-table-column prop="page_id" label="关联页面ID" width="100">
          <template slot-scope="scope">
            {{ scope.row.page_id || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="permission_code" label="权限编码" width="150">
          <template slot-scope="scope">
            <el-tag type="info" size="small" v-if="scope.row.permission_code">
              {{ scope.row.permission_code }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" class="status-tag">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="text" size="small" class="danger-text" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" class="dialog-container">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="菜单编码" prop="menu_code">
          <el-input v-model="form.menu_code" placeholder="请输入菜单编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menu_name">
          <el-input v-model="form.menu_name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-input v-model="form.menu_icon" placeholder="如: el-icon-menu, home">
            <template slot="prepend">
              <i :class="form.menu_icon" v-if="form.menu_icon"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="路由URL">
          <el-input v-model="form.route_url" placeholder="如: /home, /product/list" />
        </el-form-item>
        <el-form-item label="关联页面">
          <el-select v-model="form.page_id" placeholder="请选择关联页面（可选）" filterable clearable style="width: 100%">
            <el-option v-for="p in pages" :key="p.id" :label="`${p.page_name} (${p.page_code})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model="form.permission_code" placeholder="如: view:home, edit:product" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getMenus, createMenu, updateMenu, deleteMenu } from '@/api/menus'
import { getPages } from '@/api/pages'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash'

export default {
  name: 'Menus',
  data() {
    return {
      loading: false,
      searchForm: {
        keyword: '',
        status: undefined
      },
      tableData: [],
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      },
      pages: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: {
        id: null,
        menu_code: '',
        menu_name: '',
        menu_icon: '',
        route_url: '',
        page_id: null,
        permission_code: '',
        description: '',
        status: 1
      },
      rules: {
        menu_code: [{ required: true, message: '请输入菜单编码', trigger: 'blur' }],
        menu_name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchData()
    this.fetchPages()
  },
  methods: {
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    async fetchData() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page,
          pageSize: this.pagination.pageSize,
          ...this.searchForm
        }
        if (params.status === undefined) delete params.status
        if (!params.keyword) delete params.keyword

        const res = await getMenus(params)
        this.tableData = res.data.list
        this.pagination.total = res.data.pagination.total
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async fetchPages() {
      try {
        const res = await getPages({ status: 1, pageSize: 1000 })
        this.pages = res.data.list
      } catch (error) {
        console.error(error)
      }
    },
    handleSearch() {
      this.pagination.page = 1
      this.fetchData()
    },
    handleReset() {
      this.searchForm = {
        keyword: '',
        status: undefined
      }
      this.pagination.page = 1
      this.fetchData()
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.fetchData()
    },
    handleCurrentChange(page) {
      this.pagination.page = page
      this.fetchData()
    },
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = '新增菜单'
      this.form = {
        id: null,
        menu_code: '',
        menu_name: '',
        menu_icon: '',
        route_url: '',
        page_id: null,
        permission_code: '',
        description: '',
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑菜单'
      this.form = cloneDeep(row)
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEdit) {
              await updateMenu(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await createMenu(this.form)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.fetchData()
          } catch (error) {
            console.error(error)
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该菜单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteMenu(row.id)
          this.$message.success('删除成功')
          this.fetchData()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.danger-text {
  color: #f56c6c;
}
</style>
