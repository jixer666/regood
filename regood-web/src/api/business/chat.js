import request from '@/utils/request'

export function getConversationList() {
  return request({
    url: '/business/chat/conversations',
    method: 'get'
  })
}

export function getMessageList(conversationId) {
  return request({
    url: `/business/chat/messages/${conversationId}`,
    method: 'get'
  })
}

export function sendMessage(data) {
  return request({
    url: '/business/chat/send',
    method: 'post',
    data
  })
}

export function markAsRead(conversationId) {
  return request({
    url: `/business/chat/read/${conversationId}`,
    method: 'put'
  })
}

export function getLatestMessages(conversationId) {
  return request({
    url: `/business/chat/messages/${conversationId}/latest`,
    method: 'get'
  })
}
