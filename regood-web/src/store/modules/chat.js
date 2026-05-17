import { getConversationList, getMessageList, sendMessage, markAsRead, getLatestMessages } from '@/api/business/chat'
import { getToken } from '@/utils/auth'

const state = {
  conversations: [],
  currentConversation: null,
  messages: [],
  websocket: null,
  isConnected: false
}

const mutations = {
  SET_CONVERSATIONS: (state, conversations) => {
    state.conversations = conversations
  },
  SET_CURRENT_CONVERSATION: (state, conversation) => {
    state.currentConversation = conversation
  },
  SET_MESSAGES: (state, messages) => {
    state.messages = messages
  },
  ADD_MESSAGE: (state, message) => {
    state.messages.push(message)
  },
  SET_WEBSOCKET: (state, ws) => {
    state.websocket = ws
  },
  SET_CONNECTED: (state, status) => {
    state.isConnected = status
  },
  UPDATE_CONVERSATION: (state, message) => {
    const msgConversationId = String(message.conversationId || '')
    const msgSenderId = String(message.senderId || '')
    const msgSenderName = String(message.senderName || '').toLowerCase()
    
    let index = state.conversations.findIndex(c => {
      const cConversationId = String(c.conversationId || '')
      const cUserId = String(c.userId || '')
      const cNickname = String(c.nickname || '').toLowerCase()
      return (cConversationId && cConversationId === msgConversationId) || 
             (cUserId && cUserId === msgSenderId) ||
             (cNickname && cNickname === msgSenderName)
    })
    
    if (index !== -1) {
      state.conversations[index].lastMessage = message.content
      state.conversations[index].lastMessageTime = message.createTime
      if (message.unreadCount) {
        state.conversations[index].unreadCount = (state.conversations[index].unreadCount || 0) + message.unreadCount
      }
    } else {
      console.log('UPDATE_CONVERSATION 未找到会话，消息:', message, '现有会话:', state.conversations.map(c => ({userId: c.userId, type: typeof c.userId, nickname: c.nickname})))
      if (message.senderId && message.senderName) {
        state.conversations.unshift({
          conversationId: message.conversationId,
          userId: message.senderId,
          username: message.senderName,
          nickname: message.senderName,
          avatar: message.senderAvatar,
          lastMessage: message.content,
          lastMessageTime: message.createTime,
          unreadCount: message.unreadCount || 1
        })
      }
    }
  },
  UPDATE_CURRENT_CONVERSATION: (state, { receiverId, content, createTime }) => {
    const receiverIdStr = String(receiverId || '')
    let index = state.conversations.findIndex(c => String(c.userId || '') === receiverIdStr)
    if (index !== -1) {
      state.conversations[index].lastMessage = content
      state.conversations[index].lastMessageTime = createTime
    }
  }
}

const actions = {
  getConversations({ commit }) {
    return new Promise((resolve, reject) => {
      getConversationList().then(response => {
        const { data } = response
        commit('SET_CONVERSATIONS', data || [])
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  getMessages({ commit }, conversationId) {
    return new Promise((resolve, reject) => {
      getMessageList(conversationId).then(response => {
        const { data } = response
        commit('SET_MESSAGES', data || [])
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  sendMessage({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      sendMessage(data).then(response => {
        const { data: message } = response
        if (!message.senderId) {
          message.senderId = rootGetters.userId
          message.senderName = rootGetters.name
          message.senderAvatar = rootGetters.avatar
        }
        commit('ADD_MESSAGE', message)
        commit('UPDATE_CURRENT_CONVERSATION', {
          receiverId: data.receiverId,
          content: data.content,
          createTime: message.createTime || new Date().toISOString()
        })
        resolve(message)
      }).catch(error => {
        reject(error)
      })
    })
  },

  markAsRead({ commit }, conversationId) {
    return new Promise((resolve, reject) => {
      markAsRead(conversationId).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  getLatestMessages({ commit }, conversationId) {
    return new Promise((resolve, reject) => {
      getLatestMessages(conversationId).then(response => {
        const { data } = response
        if (data && data.length > 0) {
          data.forEach(msg => commit('ADD_MESSAGE', msg))
        }
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  connectWebSocket({ commit, state, rootGetters }) {
    return new Promise((resolve) => {
      if (state.websocket && state.isConnected) {
        resolve()
        return
      }

      const token = getToken()
      const baseUrl = process.env.VUE_APP_BACKEND || 'http://127.0.0.1:15000'
      const wsProtocol = baseUrl.startsWith('https') ? 'wss' : 'ws'
      const wsHost = baseUrl.replace(/^https?:\/\//, '')
      const wsUrl = `${wsProtocol}://${wsHost}/ws/chat?token=${token}`
      console.log('WebSocket连接地址:', wsUrl)
      const ws = new WebSocket(wsUrl)

      ws.onopen = () => {
        commit('SET_CONNECTED', true)
        console.log('WebSocket连接成功')
        resolve()
      }

      ws.onmessage = (event) => {
        const message = JSON.parse(event.data)
        const currentUserId = String(rootGetters.userId)
        const senderId = String(message.senderId || '')
        if (senderId !== currentUserId) {
          commit('ADD_MESSAGE', message)
          commit('UPDATE_CONVERSATION', {
            ...message,
            unreadCount: 1
          })
        }
      }

      ws.onerror = (error) => {
        console.error('WebSocket连接错误:', error)
        commit('SET_CONNECTED', false)
      }

      ws.onclose = () => {
        console.log('WebSocket连接关闭')
        commit('SET_CONNECTED', false)
      }

      commit('SET_WEBSOCKET', ws)
    })
  },

  disconnectWebSocket({ commit, state }) {
    if (state.websocket) {
      state.websocket.close()
      commit('SET_WEBSOCKET', null)
      commit('SET_CONNECTED', false)
    }
  },

  sendWebSocketMessage({ state, commit, rootGetters }, data) {
    if (state.websocket && state.isConnected) {
      const now = new Date().toISOString()
      const message = {
        messageId: Date.now(),
        conversationId: state.currentConversation?.conversationId,
        senderId: rootGetters.userId,
        senderName: rootGetters.name,
        senderAvatar: rootGetters.avatar,
        receiverId: data.receiverId,
        content: data.content,
        messageType: data.messageType || 1,
        createTime: now
      }
      commit('ADD_MESSAGE', message)
      commit('UPDATE_CURRENT_CONVERSATION', {
        receiverId: data.receiverId,
        content: data.content,
        createTime: now
      })
      
      state.websocket.send(JSON.stringify({
        type: 'message',
        receiverId: data.receiverId,
        content: data.content,
        messageType: data.messageType || 1
      }))
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
