<template>
  <div class="xyh-home">
    <xyh-header />
    
    <div class="home-container">
      <div class="home-wrapper">
        <!-- 左侧分类树 -->
        <div class="category-sidebar">
          <div class="category-title">
            <i class="el-icon-menu"></i>
            <span>商品分类</span>
          </div>
          <el-menu
            :default-active="activeCategory"
            class="category-menu"
            @select="handleCategorySelect"
            :collapse="false"
          >
            <el-menu-item
              v-for="category in categories"
              :key="category.id"
              :index="category.id"
            >
              <span class="category-icon">{{ category.icon }}</span>
              <span class="category-name">{{ category.name }}</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧内容区 -->
        <div class="content-area">
          <!-- Banner 轮播图 -->
          <div class="banner-section">
            <el-carousel height="320px" :autoplay="true" :interval="5000">
              <el-carousel-item v-for="banner in banners" :key="banner.id">
                <div class="banner-item" :style="{ background: banner.gradient }">
                  <div class="banner-content">
                    <h2 class="banner-title">{{ banner.title }}</h2>
                    <p class="banner-desc">{{ banner.description }}</p>
                    <el-button type="primary" class="banner-btn">
                      {{ banner.buttonText }}
                    </el-button>
                  </div>
                  <div class="banner-image">{{ banner.emoji }}</div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>

          <!-- 筛选栏 -->
          <div class="filter-bar">
            <div class="filter-left">
              <el-radio-group v-model="filterType" size="small">
                <el-radio-button label="all">全部</el-radio-button>
                <el-radio-button label="new">最新发布</el-radio-button>
                <el-radio-button label="hot">最热</el-radio-button>
                <el-radio-button label="sale">特价</el-radio-button>
              </el-radio-group>
            </div>
            <div class="filter-right">
              <el-select v-model="sortType" size="small" placeholder="排序" @change="handleSort">
                <el-option label="综合排序" value="default"></el-option>
                <el-option label="价格从低到高" value="price_asc"></el-option>
                <el-option label="价格从高到低" value="price_desc"></el-option>
                <el-option label="最新发布" value="newest"></el-option>
              </el-select>
            </div>
          </div>

          <!-- 商品瀑布流 -->
          <div class="goods-waterfall">
            <div
              v-for="goods in goodsList"
              :key="goods.id"
              class="goods-card"
              @click="goToDetail(goods.id)"
            >
              <!-- 商品图片 -->
              <div class="goods-image-wrapper">
                <el-image
                  :src="goods.image"
                  class="goods-image"
                  fit="cover"
                  :lazy="true"
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <!-- 已售出遮罩 -->
                <div v-if="goods.status === 'sold'" class="sold-overlay">
                  <span>已售出</span>
                </div>
                <!-- 成色标签 -->
                <div class="condition-tag">{{ goods.condition }}</div>
              </div>

              <!-- 商品信息 -->
              <div class="goods-info">
                <h3 class="goods-title" :title="goods.title">{{ goods.title }}</h3>
                <div class="goods-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ goods.price }}</span>
                  <span v-if="goods.originalPrice" class="original-price">
                    ¥{{ goods.originalPrice }}
                  </span>
                </div>
                <div class="goods-meta">
                  <div class="seller-info">
                    <el-avatar :src="goods.sellerAvatar" :size="20" class="seller-avatar"></el-avatar>
                    <span class="seller-name">{{ goods.sellerName }}</span>
                  </div>
                  <div class="sales-info">
                    <span>{{ goods.sales }}人想要</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-wrapper">
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>

    <xyh-footer />
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'
import XyhFooter from '@/components/XyhFooter'

export default {
  name: 'XyhHome',
  components: {
    XyhHeader,
    XyhFooter
  },
  data() {
    return {
      activeCategory: 'all',
      filterType: 'all',
      sortType: 'default',
      currentPage: 1,
      pageSize: 20,
      total: 100,
      categories: [
        {
          id: 'all',
          name: '全部分类',
          icon: '📦',
          children: []
        },
        {
          id: 'books',
          name: '书籍教材',
          icon: '📚',
          children: [
            { id: 'textbook', name: '教材' },
            { id: 'novel', name: '小说' },
            { id: 'exam', name: '考试资料' }
          ]
        },
        {
          id: 'digital',
          name: '数码电子',
          icon: '📱',
          children: [
            { id: 'phone', name: '手机' },
            { id: 'computer', name: '电脑' },
            { id: 'accessories', name: '配件' }
          ]
        },
        {
          id: 'transport',
          name: '生活代步',
          icon: '🚲',
          children: [
            { id: 'bike', name: '自行车' },
            { id: 'ebike', name: '电动车' }
          ]
        },
        {
          id: 'clothing',
          name: '衣物鞋帽',
          icon: '👕',
          children: []
        },
        {
          id: 'beauty',
          name: '美妆日用',
          icon: '💄',
          children: []
        },
        {
          id: 'sports',
          name: '运动户外',
          icon: '⚽',
          children: []
        },
        {
          id: 'other',
          name: '其他物品',
          icon: '🎁',
          children: []
        }
      ],
      banners: [
        {
          id: 1,
          title: '毕业季清仓大促',
          description: '学长学姐离校甩卖，低至 1 折起',
          buttonText: '立即抢购',
          emoji: '🎓',
          gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        },
        {
          id: 2,
          title: '新生开学特惠',
          description: '必备好物推荐，新生专享优惠',
          buttonText: '查看详情',
          emoji: '🎒',
          gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
        },
        {
          id: 3,
          title: '期末复习资料专场',
          description: '高分笔记、历年真题，助你顺利通关',
          buttonText: '去看看',
          emoji: '📖',
          gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
        }
      ],
      goodsList: []
    }
  },
  created() {
    this.loadGoodsList()
    if (this.$route.query.keyword) {
      this.searchKeyword = this.$route.query.keyword
    }
  },
  methods: {
    handleCategorySelect(categoryId) {
      this.activeCategory = categoryId
      this.currentPage = 1
      this.loadGoodsList()
    },
    handleSort() {
      this.currentPage = 1
      this.loadGoodsList()
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadGoodsList()
      window.scrollTo(0, 0)
    },
    loadGoodsList() {
      // 模拟商品数据
      this.goodsList = Array.from({ length: this.pageSize }, (_, index) => ({
        id: index + 1,
        title: [
          'iPhone 13 Pro 256G 远峰蓝 95 新',
          '高等数学（第七版）上下册 同济版',
          '捷安特山地自行车 9 成新 骑了一年',
          'Nike Air Force 1 白鞋 42 码 几乎全新',
          'iPad Pro 2021 11 寸 128G WiFi 版',
          '小米台灯 1S 护眼学习工作阅读',
          '考研英语真题详解 2024 版',
          '罗技 MX Master 3 无线鼠标',
          'Sony WH-1000XM4 降噪耳机',
          '人体工学椅 电脑椅 办公椅'
        ][index % 10],
        image: [
          'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400',
          'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400',
          'https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=400',
          'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400',
          'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400',
          'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400',
          'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400',
          'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400',
          'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400',
          'https://images.unsplash.com/photo-1505843490538-5133c6c7d0e1?w=400'
        ][index % 10],
        price: (Math.random() * 1000 + 50).toFixed(2),
        originalPrice: (Math.random() * 2000 + 500).toFixed(2),
        condition: ['全新', '99 新', '9 成新', '8 成新'][Math.floor(Math.random() * 4)],
        sellerName: `用户${Math.floor(Math.random() * 1000)}`,
        sellerAvatar: `https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png`,
        sales: Math.floor(Math.random() * 100),
        status: Math.random() > 0.9 ? 'sold' : 'onsale'
      }))
    },
    goToDetail(id) {
      this.$router.push(`/goods/${id}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-home {
  min-height: 100vh;
  background: #f5f7fa;
}

.home-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.home-wrapper {
  display: flex;
  gap: 20px;
}

.category-sidebar {
  width: 220px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.category-title {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #e8e8e8;
}

.category-menu {
  border-right: none;
  
  .el-menu-item {
    height: 50px;
    line-height: 50px;
    position: relative;
    
    &:hover {
      background: #f5f7fa;
    }
    
    &.is-active {
      background: #e6f7ff;
      color: #1890ff;
    }
    
    .category-icon {
      font-size: 18px;
      margin-right: 8px;
    }
    
    .category-name {
      flex: 1;
    }
  }
}

.content-area {
  flex: 1;
  min-width: 0;
}

.banner-section {
  margin-bottom: 20px;
  
  .banner-item {
    height: 320px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 40px 60px;
    border-radius: 8px;
    overflow: hidden;
    
    .banner-content {
      flex: 1;
      color: #fff;
      
      .banner-title {
        font-size: 36px;
        font-weight: 700;
        margin-bottom: 12px;
      }
      
      .banner-desc {
        font-size: 18px;
        opacity: 0.9;
        margin-bottom: 24px;
      }
      
      .banner-btn {
        background: #fff;
        color: #333;
        border: none;
        padding: 12px 32px;
        font-weight: 600;
        
        &:hover {
          background: #f5f5f5;
        }
      }
    }
    
    .banner-image {
      font-size: 120px;
      opacity: 0.8;
    }
  }
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.goods-waterfall {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.goods-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    transform: translateY(-4px);
  }
  
  .goods-image-wrapper {
    position: relative;
    height: 220px;
    overflow: hidden;
    
    .goods-image {
      width: 100%;
      height: 100%;
    }
    
    .image-slot {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      height: 100%;
      background: #f5f7fa;
      color: #999;
      font-size: 40px;
    }
    
    .sold-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;
      
      span {
        color: #fff;
        font-size: 24px;
        font-weight: 600;
      }
    }
    
    .condition-tag {
      position: absolute;
      top: 10px;
      left: 10px;
      padding: 4px 8px;
      background: rgba(0, 0, 0, 0.6);
      color: #fff;
      font-size: 12px;
      border-radius: 4px;
    }
  }
  
  .goods-info {
    padding: 12px;
    
    .goods-title {
      font-size: 14px;
      color: #333;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      line-height: 1.5;
      height: 42px;
    }
    
    .goods-price {
      display: flex;
      align-items: baseline;
      gap: 8px;
      margin-bottom: 8px;
      
      .price-symbol {
        color: #f5222d;
        font-size: 14px;
      }
      
      .price-value {
        color: #f5222d;
        font-size: 22px;
        font-weight: 600;
      }
      
      .original-price {
        color: #999;
        font-size: 13px;
        text-decoration: line-through;
      }
    }
    
    .goods-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 12px;
      color: #999;
      
      .seller-info {
        display: flex;
        align-items: center;
        gap: 6px;
        
        .seller-avatar {
          border: 1px solid #e8e8e8;
        }
        
        .seller-name {
          max-width: 80px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .home-wrapper {
    flex-direction: column;
  }
  
  .category-sidebar {
    width: 100%;
  }
  
  .banner-section .banner-item {
    padding: 20px;
    
    .banner-content {
      .banner-title {
        font-size: 24px;
      }
    }
  }
}
</style>
