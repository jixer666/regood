<template>
  <div class="goods-detail-page">
    <xyh-header />

    <div class="detail-container">
      <div class="detail-main">
        <!-- 左侧图片区 -->
        <div class="left-section">
          <div class="image-container">
            <div class="main-image">
              <el-image
                :src="currentImage"
                fit="contain"
                :preview-src-list="imageList"
                :initial-index="activeIndex"
              >
                <div slot="error" class="image-error">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>
            <div class="image-nav">
              <div class="nav-btn prev" @click="prevImage"><i class="el-icon-arrow-left"></i></div>
              <div class="nav-btn next" @click="nextImage"><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
          <div class="thumbnail-list">
            <div
              v-for="(image, index) in goods.images"
              :key="index"
              :class="['thumbnail-item', { active: activeIndex === index }]"
              @click="activeIndex = index"
            >
              <el-image
                :src="typeof image === 'string' ? image : image.url"
                fit="cover"
              />
            </div>
          </div>
        </div>

        <!-- 右侧信息区 -->
        <div class="right-section">
          <!-- 价格信息 -->
          <div class="price-section">
            <div class="price-main">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ goods.price }}</span>
            </div>
            <div class="price-info">
              <span class="price-unit">{{ goods.priceUnit }}</span>
              <span class="price-desc">{{ goods.priceDesc }}</span>
            </div>
          </div>

          <!-- 商品标题 -->
          <div class="goods-title">
            {{ goods.title }}
          </div>

          <!-- 商品描述 -->
          <div class="goods-desc">
            {{ goods.description }}
          </div>

          <!-- 商品属性 -->
          <div class="goods-attrs">
            <div v-for="(attr, index) in goods.attrs" :key="index" class="attr-item">
              <span class="attr-label">{{ attr.label }}:</span>
              <span class="attr-value">{{ attr.value }}</span>
            </div>
          </div>

          <!-- 卖家信息 -->
          <div class="seller-section">
            <div class="seller-header">
              <div class="seller-avatar">
                <el-avatar :src="goods.sellerAvatar" :size="48"></el-avatar>
              </div>
              <div class="seller-info">
                <div class="seller-name">{{ goods.sellerName }}</div>
                <div class="seller-stats">
                  <span class="stat-item">{{ goods.sellerStats.join(' | ') }}</span>
                </div>
              </div>
              <div class="seller-badge">
                <span class="badge-text">认证商家</span>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <div class="action-btn chat-btn" @click="startChat">
              <i class="el-icon-chat-dot-round"></i>
              <span>聊一聊</span>
            </div>
            <div class="action-btn buy-btn" @click="buyNow">
              <span>立即购买</span>
            </div>
            <div class="action-btn favorite-btn" @click="handleFavorite">
              <i :class="['el-icon-star-off', { 'is-favorite': isFavorite }]"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- 猜你喜欢 -->
      <div class="recommend-section">
        <div class="section-title">
          猜你喜欢
        </div>
        <div class="recommend-list">
          <div
            v-for="item in recommendList"
            :key="item.id"
            class="recommend-item"
            @click="goToDetail(item.id)"
          >
            <el-image :src="item.image" class="recommend-image" fit="cover">
              <div slot="error" class="image-error-small">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div class="recommend-info">
              <div class="recommend-title">{{ item.title }}</div>
              <div class="recommend-price">¥{{ item.price }}</div>
            </div>
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
import { getProductDetail, addFavorite, removeFavorite, getRecommendProducts } from '@/api/business/product'
import { addToCart } from '@/api/business/cart'

const TRADE_METHODS_MAP = {
  face: '面交',
  two_canteen: '二分食堂',
  school_express: '校内快递'
}

export default {
  name: 'GoodsDetail',
  components: {
    XyhHeader,
    XyhFooter
  },
  data() {
    return {
      activeIndex: 0,
      isFavorite: false,
      goodsId: null,
      goods: {
        id: null,
        title: '',
        description: '',
        price: 0,
        priceUnit: '',
        priceDesc: '',
        images: [],
        attrs: [],
        sellerName: '',
        sellerAvatar: '',
        sellerStats: [],
        details: []
      },
      recommendList: []
    }
  },
  created() {
    this.goodsId = this.$route.params.id || this.$route.query.id
    if (this.goodsId) {
      this.loadGoodsDetail()
    }
  },
  computed: {
    currentImage() {
      if (this.goods.images && this.goods.images.length > 0) {
        const img = this.goods.images[this.activeIndex]
        return typeof img === 'string' ? img : (img.url || '')
      }
      return ''
    },
    imageList() {
      if (this.goods.images) {
        return this.goods.images.map(img => typeof img === 'string' ? img : img.url)
      }
      return []
    }
  },
  methods: {
    async loadGoodsDetail() {
      try {
        const res = await getProductDetail(this.goodsId)
        if (res.code === 200) {
          const data = res.data
          this.goods = {
            id: data.productId,
            productId: data.productId,
            title: data.title,
            description: data.description,
            price: data.price,
            priceUnit: '',
            priceDesc: data.freeShipping ? '包邮' : '不包邮',
            images: (data.images || []).map((url, index) => ({ url, id: index })),
            attrs: this.buildAttrs(data),
            sellerName: data.sellerName,
            sellerAvatar: data.sellerAvatar ,
            sellerStats: [
              data.sellerPublishCount ? `发布${data.sellerPublishCount}件` : '',
              data.sellerSoldCount ? `卖出${data.sellerSoldCount}件` : ''
            ].filter(Boolean),
            details: data.description ? [{ title: '商品介绍', content: data.description }] : [],
            isFavorite: data.isFavorite,
            freeShipping: data.freeShipping,
            shippingFee: data.shippingFee,
            condition: data.condition,
            categoryName: data.categoryName,
            tradeMethods: data.tradeMethods,
            meetingPlace: data.meetingPlace,
            viewCount: data.viewCount,
            wantCount: data.wantCount
          }
          this.isFavorite = data.isFavorite || false
        }
      } catch (error) {
        console.error('加载商品详情失败', error)
        this.$message.error('加载商品详情失败')
      }
    },
    buildAttrs(data) {
      const attrs = []
      if (data.condition) attrs.push({ label: '成色', value: data.condition })
      if (data.categoryName) attrs.push({ label: '分类', value: data.categoryName })
      if (data.freeShipping !== undefined) attrs.push({ label: '配送', value: data.freeShipping ? '包邮' : '自提/不包邮' })
      if (data.tradeMethods) {
        const methods = typeof data.tradeMethods === 'string'
          ? data.tradeMethods.split(',')
          : data.tradeMethods
        if (methods.length > 0) {
          const chineseMethods = methods.map(m => TRADE_METHODS_MAP[m] || m).join('、')
          attrs.push({ label: '交易方式', value: chineseMethods })
        }
      }
      return attrs
    },
    async loadRecommendProducts() {
      try {
        const res = await getRecommendProducts()
        if (res.code === 200) {
          this.recommendList = (res.data || []).slice(0, 5).map(item => ({
            id: item.productId,
            productId: item.productId,
            title: item.title,
            price: item.price,
            image: (item.images && item.images.length > 0) ? item.images[0] : 'https://picsum.photos/200/200?random=default'
          }))
        }
      } catch (error) {
        console.error('加载推荐失败', error)
      }
    },
    prevImage() {
      if (this.activeIndex > 0) {
        this.activeIndex--
      }
    },
    nextImage() {
      if (this.goods.images && this.activeIndex < this.goods.images.length - 1) {
        this.activeIndex++
      }
    },
    async handleFavorite() {
      try {
        if (this.isFavorite) {
          await removeFavorite(this.goodsId)
          this.isFavorite = false
          this.$message.success('已取消收藏')
        } else {
          await addFavorite(this.goodsId)
          this.isFavorite = true
          this.$message.success('已收藏')
        }
      } catch (error) {
        console.error('收藏操作失败', error)
      }
    },
    startChat() {
      this.$router.push({ path: '/message', query: { goodsId: this.goodsId } })
    },
    async buyNow() {
      try {
        await addToCart({ productId: this.goodsId })
        this.$message.success('已加入购物车')
        this.$router.push('/cart')
      } catch (error) {
        console.error('加入购物车失败', error)
      }
    },
    goToDetail(id) {
      this.$router.push(`/goods/${id}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.goods-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-main {
  display: flex;
  gap: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.left-section {
  width: 480px;
  flex-shrink: 0;
}

.image-container {
  position: relative;
  width: 480px;
  height: 480px;
  background: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;

  .main-image {
    width: 100%;
    height: 100%;
  }

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    font-size: 64px;
    color: #ccc;
  }

  .image-nav {
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    transform: translateY(-50%);
    display: flex;
    justify-content: space-between;
    padding: 0 10px;

    .nav-btn {
      width: 36px;
      height: 36px;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: rgba(0, 0, 0, 0.7);
      }
    }
  }
}

.thumbnail-list {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;

  &::-webkit-scrollbar {
    height: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #ddd;
    border-radius: 3px;
  }

  .thumbnail-item {
    width: 80px;
    height: 80px;
    border-radius: 6px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.3s;

    &:hover {
      border-color: #ff6b00;
    }

    &.active {
      border-color: #ff6b00;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

.right-section {
  flex: 1;
  min-width: 0;
}

.price-section {
  margin-bottom: 20px;

  .price-main {
    display: flex;
    align-items: baseline;
    margin-bottom: 8px;

    .price-symbol {
      font-size: 20px;
      color: #ff4d4f;
      font-weight: 500;
    }

    .price-value {
      font-size: 40px;
      font-weight: 700;
      color: #ff4d4f;
    }
  }

  .price-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .price-unit {
      font-size: 14px;
      color: #ff4d4f;
      background: #fff5f5;
      padding: 2px 8px;
      border-radius: 4px;
    }

    .price-desc {
      font-size: 14px;
      color: #666;
    }
  }
}

.goods-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.5;
  margin-bottom: 12px;
}

.goods-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.goods-attrs {
  margin-bottom: 24px;

  .attr-item {
    display: flex;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;

    .attr-label {
      width: 80px;
      font-size: 14px;
      color: #999;
      flex-shrink: 0;
    }

    .attr-value {
      flex: 1;
      font-size: 14px;
      color: #333;
    }
  }
}

.action-buttons {
  display: flex;
  gap: 12px;

  .action-btn {
    flex: 1;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 500;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &.chat-btn {
      background: #f5f5f5;
      color: #333;
      border: 1px solid #d9d9d9;

      i {
        margin-right: 6px;
        font-size: 18px;
      }
    }

    &.buy-btn {
      background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
      color: #fff;
    }

    &.favorite-btn {
      width: 56px;
      flex-shrink: 0;
      background: #f5f5f5;
      color: #666;

      i {
        font-size: 20px;

        &.is-favorite {
          color: #ff6b00;
        }
      }
    }
  }
}

.seller-section {
  background: #f4f4f4;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;

  .seller-header {
    display: flex;
    align-items: center;
    gap: 16px;

    .seller-avatar {
      position: relative;
    }

    .seller-info {
      flex: 1;

      .seller-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .seller-stats {
        font-size: 12px;
        color: #999;

        .stat-item {
          &:not(:last-child) {
            margin-right: 8px;
          }
        }
      }
    }

    .seller-badge {
      padding: 4px 12px;
      background: #ff6b00;
      color: #fff;
      font-size: 12px;
      border-radius: 12px;
    }
  }
}

.detail-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid #f0f0f0;
  }

  .detail-content {
    .detail-item {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      .detail-subtitle {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 12px;
      }

      .detail-text {
        font-size: 14px;
        color: #666;
        line-height: 1.8;
      }
    }
  }
}

.recommend-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid #f0f0f0;
  }

  .recommend-list {
    display: flex;
    gap: 16px;
    overflow-x: auto;

    &::-webkit-scrollbar {
      height: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: #ddd;
      border-radius: 3px;
    }

    .recommend-item {
      flex-shrink: 0;
      width: 160px;
      cursor: pointer;

      .recommend-image {
        width: 160px;
        height: 160px;
        border-radius: 8px;
        margin-bottom: 12px;
      }

      .image-error-small {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background: #f9f9f9;
        color: #ccc;
        font-size: 32px;
      }

      .recommend-info {
        .recommend-title {
          font-size: 14px;
          color: #333;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          line-height: 1.4;
          margin-bottom: 8px;
        }

        .recommend-price {
          font-size: 16px;
          font-weight: 600;
          color: #ff4d4f;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .detail-main {
    flex-direction: column;
  }

  .left-section {
    width: 100%;
  }

  .image-container {
    width: 100%;
    height: 300px;
  }

  .action-buttons {
    .action-btn {
      font-size: 14px;
    }
  }
}
</style>
