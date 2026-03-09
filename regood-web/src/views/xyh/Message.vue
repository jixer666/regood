<template>
  <div class="xyh-message">
    <xyh-header />
    
    <div class="message-container">
      <!-- 左侧联系人列表 -->
      <div class="contact-list">
        <div class="list-header">
          <h3 class="list-title">消息</h3>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索联系人..."
            prefix-icon="el-icon-search"
            size="small"
            class="search-input"
          />
        </div>
        <div class="contact-list-content">
          <div
            v-for="contact in contacts"
            :key="contact.id"
            class="contact-item"
            :class="{ active: activeContact === contact.id }"
            @click="selectContact(contact.id)"
          >
            <div class="contact-info">
              <el-avatar :src="contact.avatar" class="contact-avatar"></el-avatar>
              <div class="contact-detail">
                <div class="contact-name">{{ contact.name }}</div>
                <div class="contact-preview">
                  <span class="preview-text">{{ contact.lastMessage }}</span>
                  <span class="preview-time">{{ contact.lastTime }}</span>
                </div>
              </div>
            </div>
            <div class="contact-meta">
              <el-badge
                :value="contact.unreadCount"
                :hidden="contact.unreadCount === 0"
                class="badge"
              >
                <i
                  v-if="contact.hasGoods"
                  class="el-icon-shopping-cart-2"
                  title="关联商品"
                ></i>
              </el-badge>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧聊天窗口 -->
      <div class="chat-window">
        <div class="chat-header">
          <div class="chat-header-info">
            <el-avatar :src="activeContactInfo.avatar" class="chat-avatar"></el-avatar>
            <div class="chat-header-detail">
              <div class="chat-header-name">{{ activeContactInfo.name }}</div>
              <div class="chat-header-meta">
                <span class="header-meta-item">
                  <i class="el-icon-location-outline"></i>
                  {{ activeContactInfo.campus }}
                </span>
                <span class="header-meta-item">
                  <i class="el-icon-star-on"></i>
                  信用分 {{ activeContactInfo.creditScore }}
                </span>
              </div>
            </div>
          </div>
          <div class="chat-header-actions">
            <el-button
              type="info"
              size="small"
              icon="el-icon-phone"
              @click="callContact"
            ></el-button>
            <el-button
              type="info"
              size="small"
              icon="el-icon-video-camera"
              @click="videoChat"
            ></el-button>
          </div>
        </div>

        <!-- 关联商品卡片 -->
        <div v-if="activeContactInfo.goods" class="goods-card">
          <div class="goods-card-header">
            <span class="goods-card-title">正在沟通的商品</span>
            <el-tag size="mini" :type="activeContactInfo.goods.status === 'onsale' ? 'success' : 'info'">
              {{ activeContactInfo.goods.statusText }}
            </el-tag>
          </div>
          <div class="goods-card-body">
            <el-image
              :src="activeContactInfo.goods.image"
              class="goods-image"
              fit="cover"
            />
            <div class="goods-info">
              <div class="goods-title">{{ activeContactInfo.goods.title }}</div>
              <div class="goods-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ activeContactInfo.goods.price }}</span>
              </div>
              <div class="goods-meta">
                <span class="condition">{{ activeContactInfo.goods.condition }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 聊天记录 -->
        <div class="chat-messages" ref="chatMessages">
          <div
            v-for="(message, index) in chatMessages"
            :key="index"
            class="message-item"
            :class="{ 'my-message': message.isSelf }"
          >
            <div class="message-content">
              <div class="message-bubble">
                <div class="message-text">{{ message.text }}</div>
                <div class="message-meta">
                  <span class="message-time">{{ message.time }}</span>
                  <span v-if="message.isSelf" class="message-status">
                    <i v-if="message.read" class="el-icon-check-double" title="已读"></i>
                    <i v-else class="el-icon-check" title="已发送"></i>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷短语 -->
        <div class="quick-phrases" v-if="showQuickPhrases">
          <div class="quick-phrases-title">快捷短语</div>
          <div class="quick-phrases-list">
            <div
              v-for="phrase in quickPhrases"
              :key="phrase.id"
              class="quick-phrase"
              @click="insertQuickPhrase(phrase.text)"
            >
              {{ phrase.text }}
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="chat-input-area">
          <div class="input-actions">
            <el-button
              type="text"
              icon="el-icon-picture"
              @click="uploadImage"
            ></el-button>
            <el-button
              type="text"
              icon="el-icon-document"
              @click="toggleQuickPhrases"
            ></el-button>
          </div>
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="1"
            placeholder="输入消息..."
            resize="none"
            @keyup.enter.native="sendMessage"
            class="message-input"
          />
          <div class="input-actions-right">
            <el-button
              type="primary"
              size="small"
              :disabled="!inputText.trim()"
              @click="sendMessage"
            >
              发送
            </el-button>
          </div>
        </div>

        <!-- 防骗提示 -->
        <div class="warning-box">
          <div class="warning-icon">🛡️</div>
          <div class="warning-content">
            <p class="warning-text">
              <strong>温馨提示：</strong>请勿脱离本平台进行交易，谨防诈骗！
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'

export default {
  name: 'XyhMessage',
  components: {
    XyhHeader
  },
  data() {
    return {
      searchKeyword: '',
      activeContact: 1,
      inputText: '',
      showQuickPhrases: false,
      contacts: [
        {
          id: 1,
          name: '学长小李',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          lastMessage: '手机还在，需要面交吗？',
          lastTime: '10:30',
          unreadCount: 2,
          hasGoods: true,
          campus: '主校区',
          creditScore: 850,
          goods: {
            id: 1,
            title: 'iPhone 13 Pro 256G 远峰蓝 95 新',
            image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100',
            price: 5299,
            condition: '95 新',
            status: 'onsale',
            statusText: '在售'
          }
        },
        {
          id: 2,
          name: '学弟小王',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          lastMessage: '自行车还在吗？',
          lastTime: '昨天',
          unreadCount: 0,
          hasGoods: true,
          campus: '东校区',
          creditScore: 920,
          goods: {
            id: 2,
            title: '捷安特山地自行车 9 成新',
            image: 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=100',
            price: 1299,
            condition: '9 成新',
            status: 'onsale',
            statusText: '在售'
          }
        },
        {
          id: 3,
          name: '学妹小张',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          lastMessage: '谢谢，东西收到了',
          lastTime: '前天',
          unreadCount: 0,
          hasGoods: false,
          campus: '主校区',
          creditScore: 880,
          goods: null
        }
      ],
      chatMessages: [
        {
          id: 1,
          text: '你好，iPhone 13 Pro 还在吗？',
          isSelf: true,
          time: '10:00',
          read: true
        },
        {
          id: 2,
          text: '在的，95 新，原装包装盒都在。',
          isSelf: false,
          time: '10:05',
          read: true
        },
        {
          id: 3,
          text: '手机还在，需要面交吗？',
          isSelf: false,
          time: '10:30',
          read: false
        }
      ],
      quickPhrases: [
        { id: 1, text: '同学，东西还在吗？' },
        { id: 2, text: '能在食堂面交吗？' },
        { id: 3, text: '可以便宜点吗？' },
        { id: 4, text: '支持快递吗？' },
        { id: 5, text: '能再送点小配件吗？' }
      ]
    }
  },
  computed: {
    activeContactInfo() {
      return this.contacts.find(c => c.id === this.activeContact) || {}
    }
  },
  watch: {
    activeContact() {
      this.scrollToBottom()
    },
    chatMessages() {
      this.scrollToBottom()
    }
  },
  mounted() {
    this.scrollToBottom()
  },
  methods: {
    selectContact(id) {
      this.activeContact = id
      // 标记已读
      const contact = this.contacts.find(c => c.id === id)
      if (contact) {
        contact.unreadCount = 0
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.chatMessages) {
          this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight
        }
      })
    },
    sendMessage() {
      if (!this.inputText.trim()) return
      
      const message = {
        id: Date.now(),
        text: this.inputText,
        isSelf: true,
        time: this.getCurrentTime(),
        read: false
      }
      
      this.chatMessages.push(message)
      this.inputText = ''
      
      // 模拟回复
      setTimeout(() => {
        const reply = {
          id: Date.now() + 1,
          text: '好的，收到！',
          isSelf: false,
          time: this.getCurrentTime(),
          read: false
        }
        this.chatMessages.push(reply)
      }, 1000)
    },
    getCurrentTime() {
      const now = new Date()
      return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
    },
    insertQuickPhrase(text) {
      this.inputText = text
      this.showQuickPhrases = false
    },
    toggleQuickPhrases() {
      this.showQuickPhrases = !this.showQuickPhrases
    },
    uploadImage() {
      this.$message.info('图片上传功能')
    },
    callContact() {
      this.$message.info('语音通话功能')
    },
    videoChat() {
      this.$message.info('视频通话功能')
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-message {
  min-height: 100vh;
  background: #f5f7fa;
}

.message-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  gap: 20px;
  height: calc(100vh - 120px);
}

.contact-list {
  width: 320px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.list-header {
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
  
  .list-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 12px;
  }
  
  .search-input {
    ::v-deep .el-input__inner {
      border-radius: 20px;
    }
  }
}

.contact-list-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: #f5f7fa;
  }
  
  &.active {
    background: #e6f7ff;
    border: 1px solid #1890ff;
  }
  
  .contact-info {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
    
    .contact-avatar {
      flex-shrink: 0;
    }
    
    .contact-detail {
      flex: 1;
      min-width: 0;
      
      .contact-name {
        font-size: 14px;
        font-weight: 500;
        color: #333;
        margin-bottom: 4px;
      }
      
      .contact-preview {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 12px;
        color: #999;
        
        .preview-text {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .preview-time {
          flex-shrink: 0;
        }
      }
    }
  }
  
  .contact-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .badge {
      ::v-deep .el-badge__content {
        background: #f5222d;
      }
    }
    
    i {
      font-size: 18px;
      color: #1890ff;
    }
  }
}

.chat-window {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .chat-header-info {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .chat-avatar {
      border: 2px solid #1890ff;
    }
    
    .chat-header-detail {
      .chat-header-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }
      
      .chat-header-meta {
        display: flex;
        gap: 16px;
        font-size: 12px;
        color: #999;
        
        .header-meta-item {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
  
  .chat-header-actions {
    .el-button {
      background: #f5f7fa;
      border: none;
      color: #666;
      
      &:hover {
        background: #e8e8e8;
        color: #333;
      }
    }
  }
}

.goods-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #fff 100%);
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin: 16px;
  overflow: hidden;
  
  .goods-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: #fff;
    border-bottom: 1px solid #e8e8e8;
    
    .goods-card-title {
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }
  }
  
  .goods-card-body {
    display: flex;
    gap: 16px;
    padding: 16px;
    
    .goods-image {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      flex-shrink: 0;
    }
    
    .goods-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
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
      
      .goods-price {
        display: flex;
        align-items: baseline;
        gap: 4px;
        margin-bottom: 4px;
        
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
      
      .goods-meta {
        .condition {
          font-size: 12px;
          color: #999;
          background: #f5f5f5;
          padding: 2px 6px;
          border-radius: 4px;
        }
      }
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.message-item {
  margin-bottom: 16px;
  
  &.my-message {
    display: flex;
    justify-content: flex-end;
    
    .message-content {
      .message-bubble {
        background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
        color: #fff;
        align-self: flex-end;
        
        .message-meta {
          .message-status {
            i {
              color: rgba(255, 255, 255, 0.8);
            }
          }
        }
      }
    }
  }
  
  .message-content {
    max-width: 70%;
    
    .message-bubble {
      padding: 12px 16px;
      border-radius: 18px;
      background: #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      
      .message-text {
        font-size: 14px;
        line-height: 1.6;
        color: #333;
      }
      
      .message-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 8px;
        font-size: 12px;
        color: #999;
        
        .message-status {
          i {
            font-size: 14px;
          }
        }
      }
    }
  }
}

.quick-phrases {
  padding: 12px 20px;
  border-top: 1px solid #e8e8e8;
  background: #fff;
  
  .quick-phrases-title {
    font-size: 12px;
    color: #999;
    margin-bottom: 8px;
  }
  
  .quick-phrases-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    
    .quick-phrase {
      padding: 6px 12px;
      background: #f5f7fa;
      border-radius: 16px;
      font-size: 13px;
      color: #666;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        background: #1890ff;
        color: #fff;
      }
    }
  }
}

.chat-input-area {
  padding: 16px 20px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  align-items: flex-end;
  gap: 12px;
  
  .input-actions {
    display: flex;
    gap: 8px;
    
    .el-button {
      background: #f5f7fa;
      border: none;
      color: #666;
      
      &:hover {
        background: #e8e8e8;
        color: #333;
      }
    }
  }
  
  .message-input {
    flex: 1;
    
    ::v-deep .el-textarea__inner {
      border-radius: 20px;
      padding-right: 40px;
    }
  }
  
  .input-actions-right {
    .el-button {
      background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
      border: none;
      height: 40px;
      padding: 0 24px;
      font-weight: 500;
      
      &:disabled {
        opacity: 0.6;
      }
      
      &:hover {
        opacity: 0.9;
      }
    }
  }
}

.warning-box {
  padding: 12px 20px;
  background: linear-gradient(135deg, #fff7e6 0%, #fff 100%);
  border-top: 1px solid #ffd591;
  
  .warning-icon {
    font-size: 18px;
    margin-right: 8px;
  }
  
  .warning-content {
    .warning-text {
      color: #fa8c16;
      font-size: 13px;
      margin: 0;
      line-height: 1.5;
      
      strong {
        font-weight: 600;
      }
    }
  }
}

@media (max-width: 768px) {
  .message-container {
    flex-direction: column;
    height: auto;
  }
  
  .contact-list {
    width: 100%;
    max-height: 300px;
  }
  
  .chat-window {
    max-height: 500px;
  }
}
</style>
