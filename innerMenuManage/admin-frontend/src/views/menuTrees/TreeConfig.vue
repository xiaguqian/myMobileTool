<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <el-button type="text" @click="$router.back()">
          <i class="el-icon-arrow-left"></i> 返回
        </el-button>
        <span class="title">菜单树配置 - {{ treeInfo?.tree_name }}</span>
      </div>
      <el-button type="primary" @click="handleAddRoot">
        <i class="el-icon-plus"></i> 新增根节点
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>树形结构</span>
            <el-button type="text" size="small" @click="refreshTree">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          
          <div class="tree-container">
            <el-tree
              :data="treeData"
              :props="defaultProps"
              default-expand-all
              highlight-current
              :expand-on-click-node="false"
              node-key="id"
              ref="treeRef"
            >
              <span slot-scope="{ node, data }" class="tree-node">
                <span class="node-content">
                  <span class="node-icon" :class="data.nodeIcon || data.node_type === 1 ? 'el-icon-folder' : 'el-icon-document'"></span>
                  <span class="node-name">{{ data.nodeName }}</span>
                  <el-tag v-if="data.menu" type="primary" size="mini" class="menu-tag">
                    {{ data.menu.menuName }}
                  </el-tag>
                  <span class="node-type">
                    {{ data.node_type === 1 ? '[目录]' : '[菜单]' }}
                  </span>
                </span>
                <span class="node-actions">
                  <el-button type="text" size="mini" @click.stop="handleAddChild(node, data)">
                    添加子节点
                  </el-button>
                  <el-button type="text" size="mini" @click.stop="handleEditNode(data)">
                    编辑
                  </el-button>
                  <el-button type="text" size="mini" class="danger-text" @click.stop="handleDeleteNode(data)">
                    删除
                  </el-button>
                </span>
              </span>
            </el-tree>

            <el-empty v-if="treeData.length === 0" description="暂无节点，请点击右上角添加">
              <el-button type="primary" @click="handleAddRoot">新增根节点</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <div slot="header">
            <span>菜单树信息</span>
          </div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="树编码">{{ treeInfo?.tree_code }}</el-descriptions-item>
            <el-descriptions-item label="树名称">{{ treeInfo?.tree_name }}</el-descriptions-item>
            <el-descriptions-item label="描述">{{ treeInfo?.description || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="treeInfo?.status === 1 ? 'success' : 'danger'">
                {{ treeInfo?.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card style="margin-top: 20px">
          <div slot="header">
            <span>提示</span>
          </div>
          <el-alert
            title="目录节点：用于分组，不关联菜单"
            type="info"
            show-icon
            :closable="false"
            style="margin-bottom: 10px"
          />
          <el-alert
            title="菜单节点：关联具体的菜单，点击跳转页面"
            type="success"
            show-icon
            :closable="false"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog :title="nodeDialogTitle" :visible.sync="nodeDialogVisible" width="550px">
      <el-form :model="nodeForm" :rules="nodeRules" ref="nodeFormRef" label-width="100px">
        <el-form-item label="节点编码" prop="nodeCode">
          <el-input v-model="nodeForm.nodeCode" placeholder="请输入节点编码" />
        </el-form-item>
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model="nodeForm.nodeName" placeholder="请输入节点名称" />
        </el-form-item>
        <el-form-item label="节点图标">
          <el-input v-model="nodeForm.nodeIcon" placeholder="请输入图标类名（可选）" />
        </el-form-item>
        <el-form-item label="节点类型" prop="nodeType">
          <el-radio-group v-model="nodeForm.nodeType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联菜单" v-if="nodeForm.nodeType === 2">
          <el-select v-model="nodeForm.menuId" placeholder="请选择关联菜单" filterable style="width: 100%">
            <el-option v-for="m in menus" :key="m.id" :label="`${m.menu_name} (${m.menu_code})`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="nodeForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="nodeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmitNode">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getMenuTreeById, getMenuTreeStructure, addTreeNode, updateTreeNode, deleteTreeNode } from '@/api/menuTrees'
import { getActiveMenus } from '@/api/menus'
import { cloneDeep } from 'lodash'

export default {
  name: 'TreeConfig',
  data() {
    return {
      treeId: null,
      treeInfo: null,
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'nodeName'
      },
      menus: [],
      nodeDialogVisible: false,
      nodeDialogTitle: '',
      isEditNode: false,
      currentParent: null,
      nodeForm: {
        id: null,
        nodeCode: '',
        nodeName: '',
        nodeIcon: '',
        nodeType: 1,
        menuId: null,
        sortOrder: 0
      },
      nodeRules: {
        nodeCode: [{ required: true, message: '请输入节点编码', trigger: 'blur' }],
        nodeName: [{ required: true, message: '请输入节点名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.treeId = this.$route.params.id
    this.fetchTreeInfo()
    this.fetchMenus()
  },
  methods: {
    async fetchTreeInfo() {
      try {
        const res = await getMenuTreeById(this.treeId)
        this.treeInfo = res.data
        this.refreshTree()
      } catch (error) {
        console.error(error)
      }
    },
    async refreshTree() {
      try {
        const res = await getMenuTreeStructure(this.treeId)
        this.treeData = this.transformTreeData(res.data)
      } catch (error) {
        console.error(error)
      }
    },
    transformTreeData(data) {
      if (!data || data.length === 0) return []
      
      const transform = (nodes) => {
        return nodes.map(node => {
          const transformed = {
            id: node.id,
            nodeCode: node.nodeCode,
            nodeName: node.nodeName,
            nodeIcon: node.nodeIcon,
            parentId: node.parentId,
            menuId: node.menuId,
            node_type: node.nodeType,
            sortOrder: node.sortOrder,
            menu: node.menu
          }
          if (node.children && node.children.length > 0) {
            transformed.children = transform(node.children)
          }
          return transformed
        })
      }
      
      return transform(data)
    },
    async fetchMenus() {
      try {
        const res = await getActiveMenus()
        this.menus = res.data || []
      } catch (error) {
        console.error(error)
      }
    },
    handleAddRoot() {
      this.isEditNode = false
      this.currentParent = null
      this.nodeDialogTitle = '新增根节点'
      this.nodeForm = {
        id: null,
        nodeCode: '',
        nodeName: '',
        nodeIcon: '',
        nodeType: 1,
        menuId: null,
        sortOrder: this.treeData.length
      }
      this.nodeDialogVisible = true
    },
    handleAddChild(node, data) {
      this.isEditNode = false
      this.currentParent = data
      this.nodeDialogTitle = '新增子节点'
      const childrenCount = data.children ? data.children.length : 0
      this.nodeForm = {
        id: null,
        nodeCode: '',
        nodeName: '',
        nodeIcon: '',
        nodeType: 1,
        menuId: null,
        sortOrder: childrenCount
      }
      this.nodeDialogVisible = true
    },
    handleEditNode(data) {
      this.isEditNode = true
      this.nodeDialogTitle = '编辑节点'
      this.nodeForm = cloneDeep({
        id: data.id,
        nodeCode: data.nodeCode,
        nodeName: data.nodeName,
        nodeIcon: data.nodeIcon || '',
        nodeType: data.node_type,
        menuId: data.menuId,
        sortOrder: data.sortOrder
      })
      this.nodeDialogVisible = true
    },
    handleDeleteNode(data) {
      this.$confirm('确定要删除该节点吗？子节点也会被一起删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteTreeNode(data.id)
          this.$message.success('删除成功')
          this.refreshTree()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    async handleSubmitNode() {
      this.$refs.nodeFormRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEditNode) {
              await updateTreeNode({
                nodeId: this.nodeForm.id,
                nodeCode: this.nodeForm.nodeCode,
                nodeName: this.nodeForm.nodeName,
                nodeIcon: this.nodeForm.nodeIcon,
                nodeType: this.nodeForm.nodeType,
                menuId: this.nodeForm.nodeType === 2 ? this.nodeForm.menuId : null,
                sortOrder: this.nodeForm.sortOrder
              })
              this.$message.success('更新成功')
            } else {
              await addTreeNode({
                treeId: this.treeId,
                nodeCode: this.nodeForm.nodeCode,
                nodeName: this.nodeForm.nodeName,
                parentId: this.currentParent ? this.currentParent.id : null,
                nodeIcon: this.nodeForm.nodeIcon,
                nodeType: this.nodeForm.nodeType,
                menuId: this.nodeForm.nodeType === 2 ? this.nodeForm.menuId : null,
                sortOrder: this.nodeForm.sortOrder
              })
              this.$message.success('添加成功')
            }
            this.nodeDialogVisible = false
            this.refreshTree()
          } catch (error) {
            console.error(error)
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;

  .node-content {
    display: flex;
    align-items: center;

    .node-icon {
      margin-right: 5px;
      color: #409EFF;
    }

    .node-name {
      margin-right: 8px;
    }

    .menu-tag {
      margin-right: 8px;
    }

    .node-type {
      color: #909399;
      font-size: 12px;
    }
  }

  .node-actions {
    opacity: 0;
    transition: opacity 0.3s;

    .danger-text {
      color: #f56c6c;
    }
  }

  &:hover .node-actions {
    opacity: 1;
  }
}
</style>
