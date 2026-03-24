<template>
  <div class="product-container">
    <div>
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.title" placeholder="请输入商品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="商品状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="上架" :value="1"></el-option>
            <el-option label="下架" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList" size="medium">搜索</el-button>
          <el-button @click="resetSearch" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="btn-div">
      <div>
        <el-button plain size="mini" type="danger" @click="handleDelete(multipleSelectionIds)" :disabled="multipleSelectionIds.length === 0">批量删除</el-button>
      </div>
    </div>
    <div>
      <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="商品图片" align="center" width="100">
          <template slot-scope="scope">
            <el-avatar v-if="scope.row.images && scope.row.images.length > 0" size="medium" :src="scope.row.images[0]"></el-avatar>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" align="center" prop="title" :show-overflow-tooltip="true" min-width="150" />
        <el-table-column label="价格" align="center" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column label="原价" align="center" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.originalPrice }}
          </template>
        </el-table-column>
        <el-table-column label="分类" align="center" prop="categoryName" width="100" />
        <el-table-column label="卖家" align="center" prop="sellerName" width="100" />
        <el-table-column label="浏览量" align="center" prop="viewCount" width="80" />
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.auditStatus === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 1" type="success" size="small">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 2" type="danger" size="small">已拒绝</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="text" @click="handleOffline(scope.row)" v-if="scope.row.status === 1">下架</el-button>
            <el-button size="mini" type="text" @click="handleOnline(scope.row)" v-if="scope.row.status === 0">上架</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete([scope.row.productId])">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination
      v-show="searchForm.total > 0"
      :total="searchForm.total"
      :page.sync="searchForm.pageNum"
      :limit.sync="searchForm.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { getProductPage, deleteProduct, offlineProduct, onlineProduct } from '@/api/business/product'

export default {
  name: 'Product',
  data() {
    return {
      loading: false,
      tableList: [],
      multipleSelectionIds: [],
      searchForm: {
        title: '',
        status: null,
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getProductPage(this.searchForm).then(response => {
        this.tableList = response.data.list || []
        this.searchForm.total = response.data.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    resetSearch() {
      this.searchForm.title = ''
      this.searchForm.status = null
      this.searchForm.pageNum = 1
      this.getList()
    },
    handleSelectionChange(val) {
      this.multipleSelectionIds = val.map(item => item.productId)
    },
    handleView(row) {
      window.open(`/goods/${row.productId}`, '_blank')
    },
    handleOffline(row) {
      this.$confirm('确定要下架该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        offlineProduct(row.productId).then(() => {
          this.$message.success('下架成功')
          this.getList()
        })
      })
    },
    handleOnline(row) {
      this.$confirm('确定要上架该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        onlineProduct(row.productId).then(() => {
          this.$message.success('上架成功')
          this.getList()
        })
      })
    },
    handleDelete(ids) {
      if (!ids || ids.length === 0) {
        return
      }
      this.$confirm('确定要删除选中的商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteProduct(ids[0]).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.product-container {
  padding: 20px;
}
.btn-div {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}
</style>
