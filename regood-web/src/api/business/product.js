import request from '@/utils/request'

export function getProductPage(query) {
  return request({
    url: '/business/product/page',
    method: 'get',
    params: query
  })
}

export function getMyProducts(query) {
  return request({
    url: '/business/product/my',
    method: 'get',
    params: query
  })
}

export function getProductDetail(productId) {
  return request({
    url: '/business/product/detail',
    method: 'get',
    params: { productId }
  })
}

export function saveProduct(data) {
  return request({
    url: '/business/product',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/business/product',
    method: 'put',
    data
  })
}

export function deleteProduct(productId) {
  return request({
    url: `/business/product/${productId}`,
    method: 'delete'
  })
}

export function offlineProduct(productId) {
  return request({
    url: `/business/product/offline/${productId}`,
    method: 'put'
  })
}

export function onlineProduct(productId) {
  return request({
    url: `/business/product/online/${productId}`,
    method: 'put'
  })
}

export function getRecommendProducts() {
  return request({
    url: '/business/product/recommend',
    method: 'get'
  })
}

export function addFavorite(productId) {
  return request({
    url: `/business/product/favorite/${productId}`,
    method: 'post'
  })
}

export function removeFavorite(productId) {
  return request({
    url: `/business/product/favorite/${productId}`,
    method: 'delete'
  })
}

export function getPendingAuditList(query) {
  return request({
    url: '/business/product/pending/audit',
    method: 'get',
    params: query
  })
}

export function approveProduct(productId) {
  return request({
    url: `/business/product/approve/${productId}`,
    method: 'put'
  })
}

export function rejectProduct(productId, data) {
  return request({
    url: `/business/product/reject/${productId}`,
    method: 'put',
    data
  })
}
