<template>
  <el-table :data="list" style="width: 100%;padding-top: 15px;" v-loading="loading">
    <el-table-column label="订单号" min-width="200">
      <template slot-scope="scope">
        {{ scope.row.orderNo }}
      </template>
    </el-table-column>
    <el-table-column label="商品" min-width="150">
      <template slot-scope="scope">
        {{ scope.row.productTitle || '-' }}
      </template>
    </el-table-column>
    <el-table-column label="金额" width="120" align="center">
      <template slot-scope="scope">
        ¥{{ scope.row.price }}
      </template>
    </el-table-column>
    <el-table-column label="状态" width="100" align="center">
      <template slot-scope="{row}">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="创建时间" width="180" align="center">
      <template slot-scope="scope">
        {{ scope.row.createTime }}
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import { getAllOrderPage } from '@/api/business/order'

const STATUS_MAP = {
  0: { text: '待支付', type: 'warning' },
  1: { text: '已支付', type: 'primary' },
  2: { text: '已发货', type: 'primary' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'info' }
}

export default {
  data() {
    return {
      list: [],
      loading: false
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getAllOrderPage({ pageNum: 1, pageSize: 10 })
        if (res.code === 200 && res.data) {
          this.list = res.data.records || res.data.list || []
        }
      } catch (error) {
        console.error('获取订单列表失败', error)
      } finally {
        this.loading = false
      }
    },
    getStatusText(status) {
      return STATUS_MAP[status]?.text || '未知'
    },
    getStatusType(status) {
      return STATUS_MAP[status]?.type || 'info'
    }
  }
}
</script>
