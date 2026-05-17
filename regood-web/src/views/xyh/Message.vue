<template>
  <div class="xyh-message">
    <xyh-header />
    
    <div class="message-container">
      <!-- 左侧联系人列表 -->
      <div class="contact-list">
        <div class="list-header">
          <h3 class="list-title">消息</h3>
        </div>
        <div class="contact-list-content">
          <div
            v-for="contact in conversations"
            :key="contact.conversationId || contact.userId"
            class="contact-item"
            :class="{ active: isActiveConversation(contact) }"
            @click="selectConversation(contact)"
          >
            <div class="contact-info">
              <el-avatar :src="contact.avatar" class="contact-avatar">
                {{ contact.nickname ? contact.nickname.charAt(0) : 'U' }}
              </el-avatar>
              <div class="contact-detail">
                <div class="contact-name">{{ contact.nickname || contact.username || '用户' }}</div>
                <div class="contact-preview">
                  <span class="preview-text">{{ contact.lastMessage || '暂无消息' }}</span>
                  <span class="preview-time">{{ formatTime(contact.lastMessageTime) }}</span>
                </div>
              </div>
            </div>
            <div class="contact-meta">
              <el-badge
                :value="contact.unreadCount"
                :hidden="!contact.unreadCount || contact.unreadCount === 0"
                class="badge"
              />
            </div>
          </div>
          <div v-if="conversations.length === 0" class="empty-contacts">
            <div class="empty-text">暂无会话</div>
          </div>
        </div>
      </div>

      <!-- 右侧聊天窗口 -->
      <div class="chat-window">
        <template v-if="currentConversation">
          <div class="chat-header">
            <div class="chat-header-info">
              <el-avatar :src="currentConversation.avatar" class="chat-avatar">
                {{ currentConversation.nickname ? currentConversation.nickname.charAt(0) : 'U' }}
              </el-avatar>
              <div class="chat-header-detail">
                <div class="chat-header-name">{{ currentConversation.nickname || currentConversation.username || '用户' }}</div>
              </div>
            </div>
          </div>

          <!-- 聊天记录 -->
          <div class="chat-messages" ref="chatMessages">
            <div
              v-for="message in messages"
              :key="message.messageId"
              class="message-item"
              :class="{ 'my-message': isMyMessage(message) }"
            >
              <el-avatar 
                :src="message.senderAvatar" 
                :size="36"
                class="message-avatar"
              >
                {{ message.senderName ? message.senderName.charAt(0) : 'U' }}
              </el-avatar>
              <div class="message-content">
                <div class="message-bubble">
                  <div class="message-text">{{ message.content }}</div>
                  <div class="message-meta">
                    <span class="message-time">{{ formatTime(message.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="messages.length === 0" class="empty-messages">
              <div class="empty-text">暂无消息，发送第一条消息吧</div>
            </div>
          </div>

          <!-- 输入区 -->
          <div class="chat-input-area">
            <el-input
              v-model="inputText"
              type="textarea"
              :rows="2"
              placeholder="输入消息..."
              resize="none"
              @keyup.enter.native="handleSend"
              class="message-input"
            />
            <div class="input-actions-right">
              <el-button
                type="primary"
                size="small"
                :disabled="!inputText.trim()"
                @click="handleSend"
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
        </template>
        <div v-else class="empty-chat">
          <div class="empty-text">请选择一个会话开始聊天</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'
import { mapState, mapActions } from 'vuex'

export default {
  name: 'XyhMessage',
  components: {
    XyhHeader
  },
  data() {
    return {
      inputText: '',
      currentUserId: null
    }
  },
  computed: {
    ...mapState('chat', ['conversations', 'currentConversation', 'messages', 'isConnected'])
  },
  created() {
    this.currentUserId = this.$store.getters.userId
    this.initChat()
    
    // 检查是否从商品详情页跳转过来
    const { userId, userName } = this.$route.query
    if (userId) {
      this.createConversationWithUser(userId, userName)
    }
  },
  mounted() {
    this.connectWebSocket()
  },
  beforeDestroy() {
    this.disconnectWebSocket()
  },
  methods: {
    ...mapActions('chat', [
      'getConversations',
      'getMessages',
      'sendMessage',
      'markAsRead',
      'connectWebSocket',
      'disconnectWebSocket',
      'sendWebSocketMessage',
      'getLatestMessages'
    ]),
    async initChat() {
      try {
        await this.getConversations()
      } catch (error) {
        console.error('加载会话列表失败', error)
        this.$message.error('加载会话列表失败')
      }
    },
    isActiveConversation(contact) {
      if (!this.currentConversation) return false
      const currentConvId = String(this.currentConversation.conversationId || '')
      const currentUserId = String(this.currentConversation.userId || '')
      const contactConvId = String(contact.conversationId || '')
      const contactUserId = String(contact.userId || '')
      return (currentConvId && currentConvId === contactConvId) || 
             (currentUserId && currentUserId === contactUserId)
    },
    isMyMessage(message) {
      return String(message.senderId) === String(this.currentUserId)
    },
    async createConversationWithUser(userId, userName) {
      const userIdStr = String(userId)
      let conversation = this.conversations.find(c => String(c.userId) === userIdStr)
      
      if (!conversation) {
        conversation = {
          conversationId: null,
          userId: userId,
          username: userName || '用户',
          nickname: userName || '用户',
          avatar: '',
          lastMessage: '',
          lastMessageTime: new Date(),
          unreadCount: 0
        }
        this.conversations.unshift(conversation)
      }
      
      await this.selectConversation(conversation)
    },
    async selectConversation(contact) {
      try {
        if (contact.conversationId) {
          await this.getMessages(contact.conversationId)
          await this.markAsRead(contact.conversationId)
        } else {
          this.$store.commit('chat/SET_MESSAGES', [])
        }
        contact.unreadCount = 0
        this.$store.commit('chat/SET_CURRENT_CONVERSATION', contact)
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      } catch (error) {
        console.error('加载消息失败', error)
        this.$message.error('加载消息失败')
      }
    },
    async handleSend() {
      if (!this.inputText.trim()) {
        return
      }

      const data = {
        receiverId: this.currentConversation.userId,
        content: this.inputText.trim(),
        messageType: 1
      }

      try {
        if (this.isConnected) {
          this.sendWebSocketMessage(data)
          this.inputText = ''
          await this.markConversationAsRead()
        } else {
          await this.sendMessage(data)
          this.inputText = ''
          await this.markConversationAsRead()
        }
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      } catch (error) {
        console.error('发送消息失败', error)
        this.$message.error('发送消息失败')
      }
    },
    async markConversationAsRead() {
      if (this.currentConversation && this.currentConversation.conversationId) {
        try {
          await this.markAsRead(this.currentConversation.conversationId)
          this.currentConversation.unreadCount = 0
        } catch (error) {
          console.error('标记已读失败', error)
        }
      }
    },
    scrollToBottom() {
      if (this.$refs.chatMessages) {
        this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight
      }
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) {
        return '刚刚'
      } else if (diff < 3600000) {
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) {
        return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
      } else if (diff < 172800000) {
        return '昨天'
      } else {
        return `${date.getMonth() + 1}/${date.getDate()}`
      }
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
    margin: 0;
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
          flex: 1;
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
  }
}

.empty-contacts {
  padding: 40px 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-text {
  text-align: center;
  color: #999;
  font-size: 14px;
  padding: 20px;
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
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 16px;
  
  &.my-message {
    flex-direction: row-reverse;
    
    .message-content {
      align-items: flex-end;
      
      .message-bubble {
        background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
        color: #fff;
      }
    }
  }
  
  .message-avatar {
    flex-shrink: 0;
  }
  
  .message-content {
    display: flex;
    flex-direction: column;
    max-width: 70%;
    
    .message-bubble {
      padding: 12px 16px;
      border-radius: 18px;
      background: #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      
      .message-text {
        font-size: 14px;
        line-height: 1.6;
        word-wrap: break-word;
      }
      
      .message-meta {
        display: flex;
        justify-content: flex-end;
        margin-top: 8px;
        font-size: 12px;
        color: #999;
        
        .my-message & {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }
}

.empty-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.chat-input-area {
  padding: 16px 20px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: #fff;
  
  .message-input {
    flex: 1;
    
    ::v-deep .el-textarea__inner {
      border-radius: 8px;
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
  display: flex;
  align-items: center;
  
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

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
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
    min-height: 500px;
  }
}
</style>
