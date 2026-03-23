<template>
  <div class="login-container">
    <div class="login-wrapper">
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

      <div class="login-right">
        <div class="login-box">
          <div class="login-header">
            <h2 class="login-title">欢迎回来</h2>
            <p class="login-subtitle">登录/注册后开启校园交易之旅</p>
          </div>

          <el-form
            ref="loginForm"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="email">
              <el-input
                v-model="loginForm.email"
                placeholder="请输入邮箱地址"
                prefix-icon="el-icon-message"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="emailCode">
              <div class="code-input-wrapper">
                <el-input
                  v-model="loginForm.emailCode"
                  placeholder="请输入6位验证码"
                  prefix-icon="el-icon-key"
                  size="large"
                  maxlength="6"
                  class="code-input"
                  @keyup.enter.native="handleLogin"
                />
                <el-button
                  class="send-code-btn"
                  :class="{ 'is-disabled': countdown > 0 }"
                  :disabled="countdown > 0"
                  :loading="sendingCode"
                  size="large"
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
              @click="handleLogin"
            >
              登录 / 注册
            </el-button>
          </el-form>

          <div class="third-party-login">
            <div class="divider">
              <span>其他登录方式</span>
            </div>
            <div class="third-party-icons">
              <div class="third-icon wechat" title="微信登录" @click="handleSocialLogin('wechat')">
                <i class="el-icon-chat-round"></i>
              </div>
              <div class="third-icon qq" title="QQ 登录" @click="handleSocialLogin('qq')">
                <i class="el-icon-chat-dot-square"></i>
              </div>
            </div>
          </div>

          <div class="footer-agreement">
            登录即同意 <a href="#" @click.prevent="showAgreement('user')">用户协议</a> 和 <a href="#" @click.prevent="showAgreement('privacy')">隐私政策</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { sendRegisterEmail } from '@/api/system/user'

export default {
  name: 'XyhLogin',
  data() {
    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮箱地址'))
      } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value)) {
        callback(new Error('请输入正确的邮箱地址'))
      } else {
        callback()
      }
    }

    const validateCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入验证码'))
      } else if (value.length !== 6) {
        callback(new Error('验证码为6位数字'))
      } else {
        callback()
      }
    }

    return {
      loginForm: {
        email: '',
        emailCode: '',
        authType: 2,
        emailUuid: ''
      },
      loginRules: {
        email: [{ required: true, validator: validateEmail, trigger: 'blur' }],
        emailCode: [{ required: true, validator: validateCode, trigger: 'blur' }]
      },
      loading: false,
      sendingCode: false,
      countdown: 0,
      timer: null
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
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  created() {
    this.redirect = this.$route.query.redirect
    const isRegister = this.$route.query.isRegister
    if (isRegister) {
      this.$message.info('请完成注册')
    }
  },
  methods: {
    handleSendCode() {
      const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
      if (!emailRegex.test(this.loginForm.email)) {
        this.$message.warning('请先输入正确的邮箱地址')
        return
      }

      this.sendingCode = true
      sendRegisterEmail({ email: this.loginForm.email })
        .then(response => {
          this.sendingCode = false
          this.loginForm.emailUuid = response.data.emailUuid
          this.$message.success('验证码已发送')
          this.countdown = 60
          this.timer = setInterval(() => {
            if (this.countdown > 0) {
              this.countdown--
            } else {
              clearInterval(this.timer)
            }
          }, 1000)
        })
        .catch(error => {
          this.sendingCode = false
          this.$message.error('发送验证码失败，请重试')
          console.error('发送验证码失败:', error)
        })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return

        this.loading = true
        this.$store.dispatch('user/login', {
          email: this.loginForm.email.trim(),
          emailCode: this.loginForm.emailCode,
          authType: this.loginForm.authType,
          emailUuid: this.loginForm.emailUuid
        })
          .then(() => {
            this.loading = false
            this.$message.success('登录成功')
            this.$router.push({ path: this.redirect || '/home' })
          })
          .catch(error => {
            this.loading = false
            console.error('登录失败:', error)
          })
      })
    },
    handleSocialLogin(type) {
      this.$message.info(`${type} 登录开发中`)
    },
    showAgreement(type) {
      this.$message.info(`查看${type === 'user' ? '用户协议' : '隐私政策'}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
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
  background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
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

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .code-input-wrapper {
    display: flex;
    gap: 12px;
    width: 100%;
    
    .code-input {
      flex: 1;
    }
    
    .send-code-btn {
      width: 120px;
      border: 1px solid #dcdfe6;
      color: #ff6b00;
      padding: 0 16px;
      
      &:hover:not(.is-disabled) {
        border-color: #ff6b00;
        background: #fff9f5;
      }
      
      &.is-disabled {
        color: #c0c4cc;
        border-color: #ebeef5;
        cursor: not-allowed;
      }
    }
  }
  
  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
    font-weight: 500;
    background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
    border: none;
    margin-top: 8px;
    
    &:hover {
      background: linear-gradient(135deg, #ff8533 0%, #ff9d55 100%);
      box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
    }
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

.footer-agreement {
  margin-top: 24px;
  font-size: 12px;
  color: #999;
  text-align: center;
  
  a {
    color: #ff6b00;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
      color: #ff8533;
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
