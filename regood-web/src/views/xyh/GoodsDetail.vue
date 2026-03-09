<template>
  <div class="xyh-goods-detail">
    <xyh-header />

    <div class="detail-container">
      <div class="detail-wrapper">
        <!-- 左侧商品展示 -->
        <div class="left-section">
          <!-- 主图区域 -->
          <div class="main-image-wrapper">
            <el-image
              :src="currentImage"
              class="main-image"
              fit="contain"
              :preview-src-list="imageList"
              :initial-index="activeIndex"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </div>

          <!-- 缩略图列表 -->
          <div class="thumbnail-list">
            <div
              v-for="(image, index) in goods.images"
              :key="image.id"
              class="thumbnail-item"
              :class="{ active: activeIndex === index }"
              @click="activeIndex = index"
            >
              <el-image
                :src="image.url"
                class="thumb"
                fit="cover"
              />
            </div>
          </div>
        </div>

        <!-- 右侧信息区 -->
        <div class="right-section">
          <!-- 商品基本信息 -->
          <div class="goods-info">
            <div class="goods-title">{{ goods.title }}</div>

            <div class="goods-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ goods.price }}</span>
              <span v-if="goods.originalPrice" class="original-price">
                ¥{{ goods.originalPrice }}
              </span>
            </div>

            <div class="goods-meta">
              <div class="meta-item">
                <span class="meta-label">成色</span>
                <span class="meta-value">{{ goods.condition }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">浏览</span>
                <span class="meta-value">{{ goods.views }}次</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">状态</span>
                <span class="meta-value" :class="goods.status">
                  {{ goods.status === 'sold' ? '已售出' : '在售' }}
                </span>
              </div>
            </div>

            <!-- 卖家信息 -->
            <div class="seller-section">
              <div class="seller-header">
                <el-avatar :src="goods.sellerAvatar" :size="48" />
                <div class="seller-info">
                  <div class="seller-name">{{ goods.sellerName }}</div>
                  <div class="seller-tags">
                    <el-tag v-if="goods.verified" type="success" size="mini">已认证</el-tag>
                    <el-tag v-if="goods.studentVerified" type="primary" size="mini">学籍认证</el-tag>
                  </div>
                </div>
              </div>
              <div class="seller-stats">
                <div class="stat-item">
                  <span class="stat-value">{{ goods.sellCount }}</span>
                  <span class="stat-label">成交</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ goods.rating }}</span>
                  <span class="stat-label">好评</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ goods.replyRate }}</span>
                  <span class="stat-label">回复</span>
                </div>
              </div>
            </div>

            <!-- 交易信息 -->
            <div class="trade-info">
              <div class="trade-item">
                <i class="el-icon-location-outline"></i>
                <span>{{ goods.campus }}</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons" v-if="goods.status !== 'sold'">
              <el-button class="action-btn chat-btn" @click="startChat">
                <i class="el-icon-chat-dot-round"></i> 私信
              </el-button>
              <el-button class="action-btn cart-btn" @click="addToCart">
                <i class="el-icon-shopping-cart-2"></i> 购物车
              </el-button>
              <el-button type="primary" class="action-btn buy-btn" @click="buyNow">
                立即购买
              </el-button>
            </div>
            <div v-else class="sold-notice">
              <el-tag type="warning" size="large">该商品已售出</el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品详情 -->
      <div class="detail-section">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-content" v-html="goods.description"></div>
          </el-tab-pane>
          <el-tab-pane label="商品评价" name="comments">
            <div class="comments-list">
              <div v-for="comment in goods.comments" :key="comment.id" class="comment-item">
                <el-avatar :src="comment.avatar" :size="40" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.author }}</span>
                    <span class="comment-time">{{ comment.time }}</span>
                  </div>
                  <div class="comment-text">{{ comment.content }}</div>
                  <div class="comment-rating">
                    <i v-for="n in 5" :key="n" class="el-icon-star-on" :class="{ active: n <= comment.rating }"></i>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 防骗提示 -->
      <div class="fraud-warning">
        <i class="el-icon-warning"></i>
        <span>温馨提示：请勿脱离平台进行交易，谨防诈骗！</span>
      </div>
    </div>

    <xyh-footer />
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'
import XyhFooter from '@/components/XyhFooter'

export default {
  name: 'GoodsDetail',
  components: {
    XyhHeader,
    XyhFooter
  },
  data() {
    return {
      activeTab: 'detail',
      activeIndex: 0,
      goods: {
        id: 1,
        title: 'iPhone 13 Pro 256G 远峰蓝 95新 个人闲置转让',
        price: 5299,
        originalPrice: 7999,
        condition: '95新',
        views: 1258,
        status: 'onsale',
        images: [
          { id: 1, url: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800' },
          { id: 2, url: 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800' },
          { id: 3, url: 'https://images.unsplash.com/photo-1587574258576-306563566902?w=800' },
          { id: 4, url: 'https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=800' }
        ],
        sellerName: '学长小李',
        sellerAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        creditScore: 850,
        sellCount: 156,
        rating: 4.8,
        replyRate: '99%',
        campus: '主校区',
        verified: true,
        studentVerified: true,
        description: `
          <div class="desc-section">
            <h4>商品描述</h4>
            <p>本人因换手机而出售 iPhone 13 Pro，256G 远峰蓝版本，95新，仅使用过2个月，屏幕贴膜，原装包装盒、发票都在。</p>
          </div>
          <div class="desc-section">
            <h4>商品详情</h4>
            <ul>
              <li>A15 芯片，性能强劲</li>
              <li>120Hz ProMotion 刷新率</li>
              <li>三摄系统，拍照出色</li>
              <li>256G 大存储</li>
            </ul>
          </div>
          <div class="desc-section">
            <h4>交易方式</h4>
            <p>支持校园面交（主校区图书馆门口），也支持快递（到付）。</p>
          </div>
        `,
        comments: [
          { id: 1, author: '学弟小王', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', time: '2024-03-15', content: '手机很好，和描述一致，交易很顺利！', rating: 5 },
          { id: 2, author: '学妹小张', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', time: '2024-03-10', content: '卖家很守信用，面交很准时，手机成色不错。', rating: 4 }
        ]
      }
    }
  },
  computed: {
    currentImage() {
      return this.goods.images[this.activeIndex]?.url || ''
    },
    imageList() {
      return this.goods.images.map(img => img.url)
    }
  },
  methods: {
    startChat() {
      this.$router.push({ path: '/message', query: { goodsId: this.goods.id }})
    },
    addToCart() {
      this.$message.success('已加入购物车')
    },
    buyNow() {
      this.$router.push({ path: '/cart', query: { goodsId: this.goods.id }})
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-goods-detail {
  min-height: 100vh;
  background: #f5f7fa;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-wrapper {
  display: flex;
  gap: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.left-section {
  width: 420px;
  flex-shrink: 0;
}

.main-image-wrapper {
  width: 420px;
  height: 420px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  margin-bottom: 12px;

  .main-image {
    width: 100%;
    height: 100%;
  }

  .image-slot {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    font-size: 60px;
    color: #ccc;
  }
}

.thumbnail-list {
  display: flex;
  gap: 10px;

  .thumbnail-item {
    width: 80px;
    height: 80px;
    border-radius: 6px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.3s;

    &:hover {
      border-color: #1890ff;
    }

    &.active {
      border-color: #1890ff;
    }

    .thumb {
      width: 100%;
      height: 100%;
    }
  }
}

.right-section {
  flex: 1;
  min-width: 0;
}

.goods-info {
  .goods-title {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    line-height: 1.5;
  }

  .goods-price {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 20px;
    padding: 16px;
    background: #fff5f5;
    border-radius: 8px;

    .price-symbol {
      font-size: 18px;
      color: #f5222d;
    }

    .price-value {
      font-size: 32px;
      font-weight: 700;
      color: #f5222d;
    }

    .original-price {
      font-size: 16px;
      color: #999;
      text-decoration: line-through;
    }
  }

  .goods-meta {
    display: flex;
    gap: 24px;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .meta-label {
        color: #999;
        font-size: 14px;
      }

      .meta-value {
        color: #333;
        font-size: 14px;
        font-weight: 500;

        &.sold {
          color: #ff4d4f;
        }
      }
    }
  }

  .seller-section {
    margin-bottom: 20px;
    padding: 16px;
    background: #f9f9f9;
    border-radius: 8px;

    .seller-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;

      .seller-info {
        .seller-name {
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .seller-tags {
          display: flex;
          gap: 6px;
        }
      }
    }

    .seller-stats {
      display: flex;
      gap: 30px;

      .stat-item {
        text-align: center;

        .stat-value {
          display: block;
          font-size: 18px;
          font-weight: 600;
          color: #333;
        }

        .stat-label {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }

  .trade-info {
    margin-bottom: 24px;

    .trade-item {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #666;
      font-size: 14px;
    }
  }

  .action-buttons {
    display: flex;
    gap: 12px;

    .action-btn {
      flex: 1;
      height: 48px;
      font-size: 16px;
      border-radius: 24px;

      &.buy-btn {
        background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
        border: none;
      }

      &.chat-btn, &.cart-btn {
        background: #f5f5f5;
        border: 1px solid #d9d9d9;
        color: #333;
      }
    }
  }

  .sold-notice {
    text-align: center;
    padding: 20px;
    background: #fffbe6;
    border-radius: 8px;
  }
}

.detail-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.detail-content {
  :deep(.desc-section) {
    margin-bottom: 20px;

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 12px;
    }

    p {
      color: #666;
      line-height: 1.8;
      margin-bottom: 12px;
    }

    ul {
      padding-left: 20px;
      color: #666;

      li {
        line-height: 2;
      }
    }
  }
}

.comments-list {
  .comment-item {
    display: flex;
    gap: 12px;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .comment-content {
      flex: 1;

      .comment-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;

        .comment-author {
          font-weight: 600;
          color: #333;
        }

        .comment-time {
          font-size: 12px;
          color: #999;
        }
      }

      .comment-text {
        color: #666;
        line-height: 1.6;
        margin-bottom: 8px;
      }

      .comment-rating {
        .el-icon-star-on {
          color: #d9d9d9;
          font-size: 14px;

          &.active {
            color: #faad14;
          }
        }
      }
    }
  }
}

.fraud-warning {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 8px;
  color: #fa8c16;
  font-size: 14px;
}

@media (max-width: 768px) {
  .detail-wrapper {
    flex-direction: column;
  }

  .left-section {
    width: 100%;
  }

  .main-image-wrapper {
    width: 100%;
    height: 300px;
  }
}
</style>
