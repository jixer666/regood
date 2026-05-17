<template>
  <div class="chat-container">
    <div class="conversation-list">
      <div class="header">
        <h3>消息列表</h3>
      </div>
      <div class="list">
        <div
          v-for="item in conversations"
          :key="item.conversationId"
          class="conversation-item"
          :class="{ active: currentConversation && currentConversation.conversationId === item.conversationId }"
          @click="selectConversation(item)"
        >
          <el-avatar :src="item.avatar" :size="50">
            {{ item.nickname ? item.nickname.charAt(0) : 'U' }}
          </el-avatar>
          <div class="info">
            <div class="name">{{ item.nickname || item.username }}</div>
            <div class="last-message">{{ item.lastMessage }}</div>
          </div>
          <div class="meta">
            <div class="time">{{ formatTime(item.lastMessageTime) }}</div>
            <el-badge :value="item.unreadCount" :hidden="!item.unreadCount || item.unreadCount === 0" />
          </div>
        </div>
        <div v-if="conversations.length === 0" class="empty">
          <div class="empty-text">暂无会话</div>
        </div>
      </div>
    </div>
    <div class="chat-main">
      <div v-if="currentConversation" class="chat-content">
        <div class="chat-header">
          <div class="user-info">
            <el-avatar :src="currentConversation.avatar" :size="40">
              {{ currentConversation.nickname ? currentConversation.nickname.charAt(0) : 'U' }}
            </el-avatar>
            <span class="name">{{ currentConversation.nickname || currentConversation.username }}</span>
          </div>
        </div>
        <div ref="messageList" class="message-list">
          <div
            v-for="message in messages"
            :key="message.messageId"
            class="message-item"
            :class="{ 'self': message.senderId === currentUserId }"
          >
            <el-avatar :src="message.senderAvatar" :size="40">
              {{ message.senderName ? message.senderName.charAt(0) : 'U' }}
            </el-avatar>
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.createTime) }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入消息..."
            @keyup.enter.native="handleSend"
          />
          <el-button type="primary" @click="handleSend" :disabled="!inputMessage.trim()">发送</el-button>
        </div>
      </div>
      <div v-else class="empty-chat">
        <div class="empty-text">请选择一个会话开始聊天</div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  name: 'Chat',
  data() {
    return {
      inputMessage: '',
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
      // 创建或获取与该用户的会话
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
      'sendWebSocketMessage'
    ]),
    async initChat() {
      await this.getConversations()
    },
    async createConversationWithUser(userId, userName) {
      // 检查是否已有会话
      let conversation = this.conversations.find(c => c.userId === Number(userId))
      
      if (!conversation) {
        // 创建新会话
        conversation = {
          conversationId: null,
          userId: Number(userId),
          username: userName || '用户',
          nickname: userName || '用户',
          avatar: '',
          lastMessage: '',
          lastMessageTime: new Date(),
          unreadCount: 0
        }
        this.conversations.unshift(conversation)
      }
      
      // 选中该会话
      await this.selectConversation(conversation)
    },
    async selectConversation(item) {
      if (item.conversationId) {
        await this.getMessages(item.conversationId)
        await this.markAsRead(item.conversationId)
      } else {
        // 新会话，清空消息列表
        this.$store.commit('chat/SET_MESSAGES', [])
      }
      this.$store.commit('chat/SET_CURRENT_CONVERSATION', item)
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },
    async handleSend() {
      if (!this.inputMessage.trim()) {
        return
      }

      const data = {
        receiverId: this.currentConversation.userId,
        content: this.inputMessage.trim(),
        messageType: 1
      }

      try {
        if (this.isConnected) {
          this.sendWebSocketMessage(data)
        } else {
          await this.sendMessage(data)
        }
        this.inputMessage = ''
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      } catch (error) {
        this.$message.error('发送失败')
      }
    },
    scrollToBottom() {
      if (this.$refs.messageList) {
        this.$refs.messageList.scrollTop = this.$refs.messageList.scrollHeight
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
        return Math.floor(diff / 3600000) + '小时前'
      } else {
        return date.toLocaleDateString()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 84px);
  background: #fff;
  border-radius: 4px;

  .conversation-list {
    width: 300px;
    border-right: 1px solid #e6e6e6;
    display: flex;
    flex-direction: column;

    .header {
      padding: 15px;
      border-bottom: 1px solid #e6e6e6;

      h3 {
        margin: 0;
        font-size: 16px;
      }
    }

    .list {
      flex: 1;
      overflow-y: auto;

      .conversation-item {
        display: flex;
        align-items: center;
        padding: 15px;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background-color: #f5f5f5;
        }

        &.active {
          background-color: #e6f7ff;
        }

        .info {
          flex: 1;
          margin-left: 10px;
          overflow: hidden;

          .name {
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 5px;
          }

          .last-message {
            font-size: 12px;
            color: #999;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .meta {
          text-align: right;

          .time {
            font-size: 12px;
            color: #999;
            margin-bottom: 5px;
          }
        }
      }

      .empty {
        padding: 50px 0;
        text-align: center;
      }
    }
  }

  .empty-text {
    text-align: center;
    color: #999;
    font-size: 14px;
    padding: 20px;
  }

  .chat-main {
    flex: 1;
    display: flex;
    flex-direction: column;

    .chat-content {
      display: flex;
      flex-direction: column;
      height: 100%;

      .chat-header {
        padding: 15px;
        border-bottom: 1px solid #e6e6e6;

        .user-info {
          display: flex;
          align-items: center;

          .name {
            margin-left: 10px;
            font-size: 16px;
            font-weight: 500;
          }
        }
      }

      .message-list {
        flex: 1;
        padding: 20px;
        overflow-y: auto;

        .message-item {
          display: flex;
          margin-bottom: 20px;

          &.self {
            flex-direction: row-reverse;

            .message-content {
              align-items: flex-end;
              margin-left: 0;
              margin-right: 10px;
            }
          }

          .message-content {
            display: flex;
            flex-direction: column;
            margin-left: 10px;

            .message-text {
              max-width: 400px;
              padding: 10px 15px;
              background: #f5f5f5;
              border-radius: 8px;
              word-wrap: break-word;
            }

            .message-time {
              font-size: 12px;
              color: #999;
              margin-top: 5px;
            }
          }

          &.self .message-text {
            background: #e6f7ff;
          }
        }
      }

      .chat-input {
        padding: 15px;
        border-top: 1px solid #e6e6e6;
        display: flex;
        align-items: flex-end;

        .el-textarea {
          flex: 1;
          margin-right: 10px;
        }
      }
    }

    .empty-chat {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
</style>
