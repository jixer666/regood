<template>
  <div class="test-page">
    <div class="test-container">
      <h1 class="test-title">学易换校园二手交易平台</h1>
      <p class="test-desc">前端页面已成功加载！</p>
      <div class="test-links">
        <el-button type="primary" @click="goHome">访问首页</el-button>
        <el-button type="info" @click="goLogin">访问登录页</el-button>
        <el-button type="success" @click="goPublish">访问发布页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TestPage',
  methods: {
    goHome() {
      this.$router.push('/home')
    },
    goLogin() {
      this.$router.push('/login')
    },
    goPublish() {
      this.$router.push('/publish')
    }
  }
}
</script>

<style lang="scss" scoped>
.test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.test-container {
  text-align: center;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  padding: 40px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
}

.test-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.test-desc {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.test-links {
  .el-button {
    margin: 0 10px;
    padding: 12px 32px;
    font-size: 16px;
  }
}
</style>
