import request from '@/utils/request'

export function getDashboardStatistics() {
  return request({
    url: '/business/statistics/dashboard',
    method: 'get'
  })
}
