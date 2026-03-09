<template>
  <div class="xyh-cart">
    <xyh-header />
    
    <div class="cart-container">
      <!-- 购物车列表 -->
      <div class="cart-list" v-if="cartItems.length > 0">
        <div class="cart-header">
          <h2 class="cart-title">购物车</h2>
          <span class="item-count">{{ cartItems.length }} 件商品</span>
        </div>

        <div class="cart-items">
          <div
            v-for="item in cartItems"
            :key="item.id"
            class="cart-item"
          >
            <el-checkbox
              v-model="item.selected"
              class="select-checkbox"
            ></el-checkbox>

            <el-image
              :src="item.image"
              class="item-image"
              fit="cover"
            />

            <div class="item-info">
              <div class="item-title">{{ item.title }}</div>
              <div class="item-meta">
                <span class="seller">卖家：{{ item.seller }}</span>
                <span class="condition">{{ item.condition }}</span>
              </div>
              <div class="item-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ item.price }}</span>
              </div>
            </div>

            <div class="item-actions">
              <el-button
                type="text"
                icon="el-icon-delete"
                @click="removeItem(item.id)"
              ></el-button>
            </div>
          </div>
        </div>

        <!-- 底部悬浮栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox
              v-model="selectAll"
              @change="toggleSelectAll"
            >
              全选
            </el-checkbox>
          </div>

          <div class="footer-center">
            <div class="total-info">
              <span class="label">合计：</span>
              <span class="total-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ totalPrice }}</span>
              </span>
            </div>
            <div class="discount-info" v-if="discount > 0">
              <span class="discount-label">已优惠：</span>
              <span class="discount-value">-¥{{ discount }}</span>
            </div>
          </div>

          <div class="footer-right">
            <el-button
              type="primary"
              size="large"
              class="checkout-btn"
              @click="goToCheckout"
            >
              去结算
              <span class="count-badge">{{ selectedCount }}</span>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空购物车 -->
      <div class="empty-cart" v-else>
        <div class="empty-icon">🛒</div>
        <div class="empty-title">购物车是空的</div>
        <div class="empty-desc">快去挑选心仪的商品吧！</div>
        <el-button
          type="primary"
          size="large"
          class="go-shopping-btn"
          @click="goToHome"
        >
          去购物
        </el-button>
      </div>
    </div>

    <!-- 结算页面 -->
    <div class="checkout-section" v-if="showCheckout">
      <div class="checkout-container">
        <div class="checkout-header">
          <h2 class="checkout-title">确认订单</h2>
          <el-button
            type="text"
            icon="el-icon-close"
            @click="showCheckout = false"
          ></el-button>
        </div>

        <!-- 收货/交易方式 -->
        <div class="delivery-section">
          <div class="section-title">交易方式</div>
          <el-radio-group v-model="deliveryType" class="delivery-options">
            <el-radio-button label="faceToFace">
              <div class="radio-option">
                <i class="el-icon-location-circle"></i>
                <div class="option-info">
                  <div class="option-title">当面交易</div>
                  <div class="option-desc">校园内面交，安全便捷</div>
                </div>
              </div>
            </el-radio-button>
            <el-radio-button label="express">
              <div class="radio-option">
                <i class="el-icon-s-promotion"></i>
                <div class="option-info">
                  <div class="option-title">快递寄送</div>
                  <div class="option-desc">支持全国快递（到付）</div>
                </div>
              </div>
            </el-radio-button>
          </el-radio-group>

          <div v-if="deliveryType === 'faceToFace'" class="meeting-place">
            <el-select
              v-model="meetingPlace"
              placeholder="请选择面交地点"
              style="width: 100%"
            >
              <el-option label="一食堂门口" value="one_canteen"></el-option>
              <el-option label="二食堂门口" value="two_canteen"></el-option>
              <el-option label="图书馆门口" value="library"></el-option>
              <el-option label="教学楼楼下" value="classroom"></el-option>
              <el-option label="宿舍楼下" value="dormitory"></el-option>
              <el-option label="其他地点" value="other"></el-option>
            </el-select>
          </div>

          <div v-if="deliveryType === 'express'" class="express-address">
            <el-input
              v-model="expressAddress"
              type="textarea"
              :rows="2"
              placeholder="请输入校内宿舍地址（如：XX栋XX室）"
            />
          </div>
        </div>

        <!-- 商品清单 -->
        <div class="order-items">
          <div class="section-title">商品清单</div>
          <div
            v-for="item in selectedItems"
            :key="item.id"
            class="order-item"
          >
            <el-image
              :src="item.image"
              class="order-image"
              fit="cover"
            />
            <div class="order-info">
              <div class="order-title">{{ item.title }}</div>
              <div class="order-meta">
                <span class="seller">卖家：{{ item.seller }}</span>
                <span class="condition">{{ item.condition }}</span>
              </div>
              <div class="order-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ item.price }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 买家留言 -->
        <div class="buyer-message">
          <div class="section-title">买家留言</div>
          <el-input
            v-model="buyerMessage"
            type="textarea"
            :rows="3"
            placeholder="给卖家的备注，如：面交时间、颜色偏好等"
            maxlength="200"
            show-word-limit
          />
        </div>

        <!-- 订单总额 -->
        <div class="order-total">
          <div class="total-row">
            <span class="total-label">商品总额：</span>
            <span class="total-value">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ totalPrice }}</span>
            </span>
          </div>
          <div class="total-row" v-if="discount > 0">
            <span class="total-label">优惠：</span>
            <span class="total-value" style="color: #f56c6c">
              -¥{{ discount }}
            </span>
          </div>
          <div class="total-row total-row-highlight">
            <span class="total-label">应付金额：</span>
            <span class="total-value">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ totalPrice }}</span>
            </span>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="payment-section">
          <div class="section-title">支付方式</div>
          <el-radio-group v-model="paymentMethod">
            <el-radio label="wechat">
              <i class="el-icon-chat-round"></i>
              微信支付
            </el-radio>
            <el-radio label="alipay">
              <i class="el-icon-bank-card"></i>
              支付宝
            </el-radio>
            <el-radio label="campus">
              <i class="el-icon-reading"></i>
              校园卡余额
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 提交订单 -->
        <div class="checkout-actions">
          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="submitLoading"
            @click="submitOrder"
          >
            确认支付
            <span class="submit-price">¥{{ totalPrice }}</span>
          </el-button>
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
  name: 'XyhCart',
  components: {
    XyhHeader,
    XyhFooter
  },
  data() {
    return {
      showCheckout: false,
      deliveryType: 'faceToFace',
      meetingPlace: '',
      expressAddress: '',
      buyerMessage: '',
      paymentMethod: 'wechat',
      submitLoading: false,
      cartItems: [
        {
          id: 1,
          title: 'iPhone 13 Pro 256G 远峰蓝 95 新',
          image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200',
          seller: '学长小李',
          condition: '95 新',
          price: 5299,
          selected: true
        },
        {
          id: 2,
          title: '捷安特山地自行车 9 成新',
          image: 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=200',
          seller: '学长小张',
          condition: '9 成新',
          price: 1299,
          selected: true
        },
        {
          id: 3,
          title: 'Sony WH-1000XM4 降噪耳机',
          image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200',
          seller: '学姐小王',
          condition: '95 新',
          price: 1999,
          selected: false
        }
      ]
    }
  },
  computed: {
    selectAll: {
      get() {
        return this.cartItems.length > 0 && this.cartItems.every(item => item.selected)
      },
      set(value) {
        this.cartItems.forEach(item => {
          item.selected = value
        })
      }
    },
    selectedItems() {
      return this.cartItems.filter(item => item.selected)
    },
    selectedCount() {
      return this.selectedItems.length
    },
    totalPrice() {
      return this.selectedItems.reduce((sum, item) => sum + item.price, 0)
    },
    discount() {
      return this.totalPrice > 3000 ? 200 : 0
    }
  },
  methods: {
    toggleSelectAll(value) {
      this.cartItems.forEach(item => {
        item.selected = value
      })
    },
    removeItem(id) {
      this.$confirm('确定要从购物车中移除该商品吗？', '提示', {
        type: 'warning'
      }).then(() => {
        this.cartItems = this.cartItems.filter(item => item.id !== id)
        this.$message.success('已移除')
      })
    },
    goToHome() {
      this.$router.push('/home')
    },
    goToCheckout() {
      if (this.selectedCount === 0) {
        this.$message.warning('请选择要结算的商品')
        return
      }
      this.showCheckout = true
    },
    submitOrder() {
      if (this.deliveryType === 'faceToFace' && !this.meetingPlace) {
        this.$message.warning('请选择面交地点')
        return
      }
      
      if (this.deliveryType === 'express' && !this.expressAddress) {
        this.$message.warning('请输入宿舍地址')
        return
      }
      
      this.submitLoading = true
      
      // 模拟提交订单
      setTimeout(() => {
        this.submitLoading = false
        this.$message.success('订单提交成功！')
        this.showCheckout = false
        this.cartItems = this.cartItems.filter(item => !item.selected)
        this.$router.push('/home')
      }, 1500)
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-cart {
  min-height: 100vh;
  background: #f5f7fa;
}

.cart-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #1890ff;
  
  .cart-title {
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }
  
  .item-count {
    font-size: 14px;
    color: #999;
  }
}

.cart-items {
  .cart-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    margin-bottom: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;
    
    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }
    
    .select-checkbox {
      flex-shrink: 0;
    }
    
    .item-image {
      width: 120px;
      height: 120px;
      border-radius: 8px;
      flex-shrink: 0;
    }
    
    .item-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .item-title {
        font-size: 16px;
        color: #333;
        margin-bottom: 8px;
        line-height: 1.5;
      }
      
      .item-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        font-size: 13px;
        color: #999;
        margin-bottom: 8px;
        
        .seller {
          color: #666;
        }
        
        .condition {
          background: #f5f5f5;
          padding: 2px 8px;
          border-radius: 4px;
        }
      }
      
      .item-price {
        display: flex;
        align-items: baseline;
        gap: 4px;
        
        .price-symbol {
          color: #f5222d;
          font-size: 14px;
        }
        
        .price-value {
          color: #f5222d;
          font-size: 24px;
          font-weight: 700;
        }
      }
    }
    
    .item-actions {
      .el-button {
        background: #f5f7fa;
        border: none;
        color: #f5222d;
        
        &:hover {
          background: #fef0f0;
        }
      }
    }
  }
}

.cart-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 16px 20px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  
  .footer-left {
    .el-checkbox {
      font-size: 16px;
      color: #333;
    }
  }
  
  .footer-center {
    flex: 1;
    margin: 0 40px;
    
    .total-info {
      display: flex;
      align-items: baseline;
      gap: 8px;
      
      .label {
        font-size: 16px;
        color: #666;
      }
      
      .total-price {
        .price-symbol {
          color: #f5222d;
          font-size: 20px;
        }
        
        .price-value {
          color: #f5222d;
          font-size: 32px;
          font-weight: 700;
        }
      }
    }
    
    .discount-info {
      margin-top: 4px;
      
      .discount-label {
        font-size: 13px;
        color: #666;
      }
      
      .discount-value {
        font-size: 14px;
        color: #f56c6c;
      }
    }
  }
  
  .footer-right {
    .checkout-btn {
      background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
      border: none;
      height: 56px;
      padding: 0 32px;
      font-size: 18px;
      font-weight: 600;
      
      .count-badge {
        margin-left: 8px;
        background: rgba(255, 255, 255, 0.3);
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 14px;
      }
      
      &:hover {
        opacity: 0.9;
      }
    }
  }
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 20px;
  
  .empty-icon {
    font-size: 80px;
    margin-bottom: 24px;
  }
  
  .empty-title {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin-bottom: 12px;
  }
  
  .empty-desc {
    font-size: 16px;
    color: #999;
    margin-bottom: 32px;
  }
  
  .go-shopping-btn {
    background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
    border: none;
    height: 52px;
    padding: 0 48px;
    font-size: 18px;
    font-weight: 600;
  }
}

.checkout-section {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow-y: auto;
}

.checkout-container {
  background: #fff;
  border-radius: 16px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.checkout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #e8e8e8;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .checkout-title {
    font-size: 24px;
    font-weight: 600;
    color: #fff;
  }
  
  .el-button {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: #fff;
    
    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  padding: 20px 32px 12px;
  margin-bottom: 0;
}

.delivery-section {
  padding: 0 32px 24px;
  
  .delivery-options {
    .el-radio-button {
      flex: 1;
      
      .el-radio-button__inner {
        padding: 20px 12px;
      }
      
      &.is-active .el-radio-button__inner {
        background: #e6f7ff;
        border-color: #1890ff;
      }
    }
    
    .radio-option {
      display: flex;
      align-items: center;
      gap: 12px;
      
      i {
        font-size: 24px;
        color: #1890ff;
      }
      
      .option-info {
        .option-title {
          font-size: 16px;
          font-weight: 600;
          color: #333;
        }
        
        .option-desc {
          font-size: 13px;
          color: #999;
        }
      }
    }
  }
  
  .meeting-place,
  .express-address {
    margin-top: 16px;
  }
}

.order-items {
  padding: 0 32px 24px;
  border-top: 1px solid #e8e8e8;
  
  .order-item {
    display: flex;
    gap: 16px;
    padding: 16px 0;
    border-bottom: 1px solid #f5f7fa;
    
    &:last-child {
      border-bottom: none;
    }
    
    .order-image {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      flex-shrink: 0;
    }
    
    .order-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .order-title {
        font-size: 14px;
        color: #333;
        margin-bottom: 8px;
      }
      
      .order-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        font-size: 12px;
        color: #999;
        margin-bottom: 8px;
        
        .seller {
          color: #666;
        }
        
        .condition {
          background: #f5f5f5;
          padding: 2px 8px;
          border-radius: 4px;
        }
      }
      
      .order-price {
        display: flex;
        align-items: baseline;
        gap: 4px;
        
        .price-symbol {
          color: #f5222d;
          font-size: 12px;
        }
        
        .price-value {
          color: #f5222d;
          font-size: 18px;
          font-weight: 600;
        }
      }
    }
  }
}

.buyer-message {
  padding: 0 32px 24px;
}

.order-total {
  padding: 24px 32px;
  background: #f5f7fa;
  border-top: 1px solid #e8e8e8;
  
  .total-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    
    .total-label {
      font-size: 14px;
      color: #666;
    }
    
    .total-value {
      font-size: 14px;
      color: #333;
      
      .price-symbol {
        color: #f5222d;
        font-size: 12px;
      }
      
      .price-value {
        color: #333;
        font-size: 14px;
        font-weight: 500;
      }
    }
  }
  
  .total-row-highlight {
    .total-label {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
    
    .total-value {
      .price-value {
        font-size: 24px;
        font-weight: 700;
        color: #f5222d;
      }
    }
  }
}

.payment-section {
  padding: 0 32px 24px;
  
  .el-radio {
    margin-bottom: 12px;
    
    .el-radio__label {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #333;
      
      i {
        color: #1890ff;
      }
    }
  }
}

.checkout-actions {
  padding: 24px 32px;
  border-top: 1px solid #e8e8e8;
  
  .submit-btn {
    width: 100%;
    height: 56px;
    font-size: 18px;
    font-weight: 600;
    background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
    border: none;
    
    .submit-price {
      margin-left: 12px;
      font-size: 24px;
      font-weight: 700;
    }
    
    &:hover {
      opacity: 0.9;
    }
  }
}

@media (max-width: 768px) {
  .cart-footer {
    flex-direction: column;
    padding: 12px;
    
    .footer-center {
      margin: 12px 0;
    }
    
    .footer-right {
      width: 100%;
      
      .checkout-btn {
        width: 100%;
      }
    }
  }
  
  .checkout-container {
    max-height: 100vh;
    border-radius: 0;
  }
}
</style>
