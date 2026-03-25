import request from '@/utils/request'

export function getFavoriteList() {
  return request({
    url: '/business/favorite/list',
    method: 'get'
  })
}

export function getAllFavoriteList() {
  return request({
    url: '/business/favorite/all',
    method: 'get'
  })
}

export function addFavorite(productId) {
  return request({
    url: `/business/favorite/${productId}`,
    method: 'post'
  })
}

export function removeFavorite(productId) {
  return request({
    url: `/business/favorite/${productId}`,
    method: 'delete'
  })
}
