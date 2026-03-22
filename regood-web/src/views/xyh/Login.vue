<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧插画区 -->
      <div class="login-left">
        <div class="illustration">
          <div class="campus-scene">
            <span class="scene-icon">🏫</span>
            <span class="scene-icon">📚</span>
            <span class="scene-icon">🚴</span>
            <span class="scene-icon">🎓</span>
          </div>
          <h1 class="slogan">学易换 · 校园二手交易</h1>
          <p class="sub-slogan">让闲置流动起来，让信任传递下去</p>
          <div class="features">
            <div class="feature-item">
              <span class="feature-icon">🛡️</span>
              <span>实名认证 · 安全可靠</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">⚡</span>
              <span>快速交易 · 便捷高效</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">💰</span>
              <span>超值好物 · 学生专享</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-right">
        <div class="login-box">
          <div class="login-header">
            <h2 class="login-title">欢迎回来</h2>
            <p class="login-subtitle">请登录您的账号</p>
          </div>

          <!-- Tab 切换 -->
          <el-tabs v-model="activeTab" class="login-tabs">
            <el-tab-pane label="密码登录" name="password"></el-tab-pane>
            <el-tab-pane label="验证码登录" name="sms"></el-tab-pane>
            <el-tab-pane label="账号注册" name="register"></el-tab-pane>
          </el-tabs>

          <!-- 密码登录表单 -->
          <el-form
            v-show="activeTab === 'password'"
            ref="passwordForm"
            :model="passwordForm"
            :rules="passwordRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="passwordForm.username"
                placeholder="请输入手机号/学号/邮箱"
                prefix-icon="el-icon-user"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="passwordForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                @keyup.enter.native="handlePasswordLogin"
              />
            </el-form-item>
            <div class="form-options">
              <el-checkbox v-model="passwordForm.remember">记住密码</el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handlePasswordLogin"
            >
              登录
            </el-button>
          </el-form>

          <!-- 验证码登录表单 -->
          <el-form
            v-show="activeTab === 'sms'"
            ref="smsForm"
            :model="smsForm"
            :rules="smsRules"
            class="login-form"
          >
            <el-form-item prop="phone">
              <el-input
                v-model="smsForm.phone"
                placeholder="请输入手机号"
                prefix-icon="el-icon-mobile"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="code">
              <div class="code-input">
                <el-input
                  v-model="smsForm.code"
                  placeholder="请输入验证码"
                  prefix-icon="el-icon-key"
                  size="large"
                />
                <el-button
                  class="send-code-btn"
                  :disabled="countdown > 0"
                  @click="handleSendCode"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleSmsLogin"
            >
              登录
            </el-button>
          </el-form>

          <!-- 注册表单 -->
          <el-form
            v-show="activeTab === 'register'"
            ref="registerForm"
            :model="registerForm"
            :rules="registerRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入手机号"
                prefix-icon="el-icon-mobile"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请设置密码（6-20 位）"
                prefix-icon="el-icon-lock"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="code">
              <div class="code-input">
                <el-input
                  v-model="registerForm.code"
                  placeholder="请输入验证码"
                  prefix-icon="el-icon-key"
                  size="large"
                />
                <el-button
                  class="send-code-btn"
                  :disabled="countdown > 0"
                  @click="handleSendCode"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item prop="studentId">
              <el-input
                v-model="registerForm.studentId"
                placeholder="请输入学号（选填，认证后可提高信誉度）"
                prefix-icon="el-icon-reading"
                size="large"
              />
            </el-form-item>
            <div class="security-tip">
              <span class="tip-icon">🛡️</span>
              <span class="tip-text">保障平台交易安全 · 学籍认证后可解锁更多功能</span>
            </div>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleRegister"
            >
              立即注册
            </el-button>
          </el-form>

          <!-- 第三方登录 -->
          <div class="third-party-login">
            <div class="divider">
              <span>其他登录方式</span>
            </div>
            <div class="third-party-icons">
              <div class="third-icon wechat" title="微信登录">
                <i class="el-icon-chat-round"></i>
              </div>
              <div class="third-icon qq" title="QQ 登录">
                <i class="el-icon-chat-dot-square"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'XyhLogin',
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }

    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6 || value.length > 20) {
        callback(new Error('密码长度为 6-20 位'))
      } else {
        callback()
      }
    }

    return {
      activeTab: 'password',
      loading: false,
      countdown: 0,
      passwordForm: {
        username: '',
        password: '',
        remember: false
      },
      smsForm: {
        phone: '',
        code: ''
      },
      registerForm: {
        username: '',
        password: '',
        code: '',
        studentId: ''
      },
      passwordRules: {
        username: [{ required: true, message: '请输入手机号/学号/邮箱', trigger: 'blur' }],
        password: [{ required: true, validator: validatePassword, trigger: 'blur' }]
      },
      smsRules: {
        phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      registerRules: {
        username: [{ required: true, validator: validatePhone, trigger: 'blur' }],
        password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapGetters(['token'])
  },
  watch: {
    token: {
      handler(newVal) {
        if (newVal) {
          this.$router.push({ path: this.redirect || '/home' })
        }
      },
      immediate: true
    }
  },
  created() {
    this.redirect = this.$route.query.redirect
  },
  methods: {
    async handlePasswordLogin() {
      try {
        await this.$refs.passwordForm.validate()
        this.loading = true
        await this.$store.dispatch('user/login', {
          username: this.passwordForm.username.trim(),
          password: this.passwordForm.password,
          authType: 1
        })
        this.$message.success('登录成功')
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async handleSmsLogin() {
      try {
        await this.$refs.smsForm.validate()
        this.loading = true
        await this.$store.dispatch('user/login', {
          username: this.smsForm.phone.trim(),
          password: this.smsForm.code,
          authType: 2
        })
        this.$message.success('登录成功')
        this.$router.push('/home')
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async handleRegister() {
      try {
        await this.$refs.registerForm.validate()
        this.loading = true
        await this.$store.dispatch('user/login', {
          username: this.registerForm.username.trim(),
          password: this.registerForm.password,
          authType: 1
        })
        this.$message.success('注册成功，请登录')
        this.activeTab = 'password'
        this.passwordForm.username = this.registerForm.username
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    handleSendCode() {
      if (this.countdown > 0) return
      
      const phone = this.passwordForm.username || this.smsForm.phone || this.registerForm.username
      if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
        this.$message.warning('请输入正确的手机号')
        return
      }
      
      // 模拟发送验证码
      this.$message.success('验证码已发送')
      this.countdown = 60
      const timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-wrapper {
  width: 100%;
  max-width: 1000px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.illustration {
  text-align: center;
  color: #fff;
  
  .campus-scene {
    margin-bottom: 30px;
    
    .scene-icon {
      font-size: 60px;
      margin: 0 10px;
      display: inline-block;
      animation: bounce 2s infinite;
      
      &:nth-child(2) {
        animation-delay: 0.2s;
      }
      &:nth-child(3) {
        animation-delay: 0.4s;
      }
      &:nth-child(4) {
        animation-delay: 0.6s;
      }
    }
  }
  
  .slogan {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 12px;
  }
  
  .sub-slogan {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 40px;
  }
  
  .features {
    text-align: left;
    
    .feature-item {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
      font-size: 16px;
      
      .feature-icon {
        font-size: 24px;
      }
    }
  }
}

.login-right {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 100%;
  max-width: 360px;
}

.login-header {
  margin-bottom: 30px;
  
  .login-title {
    font-size: 28px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
  
  .login-subtitle {
    font-size: 14px;
    color: #999;
  }
}

.login-tabs {
  margin-bottom: 24px;
  
  ::v-deep .el-tabs__item {
    font-size: 16px;
  }
  
  ::v-deep .el-tabs__active-bar {
    background: #1890ff;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .code-input {
    display: flex;
    gap: 12px;
    
    .el-input {
      flex: 1;
    }
    
    .send-code-btn {
      width: 120px;
    }
  }
  
  .security-tip {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px;
    background: #f6ffed;
    border: 1px solid #b7eb8f;
    border-radius: 8px;
    margin-bottom: 20px;
    
    .tip-icon {
      font-size: 18px;
    }
    
    .tip-text {
      font-size: 13px;
      color: #52c41a;
    }
  }
  
  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
    font-weight: 500;
    background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
    border: none;
  }
}

.third-party-login {
  margin-top: 30px;
  
  .divider {
    position: relative;
    text-align: center;
    margin-bottom: 20px;
    
    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      right: 0;
      height: 1px;
      background: #e8e8e8;
    }
    
    span {
      position: relative;
      background: #fff;
      padding: 0 16px;
      color: #999;
      font-size: 14px;
    }
  }
  
  .third-party-icons {
    display: flex;
    justify-content: center;
    gap: 20px;
    
    .third-icon {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s;
      
      i {
        font-size: 24px;
        color: #fff;
      }
      
      &.wechat {
        background: #07c160;
        
        &:hover {
          background: #06ad56;
          transform: scale(1.1);
        }
      }
      
      &.qq {
        background: #12b7f5;
        
        &:hover {
          background: #0ea6e1;
          transform: scale(1.1);
        }
      }
    }
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@media (max-width: 768px) {
  .login-left {
    display: none;
  }
  
  .login-right {
    padding: 40px 20px;
  }
}
</style>
