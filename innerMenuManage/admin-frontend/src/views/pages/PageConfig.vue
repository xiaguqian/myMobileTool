<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <el-button type="text" @click="$router.back()">
          <i class="el-icon-arrow-left"></i> 返回
        </el-button>
        <span class="title">页面组件配置 - {{ pageInfo?.page_name }}</span>
      </div>
      <el-button type="primary" @click="handleSaveAll">
        <i class="el-icon-check"></i> 保存配置
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>已添加的组件</span>
            <el-button type="text" size="small" @click="showAddDialog = true">
              <i class="el-icon-plus"></i> 添加组件
            </el-button>
          </div>
          
          <draggable v-model="pageComponents" handle=".drag-handle" animation="300" @end="handleDragEnd">
            <div v-for="(item, index) in pageComponents" :key="item.relation_id" class="component-item">
              <div class="component-header">
                <div class="drag-handle">
                  <i class="el-icon-rank"></i>
                </div>
                <div class="component-info">
                  <span class="component-name">{{ item.component_name }}</span>
                  <span class="component-type">({{ item.component_type }})</span>
                </div>
                <div class="component-actions">
                  <el-button type="text" size="small" @click="handleEditConfig(item)">
                    <i class="el-icon-edit"></i> 配置
                  </el-button>
                  <el-button type="text" size="small" class="danger-text" @click="handleRemoveComponent(item)">
                    <i class="el-icon-delete"></i> 移除
                  </el-button>
                </div>
              </div>
              <div v-if="item.config" class="component-config-preview">
                <pre>{{ formatJson(item.config) }}</pre>
              </div>
            </div>
          </draggable>

          <el-empty v-if="pageComponents.length === 0" description="暂无组件，点击右上角添加" />
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <div slot="header">
            <span>页面信息</span>
          </div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="页面编码">{{ pageInfo?.page_code }}</el-descriptions-item>
            <el-descriptions-item label="页面名称">{{ pageInfo?.page_name }}</el-descriptions-item>
            <el-descriptions-item label="路由路径">{{ pageInfo?.route_path }}</el-descriptions-item>
            <el-descriptions-item label="描述">{{ pageInfo?.description || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="pageInfo?.status === 1 ? 'success' : 'danger'">
                {{ pageInfo?.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card style="margin-top: 20px">
          <div slot="header">
            <span>组件预览顺序</span>
          </div>
          <ul class="order-list">
            <li v-for="(item, index) in pageComponents" :key="item.relation_id">
              <span class="order-index">{{ index + 1 }}</span>
              <span>{{ item.component_name }}</span>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog title="添加组件" :visible.sync="showAddDialog" width="500px">
      <el-form label-width="100px">
        <el-form-item label="选择组件">
          <el-select v-model="selectedComponent" placeholder="请选择组件" filterable style="width: 100%">
            <el-option
              v-for="c in availableComponents"
              :key="c.id"
              :label="`${c.component_name} (${c.component_type})`"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="组件配置">
          <el-input
            v-model="componentConfigText"
            type="textarea"
            :rows="8"
            placeholder="请输入JSON格式的配置，如: {\"key\": \"value\"}"
            class="json-editor"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showAddDialog = false">取 消</el-button>
        <el-button type="primary" @click="handleAddComponent">添 加</el-button>
      </span>
    </el-dialog>

    <el-dialog title="编辑组件配置" :visible.sync="showConfigDialog" width="500px">
      <el-form label-width="100px">
        <el-form-item label="组件名称">
          <el-input v-model="currentEditingComponent?.component_name" disabled />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="currentEditingComponent?.sort_order" :min="0" />
        </el-form-item>
        <el-form-item label="组件配置">
          <el-input
            v-model="currentEditingConfigText"
            type="textarea"
            :rows="10"
            placeholder="请输入JSON格式的配置"
            class="json-editor"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showConfigDialog = false">取 消</el-button>
        <el-button type="primary" @click="handleUpdateConfig">保 存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getPageById, addPageComponent, removePageComponent, updateComponentConfig } from '@/api/pages'
import { getComponents } from '@/api/components'
import draggable from 'vuedraggable'

export default {
  name: 'PageConfig',
  components: { draggable },
  data() {
    return {
      pageId: null,
      pageInfo: null,
      pageComponents: [],
      allComponents: [],
      showAddDialog: false,
      showConfigDialog: false,
      selectedComponent: null,
      sortOrder: 0,
      componentConfigText: '',
      currentEditingComponent: null,
      currentEditingConfigText: ''
    }
  },
  computed: {
    availableComponents() {
      const addedIds = this.pageComponents.map(c => c.id)
      return this.allComponents.filter(c => !addedIds.includes(c.id))
    }
  },
  created() {
    this.pageId = this.$route.params.id
    this.fetchPageInfo()
    this.fetchAllComponents()
  },
  methods: {
    formatJson(obj) {
      try {
        return JSON.stringify(typeof obj === 'string' ? JSON.parse(obj) : obj, null, 2)
      } catch {
        return JSON.stringify(obj)
      }
    },
    async fetchPageInfo() {
      try {
        const res = await getPageById(this.pageId)
        this.pageInfo = res.data
        this.pageComponents = (res.data.components || []).map(c => ({
          ...c,
          config: c.config
        }))
      } catch (error) {
        console.error(error)
      }
    },
    async fetchAllComponents() {
      try {
        const res = await getComponents({ status: 1, pageSize: 1000 })
        this.allComponents = res.data.list
      } catch (error) {
        console.error(error)
      }
    },
    handleDragEnd() {
      this.pageComponents.forEach((item, index) => {
        item.sort_order = index + 1
      })
    },
    async handleAddComponent() {
      if (!this.selectedComponent) {
        this.$message.warning('请选择组件')
        return
      }

      let config = null
      if (this.componentConfigText) {
        try {
          config = JSON.parse(this.componentConfigText)
        } catch (e) {
          this.$message.error('JSON格式错误')
          return
        }
      }

      try {
        await addPageComponent({
          pageId: this.pageId,
          componentId: this.selectedComponent,
          componentConfig: config,
          sortOrder: this.sortOrder || this.pageComponents.length + 1
        })
        this.$message.success('添加成功')
        this.showAddDialog = false
        this.selectedComponent = null
        this.componentConfigText = ''
        this.sortOrder = 0
        this.fetchPageInfo()
      } catch (error) {
        console.error(error)
      }
    },
    handleEditConfig(item) {
      this.currentEditingComponent = { ...item }
      this.currentEditingConfigText = item.config ? this.formatJson(item.config) : ''
      this.showConfigDialog = true
    },
    async handleUpdateConfig() {
      let config = null
      if (this.currentEditingConfigText) {
        try {
          config = JSON.parse(this.currentEditingConfigText)
        } catch (e) {
          this.$message.error('JSON格式错误')
          return
        }
      }

      try {
        await updateComponentConfig({
          pageId: this.pageId,
          componentId: this.currentEditingComponent.id,
          componentConfig: config
        })
        this.$message.success('更新成功')
        this.showConfigDialog = false
        this.fetchPageInfo()
      } catch (error) {
        console.error(error)
      }
    },
    handleRemoveComponent(item) {
      this.$confirm('确定要移除该组件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await removePageComponent({
            pageId: this.pageId,
            componentId: item.id
          })
          this.$message.success('移除成功')
          this.fetchPageInfo()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    async handleSaveAll() {
      try {
        for (let i = 0; i < this.pageComponents.length; i++) {
          const item = this.pageComponents[i]
          await addPageComponent({
            pageId: this.pageId,
            componentId: item.id,
            componentConfig: item.config,
            sortOrder: i + 1
          })
        }
        this.$message.success('保存成功')
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.component-item {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 10px;
  overflow: hidden;

  .component-header {
    display: flex;
    align-items: center;
    background-color: #f5f7fa;
    padding: 10px 15px;

    .drag-handle {
      cursor: move;
      color: #909399;
      margin-right: 10px;
    }

    .component-info {
      flex: 1;

      .component-name {
        font-weight: bold;
        color: #303133;
      }

      .component-type {
        color: #909399;
        font-size: 12px;
      }
    }

    .component-actions {
      .danger-text {
        color: #f56c6c;
      }
    }
  }

  .component-config-preview {
    padding: 15px;
    background-color: #fafafa;
    border-top: 1px solid #dcdfe6;

    pre {
      margin: 0;
      white-space: pre-wrap;
      word-break: break-all;
      font-size: 12px;
      color: #606266;
      max-height: 200px;
      overflow: auto;
    }
  }
}

.order-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    padding: 8px 0;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;

    &:last-child {
      border-bottom: none;
    }

    .order-index {
      width: 24px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      background-color: #409EFF;
      color: #fff;
      border-radius: 50%;
      margin-right: 10px;
      font-size: 12px;
    }
  }
}
</style>
