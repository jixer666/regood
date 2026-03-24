import request from '@/utils/request'

export function getCategoryTree() {
  return request({
    url: '/business/category/tree',
    method: 'get'
  })
}

export function getCategoryList(query) {
  return request({
    url: '/business/category/list',
    method: 'get',
    params: query
  })
}

export function saveCategory(data) {
  return request({
    url: '/business/category',
    method: 'post',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/business/category',
    method: 'put',
    data
  })
}

export function deleteCategory(categoryId) {
  return request({
    url: `/business/category/${categoryId}`,
    method: 'delete'
  })
}
