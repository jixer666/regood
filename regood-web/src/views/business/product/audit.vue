<template>
  <div class="audit-container">
    <div>
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.title" placeholder="请输入商品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="卖家">
          <el-input v-model="searchForm.sellerName" placeholder="请输入卖家名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList" size="medium">搜索</el-button>
          <el-button @click="resetSearch" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-table v-loading="loading" :data="tableList">
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
        <el-table-column label="分类" align="center" prop="categoryName" width="100" />
        <el-table-column label="卖家" align="center" prop="sellerName" width="100" />
        <el-table-column label="发布时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="text" @click="handleApprove(scope.row)" v-if="scope.row.auditStatus === 0">通过</el-button>
            <el-button size="mini" type="text" @click="handleReject(scope.row)" v-if="scope.row.auditStatus === 0">拒绝</el-button>
            <el-tag v-if="scope.row.auditStatus === 1" type="success" size="small">已通过</el-tag>
            <el-tag v-if="scope.row.auditStatus === 2" type="danger" size="small">已拒绝</el-tag>
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

    <el-dialog title="商品详情" :visible.sync="detailVisible" width="700px" append-to-body>
      <div v-if="currentProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-carousel height="300px" v-if="currentProduct.images && currentProduct.images.length > 0">
              <el-carousel-item v-for="(img, index) in currentProduct.images" :key="index">
                <el-image :src="img" fit="cover" style="width: 100%; height: 100%"></el-image>
              </el-carousel-item>
            </el-carousel>
            <div v-else class="no-image">暂无图片</div>
          </el-col>
          <el-col :span="14">
            <el-form label-width="80px">
              <el-form-item label="商品名称">
                <span>{{ currentProduct.title }}</span>
              </el-form-item>
              <el-form-item label="商品描述">
                <span>{{ currentProduct.description || '暂无描述' }}</span>
              </el-form-item>
              <el-form-item label="价格">
                <span class="price">¥{{ currentProduct.price }}</span>
              </el-form-item>
              <el-form-item label="原价">
                <span>¥{{ currentProduct.originalPrice }}</span>
              </el-form-item>
              <el-form-item label="商品分类">
                <span>{{ currentProduct.categoryName }}</span>
              </el-form-item>
              <el-form-item label="卖家">
                <span>{{ currentProduct.sellerName }}</span>
              </el-form-item>
              <el-form-item label="发布时间">
                <span>{{ currentProduct.createTime }}</span>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
        <el-button type="primary" @click="handleApprove(currentProduct)" v-if="currentProduct && currentProduct.auditStatus === 0">通过审核</el-button>
        <el-button type="danger" @click="handleReject(currentProduct)" v-if="currentProduct && currentProduct.auditStatus === 0">拒绝审核</el-button>
      </div>
    </el-dialog>

    <el-dialog title="拒绝原因" :visible.sync="rejectVisible" width="500px" append-to-body>
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectForm">
        <el-form-item label="拒绝原因" prop="rejectReason">
          <el-input v-model="rejectForm.rejectReason" type="textarea" :rows="4" placeholder="请输入拒绝原因"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rejectVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReject">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPendingAuditList, approveProduct, rejectProduct } from '@/api/business/product'

export default {
  name: 'ProductAudit',
  data() {
    return {
      loading: false,
      tableList: [],
      searchForm: {
        title: '',
        sellerName: '',
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      detailVisible: false,
      currentProduct: null,
      rejectVisible: false,
      rejectForm: {
        productId: null,
        rejectReason: ''
      },
      rejectRules: {
        rejectReason: [
          { required: true, message: '请输入拒绝原因', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getPendingAuditList(this.searchForm).then(response => {
        this.tableList = response.data.list || []
        this.searchForm.total = response.data.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    resetSearch() {
      this.searchForm.title = ''
      this.searchForm.sellerName = ''
      this.searchForm.pageNum = 1
      this.getList()
    },
    handleView(row) {
      this.currentProduct = row
      this.detailVisible = true
    },
    handleApprove(row) {
      this.$confirm('确定通过该商品的审核吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        approveProduct(row.productId).then(() => {
          this.$message.success('审核通过')
          this.detailVisible = false
          this.getList()
        })
      })
    },
    handleReject(row) {
      this.rejectForm.productId = row.productId
      this.rejectForm.rejectReason = ''
      this.detailVisible = false
      this.rejectVisible = true
    },
    submitReject() {
      this.$refs.rejectForm.validate(valid => {
        if (valid) {
          rejectProduct(this.rejectForm.productId, { rejectReason: this.rejectForm.rejectReason }).then(() => {
            this.$message.success('已拒绝')
            this.rejectVisible = false
            this.getList()
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.audit-container {
  padding: 20px;
}
.btn-div {
  margin-bottom: 15px;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  background: #f5f7fa;
  color: #909399;
}
.product-detail {
  padding: 10px;
}
</style>
