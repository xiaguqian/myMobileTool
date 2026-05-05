<template>
  <div class="page-container">
    <div class="page-header">
      <span class="title">菜单树管理</span>
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增菜单树
      </el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="树编码/名称" clearable @keyup.enter.native="handleSearch" />
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
        <el-table-column prop="tree_code" label="树编码" width="180" />
        <el-table-column prop="tree_name" label="树名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
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
        <el-table-column label="操作" width="250" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="text" size="small" @click="handleConfig(scope.row)">
              配置节点
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
        <el-form-item label="树编码" prop="tree_code">
          <el-input v-model="form.tree_code" placeholder="请输入树编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="树名称" prop="tree_name">
          <el-input v-model="form.tree_name" placeholder="请输入树名称" />
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
import { getMenuTrees, createMenuTree, updateMenuTree, deleteMenuTree } from '@/api/menuTrees'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash'

export default {
  name: 'MenuTrees',
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
        tree_code: '',
        tree_name: '',
        description: '',
        status: 1
      },
      rules: {
        tree_code: [{ required: true, message: '请输入树编码', trigger: 'blur' }],
        tree_name: [{ required: true, message: '请输入树名称', trigger: 'blur' }]
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

        const res = await getMenuTrees(params)
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
      this.dialogTitle = '新增菜单树'
      this.form = {
        id: null,
        tree_code: '',
        tree_name: '',
        description: '',
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑菜单树'
      this.form = cloneDeep(row)
      this.dialogVisible = true
    },
    handleConfig(row) {
      this.$router.push({ path: `/menu-trees/${row.id}/config` })
    },
    async handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEdit) {
              await updateMenuTree(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await createMenuTree(this.form)
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
      this.$confirm('确定要删除该菜单树吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteMenuTree(row.id)
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
