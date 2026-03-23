<template>
  <div class="cart-container">
    <div class="btn-div">
      <el-button plain size="mini" type="primary" @click="getList">刷新</el-button>
      <el-button plain size="mini" type="danger" @click="handleDelete(multipleSelectionIds)" :disabled="multipleSelectionIds.length === 0">批量删除</el-button>
    </div>
    <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="商品图片" align="center" key="productImage" width="100">
        <template slot-scope="scope">
          <el-avatar v-if="scope.row.productImage" size="medium" :src="scope.row.productImage"></el-avatar>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" key="productName" prop="productName" :show-overflow-tooltip="true" />
      <el-table-column label="商品价格" align="center" key="price" width="100">
        <template slot-scope="scope">
          ¥{{ scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column label="数量" align="center" key="count" prop="count" width="80" />
      <el-table-column label="用户" align="center" key="nickname" prop="nickname" width="120" />
      <el-table-column label="添加时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete([scope.row.cartId])">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getCartList, removeFromCart } from '@/api/business/cart'

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
      getCartList().then(response => {
        this.tableList = response.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSelectionChange(val) {
      this.multipleSelectionIds = val.map(item => item.cartId)
    },
    handleDelete(ids) {
      if (!ids || ids.length === 0) {
        return
      }
      this.$confirm('确定要删除选中的购物车记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeFromCart(ids[0]).then(() => {
          this.$message.success('删除成功')
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
