<template>
  <div class="page-container">
    <div class="page-header">
      <span class="title">发布管理</span>
      <el-button type="primary" :loading="publishing" @click="handlePublish">
        <i class="el-icon-upload2"></i> 发布数据
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>当前发布状态</span>
          </div>
          
          <div v-if="latestPublish" class="publish-status">
            <el-alert
              title="数据已发布"
              type="success"
              :closable="false"
              show-icon
            >
              <template slot="default">
                <p>版本号：{{ latestPublish.version }}</p>
                <p>发布时间：{{ formatDate(latestPublish.publish_time) }}</p>
                <p>发布人：{{ latestPublish.publish_user || '系统' }}</p>
                <p>文件名：{{ latestPublish.file_name }}</p>
                <p>描述：{{ latestPublish.description || '-' }}</p>
              </template>
            </el-alert>
            
            <div class="action-buttons" style="margin-top: 15px">
              <el-button type="primary" @click="viewPublishedData">
                <i class="el-icon-view"></i> 查看发布数据
              </el-button>
              <el-button type="success" @click="downloadData">
                <i class="el-icon-download"></i> 下载JSON文件
              </el-button>
            </div>
          </div>

          <div v-else class="empty-status">
            <el-empty description="暂无发布记录，请点击上方发布按钮">
              <el-button type="primary" @click="handlePublish">立即发布</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>发布说明</span>
          </div>
          
          <el-timeline>
            <el-timeline-item
              v-for="(step, index) in publishSteps"
              :key="index"
              :type="step.type"
              :icon="step.icon"
            >
              <h4>{{ step.title }}</h4>
              <p>{{ step.description }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <div slot="header">
        <span>发布历史</span>
      </div>
      
      <el-table :data="historyList" v-loading="loadingHistory" border stripe>
        <el-table-column prop="version" label="版本号" min-width="280" />
        <el-table-column prop="publish_time" label="发布时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.publish_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="publish_user" label="发布人" width="120">
          <template slot-scope="scope">
            {{ scope.row.publish_user || '系统' }}
          </template>
        </el-table-column>
        <el-table-column prop="file_name" label="文件名" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="downloadHistoryFile(scope.row)">
              下载
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.page"
          :page-sizes="[10, 20, 50]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        />
      </div>
    </el-card>

    <el-dialog title="发布数据" :visible.sync="publishDialogVisible" width="500px">
      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="发布描述">
          <el-input v-model="publishForm.description" type="textarea" :rows="4" placeholder="请输入发布描述（可选）" />
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="publishForm.publishUser" placeholder="请输入发布人姓名" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="publishDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="publishing" @click="confirmPublish">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="查看发布数据" :visible.sync="dataDialogVisible" width="800px">
      <div class="json-viewer">
        <pre>{{ formattedData }}</pre>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getLatestPublish, getPublishHistory, publish, getPublishedData } from '@/api/publish'
import dayjs from 'dayjs'

export default {
  name: 'Publish',
  data() {
    return {
      latestPublish: null,
      historyList: [],
      loadingHistory: false,
      publishing: false,
      publishDialogVisible: false,
      dataDialogVisible: false,
      publishForm: {
        description: '',
        publishUser: 'admin'
      },
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      },
      publishedData: null,
      publishSteps: [
        {
          title: '收集所有配置数据',
          description: '从数据库获取栏位、页面、组件、菜单、菜单树等所有配置数据',
          type: 'primary',
          icon: 'el-icon-document'
        },
        {
          title: '组装成完整数据结构',
          description: '将关联关系、配置信息等组装成完整的树形结构',
          type: 'success',
          icon: 'el-icon-setting'
        },
        {
          title: '生成JSON文件',
          description: '将完整数据序列化为JSON格式并写入文件',
          type: 'warning',
          icon: 'el-icon-edit'
        },
        {
          title: '记录发布信息',
          description: '记录发布时间、版本号、发布人等信息到数据库',
          type: 'info',
          icon: 'el-icon-s-data'
        }
      ]
    }
  },
  computed: {
    formattedData() {
      if (!this.publishedData) return ''
      return JSON.stringify(this.publishedData, null, 2)
    }
  },
  created() {
    this.fetchLatest()
    this.fetchHistory()
  },
  methods: {
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    async fetchLatest() {
      try {
        const res = await getLatestPublish()
        this.latestPublish = res.data
      } catch (error) {
        console.error(error)
      }
    },
    async fetchHistory() {
      this.loadingHistory = true
      try {
        const res = await getPublishHistory({
          page: this.pagination.page,
          pageSize: this.pagination.pageSize
        })
        this.historyList = res.data.list
        this.pagination.total = res.data.pagination.total
      } catch (error) {
        console.error(error)
      } finally {
        this.loadingHistory = false
      }
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.fetchHistory()
    },
    handleCurrentChange(page) {
      this.pagination.page = page
      this.fetchHistory()
    },
    handlePublish() {
      this.publishForm = {
        description: '',
        publishUser: 'admin'
      }
      this.publishDialogVisible = true
    },
    async confirmPublish() {
      this.publishing = true
      try {
        const res = await publish(this.publishForm)
        this.$message.success('发布成功！')
        this.$alert(`
          发布成功！
          版本号：${res.data.version}
          文件名：${res.data.fileName}
          
          请将生成的JSON文件上传到静态资源服务器。
        `, '发布成功', {
          confirmButtonText: '确定'
        })
        this.publishDialogVisible = false
        this.fetchLatest()
        this.fetchHistory()
      } catch (error) {
        console.error(error)
      } finally {
        this.publishing = false
      }
    },
    async viewPublishedData() {
      try {
        const res = await getPublishedData()
        this.publishedData = res.data
        this.dataDialogVisible = true
      } catch (error) {
        console.error(error)
      }
    },
    downloadData() {
      if (this.latestPublish) {
        window.open(`/api/publish/download/${this.latestPublish.file_name}`, '_blank')
      }
    },
    downloadHistoryFile(row) {
      window.open(`/api/publish/download/${row.file_name}`, '_blank')
    }
  }
}
</script>

<style lang="scss" scoped>
.publish-status {
  .action-buttons {
    text-align: center;
  }
}

.empty-status {
  padding: 40px 0;
}

.json-viewer {
  max-height: 500px;
  overflow-y: auto;
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 15px;

  pre {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
    font-family: 'Consolas', 'Monaco', monospace;
    font-size: 12px;
    line-height: 1.8;
    color: #606266;
  }
}
</style>
