<template>
  <div class="xyh-home">
    <xyh-header />

    <div class="home-main">
      <div class="category-tabs">
        <div class="tabs-wrapper">
          <div
            v-for="category in categories"
            :key="category.categoryId"
            :class="['tab-item', { active: activeCategory === category.categoryId }]"
            @click="handleCategorySelect(category.categoryId)"
          >
            <span class="tab-icon">{{ category.icon }}</span>
            <span class="tab-name">{{ category.name }}</span>
          </div>
        </div>
      </div>

      <div class="filter-section">
        <div class="filter-tags">
          <span
            v-for="tag in filterTags"
            :key="tag.value"
            :class="['filter-tag', { active: filterType === tag.value }]"
            @click="handleFilterChange(tag.value)"
          >
            {{ tag.label }}
          </span>
        </div>
        <div class="sort-options">
          <el-select v-model="sortType" size="small" placeholder="排序" @change="handleSort">
            <el-option label="综合排序" value="default"></el-option>
            <el-option label="价格从低到高" value="price_asc"></el-option>
            <el-option label="价格从高到低" value="price_desc"></el-option>
            <el-option label="最新发布" value="newest"></el-option>
          </el-select>
        </div>
      </div>

      <div class="goods-waterfall" ref="waterfallRef">
        <div
          v-for="goods in goodsList"
          :key="goods.id"
          class="goods-card"
          @click="goToDetail(goods.id)"
        >
          <div class="goods-image-wrapper">
            <el-image
              :src="goods.image"
              class="goods-image"
              fit="cover"
              :lazy="true"
            >
              <div slot="error" class="image-placeholder">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div v-if="goods.status === 'sold'" class="sold-mask">
              <span>已售出</span>
            </div>
            <div class="condition-badge">{{ goods.condition }}</div>
          </div>

          <div class="goods-content">
            <h3 class="goods-title">{{ goods.title }}</h3>
            <div class="goods-desc" v-if="goods.description">{{ goods.description }}</div>
            <div class="goods-price-row">
              <div class="price-info">
                <span class="price-symbol">¥</span>
                <span class="price-num">{{ goods.price }}</span>
              </div>
              <span v-if="goods.originalPrice" class="original-price">¥{{ goods.originalPrice }}</span>
            </div>
            <div class="goods-footer">
              <div class="seller-info">
                <el-avatar :src="goods.sellerAvatar" :size="24" class="seller-avatar"></el-avatar>
                <span class="seller-name">{{ goods.sellerName }}</span>
              </div>
              <div class="location-info" v-if="goods.location">
                <i class="el-icon-location-outline"></i>
                <span>{{ goods.location }}</span>
              </div>
            </div>
            <div class="want-btn" @click.stop="handleWant(goods)">
              <span>{{ goods.wantCount || 0 }}想要</span>
            </div>
          </div>
        </div>
      </div>

      <div class="load-more" v-if="hasMore" @click="loadMore">
        <span v-if="!loading">加载更多</span>
        <span v-else>加载中...</span>
      </div>

      <div class="no-more" v-if="!hasMore && goodsList.length > 0">
        <span>没有更多了</span>
      </div>

      <div class="empty-state" v-if="goodsList.length === 0 && !loading">
        <i class="el-icon-box"></i>
        <p>暂无商品</p>
      </div>
    </div>

    <xyh-footer />

    <div class="publish-btn" @click="goToPublish">
      <i class="el-icon-plus"></i>
      <span>发布</span>
    </div>
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'
import XyhFooter from '@/components/XyhFooter'
import { getProductPage, getRecommendProducts, addFavorite } from '@/api/business/product'
import { getCategoryTree } from '@/api/business/category'

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
      searchKeyword: '',
      currentPage: 1,
      pageSize: 20,
      total: 0,
      hasMore: true,
      loading: false,
      categories: [
        { categoryId: 'all', name: '推荐', icon: '🔥' },
        { categoryId: 'books', name: '书籍', icon: '📚' },
        { categoryId: 'digital', name: '数码', icon: '📱' },
        { categoryId: 'transport', name: '代步', icon: '🚲' },
        { categoryId: 'clothing', name: '服饰', icon: '👕' },
        { categoryId: 'beauty', name: '美妆', icon: '💄' },
        { categoryId: 'sports', name: '运动', icon: '⚽' },
        { categoryId: 'furniture', name: '家居', icon: '🏠' },
        { categoryId: 'ticket', name: '票券', icon: '🎫' },
        { categoryId: 'game', name: '游戏', icon: '🎮' },
        { categoryId: 'other', name: '其他', icon: '🎁' }
      ],
      filterTags: [
        { label: '全部', value: 'all' },
        { label: '在售', value: 'onsale' },
        { label: '已卖出', value: 'sold' },
        { label: '包邮', value: 'freeShipping' }
      ],
      goodsList: []
    }
  },
  watch: {
    '$route.query': {
      handler(newQuery, oldQuery) {
        if (newQuery.t && newQuery.t !== oldQuery.t) {
          this.searchKeyword = newQuery.keyword || ''
          this.activeCategory = (newQuery.type && newQuery.type !== 'all') ? newQuery.type : 'all'
          this.currentPage = 1
          this.goodsList = []
          this.hasMore = true
          this.loadGoodsList()
        }
      },
      immediate: false
    }
  },
  created() {
    this.initSearchFromQuery()
    this.loadCategoryTree()
    this.loadGoodsList()
  },
  methods: {
    initSearchFromQuery() {
      const { keyword, type } = this.$route.query
      if (keyword) {
        this.searchKeyword = keyword
      }
      if (type && type !== 'all') {
        this.activeCategory = type
      }
    },
    async loadCategoryTree() {
      try {
        const res = await getCategoryTree()
        if (res.code === 200 && res.data) {
          const backendCategories = res.data.map(item => ({
            categoryId: item.categoryId,
            name: item.name,
            icon: item.icon || '📦'
          }))
          this.categories = [
            { categoryId: 'all', name: '推荐', icon: '🔥' },
            ...backendCategories
          ]
        }
      } catch (error) {
        console.error('加载分类失败', error)
      }
    },
    handleCategorySelect(categoryId) {
      this.activeCategory = categoryId
      this.currentPage = 1
      this.goodsList = []
      this.hasMore = true
      this.loadGoodsList()
    },
    handleFilterChange(value) {
      this.filterType = value
      this.currentPage = 1
      this.goodsList = []
      this.hasMore = true
      this.loadGoodsList()
    },
    handleSort() {
      this.currentPage = 1
      this.goodsList = []
      this.hasMore = true
      this.loadGoodsList()
    },
    async loadGoodsList() {
      this.loading = true
      try {
        const query = {
          pageNum: this.currentPage,
          pageSize: this.pageSize
        }
        if (this.filterType === 'all') {
          query.statusList = '1,2'
        } else if (this.filterType === 'onsale') {
          query.status = 1
        } else if (this.filterType === 'sold') {
          query.status = 2
        } else if (this.filterType === 'freeShipping') {
          query.status = 1
          query.freeShipping = true
        }
        if (this.activeCategory !== 'all') {
          query.categoryIdForQuery = this.activeCategory
        }
        if (this.searchKeyword && this.searchKeyword.trim()) {
          query.title = this.searchKeyword.trim()
        }
        if (this.sortType === 'price_asc') {
          query.sortType = 'price_asc'
        } else if (this.sortType === 'price_desc') {
          query.sortType = 'price_desc'
        } else if (this.sortType === 'newest') {
          query.sortType = 'newest'
        }
        const res = await getProductPage(query)
        if (res.code === 200) {
          const newList = (res.data.list || []).map(item => this.convertToGoodsItem(item))
          if (this.currentPage === 1) {
            this.goodsList = newList
          } else {
            this.goodsList = [...this.goodsList, ...newList]
          }
          this.total = res.data.total || 0
          this.hasMore = this.goodsList.length < this.total
        }
      } catch (error) {
        console.error('加载商品失败', error)
        this.$message.error('加载商品失败')
      } finally {
        this.loading = false
      }
    },
    convertToGoodsItem(item) {
      const images = item.images && item.images.length > 0 ? item.images : ['https://picsum.photos/400/400?random=default']
      return {
        id: item.productId,
        productId: item.productId,
        title: item.title,
        description: item.description,
        image: images[0],
        images: images,
        price: item.price,
        originalPrice: item.originalPrice,
        condition: item.condition,
        sellerName: item.sellerName,
        sellerAvatar: item.sellerAvatar,
        location: item.meetingPlace,
        wantCount: item.wantCount || 0,
        status: item.status === 1 ? 'onsale' : (item.status === 2 ? 'sold' : 'offline'),
        freeShipping: item.freeShipping,
        shippingFee: item.shippingFee,
        categoryId: item.categoryId,
        categoryName: item.categoryName,
        tradeMethods: item.tradeMethods,
        sellerId: item.sellerId
      }
    },
    loadMore() {
      if (this.loading || !this.hasMore) return
      this.currentPage++
      this.loadGoodsList()
    },
    goToDetail(id) {
      this.$router.push(`/goods/${id}`)
    },
    goToPublish() {
      this.$router.push('/publish')
    },
    async handleWant(goods) {
      try {
        await addFavorite(goods.productId)
        goods.wantCount = (goods.wantCount || 0) + 1
        this.$message.success('已添加到想要列表')
      } catch (error) {
        console.error('添加想要失败', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-home {
  min-height: 100vh;
  background: #f5f5f5;
}

.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 16px 80px;
}

.category-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 12px 0;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .tabs-wrapper {
    display: flex;
    overflow-x: auto;
    padding: 0 12px;
    scrollbar-width: none;
    &::-webkit-scrollbar {
      display: none;
    }
  }

  .tab-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px 16px;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.2s;
    flex-shrink: 0;

    &:hover {
      background: #f9f9f9;
    }

    &.active {
      background: #fff5e6;

      .tab-icon {
        transform: scale(1.1);
      }

      .tab-name {
        color: #ff6b00;
        font-weight: 600;
      }
    }

    .tab-icon {
      font-size: 24px;
      margin-bottom: 4px;
      transition: transform 0.2s;
    }

    .tab-name {
      font-size: 13px;
      color: #333;
      white-space: nowrap;
    }
  }
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .filter-tags {
    display: flex;
    gap: 8px;
  }

  .filter-tag {
    padding: 6px 14px;
    font-size: 13px;
    color: #666;
    background: #f5f5f5;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: #eee;
    }

    &.active {
      background: #ff6b00;
      color: #fff;
    }
  }
}

.goods-waterfall {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;

  @media (min-width: 640px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (min-width: 900px) {
    grid-template-columns: repeat(4, 1fr);
  }

  @media (min-width: 1100px) {
    grid-template-columns: repeat(5, 1fr);
  }
}

.goods-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .goods-image-wrapper {
    position: relative;
    width: 100%;
    padding-top: 100%;
    background: #f9f9f9;

    .goods-image {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
    }

    .image-placeholder {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #ccc;
      font-size: 48px;
    }

    .sold-mask {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;

      span {
        padding: 8px 20px;
        background: rgba(0, 0, 0, 0.7);
        color: #fff;
        font-size: 14px;
        border-radius: 20px;
      }
    }

    .condition-badge {
      position: absolute;
      top: 8px;
      left: 8px;
      padding: 2px 8px;
      background: rgba(0, 0, 0, 0.6);
      color: #fff;
      font-size: 11px;
      border-radius: 4px;
    }
  }

  .goods-content {
    padding: 10px;

    .goods-title {
      font-size: 14px;
      color: #333;
      line-height: 1.4;
      margin-bottom: 6px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      font-weight: 400;
    }

    .goods-desc {
      font-size: 12px;
      color: #999;
      margin-bottom: 6px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .goods-price-row {
      display: flex;
      align-items: baseline;
      gap: 6px;
      margin-bottom: 8px;

      .price-info {
        display: flex;
        align-items: baseline;

        .price-symbol {
          color: #ff4d4f;
          font-size: 13px;
          font-weight: 500;
        }

        .price-num {
          color: #ff4d4f;
          font-size: 18px;
          font-weight: 600;
        }
      }

      .original-price {
        color: #bbb;
        font-size: 12px;
        text-decoration: line-through;
      }
    }

    .goods-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 12px;
      color: #999;
      margin-bottom: 8px;

      .seller-info {
        display: flex;
        align-items: center;
        gap: 6px;

        .seller-avatar {
          border-radius: 50%;
        }

        .seller-name {
          max-width: 70px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .location-info {
        display: flex;
        align-items: center;
        gap: 2px;

        i {
          font-size: 12px;
        }
      }
    }

    .want-btn {
      display: inline-flex;
      align-items: center;
      padding: 4px 10px;
      background: #fff5f0;
      color: #ff6b00;
      font-size: 12px;
      border-radius: 12px;
      transition: all 0.2s;

      &:hover {
        background: #ffe8db;
      }
    }
  }
}

.load-more {
  text-align: center;
  padding: 20px;
  color: #666;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    color: #ff6b00;
  }
}

.no-more {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 13px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;

  i {
    font-size: 64px;
    margin-bottom: 16px;
  }

  p {
    font-size: 14px;
  }
}

.publish-btn {
  position: fixed;
  right: 24px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(255, 107, 0, 0.4);
  transition: all 0.3s;
  z-index: 100;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 6px 20px rgba(255, 107, 0, 0.5);
  }

  i {
    font-size: 20px;
  }

  span {
    font-size: 11px;
    margin-top: 2px;
  }
}
</style>
