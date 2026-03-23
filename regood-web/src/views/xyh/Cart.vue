<template>
  <div class="cart-page">
    <xyh-header />

    <div class="cart-content">
      <div class="page-header">
        <h1 class="page-title">购物车</h1>
        <span class="edit-btn" @click="toggleEdit">{{ isEditing ? '完成' : '管理' }}</span>
      </div>

      <div class="cart-list" v-if="cartItems.length > 0">
        <div
          v-for="item in cartItems"
          :key="item.id"
          class="cart-item"
        >
          <div class="item-checkbox">
            <div
              :class="['checkbox', { checked: item.selected }]"
              @click="toggleSelect(item)"
            >
              <i class="el-icon-check" v-if="item.selected"></i>
            </div>
          </div>

          <div class="item-image" @click="goToDetail(item.id)">
            <el-image :src="item.image" fit="cover">
              <div slot="error" class="image-error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div class="sold-tag" v-if="item.status === 'sold'">已售</div>
          </div>

          <div class="item-info" @click="goToDetail(item.id)">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-desc">{{ item.description }}</div>
            <div class="item-tags">
              <span class="tag condition">{{ item.condition }}</span>
              <span class="tag" v-if="item.freeShipping">包邮</span>
            </div>
            <div class="item-bottom">
              <div class="item-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ item.price }}</span>
              </div>
              <div class="seller-info">
                <el-avatar :src="item.sellerAvatar" :size="16"></el-avatar>
                <span class="seller-name">{{ item.sellerName }}</span>
              </div>
            </div>
          </div>

          <div class="item-delete" v-if="isEditing" @click="removeItem(item.id)">
            <i class="el-icon-delete"></i>
          </div>
        </div>
      </div>

      <div class="empty-cart" v-else>
        <div class="empty-icon">
          <i class="el-icon-shopping-cart-2"></i>
        </div>
        <div class="empty-text">购物车空空如也</div>
        <div class="empty-desc">快去挑选心仪的好物吧</div>
        <div class="empty-btn" @click="goToHome">去逛逛</div>
      </div>

      <div class="recommend-section" v-if="recommendList.length">
        <div class="section-header">
          <span class="section-title">猜你喜欢</span>
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

    <div class="bottom-bar" v-if="cartItems.length > 0">
      <div class="bar-left">
        <div class="select-all" @click="toggleSelectAll">
          <div :class="['checkbox', { checked: isAllSelected }]">
            <i class="el-icon-check" v-if="isAllSelected"></i>
          </div>
          <span>全选</span>
        </div>
      </div>
      <div class="bar-right">
        <div class="total-info">
          <span class="total-label">合计:</span>
          <span class="total-price">¥{{ totalPrice }}</span>
        </div>
        <div
          :class="['checkout-btn', { disabled: selectedCount === 0 }]"
          @click="goToCheckout"
        >
          结算({{ selectedCount }})
        </div>
      </div>
    </div>

    <el-dialog
      title="确认订单"
      :visible.sync="showCheckout"
      width="500px"
      custom-class="checkout-dialog"
      :close-on-click-modal="false"
    >
      <div class="checkout-body">
        <div class="checkout-section">
          <div class="section-label">收货方式</div>
          <el-select v-model="deliveryType" placeholder="请选择配送方式" class="delivery-select">
            <el-option
              v-for="item in deliveryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <div class="delivery-option-item">
                <i :class="item.icon"></i>
                <div class="option-text">
                  <span class="option-label">{{ item.label }}</span>
                </div>
              </div>
            </el-option>
          </el-select>
          <div class="address-input" v-if="deliveryType === 'face'">
            <el-input
              v-model="meetingPlace"
              placeholder="请输入面交地点"
              prefix-icon="el-icon-location"
            />
          </div>
          <div class="address-input" v-else>
            <el-input
              type="textarea"
              v-model="expressAddress"
              placeholder="请输入收货地址（省市区街道门牌号）"
              :rows="2"
            />
          </div>
        </div>

        <div class="checkout-section">
          <div class="section-label">商品信息</div>
          <div class="goods-list">
            <div
              v-for="item in selectedItems"
              :key="item.id"
              class="goods-item"
            >
              <el-image :src="item.image" class="goods-image" fit="cover" />
              <div class="goods-info">
                <div class="goods-title">{{ item.title }}</div>
                <div class="goods-meta">
                  <span class="goods-condition">{{ item.condition }}</span>
                  <span class="goods-price">¥{{ item.price }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="checkout-section">
          <div class="section-label">买家留言</div>
          <el-input
            type="textarea"
            v-model="buyerMessage"
            placeholder="选填，给卖家留言（如发货时间、包装要求等）"
            :rows="2"
            maxlength="100"
            show-word-limit
          />
        </div>

        <div class="checkout-section price-section">
          <div class="price-row">
            <span>商品金额</span>
            <span>¥{{ totalPrice }}</span>
          </div>
          <div class="price-row" v-if="deliveryType === 'express'">
            <span>运费</span>
            <span>¥{{ shippingFee }}</span>
          </div>
          <div class="price-row total">
            <span>实付款</span>
            <span class="final-price">¥{{ finalPrice }}</span>
          </div>
        </div>

        <div class="checkout-section">
          <div class="section-label">支付方式</div>
          <div class="pay-methods">
            <div
              v-for="method in payMethods"
              :key="method.value"
              :class="['pay-item', { active: paymentMethod === method.value }]"
              @click="paymentMethod = method.value"
            >
              <div :class="['pay-icon', method.value]">
                <i :class="method.icon"></i>
              </div>
              <span>{{ method.label }}</span>
              <i class="el-icon-check check-icon" v-if="paymentMethod === method.value"></i>
            </div>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="showCheckout = false">取消</el-button>
        <el-button type="primary" @click="submitOrder">
          确认支付 ¥{{ finalPrice }}
        </el-button>
      </span>
    </el-dialog>

    <xyh-footer />
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'
import XyhFooter from '@/components/XyhFooter'
import { getCartList, addToCart, removeFromCart } from '@/api/business/cart'
import { getProductPage } from '@/api/business/product'
import { createOrder } from '@/api/business/order'

export default {
  name: 'XyhCart',
  components: {
    XyhHeader,
    XyhFooter
  },
  data() {
    return {
      isEditing: false,
      showCheckout: false,
      deliveryType: 'face',
      meetingPlace: '',
      expressAddress: '',
      buyerMessage: '',
      paymentMethod: 'wechat',
      deliveryOptions: [
        { label: '当面交易', value: 'face', icon: 'el-icon-user', desc: '校园内面交，安全便捷' },
        { label: '快递配送', value: 'express', icon: 'el-icon-truck', desc: '全国配送，运费到付' }
      ],
      cartItems: [],
      recommendList: [],
      payMethods: [
        { label: '微信支付', value: 'wechat', icon: 'el-icon-chat-round' },
        { label: '支付宝', value: 'alipay', icon: 'el-icon-bank-card' }
      ]
    }
  },
  computed: {
    isAllSelected() {
      return this.cartItems.length > 0 && this.cartItems.every(item => item.selected)
    },
    selectedItems() {
      return this.cartItems.filter(item => item.selected)
    },
    selectedCount() {
      return this.selectedItems.length
    },
    totalPrice() {
      return this.selectedItems.reduce((sum, item) => sum + Number(item.price), 0)
    },
    shippingFee() {
      return this.deliveryType === 'express' ? 10 : 0
    },
    finalPrice() {
      return this.totalPrice + this.shippingFee
    }
  },
  created() {
    this.loadCartList()
    this.loadRecommendProducts()
  },
  methods: {
    async loadCartList() {
      try {
        const res = await getCartList()
        if (res.code === 200) {
          this.cartItems = (res.data.list || []).map(item => this.convertToCartItem(item))
        }
      } catch (error) {
        console.error('加载购物车失败', error)
      }
    },
    convertToCartItem(item) {
      const images = item.images && item.images.length > 0 ? item.images : ['https://picsum.photos/200/200?random=default']
      return {
        id: item.cartId,
        cartId: item.cartId,
        productId: item.productId,
        title: item.title,
        description: item.description || '',
        image: images[0],
        price: item.price,
        condition: item.condition,
        freeShipping: item.freeShipping,
        status: 'onsale',
        selected: false,
        sellerName: item.sellerName,
        sellerAvatar: item.sellerAvatar
      }
    },
    async loadRecommendProducts() {
      try {
        const res = await getProductPage({ pageNum: 1, pageSize: 6 })
        if (res.code === 200) {
          this.recommendList = (res.data.list || []).map(item => ({
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
    toggleEdit() {
      this.isEditing = !this.isEditing
    },
    toggleSelect(item) {
      item.selected = !item.selected
    },
    toggleSelectAll() {
      const newState = !this.isAllSelected
      this.cartItems.forEach(item => {
        item.selected = newState
      })
    },
    async removeItem(id) {
      try {
        await this.$confirm('确定要删除该商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await removeFromCart(id)
        this.cartItems = this.cartItems.filter(item => item.id !== id)
        this.$message.success('已删除')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败', error)
        }
      }
    },
    goToDetail(id) {
      this.$router.push(`/goods/${id}`)
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
    async submitOrder() {
      try {
        if (this.deliveryType === 'face' && !this.meetingPlace) {
          this.$message.warning('请输入面交地点')
          return
        }
        if (this.deliveryType === 'express' && !this.expressAddress) {
          this.$message.warning('请输入收货地址')
          return
        }
        for (const item of this.selectedItems) {
          await createOrder({
            productId: item.productId,
            tradeMethod: this.deliveryType === 'face' ? '当面交易' : '快递配送',
            meetingPlace: this.meetingPlace,
            address: this.expressAddress
          })
        }
        this.$message.success('订单提交成功')
        this.showCheckout = false
        this.cartItems = this.cartItems.filter(item => !item.selected)
      } catch (error) {
        console.error('提交订单失败', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.cart-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 70px;
}

.cart-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 12px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;

  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }

  .edit-btn {
    font-size: 14px;
    color: #ff6b00;
    cursor: pointer;
  }
}

.cart-list {
  .cart-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    background: #fff;
    border-radius: 12px;
    padding: 12px;
    margin-bottom: 10px;

    .item-checkbox {
      padding-top: 30px;

      .checkbox {
        width: 20px;
        height: 20px;
        border: 2px solid #ddd;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;

        &.checked {
          background: #ff6b00;
          border-color: #ff6b00;

          i {
            color: #fff;
            font-size: 12px;
          }
        }
      }
    }

    .item-image {
      width: 90px;
      height: 90px;
      border-radius: 8px;
      overflow: hidden;
      position: relative;
      flex-shrink: 0;

      .image-error {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background: #f9f9f9;
        color: #ccc;
        font-size: 32px;
      }

      .sold-tag {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 14px;
      }
    }

    .item-info {
      flex: 1;
      min-width: 0;

      .item-title {
        font-size: 14px;
        color: #333;
        line-height: 1.4;
        margin-bottom: 4px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .item-desc {
        font-size: 12px;
        color: #999;
        margin-bottom: 6px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .item-tags {
        display: flex;
        gap: 6px;
        margin-bottom: 8px;

        .tag {
          font-size: 11px;
          padding: 2px 6px;
          border-radius: 4px;
          background: #f5f5f5;
          color: #666;

          &.condition {
            background: #fff5e6;
            color: #ff6b00;
          }
        }
      }

      .item-bottom {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .item-price {
          display: flex;
          align-items: baseline;

          .price-symbol {
            font-size: 12px;
            color: #ff4d4f;
          }

          .price-value {
            font-size: 18px;
            font-weight: 600;
            color: #ff4d4f;
          }
        }

        .seller-info {
          display: flex;
          align-items: center;
          gap: 4px;

          .seller-name {
            font-size: 12px;
            color: #999;
          }
        }
      }
    }

    .item-delete {
      padding: 8px;
      color: #999;
      cursor: pointer;

      &:hover {
        color: #ff4d4f;
      }
    }
  }
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 12px;

  .empty-icon {
    width: 80px;
    height: 80px;
    background: #f5f5f5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;

    i {
      font-size: 40px;
      color: #ccc;
    }
  }

  .empty-text {
    font-size: 16px;
    color: #333;
    margin-bottom: 8px;
  }

  .empty-desc {
    font-size: 14px;
    color: #999;
    margin-bottom: 20px;
  }

  .empty-btn {
    padding: 10px 32px;
    background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
    color: #fff;
    font-size: 14px;
    border-radius: 20px;
    cursor: pointer;
  }
}

.recommend-section {
  margin-top: 20px;

  .section-header {
    padding: 16px 0;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }

  .recommend-list {
    display: flex;
    gap: 10px;
    overflow-x: auto;

    &::-webkit-scrollbar {
      display: none;
    }

    .recommend-item {
      flex-shrink: 0;
      width: 140px;
      background: #fff;
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;

      .recommend-image {
        width: 140px;
        height: 140px;
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
        padding: 8px;

        .recommend-title {
          font-size: 13px;
          color: #333;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          margin-bottom: 4px;
        }

        .recommend-price {
          font-size: 14px;
          font-weight: 600;
          color: #ff4d4f;
        }
      }
    }
  }
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;

  .bar-left {
    .select-all {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;

      .checkbox {
        width: 20px;
        height: 20px;
        border: 2px solid #ddd;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;

        &.checked {
          background: #ff6b00;
          border-color: #ff6b00;

          i {
            color: #fff;
            font-size: 12px;
          }
        }
      }

      span {
        font-size: 14px;
        color: #333;
      }
    }
  }

  .bar-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .total-info {
      .total-label {
        font-size: 14px;
        color: #666;
      }

      .total-price {
        font-size: 20px;
        font-weight: 600;
        color: #ff4d4f;
      }
    }

    .checkout-btn {
      padding: 10px 24px;
      background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      border-radius: 20px;
      cursor: pointer;

      &.disabled {
        background: #ccc;
        cursor: not-allowed;
      }
    }
  }
}
</style>

<style lang="scss">
.checkout-dialog {
  border-radius: 12px;

  .el-dialog__header {
    border-bottom: 1px solid #f0f0f0;
    padding: 16px 20px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }

  .el-dialog__body {
    padding: 0;
  }

  .el-dialog__footer {
    border-top: 1px solid #f0f0f0;
    padding: 16px 20px;

    .el-button--primary {
      background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
      border: none;
    }
  }

  .checkout-body {
    max-height: 60vh;
    overflow-y: auto;
  }

  .checkout-section {
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .section-label {
      font-size: 14px;
      color: #999;
      margin-bottom: 12px;
    }
  }

  .delivery-select {
    width: 100%;
    margin-bottom: 12px;

    .el-input__inner {
      border-radius: 8px;
    }
  }

  .address-input {
    .el-input__inner,
    .el-textarea__inner {
      border-radius: 8px;
    }
  }

  .goods-list {
    max-height: 180px;
    overflow-y: auto;

    .goods-item {
      display: flex;
      gap: 12px;
      padding: 10px 0;
      border-bottom: 1px solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .goods-image {
        width: 60px;
        height: 60px;
        border-radius: 6px;
        flex-shrink: 0;
      }

      .goods-info {
        flex: 1;
        min-width: 0;

        .goods-title {
          font-size: 14px;
          color: #333;
          margin-bottom: 8px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .goods-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .goods-condition {
            font-size: 12px;
            color: #999;
          }

          .goods-price {
            font-size: 14px;
            font-weight: 600;
            color: #ff4d4f;
          }
        }
      }
    }
  }

  .price-section {
    background: #fafafa;

    .price-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
      font-size: 14px;
      color: #666;

      &.total {
        margin-top: 12px;
        padding-top: 12px;
        border-top: 1px solid #e8e8e8;
        font-size: 15px;
        color: #333;

        .final-price {
          font-size: 20px;
          font-weight: 600;
          color: #ff4d4f;
        }
      }
    }
  }

  .pay-methods {
    display: flex;
    gap: 12px;

    .pay-item {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 14px 16px;
      border: 1px solid #e8e8e8;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        border-color: #ff6b00;
      }

      &.active {
        border-color: #ff6b00;
        background: #fff5e6;
      }

      .pay-icon {
        width: 32px;
        height: 32px;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;

        i {
          font-size: 18px;
          color: #fff;
        }

        &.wechat {
          background: #07c160;
        }

        &.alipay {
          background: #1677ff;
        }
      }

      > span {
        flex: 1;
        font-size: 14px;
        color: #333;
      }

      .check-icon {
        color: #ff6b00;
        font-size: 16px;
      }
    }
  }
}

.delivery-option-item {
  display: flex;
  align-items: center;
  gap: 10px;
  line-height: 1.4;
  padding: 8px 0;

  > i {
    font-size: 18px;
    color: #ff6b00;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
  }

  .option-text {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-width: 0;

    .option-label {
      font-size: 14px;
      color: #333;
      line-height: 1.4;
    }

    .option-desc {
      font-size: 12px;
      color: #999;
      line-height: 1.4;
    }
  }
}
</style>
