<template>
  <div class="xyh-profile">
    <xyh-header />
    
    <div class="profile-container">
      <div class="profile-wrapper">
        <!-- 左侧菜单 -->
        <div class="sidebar">
          <div class="user-info-card">
            <div class="user-header">
              <el-avatar :src="userAvatar" :size="80" class="user-avatar"></el-avatar>
              <div class="user-detail">
                <div class="user-name">{{ userName }}</div>
                <div class="user-meta">
                  <el-tag v-if="userVerified" type="success" size="mini" class="cert-tag">
                    <i class="el-icon-success"></i> 已认证
                  </el-tag>
                  <el-tag v-if="studentVerified" type="primary" size="mini" class="cert-tag">
                    <i class="el-icon-reading"></i> 学籍认证
                  </el-tag>
                </div>
              </div>
            </div>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">{{ userStats.sellCount }}</div>
                <div class="stat-label">在售</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.soldCount }}</div>
                <div class="stat-label">卖出</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.boughtCount }}</div>
                <div class="stat-label">买入</div>
              </div>
            </div>
          </div>

          <el-menu
            :default-active="activeMenu"
            class="menu-list"
            @select="handleMenuSelect"
          >
            <el-menu-item index="profile">
              <i class="el-icon-user"></i>
              <span>个人资料</span>
            </el-menu-item>
            <el-menu-item index="my-publish">
              <i class="el-icon-document"></i>
              <span>我的发布</span>
              <el-badge :value="myPublishCount" class="badge" />
            </el-menu-item>
            <el-menu-item index="my-sold">
              <i class="el-icon-shopping-bag-1"></i>
              <span>我卖出的</span>
              <el-badge :value="mySoldCount" class="badge" />
            </el-menu-item>
            <el-menu-item index="my-bought">
              <i class="el-icon-shopping-bag-2"></i>
              <span>我买到的</span>
              <el-badge :value="myBoughtCount" class="badge" />
            </el-menu-item>
            <el-menu-item index="my-favorite">
              <i class="el-icon-star-on"></i>
              <span>我的收藏</span>
              <el-badge :value="myFavoriteCount" class="badge" />
            </el-menu-item>
            <el-menu-item index="settings">
              <i class="el-icon-setting"></i>
              <span>账号设置</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧内容区 -->
        <div class="content-area">
          <!-- 个人资料 -->
          <div v-if="activeMenu === 'profile'" class="profile-content">
            <div class="section-title">个人资料</div>
            <div class="profile-form">
              <el-form :model="profileForm" label-width="100px" class="form-content">
                <el-form-item label="用户名">
                  <el-input v-model="profileForm.username" disabled />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="profileForm.nickname" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>
                <el-form-item label="个性签名">
                  <el-input
                    v-model="profileForm.signature"
                    type="textarea"
                    :rows="3"
                    placeholder="写点什么，展示你的个性"
                    maxlength="100"
                    show-word-limit
                  />
                </el-form-item>
                <el-form-item label="信用分">
                  <div class="credit-score">
                    <span class="score-value">{{ profileForm.creditScore }}</span>
                    <el-progress
                      :percentage="profileForm.creditScore"
                      :color="getProgressColor(profileForm.creditScore)"
                      :width="120"
                    />
                  </div>
                </el-form-item>
                <el-form-item label="注册时间">
                  <span>{{ profileForm.registerTime }}</span>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSaveProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>

          <!-- 我的发布 -->
          <div v-if="activeMenu === 'my-publish'" class="profile-content">
            <div class="section-title">我的发布</div>
            <div class="goods-list">
              <div
                v-for="item in publishList"
                :key="item.id"
                class="goods-item"
              >
                <el-image
                  :src="item.image"
                  class="goods-image"
                  fit="cover"
                />
                <div class="goods-info">
                  <div class="goods-title">{{ item.title }}</div>
                  <div class="goods-meta">
                    <span class="price">¥{{ item.price }}</span>
                    <span class="status" :class="item.status">{{ item.statusText }}</span>
                  </div>
                  <div class="goods-actions">
                    <el-button type="primary" size="small" @click="editGoods(item.id)">编辑</el-button>
                    <el-button type="danger" size="small" @click="deleteGoods(item.id)">删除</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 我卖出的 -->
          <div v-if="activeMenu === 'my-sold'" class="profile-content">
            <div class="section-title">我卖出的</div>
            <div class="order-list">
              <div
                v-for="order in soldList"
                :key="order.id"
                class="order-item"
              >
                <div class="order-header">
                  <el-image
                    :src="order.image"
                    class="order-image"
                    fit="cover"
                  />
                  <div class="order-info">
                    <div class="order-title">{{ order.title }}</div>
                    <div class="order-meta">
                      <span class="price">¥{{ order.price }}</span>
                      <span class="quantity">x{{ order.quantity }}</span>
                    </div>
                  </div>
                </div>
                <div class="order-body">
                  <div class="order-status">
                    <span class="status-label">交易状态：</span>
                    <el-tag :type="order.statusType" size="small">{{ order.statusText }}</el-tag>
                  </div>
                  <div class="order-meta-info">
                    <div class="meta-item">
                      <span class="meta-label">买家：</span>
                      <span class="meta-value">{{ order.buyer }}</span>
                    </div>
                    <div class="meta-item">
                      <span class="meta-label">成交时间：</span>
                      <span class="meta-value">{{ order.time }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 我买到的 -->
          <div v-if="activeMenu === 'my-bought'" class="profile-content">
            <div class="section-title">我买到的</div>
            <div class="order-list">
              <div
                v-for="order in boughtList"
                :key="order.id"
                class="order-item"
              >
                <div class="order-header">
                  <el-image
                    :src="order.image"
                    class="order-image"
                    fit="cover"
                  />
                  <div class="order-info">
                    <div class="order-title">{{ order.title }}</div>
                    <div class="order-meta">
                      <span class="price">¥{{ order.price }}</span>
                      <span class="quantity">x{{ order.quantity }}</span>
                    </div>
                  </div>
                </div>
                <div class="order-body">
                  <div class="order-status">
                    <span class="status-label">交易状态：</span>
                    <el-tag :type="order.statusType" size="small">{{ order.statusText }}</el-tag>
                  </div>
                  <div class="order-meta-info">
                    <div class="meta-item">
                      <span class="meta-label">卖家：</span>
                      <span class="meta-value">{{ order.seller }}</span>
                    </div>
                    <div class="meta-item">
                      <span class="meta-label">成交时间：</span>
                      <span class="meta-value">{{ order.time }}</span>
                    </div>
                  </div>
                  <div class="order-actions">
                    <el-button
                      v-if="order.canComment"
                      type="primary"
                      size="small"
                      @click="commentOrder(order.id)"
                    >
                      评价
                    </el-button>
                    <el-button
                      v-if="order.canReview"
                      type="info"
                      size="small"
                      @click="reviewSeller(order.id)"
                    >
                      评价卖家
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的收藏 -->
          <div v-if="activeMenu === 'my-favorite'" class="profile-content">
            <div class="section-title">我的收藏</div>
            <div class="goods-list">
              <div
                v-for="item in favoriteList"
                :key="item.id"
                class="goods-item"
              >
                <el-image
                  :src="item.image"
                  class="goods-image"
                  fit="cover"
                />
                <div class="goods-info">
                  <div class="goods-title">{{ item.title }}</div>
                  <div class="goods-meta">
                    <span class="price">¥{{ item.price }}</span>
                    <span class="condition">{{ item.condition }}</span>
                  </div>
                  <div class="goods-actions">
                    <el-button type="primary" size="small" @click="buyNow(item.id)">立即购买</el-button>
                    <el-button type="info" size="small" @click="collect(item.id)">取消收藏</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 账号设置 -->
          <div v-if="activeMenu === 'settings'" class="profile-content">
            <div class="section-title">账号设置</div>
            <div class="settings-list">
              <div class="setting-item">
                <div class="setting-title">修改密码</div>
                <div class="setting-desc">修改您的登录密码</div>
                <el-button type="primary" size="small" @click="showPasswordModal = true">修改</el-button>
              </div>
              <div class="setting-item">
                <div class="setting-title">账号安全</div>
                <div class="setting-desc">查看账号安全等级</div>
                <el-button type="info" size="small" @click="viewSecurity">查看</el-button>
              </div>
              <div class="setting-item">
                <div class="setting-title">通知设置</div>
                <div class="setting-desc">管理您的消息通知</div>
                <el-switch v-model="notificationEnabled" active-color="#1890ff" inactive-color="#ccc" />
              </div>
              <div class="setting-item">
                <div class="setting-title">退出登录</div>
                <div class="setting-desc">退出当前账号</div>
                <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog
      title="修改密码"
      :visible.sync="showPasswordModal"
      width="500px"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showPasswordModal = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
      </span>
    </el-dialog>

    <xyh-footer />
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'
import XyhFooter from '@/components/XyhFooter'

export default {
  name: 'XyhProfile',
  components: {
    XyhHeader,
    XyhFooter
  },
  data() {
    return {
      activeMenu: 'profile',
      showPasswordModal: false,
      notificationEnabled: true,
      userAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      userName: '学长小李',
      userVerified: true,
      studentVerified: true,
      userStats: {
        sellCount: 8,
        soldCount: 24,
        boughtCount: 5
      },
      profileForm: {
        username: 'xuelao666',
        nickname: '学长小李',
        email: 'xuelao@example.com',
        phone: '138****1234',
        signature: '爱生活，爱交易！',
        creditScore: 850,
        registerTime: '2023-09-01'
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.validatePassword, trigger: 'blur' }
        ]
      },
      myPublishCount: 8,
      mySoldCount: 24,
      myBoughtCount: 5,
      myFavoriteCount: 12,
      publishList: [
        {
          id: 1,
          title: 'iPhone 13 Pro 256G 远峰蓝 95 新',
          image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200',
          price: 5299,
          status: 'onsale',
          statusText: '在售'
        },
        {
          id: 2,
          title: '捷安特山地自行车 9 成新',
          image: 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=200',
          price: 1299,
          status: 'sold',
          statusText: '已售出'
        }
      ],
      soldList: [
        {
          id: 1,
          title: '高等数学（第七版）上下册',
          image: 'https://images.unsplash.com/photo-1496930012345-2e5b5274141c?w=200',
          price: 88,
          quantity: 2,
          statusType: 'success',
          statusText: '交易成功',
          buyer: '学弟小王',
          time: '2024-03-15'
        }
      ],
      boughtList: [
        {
          id: 1,
          title: 'iPad Pro 2021 11 寸',
          image: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=200',
          price: 3500,
          quantity: 1,
          statusType: 'warning',
          statusText: '交易中',
          seller: '学长小张',
          time: '2024-03-10'
        }
      ],
      favoriteList: [
        {
          id: 1,
          title: 'Sony WH-1000XM4 降噪耳机',
          image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200',
          price: 1999,
          condition: '95 新'
        },
        {
          id: 2,
          title: 'Nike Air Force 1 白鞋',
          image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=200',
          price: 599,
          condition: '全新'
        }
      ]
    }
  },
  created() {
    const menu = this.$route.query.menu
    if (menu) {
      this.activeMenu = menu
    }
  },
  methods: {
    handleMenuSelect(index) {
      this.activeMenu = index
    },
    getProgressColor(score) {
      if (score >= 90) return '#67c23a'
      if (score >= 80) return '#e6a23c'
      return '#f56c6c'
    },
    handleSaveProfile() {
      this.$message.success('保存成功')
    },
    editGoods(id) {
      this.$message.info(`编辑商品 ${id}`)
    },
    deleteGoods(id) {
      this.$confirm('确定要删除此商品吗？', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功')
      })
    },
    commentOrder(id) {
      this.$message.info(`评价订单 ${id}`)
    },
    reviewSeller(id) {
      this.$message.info(`评价卖家 ${id}`)
    },
    buyNow(id) {
      this.$message.info(`立即购买 ${id}`)
    },
    collect(id) {
      this.$message.success('已取消收藏')
    },
    viewSecurity() {
      this.$message.info('查看账号安全')
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('退出成功')
        this.$router.push('/login')
      })
    },
    handleUpdatePassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          this.$message.success('密码修改成功')
          this.showPasswordModal = false
        }
      })
    },
    validatePassword(rule, value, callback) {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-profile {
  min-height: 100vh;
  background: #f5f7fa;
}

.profile-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.profile-wrapper {
  display: flex;
  gap: 20px;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.user-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 24px;
  color: #fff;
  margin-bottom: 20px;
  
  .user-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;
    
    .user-avatar {
      border: 3px solid rgba(255, 255, 255, 0.3);
    }
    
    .user-detail {
      .user-name {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 8px;
      }
      
      .cert-tag {
        background: rgba(255, 255, 255, 0.2);
        border: none;
        color: #fff;
        margin-right: 8px;
      }
    }
  }
  
  .user-stats {
    display: flex;
    justify-content: space-around;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 16px;
    
    .stat-item {
      text-align: center;
      
      .stat-value {
        font-size: 24px;
        font-weight: 700;
      }
      
      .stat-label {
        font-size: 12px;
        opacity: 0.9;
      }
    }
  }
}

.menu-list {
  border-right: none;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  .el-menu-item {
    height: 50px;
    line-height: 50px;
    font-size: 14px;
    
    i {
      margin-right: 8px;
    }
    
    .badge {
      margin-left: auto;
    }
    
    &.is-active {
      background: #e6f7ff;
      color: #1890ff;
    }
  }
}

.content-area {
  flex: 1;
  min-width: 0;
}

.profile-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 24px;
  
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 24px;
    padding-bottom: 12px;
    border-bottom: 2px solid #1890ff;
  }
}

.profile-form {
  .form-content {
    .credit-score {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .score-value {
        font-size: 32px;
        font-weight: 700;
        color: #1890ff;
      }
    }
  }
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  
  .goods-item {
    display: flex;
    gap: 16px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;
    transition: all 0.3s;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
    
    .goods-image {
      width: 100px;
      height: 100px;
      border-radius: 8px;
      flex-shrink: 0;
    }
    
    .goods-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .goods-title {
        font-size: 14px;
        color: #333;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      
      .goods-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        
        .price {
          color: #f5222d;
          font-size: 18px;
          font-weight: 600;
        }
        
        .condition {
          font-size: 12px;
          color: #999;
        }
      }
      
      .goods-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.order-list {
  .order-item {
    background: #f5f7fa;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 16px;
    
    .order-header {
      display: flex;
      gap: 16px;
      margin-bottom: 16px;
      
      .order-image {
        width: 100px;
        height: 100px;
        border-radius: 8px;
        flex-shrink: 0;
      }
      
      .order-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        
        .order-title {
          font-size: 16px;
          color: #333;
          margin-bottom: 8px;
        }
        
        .order-meta {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .price {
            color: #f5222d;
            font-size: 18px;
            font-weight: 600;
          }
          
          .quantity {
            color: #999;
          }
        }
      }
    }
    
    .order-body {
      background: #fff;
      border-radius: 8px;
      padding: 16px;
      
      .order-status {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;
        
        .status-label {
          color: #666;
        }
      }
      
      .order-meta-info {
        display: flex;
        gap: 32px;
        margin-bottom: 12px;
        
        .meta-item {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .meta-label {
            color: #666;
          }
          
          .meta-value {
            color: #333;
          }
        }
      }
      
      .order-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.settings-list {
  .setting-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 0;
    border-bottom: 1px solid #e8e8e8;
    
    &:last-child {
      border-bottom: none;
    }
    
    .setting-title {
      font-size: 16px;
      color: #333;
    }
    
    .setting-desc {
      font-size: 14px;
      color: #999;
    }
  }
}

@media (max-width: 768px) {
  .profile-wrapper {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .goods-list {
    grid-template-columns: 1fr;
  }
}
</style>
