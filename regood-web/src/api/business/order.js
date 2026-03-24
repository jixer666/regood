import request from '@/utils/request'

export function getOrderPage(query) {
  return request({
    url: '/business/order/page',
    method: 'get',
    params: query
  })
}

export function getAllOrderPage(query) {
  return request({
    url: '/business/order/all',
    method: 'get',
    params: query
  })
}

export function getOrderDetail(orderId) {
  return request({
    url: '/business/order/detail',
    method: 'get',
    params: { orderId }
  })
}

export function createOrder(data) {
  return request({
    url: '/business/order',
    method: 'post',
    data
  })
}

export function cancelOrder(orderId) {
  return request({
    url: `/business/order/cancel/${orderId}`,
    method: 'put'
  })
}

export function payOrder(orderId) {
  return request({
    url: `/business/order/pay/${orderId}`,
    method: 'put'
  })
}

export function confirmOrder(orderId) {
  return request({
    url: `/business/order/confirm/${orderId}`,
    method: 'put'
  })
}

export function getMySoldList() {
  return request({
    url: '/business/order/sold',
    method: 'get'
  })
}

export function getMyBoughtList() {
  return request({
    url: '/business/order/bought',
    method: 'get'
  })
}
