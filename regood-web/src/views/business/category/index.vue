<template>
  <div class="category-container">
    <div class="btn-div">
      <el-button plain size="mini" type="primary" @click="getList">刷新</el-button>
    </div>
    <el-table
      v-loading="loading"
      :data="tableList"
      row-key="categoryId"
      border
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="分类名称" align="center" key="categoryName" prop="categoryName" :show-overflow-tooltip="true" />
      <el-table-column label="分类图标" align="center" key="icon" prop="icon" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.icon">{{ scope.row.icon }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" key="orderNum" prop="orderNum" width="80" />
      <el-table-column label="状态" align="center" key="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 0 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
    </el-table>
  </div>
</template>

<script>
import { getCategoryTree } from '@/api/business/category'

export default {
  name: 'Category',
  data() {
    return {
      loading: false,
      tableList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCategoryTree().then(response => {
        this.tableList = response.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.category-container {
  padding: 20px;
}
.btn-div {
  margin-bottom: 15px;
}
</style>
