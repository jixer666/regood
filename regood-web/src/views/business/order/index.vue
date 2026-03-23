<template>
  <div class="order-container">
    <div>
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号"></el-input>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待支付" :value="1"></el-option>
            <el-option label="已支付" :value="2"></el-option>
            <el-option label="已发货" :value="3"></el-option>
            <el-option label="已完成" :value="4"></el-option>
            <el-option label="已取消" :value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList" size="medium">搜索</el-button>
          <el-button @click="resetSearch" size="medium">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-table v-loading="loading" :data="tableList">
        <el-table-column label="订单编号" align="center" key="orderNo" prop="orderNo" :show-overflow-tooltip="true" width="180" />
        <el-table-column label="商品信息" align="center" key="productName" width="200">
          <template slot-scope="scope">
            <div>{{ scope.row.productName }}</div>
            <div class="text-muted">x{{ scope.row.productCount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="买家" align="center" key="buyerNickname" prop="buyerNickname" width="120" />
        <el-table-column label="卖家" align="center" key="sellerNickname" prop="sellerNickname" width="120" />
        <el-table-column label="订单金额" align="center" key="totalPrice" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column label="实付金额" align="center" key="payPrice" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.payPrice }}
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" key="status" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
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
import { getOrderPage } from '@/api/business/order'

export default {
  name: 'Order',
  data() {
    return {
      loading: false,
      tableList: [],
      searchForm: {
        orderNo: '',
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
      getOrderPage(this.searchForm).then(response => {
        this.tableList = response.data.list || []
        this.searchForm.total = response.data.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    resetSearch() {
      this.searchForm = {
        orderNo: '',
        status: null,
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
      this.getList()
    },
    getStatusText(status) {
      const statusMap = {
        1: '待支付',
        2: '已支付',
        3: '已发货',
        4: '已完成',
        5: '已取消'
      }
      return statusMap[status] || '未知'
    },
    getStatusType(status) {
      const typeMap = {
        1: 'warning',
        2: 'primary',
        3: 'info',
        4: 'success',
        5: 'danger'
      }
      return typeMap[status] || 'info'
    },
    handleView(row) {
      this.$message.info('订单详情功能开发中')
    }
  }
}
</script>

<style scoped>
.order-container {
  padding: 20px;
}
.text-muted {
  color: #999;
  font-size: 12px;
}
</style>
