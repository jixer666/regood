<template>
  <div class="dashboard-editor-container">
    <panel-group :statistics="statistics" />

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="productTrendData" title="商品发布趋势" />
    </el-row>

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <pie-chart :data="categoryDistribution" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <line-chart :chart-data="orderTrendData" title="订单趋势" />
        </div>
      </el-col>
    </el-row>

    
  </div>
</template>

<script>
import PanelGroup from './components/PanelGroup'
import LineChart from './components/LineChart'
import PieChart from './components/PieChart'
import { getDashboardStatistics } from '@/api/business/statistics'

export default {
  name: 'DashboardAdmin',
  components: {
    PanelGroup,
    LineChart,
    PieChart,
  },
  data() {
    return {
      statistics: {
        productCount: 0,
        pendingAuditCount: 0,
        orderCount: 0,
        userCount: 0,
        todayProductCount: 0,
        todayOrderCount: 0
      },
      productTrendData: {
        dates: [],
        values: []
      },
      orderTrendData: {
        dates: [],
        values: []
      },
      categoryDistribution: []
    }
  },
  created() {
    this.fetchStatistics()
  },
  methods: {
    async fetchStatistics() {
      try {
        const res = await getDashboardStatistics()
        if (res.code === 200 && res.data) {
          const data = res.data
          this.statistics = {
            productCount: Number(data.productCount) || 0,
            pendingAuditCount: Number(data.pendingAuditCount) || 0,
            orderCount: Number(data.orderCount) || 0,
            userCount: Number(data.userCount) || 0,
            todayProductCount: Number(data.todayProductCount) || 0,
            todayOrderCount: Number(data.todayOrderCount) || 0
          }
          if (data.productTrend && data.productTrend.length > 0) {
            this.productTrendData = {
              dates: data.productTrend.map(item => String(item.date)),
              values: data.productTrend.map(item => Number(item.count) || 0)
            }
          }
          if (data.orderTrend && data.orderTrend.length > 0) {
            this.orderTrendData = {
              dates: data.orderTrend.map(item => String(item.date)),
              values: data.orderTrend.map(item => Number(item.count) || 0)
            }
          }
          if (data.categoryDistribution && data.categoryDistribution.length > 0) {
            this.categoryDistribution = data.categoryDistribution.map(item => ({
              name: String(item.name),
              value: Number(item.value) || 0
            }))
          }
        }
      } catch (error) {
        console.error('获取统计数据失败', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
