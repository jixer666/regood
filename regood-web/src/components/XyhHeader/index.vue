<template>
  <div class="xyh-header">
    <div class="header-container">
      <div class="header-left">
        <div class="logo" @click="goHome">
          <div class="logo-icon">
            <i class="el-icon-s-goods"></i>
          </div>
          <div class="logo-text">
            <span class="logo-name">学易换</span>
            <span class="logo-slogan">校园闲置交易平台</span>
          </div>
        </div>
      </div>

      <div class="header-center">
        <div class="search-wrapper">
          <el-select
            v-model="searchType"
            placeholder="全部"
            class="search-type"
          >
            <el-option label="全部" value="all" />
            <el-option
              v-for="cat in categories"
              :key="cat.categoryId"
              :label="cat.name"
              :value="cat.categoryId"
            />
          </el-select>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索你想要的宝贝..."
            class="search-input"
            @keyup.enter.native="handleSearch"
          />
          <el-button type="primary" class="search-btn" @click="handleSearch">
            <i class="el-icon-search"></i>
          </el-button>
        </div>
      </div>

      <div class="header-right">
        <div class="publish-btn" @click="handlePublish" v-if="isLoggedIn">
          <i class="el-icon-plus"></i>
          <span>发布闲置</span>
        </div>

        <div class="nav-actions">
          <template v-if="isLoggedIn">
            <div class="action-item" @click="goToMessage">
              <i class="el-icon-chat-dot-round"></i>
              <span class="action-label">消息</span>
            </div>

            <div class="action-item" @click="goToCart">
              <i class="el-icon-shopping-cart-2"></i>
              <span class="action-label">购物车</span>
            </div>

            <div class="action-item" @click="goToFavorite">
              <i class="el-icon-star-off"></i>
              <span class="action-label">收藏</span>
            </div>
          </template>
        </div>

        <template v-if="isLoggedIn">
          <el-dropdown
            trigger="click"
            class="user-dropdown"
            @command="handleCommand"
          >
            <div class="user-info">
              <el-avatar :src="userAvatar" :size="38" class="user-avatar" />
              <span class="user-name">{{ userName || "用户" }}</span>
              <i class="el-icon-arrow-down"></i>
            </div>
            <el-dropdown-menu slot="dropdown" class="user-menu">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i>
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item command="logout" class="logout-item">
                <i class="el-icon-switch-button"></i>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" class="login-btn" @click="goToLogin">
            登录/注册
          </el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { getCategoryTree } from "@/api/business/category";

export default {
  name: "XyhHeader",
  data() {
    return {
      searchKeyword: "",
      searchType: "all",
      categories: [],
      unreadCount: 3,
      cartCount: 2,
    };
  },
  created() {
    this.loadCategories();
  },
  computed: {
    ...mapGetters(["avatar", "name", "userId", "token"]),
    isLoggedIn() {
      return !!this.token;
    },
    userAvatar() {
      return (
        this.avatar ||
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
      );
    },
    userName() {
      return this.name || "用户";
    },
  },
  methods: {
    async loadCategories() {
      try {
        const res = await getCategoryTree();
        if (res.code === 200 && res.data) {
          this.categories = res.data;
        }
      } catch (error) {
        console.error("加载分类失败", error);
      }
    },
    goHome() {
      this.$router.push("/home");
    },
    handleSearch() {
      const query = {
        type: this.searchType,
        t: Date.now()
      }
      if (this.searchKeyword.trim()) {
        query.keyword = this.searchKeyword
      }
      if (this.$route.path === '/home') {
        this.$router.replace({ path: '/home', query })
      } else {
        this.$router.push({ path: '/home', query })
      }
    },
    handlePublish() {
      if (!this.isLoggedIn) {
        this.$router.push(`/login?redirect=${this.$route.fullPath}`);
        return;
      }
      this.$router.push("/publish");
    },
    goToMessage() {
      this.$router.push("/message");
    },
    goToCart() {
      this.$router.push("/cart");
    },
    goToFavorite() {
      this.$router.push("/my/favorite");
    },
    handleCommand(command) {
      const routes = {
        profile: "/profile",
        publish: "/my/publish",
        sold: "/my/sold",
        bought: "/my/bought",
        favorite: "/my/favorite",
      };
      if (command === "logout") {
        this.handleLogout();
      } else if (routes[command]) {
        this.$router.push(routes[command]);
      }
    },
    async handleLogout() {
      await this.$store.dispatch("user/logout");
      this.$router.push(`/login?redirect=${this.$route.fullPath}`);
    },
    goToLogin() {
      this.$router.push(`/login?redirect=${this.$route.fullPath}`);
    },
  },
};
</script>

<style lang="scss" scoped>
.xyh-header {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  flex-shrink: 0;

  .logo {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition: transform 0.2s;

    .logo-icon {
      width: 44px;
      height: 44px;
      background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        font-size: 24px;
        color: #fff;
      }
    }

    .logo-text {
      display: flex;
      flex-direction: column;

      .logo-name {
        font-size: 22px;
        font-weight: 700;
        color: #1a1a1a;
        line-height: 1.2;
      }

      .logo-slogan {
        font-size: 11px;
        color: #86909c;
        letter-spacing: 1px;
      }
    }
  }
}

.header-center {
  flex: 1;
  max-width: 560px;
  margin: 0 32px;

  .search-wrapper {
    display: flex;
    align-items: center;
    background: #f7f8fa;
    border-radius: 24px;
    border: 2px solid transparent;
    transition: all 0.2s;

    &:hover,
    &:focus-within {
      border-color: #ff6b00;
      background: #fff;
    }

    .search-type {
      width: 100px;

      ::v-deep .el-input__inner {
        background: transparent;
        border: none;
        height: 40px;
        font-size: 14px;
        color: #4e5969;
        padding-left: 16px;
      }

      ::v-deep .el-input__suffix {
        right: 8px;
      }
    }

    .search-input {
      flex: 1;

      ::v-deep .el-input__inner {
        background: transparent;
        border: none;
        height: 40px;
        font-size: 14px;
        padding-left: 0;

        &::placeholder {
          color: #c9cdd4;
        }
      }
    }

    .search-btn {
      height: 36px;
      width: 36px;
      padding: 0;
      border-radius: 50%;
      background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
      border: none;
      margin-right: 4px;
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        font-size: 16px;
        margin: 0;
      }

      &:hover {
        background: linear-gradient(135deg, #ff8533 0%, #ff9d55 100%);
      }
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-shrink: 0;

  .publish-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 20px;
    background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
    border-radius: 20px;
    color: #fff;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;

    i {
      font-size: 16px;
    }

    &:hover {
      box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
    }
  }

  .nav-actions {
    display: flex;
    align-items: center;
    gap: 16px;

    .action-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      padding: 4px 8px;
      transition: all 0.2s;

      &:hover {
        i {
          color: #ff6b00;
        }

        .action-label {
          color: #ff6b00;
        }
      }

      ::v-deep .el-badge__content {
        background: #ff4d4f;
        border: none;
        height: 16px;
        line-height: 16px;
        padding: 0 5px;
        font-size: 10px;
      }

      i {
        font-size: 22px;
        color: #4e5969;
        transition: color 0.2s;
      }

      .action-label {
        font-size: 11px;
        color: #86909c;
        margin-top: 2px;
        transition: color 0.2s;
      }
    }
  }

  .user-dropdown {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 6px 12px 6px 6px;
      border-radius: 20px;
      transition: background 0.2s;

      &:hover {
        background: #f7f8fa;
      }

      .user-avatar {
        border: 2px solid #ff6b00;
      }

      .user-name {
        font-size: 14px;
        color: #1a1a1a;
        font-weight: 500;
        max-width: 80px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .el-icon-arrow-down {
        font-size: 12px;
        color: #86909c;
        transition: transform 0.2s;
      }
    }

    &.is-opened .user-info .el-icon-arrow-down {
      transform: rotate(180deg);
    }
  }

  .login-btn {
    background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
    border: none;
    border-radius: 20px;
    padding: 10px 24px;
    font-weight: 500;

    &:hover {
      background: linear-gradient(135deg, #ff8533 0%, #ff9d55 100%);
      box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
    }
  }
}

::v-deep .user-menu {
  padding: 0;
  width: 220px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;

  .menu-profile {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: #fafafa;
    border-bottom: 1px solid #f0f0f0;

    .profile-avatar {
      border: 2px solid #ff6b00;
    }

    .profile-info {
      .profile-name {
        font-size: 15px;
        font-weight: 600;
        color: #1a1a1a;
      }

      .profile-id {
        font-size: 12px;
        color: #86909c;
        margin-top: 2px;
      }
    }
  }

  .menu-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    padding: 8px;
    border-bottom: 1px solid #f0f0f0;

    .el-dropdown-menu__item {
      flex-direction: column;
      padding: 12px 4px;
      gap: 6px;
      justify-content: center;

      i {
        font-size: 22px;
        margin-bottom: 0;
      }

      span {
        font-size: 11px;
      }
    }
  }

  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 16px;
    font-size: 14px;
    color: #4e5969;

    i {
      font-size: 16px;
      color: #86909c;
    }

    &:hover {
      background: #fff9f5;
      color: #ff6b00;

      i {
        color: #ff6b00;
      }
    }
  }

  .logout-item {
    color: #ff4d4f;

    i {
      color: #ff4d4f;
    }

    &:hover {
      background: #fff5f5;
    }
  }
}

@media (max-width: 1200px) {
  .header-center {
    max-width: 400px;
    margin: 0 20px;
  }
}

@media (max-width: 992px) {
  .header-container {
    padding: 0 16px;
    height: 60px;
  }

  .header-left .logo {
    .logo-icon {
      width: 36px;
      height: 36px;

      i {
        font-size: 20px;
      }
    }

    .logo-text .logo-name {
      font-size: 18px;
    }
  }

  .header-center {
    margin: 0 16px;
  }

  .header-right {
    gap: 12px;

    .publish-btn span {
      display: none;
    }

    .nav-actions {
      gap: 8px;

      .action-item .action-label {
        display: none;
      }
    }

    .user-dropdown .user-info .user-name {
      display: none;
    }
  }
}

@media (max-width: 768px) {
  .header-center {
    display: none;
  }

  .header-right .nav-actions {
    display: none;
  }
}
</style>
