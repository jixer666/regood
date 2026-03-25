<template>
  <div class="xyh-footer">
    <div class="footer-container">
      <div class="footer-content">
        <!-- 平台简介 -->
        <div class="footer-section">
          <h3 class="section-title">关于学易换</h3>
          <p class="section-desc">
            学易换是专为大学生打造的校园二手交易平台，
            致力于提供安全、便捷、可信赖的校园交易服务。
          </p>
          <div class="trust-badges">
            <span class="badge">🛡️ 实名认证</span>
            <span class="badge">✅ 学籍验证</span>
            <span class="badge">💯 信用保障</span>
          </div>
        </div>

        <!-- 防骗指南 -->
        <div class="footer-section">
          <h3 class="section-title">校园防骗指南</h3>
          <ul class="safety-list">
            <li>⚠️ 请勿脱离平台进行交易</li>
            <li>⚠️ 警惕先付款后发货</li>
            <li>⚠️ 面交选择人多的公共场所</li>
            <li>⚠️ 仔细检查商品再确认收货</li>
          </ul>
        </div>

        <!-- 常见问题 -->
        <div class="footer-section">
          <h3 class="section-title">常见问题</h3>
          <ul class="faq-list">
            <li @click="showFAQ('publish')">如何发布商品？</li>
            <li @click="showFAQ('payment')">支付方式有哪些？</li>
            <li @click="showFAQ('refund')">如何申请退款？</li>
            <li @click="showFAQ('report')">如何举报违规？</li>
          </ul>
        </div>

        <!-- 联系我们 -->
        <div class="footer-section">
          <h3 class="section-title">联系管理员</h3>
          <div class="contact-info">
            <div class="contact-item">
              <i class="el-icon-service"></i>
              <span>客服QQ：1322836390</span>
            </div>
            <div class="contact-item">
              <i class="el-icon-phone"></i>
              <span>联系电话：888-888-888</span>
            </div>
            <div class="contact-item">
              <i class="el-icon-message"></i>
              <span>邮箱：1322836390@qq.com</span>
            </div>
          </div>
          <div class="social-links">
            <i class="el-icon-chat-round" title="微信"></i>
            <i class="el-icon-chat-dot-square" title="QQ"></i>
            <i class="el-icon-weibo" title="微博"></i>
          </div>
        </div>
      </div>

      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <div class="copyright">
          <p>© 2024 学易换校园二手交易平台 版权所有</p>
          <p>
            <el-link type="info" :underline="false">隐私政策</el-link>
            <span class="divider">|</span>
            <el-link type="info" :underline="false">服务条款</el-link>
            <span class="divider">|</span>
            <el-link type="info" :underline="false">帮助中心</el-link>
          </p>
        </div>
      </div>
    </div>

    <!-- FAQ 弹窗 -->
    <el-dialog
      :title="faqTitle"
      :visible.sync="faqDialogVisible"
      width="600px"
    >
      <div v-html="faqContent"></div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="faqDialogVisible = false">知道了</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'XyhFooter',
  data() {
    return {
      faqDialogVisible: false,
      faqTitle: '',
      faqContent: ''
    }
  },
  methods: {
    showFAQ(type) {
      const faqData = {
        publish: {
          title: '如何发布商品？',
          content: `
            <p>1. 点击顶部导航栏的【发布商品】按钮</p>
            <p>2. 上传商品图片（最多 9 张，首张作为封面）</p>
            <p>3. 填写商品名称、描述、分类、成色等信息</p>
            <p>4. 设置价格和交易方式</p>
            <p>5. 点击【一键发布】即可</p>
            <p style="color: #1890ff; margin-top: 12px;">💡 小贴士：描述越详细，卖出越快哦！</p>
          `
        },
        payment: {
          title: '支付方式有哪些？',
          content: `
            <p>本平台支持多种支付方式：</p>
            <p>✓ 微信支付</p>
            <p>✓ 支付宝</p>
            <p>✓ 校园卡余额（部分学校）</p>
            <p>✓ 货到付款（仅限面交）</p>
            <p style="color: #fa8c16; margin-top: 12px;">⚠️ 温馨提示：请勿通过微信、QQ 等私下转账，谨防诈骗！</p>
          `
        },
        refund: {
          title: '如何申请退款？',
          content: `
            <p>1. 在【我买到的】中找到对应订单</p>
            <p>2. 点击【申请退款】按钮</p>
            <p>3. 选择退款原因并上传凭证</p>
            <p>4. 等待卖家处理</p>
            <p>5. 如卖家拒绝，可申请平台客服介入</p>
            <p style="margin-top: 12px;">退款处理时间：1-3 个工作日</p>
          `
        },
        report: {
          title: '如何举报违规？',
          content: `
            <p>发现违规行为，您可以通过以下方式举报：</p>
            <p>1. 在商品详情页点击【举报】按钮</p>
            <p>2. 在聊天窗口点击右上角【举报】</p>
            <p>3. 联系客服微信：xueyihuan_kefu</p>
            <p>4. 发送邮件至：support@xueyihuan.com</p>
            <p style="color: #f5222d; margin-top: 12px;">🛡️ 平台承诺：对举报人信息严格保密，核实后将给予奖励！</p>
          `
        }
      }
      
      this.faqTitle = faqData[type].title
      this.faqContent = faqData[type].content
      this.faqDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-footer {
  background: #fff;
  border-top: 1px solid #e8e8e8;
  margin-top: 40px;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 2px solid #1890ff;
  }
  
  .section-desc {
    font-size: 14px;
    color: #666;
    line-height: 1.8;
  }
  
  .trust-badges {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 12px;
    
    .badge {
      font-size: 12px;
      padding: 4px 8px;
      background: #f5f7fa;
      border-radius: 4px;
      color: #666;
    }
  }
  
  .safety-list,
  .faq-list {
    list-style: none;
    padding: 0;
    margin: 0;
    
    li {
      font-size: 14px;
      color: #666;
      line-height: 2;
      cursor: pointer;
      transition: color 0.3s;
      
      &:hover {
        color: #1890ff;
      }
    }
  }
  
  .contact-info {
    .contact-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
      
      i {
        color: #1890ff;
      }
    }
  }
  
  .social-links {
    display: flex;
    gap: 16px;
    margin-top: 16px;
    
    i {
      font-size: 24px;
      color: #666;
      cursor: pointer;
      transition: color 0.3s;
      
      &:hover {
        color: #1890ff;
      }
    }
  }
}

.footer-bottom {
  border-top: 1px solid #e8e8e8;
  padding-top: 20px;
}

.copyright {
  text-align: center;
  
  p {
    font-size: 14px;
    color: #999;
    margin: 8px 0;
  }
  
  .divider {
    margin: 0 12px;
    color: #d9d9d9;
  }
}

::v-deep .el-dialog__body {
  p {
    margin: 8px 0;
    line-height: 1.8;
  }
}
</style>
