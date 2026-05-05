<template>
  <div class="page-container">
    <div class="page-header">
      <span class="title">组件管理</span>
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增组件
      </el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="组件编码/名称" clearable @keyup.enter.native="handleSearch" />
        </el-form-item>
        <el-form-item label="组件类型">
          <el-select v-model="searchForm.componentType" placeholder="全部" clearable>
            <el-option v-for="t in componentTypes" :key="t" :label="t" :value="t" />
          </el-select>
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
        <el-table-column prop="component_code" label="组件编码" width="180" />
        <el-table-column prop="component_name" label="组件名称" min-width="120" />
        <el-table-column prop="component_type" label="组件类型" width="120">
          <template slot-scope="scope">
            <el-tag type="primary" size="small">{{ scope.row.component_type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="default_config" label="默认配置" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <code>{{ JSON.stringify(scope.row.default_config) }}</code>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="200" fixed="right">
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px" class="dialog-container">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="组件编码" prop="component_code">
          <el-input v-model="form.component_code" placeholder="请输入组件编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="组件名称" prop="component_name">
          <el-input v-model="form.component_name" placeholder="请输入组件名称" />
        </el-form-item>
        <el-form-item label="组件类型" prop="component_type">
          <el-select v-model="form.component_type" placeholder="请选择组件类型" allow-create filterable style="width: 100%">
            <el-option v-for="t in componentTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认配置">
          <el-input
            v-model="form.default_config_text"
            type="textarea"
            :rows="6"
            placeholder="请输入JSON格式的默认配置"
            class="json-editor"
          />
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
import { getComponents, createComponent, updateComponent, deleteComponent, getComponentTypes } from '@/api/components'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash'

export default {
  name: 'Components',
  data() {
    return {
      loading: false,
      searchForm: {
        keyword: '',
        componentType: undefined,
        status: undefined
      },
      tableData: [],
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      },
      componentTypes: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: {
        id: null,
        component_code: '',
        component_name: '',
        component_type: '',
        default_config: null,
        default_config_text: '',
        description: '',
        status: 1
      },
      rules: {
        component_code: [{ required: true, message: '请输入组件编码', trigger: 'blur' }],
        component_name: [{ required: true, message: '请输入组件名称', trigger: 'blur' }],
        component_type: [{ required: true, message: '请输入组件类型', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchData()
    this.fetchComponentTypes()
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
          keyword: this.searchForm.keyword,
          componentType: this.searchForm.componentType,
          status: this.searchForm.status
        }
        if (params.status === undefined) delete params.status
        if (!params.keyword) delete params.keyword
        if (!params.componentType) delete params.componentType

        const res = await getComponents(params)
        this.tableData = res.data.list
        this.pagination.total = res.data.pagination.total
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async fetchComponentTypes() {
      try {
        const res = await getComponentTypes()
        this.componentTypes = res.data || []
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
        componentType: undefined,
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
      this.dialogTitle = '新增组件'
      this.form = {
        id: null,
        component_code: '',
        component_name: '',
        component_type: '',
        default_config: null,
        default_config_text: '',
        description: '',
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑组件'
      this.form = cloneDeep(row)
      this.form.default_config_text = row.default_config ? JSON.stringify(row.default_config, null, 2) : ''
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          let defaultConfig = null
          if (this.form.default_config_text) {
            try {
              defaultConfig = JSON.parse(this.form.default_config_text)
            } catch (e) {
              this.$message.error('默认配置JSON格式错误')
              return
            }
          }

          const submitData = {
            component_code: this.form.component_code,
            component_name: this.form.component_name,
            component_type: this.form.component_type,
            default_config: defaultConfig,
            description: this.form.description,
            status: this.form.status
          }

          try {
            if (this.isEdit) {
              await updateComponent(this.form.id, submitData)
              this.$message.success('更新成功')
            } else {
              await createComponent(submitData)
              this.$message.success('创建成功')
              this.fetchComponentTypes()
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
      this.$confirm('确定要删除该组件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteComponent(row.id)
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

code {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 12px;
  color: #409EFF;
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 2px;
}
</style>
