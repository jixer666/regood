import request from '@/utils/request'

export function getCartList() {
  return request({
    url: '/business/cart/list',
    method: 'get'
  })
}

export function getAllCartList() {
  return request({
    url: '/business/cart/all',
    method: 'get'
  })
}

export function addToCart(data) {
  return request({
    url: '/business/cart',
    method: 'post',
    data
  })
}

export function removeFromCart(cartId) {
  return request({
    url: `/business/cart/${cartId}`,
    method: 'delete'
  })
}

export function batchRemoveCart(ids) {
  return request({
    url: '/business/cart/batch',
    method: 'delete',
    data: ids
  })
}
