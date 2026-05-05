<template>
  <div class="page-container">
    <div class="page-header">
      <span class="title">栏位管理</span>
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增栏位
      </el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="栏位号">
          <el-input v-model="searchForm.keyword" placeholder="请输入栏位号或名称" clearable @keyup.enter.native="handleSearch" />
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
        <el-table-column prop="slot_code" label="栏位号" width="150" />
        <el-table-column prop="slot_name" label="栏位名称" min-width="150" />
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
        <el-table-column label="操作" width="300" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="text" size="small" @click="handleAssociatePage(scope.row)">
              关联页面
            </el-button>
            <el-button type="text" size="small" @click="handleAssociateTree(scope.row)">
              关联菜单树
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
        <el-form-item label="栏位号" prop="slot_code">
          <el-input v-model="form.slot_code" placeholder="请输入栏位号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="栏位名称" prop="slot_name">
          <el-input v-model="form.slot_name" placeholder="请输入栏位名称" />
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

    <el-dialog title="关联页面" :visible.sync="pageDialogVisible" width="800px">
      <div v-if="currentSlot">
        <el-divider content-position="left">已关联的页面</el-divider>
        <el-table :data="associatedPages" border size="small">
          <el-table-column prop="page_code" label="页面编码" width="150" />
          <el-table-column prop="page_name" label="页面名称" width="150" />
          <el-table-column prop="route_path" label="路由路径" />
          <el-table-column prop="sort_order" label="排序" width="100">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.sort_order" :min="0" size="small" @change="updatePageSort(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="danger-text" @click="removePage(scope.row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">添加关联</el-divider>
        <el-form inline>
          <el-form-item label="选择页面">
            <el-select v-model="selectedPage" placeholder="请选择页面" filterable style="width: 300px">
              <el-option v-for="p in allPages" :key="p.id" :label="`${p.page_name} (${p.page_code})`" :value="p.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="pageSortOrder" :min="0" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addPageAssociation">添加</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>

    <el-dialog title="关联菜单树" :visible.sync="treeDialogVisible" width="800px">
      <div v-if="currentSlot">
        <el-divider content-position="left">已关联的菜单树</el-divider>
        <el-table :data="associatedTrees" border size="small">
          <el-table-column prop="tree_code" label="树编码" width="150" />
          <el-table-column prop="tree_name" label="树名称" width="150" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="sort_order" label="排序" width="100">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.sort_order" :min="0" size="small" @change="updateTreeSort(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="danger-text" @click="removeTree(scope.row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">添加关联</el-divider>
        <el-form inline>
          <el-form-item label="选择菜单树">
            <el-select v-model="selectedTree" placeholder="请选择菜单树" filterable style="width: 300px">
              <el-option v-for="t in allTrees" :key="t.id" :label="`${t.tree_name} (${t.tree_code})`" :value="t.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="treeSortOrder" :min="0" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addTreeAssociation">添加</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSlots, createSlot, updateSlot, deleteSlot, associatePage, disassociatePage, associateMenuTree, disassociateMenuTree, getSlotById } from '@/api/slots'
import { getPages } from '@/api/pages'
import { getMenuTrees } from '@/api/menuTrees'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash'

export default {
  name: 'Slots',
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
        slot_code: '',
        slot_name: '',
        description: '',
        status: 1
      },
      rules: {
        slot_code: [{ required: true, message: '请输入栏位号', trigger: 'blur' }],
        slot_name: [{ required: true, message: '请输入栏位名称', trigger: 'blur' }]
      },
      currentSlot: null,
      pageDialogVisible: false,
      treeDialogVisible: false,
      associatedPages: [],
      associatedTrees: [],
      allPages: [],
      allTrees: [],
      selectedPage: null,
      selectedTree: null,
      pageSortOrder: 0,
      treeSortOrder: 0
    }
  },
  created() {
    this.fetchData()
    this.fetchAllPages()
    this.fetchAllTrees()
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

        const res = await getSlots(params)
        this.tableData = res.data.list
        this.pagination.total = res.data.pagination.total
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async fetchAllPages() {
      try {
        const res = await getPages({ pageSize: 1000 })
        this.allPages = res.data.list
      } catch (error) {
        console.error(error)
      }
    },
    async fetchAllTrees() {
      try {
        const res = await getMenuTrees({ pageSize: 1000 })
        this.allTrees = res.data.list
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
      this.dialogTitle = '新增栏位'
      this.form = {
        id: null,
        slot_code: '',
        slot_name: '',
        description: '',
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑栏位'
      this.form = cloneDeep(row)
      this.dialogVisible = true
    },
    async handleView(row) {
      try {
        const res = await getSlotById(row.id)
        this.$message.info(`栏位：${res.data.slot_name}，关联了 ${res.data.pages?.length || 0} 个页面，${res.data.menuTrees?.length || 0} 个菜单树`)
      } catch (error) {
        console.error(error)
      }
    },
    async handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEdit) {
              await updateSlot(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await createSlot(this.form)
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
      this.$confirm('确定要删除该栏位吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteSlot(row.id)
          this.$message.success('删除成功')
          this.fetchData()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    async handleAssociatePage(row) {
      this.currentSlot = row
      try {
        const res = await getSlotById(row.id)
        this.associatedPages = res.data.pages || []
      } catch (error) {
        this.associatedPages = []
      }
      this.selectedPage = null
      this.pageSortOrder = 0
      this.pageDialogVisible = true
    },
    async addPageAssociation() {
      if (!this.selectedPage) {
        this.$message.warning('请选择页面')
        return
      }
      try {
        await associatePage({
          slotId: this.currentSlot.id,
          pageId: this.selectedPage,
          sortOrder: this.pageSortOrder
        })
        this.$message.success('关联成功')
        const res = await getSlotById(this.currentSlot.id)
        this.associatedPages = res.data.pages || []
        this.selectedPage = null
        this.pageSortOrder = 0
      } catch (error) {
        console.error(error)
      }
    },
    async updatePageSort(row) {
      try {
        await associatePage({
          slotId: this.currentSlot.id,
          pageId: row.id,
          sortOrder: row.sort_order
        })
        this.$message.success('更新成功')
      } catch (error) {
        console.error(error)
      }
    },
    async removePage(row) {
      this.$confirm('确定要移除该关联吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await disassociatePage({
            slotId: this.currentSlot.id,
            pageId: row.id
          })
          this.$message.success('移除成功')
          const res = await getSlotById(this.currentSlot.id)
          this.associatedPages = res.data.pages || []
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    async handleAssociateTree(row) {
      this.currentSlot = row
      try {
        const res = await getSlotById(row.id)
        this.associatedTrees = res.data.menuTrees || []
      } catch (error) {
        this.associatedTrees = []
      }
      this.selectedTree = null
      this.treeSortOrder = 0
      this.treeDialogVisible = true
    },
    async addTreeAssociation() {
      if (!this.selectedTree) {
        this.$message.warning('请选择菜单树')
        return
      }
      try {
        await associateMenuTree({
          slotId: this.currentSlot.id,
          treeId: this.selectedTree,
          sortOrder: this.treeSortOrder
        })
        this.$message.success('关联成功')
        const res = await getSlotById(this.currentSlot.id)
        this.associatedTrees = res.data.menuTrees || []
        this.selectedTree = null
        this.treeSortOrder = 0
      } catch (error) {
        console.error(error)
      }
    },
    async updateTreeSort(row) {
      try {
        await associateMenuTree({
          slotId: this.currentSlot.id,
          treeId: row.id,
          sortOrder: row.sort_order
        })
        this.$message.success('更新成功')
      } catch (error) {
        console.error(error)
      }
    },
    async removeTree(row) {
      this.$confirm('确定要移除该关联吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await disassociateMenuTree({
            slotId: this.currentSlot.id,
            treeId: row.id
          })
          this.$message.success('移除成功')
          const res = await getSlotById(this.currentSlot.id)
          this.associatedTrees = res.data.menuTrees || []
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
