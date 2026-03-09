<template>
  <div class="xyh-header">
    <div class="header-container">
      <!-- 左侧 Logo 和校区切换 -->
      <div class="header-left">
        <div class="logo" @click="goHome">
          <span class="logo-icon">🎓</span>
          <span class="logo-text">学易换</span>
        </div>
        <el-dropdown trigger="click" @command="handleCampusChange">
          <div class="campus-selector">
            <span class="campus-label">当前校区：</span>
            <span class="campus-name">{{ currentCampus }}</span>
            <i class="el-icon-arrow-down"></i>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="main">主校区</el-dropdown-item>
            <el-dropdown-item command="east">东校区</el-dropdown-item>
            <el-dropdown-item command="west">西校区</el-dropdown-item>
            <el-dropdown-item command="south">南校区</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <!-- 中间搜索框 -->
      <div class="header-center">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品、分类..."
            prefix-icon="el-icon-search"
            class="xyh-search-input"
            @keyup.enter.native="handleSearch"
          />
          <el-button type="primary" class="search-btn" @click="handleSearch">搜索</el-button>
        </div>
      </div>

      <!-- 右侧功能按钮 -->
      <div class="header-right">
        <el-button 
          type="primary" 
          class="publish-btn"
          icon="el-icon-plus"
          @click="handlePublish"
        >
          发布商品
        </el-button>
        
        <div class="action-icons">
          <!-- 消息 -->
          <div class="icon-item" @click="goToMessage">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0">
              <i class="el-icon-chat-dot-round"></i>
            </el-badge>
            <span class="icon-label">消息</span>
          </div>
          
          <!-- 购物车 -->
          <div class="icon-item" @click="goToCart">
            <el-badge :value="cartCount" :hidden="cartCount === 0">
              <i class="el-icon-shopping-cart-2"></i>
            </el-badge>
            <span class="icon-label">购物车</span>
          </div>
          
          <!-- 用户头像 -->
          <el-dropdown trigger="click" class="user-dropdown">
            <div class="avatar-wrapper">
              <el-avatar :src="userAvatar" :size="36" class="user-avatar"></el-avatar>
              <i class="el-icon-arrow-down"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="goToProfile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item @click.native="goToMyPublish">
                <i class="el-icon-document"></i> 我的发布
              </el-dropdown-item>
              <el-dropdown-item @click.native="goToMySold">
                <i class="el-icon-shopping-bag-1"></i> 我卖出的
              </el-dropdown-item>
              <el-dropdown-item @click.native="goToMyBought">
                <i class="el-icon-shopping-bag-2"></i> 我买到的
              </el-dropdown-item>
              <el-dropdown-item @click.native="goToMyFavorite">
                <i class="el-icon-star-on"></i> 我的收藏
              </el-dropdown-item>
              <el-dropdown-item divided @click.native="handleLogout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'XyhHeader',
  data() {
    return {
      searchKeyword: '',
      currentCampus: '主校区',
      unreadCount: 3,
      cartCount: 2
    }
  },
  computed: {
    ...mapGetters(['avatar', 'name']),
    userAvatar() {
      return this.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }
  },
  methods: {
    goHome() {
      this.$router.push('/home')
    },
    handleCampusChange(command) {
      const campusMap = {
        main: '主校区',
        east: '东校区',
        west: '西校区',
        south: '南校区'
      }
      this.currentCampus = campusMap[command]
      this.$message.success(`已切换到${this.currentCampus}`)
    },
    handleSearch() {
      if (this.searchKeyword.trim()) {
        this.$router.push({
          path: '/home',
          query: { keyword: this.searchKeyword }
        })
      }
    },
    handlePublish() {
      this.$router.push('/publish')
    },
    goToMessage() {
      this.$router.push('/message')
    },
    goToCart() {
      this.$router.push('/cart')
    },
    goToProfile() {
      this.$router.push('/profile')
    },
    goToMyPublish() {
      this.$router.push('/my/publish')
    },
    goToMySold() {
      this.$router.push('/my/sold')
    },
    goToMyBought() {
      this.$router.push('/my/bought')
    },
    goToMyFavorite() {
      this.$router.push('/my/favorite')
    },
    async handleLogout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.3s;
  
  &:hover {
    opacity: 0.8;
  }
  
  .logo-icon {
    font-size: 32px;
  }
  
  .logo-text {
    font-size: 24px;
    font-weight: 600;
    color: #1890ff;
  }
}

.campus-selector {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
  background: #f5f7fa;
  transition: background 0.3s;
  
  &:hover {
    background: #e8e8e8;
  }
  
  .campus-label {
    font-size: 12px;
    color: #666;
  }
  
  .campus-name {
    font-size: 14px;
    font-weight: 500;
    color: #333;
  }
}

.header-center {
  flex: 1;
  max-width: 600px;
  margin: 0 40px;
}

.search-box {
  display: flex;
  gap: 0;
  
  .xyh-search-input {
    flex: 1;
    
    ::v-deep .el-input__inner {
      border-radius: 20px 0 0 20px;
      border: 2px solid #1890ff;
      height: 40px;
    }
  }
  
  .search-btn {
    border-radius: 0 20px 20px 0;
    height: 40px;
    padding: 0 24px;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.publish-btn {
  background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
  border: none;
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
}

.action-icons {
  display: flex;
  align-items: center;
  gap: 20px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s;
  
  &:hover {
    transform: translateY(-2px);
  }
  
  i {
    font-size: 24px;
    color: #666;
  }
  
  .icon-label {
    font-size: 12px;
    color: #666;
    margin-top: 4px;
  }
}

.user-dropdown {
  .avatar-wrapper {
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    
    .user-avatar {
      border: 2px solid #1890ff;
    }
  }
}
</style>
