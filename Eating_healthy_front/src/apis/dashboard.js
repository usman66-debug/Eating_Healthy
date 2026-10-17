import request from '@/utils/request'

export function getDashboardStatsData() {
  return request({
    url: '/dashboard/admin/stats',
    method: 'get',
  })
}

export function getDashboardChartsData() {
  return request({
    url: '/dashboard/admin/charts',
    method: 'get',
  })
}

export function getDashboardOverviewData() {
  return request({
    url: '/dashboard/user/overview',
    method: 'get',
  })
}
