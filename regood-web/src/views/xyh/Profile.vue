<template>
  <div class="xyh-profile">
    <xyh-header />

    <div class="profile-container">
      <div class="profile-wrapper">
        <!-- 左侧菜单 -->
        <div class="sidebar">
          <div class="user-info-card">
            <div class="user-header">
              <el-avatar
                :src="userAvatar"
                :size="80"
                class="user-avatar"
              ></el-avatar>
              <div class="user-detail">
                <div class="user-name">{{ userName }}</div>
                <div class="user-meta">
                  <el-tag
                    v-if="userVerified"
                    type="success"
                    size="mini"
                    class="cert-tag"
                  >
                    <i class="el-icon-success"></i> 已认证
                  </el-tag>
                  <el-tag
                    v-if="studentVerified"
                    type="primary"
                    size="mini"
                    class="cert-tag"
                  >
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
              <div class="stat-item">
                <div class="stat-value">{{ userStats.myFavoriteCount }}</div>
                <div class="stat-label">收藏</div>
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
            </el-menu-item>
            <el-menu-item index="my-sold">
              <i class="el-icon-shopping-bag-1"></i>
              <span>我卖出的</span>
            </el-menu-item>
            <el-menu-item index="my-bought">
              <i class="el-icon-shopping-bag-2"></i>
              <span>我买到的</span>
            </el-menu-item>
            <el-menu-item index="my-favorite">
              <i class="el-icon-star-on"></i>
              <span>我的收藏</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧内容区 -->
        <div class="content-area">
          <!-- 个人资料 -->
          <div v-if="activeMenu === 'profile'" class="profile-content">
            <div class="section-title">个人资料</div>
            <div class="profile-form">
              <el-form
                :model="profileForm"
                label-width="100px"
                class="form-content"
              >
                <el-form-item label="头像">
                  <div class="avatar-upload-wrapper">
                    <file-upload 
                      v-model="profileForm.avatar" 
                      :limit="1" 
                      :fileSize="5" 
                      :fileType="['png', 'jpg', 'jpeg']"
                      ref="avatarUploadRef"
                    />
                  </div>
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" disabled />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="profileForm.nickname" />
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
                <el-form-item>
                  <el-button type="primary" @click="handleSaveProfile">保存</el-button>
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
                <el-image :src="item.image" class="goods-image" fit="cover" />
                <div class="goods-info">
                  <div class="goods-title">{{ item.title }}</div>
                  <div class="goods-meta">
                    <span class="price">¥{{ item.price }}</span>
                    <span class="status" :class="item.status">{{
                      item.statusText
                    }}</span>
                  </div>
                  <div class="goods-actions">
                    <el-button
                      v-if="item.status === 'offline'"
                      type="success"
                      size="small"
                      @click="changeGoodsStatus(item.id, 'online')"
                      >上架</el-button
                    >
                    <el-button
                      v-if="item.status === 'onsale'"
                      type="warning"
                      size="small"
                      @click="changeGoodsStatus(item.id, 'offline')"
                      >下架</el-button
                    >
                    <el-button
                      type="primary"
                      size="small"
                      @click="editGoods(item.id)"
                      >编辑</el-button
                    >
                    <el-button
                      type="danger"
                      size="small"
                      @click="deleteGoods(item.id)"
                      >删除</el-button
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 我卖出的 -->
          <div v-if="activeMenu === 'my-sold'" class="profile-content">
            <div class="section-title">我卖出的</div>
            <div class="goods-list">
              <div v-for="order in soldList" :key="order.id" class="goods-item">
                <el-image :src="order.image" class="goods-image" fit="cover" />
                <div class="goods-info">
                  <div class="goods-title">{{ order.title }}</div>
                  <div class="goods-meta">
                    <span class="price">¥{{ order.price }}</span>
                    <el-tag :type="order.statusType" size="small">{{
                      order.statusText
                    }}</el-tag>
                  </div>
                  <div class="goods-meta">
                    <span class="meta-label">买家：{{ order.buyer }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 我买到的 -->
          <div v-if="activeMenu === 'my-bought'" class="profile-content">
            <div class="section-title">我买到的</div>
            <div class="goods-list">
              <div
                v-for="order in boughtList"
                :key="order.id"
                class="goods-item"
              >
                <el-image :src="order.image" class="goods-image" fit="cover" />
                <div class="goods-info">
                  <div class="goods-title">{{ order.title }}</div>
                  <div class="goods-meta">
                    <span class="price">¥{{ order.price }}</span>
                    <el-tag :type="order.statusType" size="small">{{
                      order.statusText
                    }}</el-tag>
                  </div>
                  <div class="goods-meta">
                    <span class="meta-label">卖家：{{ order.seller }}</span>
                  </div>
                  <div class="goods-actions">
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
                <el-image :src="item.image" class="goods-image" fit="cover" />
                <div class="goods-info">
                  <div class="goods-title">{{ item.title }}</div>
                  <div class="goods-meta">
                    <span class="price">¥{{ item.price }}</span>
                    <span class="condition">{{ item.condition }}</span>
                  </div>
                  <div class="goods-actions">
                    <el-button
                      type="primary"
                      size="small"
                      @click="buyNow(item.id)"
                      >立即购买</el-button
                    >
                    <el-button
                      type="info"
                      size="small"
                      @click="collect(item.id)"
                      >取消收藏</el-button
                    >
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
                <el-button
                  type="primary"
                  size="small"
                  @click="showPasswordModal = true"
                  >修改</el-button
                >
              </div>
              <div class="setting-item">
                <div class="setting-title">退出登录</div>
                <div class="setting-desc">退出当前账号</div>
                <el-button type="danger" size="small" @click="handleLogout"
                  >退出</el-button
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" :visible.sync="showPasswordModal" width="500px">
      <el-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordForm"
        label-width="100px"
      >
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
import XyhHeader from "@/components/XyhHeader";
import XyhFooter from "@/components/XyhFooter";
import FileUpload from "@/components/FileUpload";
import { mapGetters } from "vuex";
import {
  getMyProducts,
  deleteProduct,
  offlineProduct,
  onlineProduct,
} from "@/api/business/product";
import { getFavoriteList, removeFavorite } from "@/api/business/favorite";
import { getMySoldList, getMyBoughtList } from "@/api/business/order";
import { getInfo, updateUser } from "@/api/system/user";

const STATUS_MAP = {
  1: { text: "在售", class: "onsale" },
  2: { text: "已售", class: "sold" },
  0: { text: "下架", class: "offline" },
};

const ORDER_STATUS_MAP = {
  0: { text: "待支付", type: "warning" },
  1: { text: "已支付", type: "primary" },
  2: { text: "已发货", type: "primary" },
  3: { text: "已完成", type: "success" },
  4: { text: "已取消", type: "info" },
};

export default {
  name: "XyhProfile",
  components: {
    XyhHeader,
    XyhFooter,
    FileUpload,
  },
  data() {
    return {
      activeMenu: "profile",
      showPasswordModal: false,
      notificationEnabled: true,
      userVerified: false,
      studentVerified: false,
      userStats: {
        sellCount: 0,
        soldCount: 0,
        boughtCount: 0,
        myFavoriteCount: 0,
      },
      profileForm: {
        userId: null,
        username: "",
        nickname: "",
        email: "",
        phone: "",
        signature: "",
        creditScore: 100,
        registerTime: "",
      },
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: "请输入原密码", trigger: "blur" },
        ],
        newPassword: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          {
            min: 6,
            max: 20,
            message: "长度在 6 到 20 个字符",
            trigger: "blur",
          },
        ],
        confirmPassword: [
          { required: true, message: "请确认密码", trigger: "blur" },
          { validator: this.validatePassword, trigger: "blur" },
        ],
      },
      myPublishCount: 0,
      mySoldCount: 0,
      myBoughtCount: 0,
      publishList: [],
      soldList: [],
      boughtList: [],
      favoriteList: [],
    };
  },
  computed: {
    ...mapGetters(["name", "avatar"]),
    userName() {
      return this.profileForm.nickname || this.name || "用户";
    },
    userAvatar() {
      return (
        this.profileForm.avatar ||
        this.avatar ||
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
      );
    },
  },
  created() {
    const menu = this.$route.query.menu;
    if (menu) {
      this.activeMenu = menu;
    }
    this.loadUserInfo();
    this.loadAllData();
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await getInfo();
        if (res.code === 200 && res.data) {
          const data = res.data;
          this.profileForm = {
            userId: data.userId,
            username: data.username,
            nickname: data.nickname || data.username,
            email: data.email || "",
            phone: data.phone || "",
            signature: data.signature || "",
            creditScore: data.creditScore || 100,
            registerTime: data.createTime || "",
            avatar: data.avatar,
          };
        }
      } catch (error) {
        console.error("获取用户信息失败", error);
      }
    },
    async loadAllData() {
      await Promise.all([
        this.loadMyPublish(),
        this.loadMySold(),
        this.loadMyBought(),
        this.loadMyFavorite(),
      ]);
    },
    async loadMyPublish() {
      try {
        const res = await getMyProducts({ pageNum: 1, pageSize: 100 });
        if (res.code === 200) {
          const list = res.data.list || [];
          this.publishList = list.map((item) => ({
            id: item.productId,
            title: item.title,
            image: item.images && item.images.length > 0 ? item.images[0] : "",
            price: item.price,
            status: STATUS_MAP[item.status]?.class,
            statusText: STATUS_MAP[item.status]?.text,
          }));
          this.myPublishCount = list.filter((item) => item.status === 1).length;
          this.userStats.sellCount = this.myPublishCount;
        }
      } catch (error) {
        console.error("加载发布列表失败", error);
      }
    },
    async loadMySold() {
      try {
        const res = await getMySoldList();
        if (res.code === 200) {
          const list = res.data || [];
          this.soldList = list.map((item) => ({
            id: item.orderId,
            title: item.productTitle,
            image:
              item.productImages && item.productImages.length > 0
                ? item.productImages[0]
                : "",
            price: item.price,
            quantity: item.quantity || 1,
            statusType: ORDER_STATUS_MAP[item.status]?.type,
            statusText: ORDER_STATUS_MAP[item.status]?.text,
            buyer: item.buyerName || "买家",
            time: item.createTime,
          }));
          this.mySoldCount = list.length;
          this.userStats.soldCount = this.mySoldCount;
        }
      } catch (error) {
        console.error("加载卖出列表失败", error);
      }
    },
    async loadMyBought() {
      try {
        const res = await getMyBoughtList();
        if (res.code === 200) {
          const list = res.data || [];
          this.boughtList = list.map((item) => ({
            id: item.orderId,
            title: item.productTitle,
            image:
              item.productImages && item.productImages.length > 0
                ? item.productImages[0]
                : "",
            price: item.price,
            quantity: item.quantity || 1,
            statusType: ORDER_STATUS_MAP[item.status]?.type || "info",
            statusText: ORDER_STATUS_MAP[item.status]?.text || "未知",
            seller: item.sellerName,
            time: item.createTime,
            canComment: item.status === 3,
            canReview: item.status === 3,
          }));
          this.myBoughtCount = list.length;
          this.userStats.boughtCount = this.myBoughtCount;
        }
      } catch (error) {
        console.error("加载买入列表失败", error);
      }
    },
    async loadMyFavorite() {
      try {
        const res = await getFavoriteList();
        if (res.code === 200) {
          const list = res.data || [];
          this.favoriteList = list.map((item) => ({
            id: item.productId,
            title: item.title,
            image: item.images && item.images.length > 0 ? item.images[0] : "",
            price: item.price,
            condition: item.condition || "",
          }));
          this.userStats.myFavoriteCount = list.length;
        }
      } catch (error) {
        console.error("加载收藏列表失败", error);
      }
    },
    handleMenuSelect(index) {
      this.activeMenu = index;
    },
    getProgressColor(score) {
      if (score >= 90) return "#67c23a";
      if (score >= 80) return "#e6a23c";
      return "#f56c6c";
    },
    async handleSaveProfile() {
      try {
        await updateUser({
          userId: this.profileForm.userId,
          nickname: this.profileForm.nickname,
          email: this.profileForm.email,
          phone: this.profileForm.phone,
          signature: this.profileForm.signature,
          avatar: this.profileForm.avatar,
        });
        this.$message.success("保存成功");
        this.loadUserInfo();
      } catch (error) {
        console.error("保存失败", error);
        this.$message.error("保存失败");
      }
    },
    editGoods(id) {
      this.$router.push({ path: "/publish", query: { id } });
    },
    async deleteGoods(id) {
      try {
        await this.$confirm("确定要删除此商品吗？", "提示", {
          type: "warning",
        });
        await deleteProduct(id);
        this.$message.success("删除成功");
        this.loadMyPublish();
      } catch (error) {
        if (error !== "cancel") {
          console.error("删除失败", error);
        }
      }
    },
    async changeGoodsStatus(id, action) {
      try {
        if (action === "online") {
          await this.$confirm("确定要上架此商品吗？", "提示", { type: "info" });
          await onlineProduct(id);
          this.$message.success("上架成功");
        } else if (action === "offline") {
          await this.$confirm("确定要下架此商品吗？", "提示", {
            type: "warning",
          });
          await offlineProduct(id);
          this.$message.success("下架成功");
        }
        this.loadMyPublish();
      } catch (error) {
        if (error !== "cancel") {
          console.error("操作失败", error);
        }
      }
    },
    commentOrder(id) {
      this.$message.info("评价功能开发中");
    },
    reviewSeller(id) {
      this.$message.info("评价卖家功能开发中");
    },
    buyNow(id) {
      this.$router.push(`/goods/${id}`);
    },
    async collect(id) {
      try {
        await removeFavorite(id);
        this.$message.success("已取消收藏");
        this.loadMyFavorite();
      } catch (error) {
        console.error("取消收藏失败", error);
      }
    },
    viewSecurity() {
      this.$message.info("账号安全功能开发中");
    },
    handleLogout() {
      this.$confirm("确定要退出登录吗？", "提示", {
        type: "warning",
      })
        .then(() => {
          this.$store.dispatch("user/logout").then(() => {
            this.$router.push("/login");
          });
        })
        .catch(() => {});
    },
    handleUpdatePassword() {
      this.$refs.passwordForm.validate((valid) => {
        if (valid) {
          this.$message.info("修改密码功能开发中");
          this.showPasswordModal = false;
        }
      });
    },
    validatePassword(rule, value, callback) {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error("两次输入密码不一致"));
      } else {
        callback();
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.xyh-profile {
  min-height: 100vh;
  background: #f5f5f5;
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
  background: linear-gradient(135deg, #ff8c42 0%, #ffa94d 100%);
  border-radius: 12px;
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
    background: rgba(255, 255, 255, 0.15);
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
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  .el-menu-item {
    height: 50px;
    line-height: 50px;
    font-size: 14px;
    border-radius: 8px;
    margin: 4px 8px;

    i {
      margin-right: 8px;
      color: #666;
    }

    .badge {
      margin-left: auto;
    }

    &:hover {
      background: #fff8f5;
    }

    &.is-active {
      background: linear-gradient(135deg, #ff8c42 0%, #ffa94d 100%);
      color: #fff;

      i {
        color: #fff;
      }
    }
  }
}

.content-area {
  flex: 1;
  min-width: 0;
}

.profile-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 24px;

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 24px;
    padding-bottom: 12px;
    border-bottom: 2px solid #ff8c42;
  }
}

.profile-form {
  .form-content {
    .avatar-upload-wrapper {
      display: flex;
      align-items: center;
      gap: 20px;
    }
    .credit-score {
      display: flex;
      align-items: center;
      gap: 20px;

      .score-value {
        font-size: 32px;
        font-weight: 700;
        color: #ff8c42;
      }
    }
  }
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;

  .goods-item {
    display: flex;
    gap: 16px;
    padding: 16px;
    background: #fafafa;
    border-radius: 12px;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 16px rgba(255, 107, 0, 0.1);
      transform: translateY(-2px);
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
          color: #ff4d4f;
          font-size: 18px;
          font-weight: 600;
        }

        .status {
          font-size: 12px;
          padding: 2px 8px;
          border-radius: 4px;

          &.onsale {
            background: #fff8f5;
            color: #ff8c42;
          }

          &.sold {
            background: #f6ffed;
            color: #52c41a;
          }

          &.offline {
            background: #f5f5f5;
            color: #999;
          }
        }

        .condition {
          font-size: 12px;
          color: #999;
        }
      }

      .goods-actions {
        display: flex;
        gap: 8px;

        .el-button--primary {
          background: linear-gradient(135deg, #ff8c42 0%, #ffa94d 100%);
          border: none;

          &:hover {
            opacity: 0.9;
          }
        }
      }
    }
  }
}

.order-list {
  .order-item {
    background: #fafafa;
    border-radius: 12px;
    margin-bottom: 16px;
    overflow: hidden;

    .order-header {
      display: flex;
      gap: 16px;
      padding: 16px;

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
            color: #ff4d4f;
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
      padding: 16px;
      border-top: 1px solid #f0f0f0;

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

          .meta-label {
            color: #999;
          }

          .meta-value {
            color: #333;
          }
        }
      }

      .order-actions {
        display: flex;
        gap: 8px;

        .el-button--primary {
          background: linear-gradient(135deg, #ff8c42 0%, #ffa94d 100%);
          border: none;
        }
      }
    }
  }
}

.settings-list {
  .setting-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 0;
    border-bottom: 1px solid #f0f0f0;

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

    .el-button--primary {
      background: linear-gradient(135deg, #ff8c42 0%, #ffa94d 100%);
      border: none;
    }

    .el-button--danger {
      background: #ff4d4f;
      border: none;
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
