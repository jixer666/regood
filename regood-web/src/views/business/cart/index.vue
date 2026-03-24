<template>
  <div class="cart-container">
    <div class="btn-div">
      <el-button plain size="mini" type="primary" @click="getList">刷新</el-button>
      <el-button plain size="mini" type="danger" @click="handleBatchDelete" :disabled="multipleSelectionIds.length === 0">批量删除</el-button>
    </div>
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
      <el-table-column label="卖家" align="center" prop="sellerName" width="100" />
      <el-table-column label="买家" align="center" prop="userName" width="100" />
      <el-table-column label="添加时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row.cartId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getAllCartList, removeFromCart, batchRemoveCart } from '@/api/business/cart'

export default {
  name: 'Cart',
  data() {
    return {
      loading: false,
      tableList: [],
      multipleSelectionIds: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getAllCartList().then(response => {
        this.tableList = response.data.list || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSelectionChange(val) {
      this.multipleSelectionIds = val.map(item => item.cartId)
    },
    handleDelete(cartId) {
      this.$confirm('确定要删除该购物车记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeFromCart(cartId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    handleBatchDelete() {
      this.$confirm('确定要删除选中的购物车记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        batchRemoveCart(this.multipleSelectionIds).then(() => {
          this.$message.success('批量删除成功')
          this.getList()
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.cart-container {
  padding: 20px;
}
.btn-div {
  margin-bottom: 15px;
}
</style>
