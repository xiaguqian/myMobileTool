<template>
  <div class="page-container">
    <div class="page-header">
      <span class="title">页面管理</span>
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增页面
      </el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="页面编码/名称/路由" clearable @keyup.enter.native="handleSearch" />
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
        <el-table-column prop="page_code" label="页面编码" width="150" />
        <el-table-column prop="page_name" label="页面名称" min-width="120" />
        <el-table-column prop="route_path" label="路由路径" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
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
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="text" size="small" @click="handleConfig(scope.row)">
              配置组件
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
        <el-form-item label="页面编码" prop="page_code">
          <el-input v-model="form.page_code" placeholder="请输入页面编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="页面名称" prop="page_name">
          <el-input v-model="form.page_name" placeholder="请输入页面名称" />
        </el-form-item>
        <el-form-item label="路由路径" prop="route_path">
          <el-input v-model="form.route_path" placeholder="如: /home, /product/list" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
import { getPages, createPage, updatePage, deletePage } from '@/api/pages'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash'

export default {
  name: 'Pages',
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
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: {
        id: null,
        page_code: '',
        page_name: '',
        route_path: '',
        description: '',
        status: 1
      },
      rules: {
        page_code: [{ required: true, message: '请输入页面编码', trigger: 'blur' }],
        page_name: [{ required: true, message: '请输入页面名称', trigger: 'blur' }],
        route_path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchData()
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

        const res = await getPages(params)
        this.tableData = res.data.list
        this.pagination.total = res.data.pagination.total
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
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
      this.dialogTitle = '新增页面'
      this.form = {
        id: null,
        page_code: '',
        page_name: '',
        route_path: '',
        description: '',
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑页面'
      this.form = cloneDeep(row)
      this.dialogVisible = true
    },
    handleConfig(row) {
      this.$router.push({ path: `/pages/${row.id}/config` })
    },
    async handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEdit) {
              await updatePage(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await createPage(this.form)
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
      this.$confirm('确定要删除该页面吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deletePage(row.id)
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
