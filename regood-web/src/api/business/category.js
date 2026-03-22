import request from '@/utils/request'

export function getCategoryTree() {
  return request({
    url: '/business/category/tree',
    method: 'get'
  })
}
