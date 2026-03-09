import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/**
 * 学易换校园二手交易平台路由配置
 * 这些路由需要在 main.js 中通过 router.addRoutes() 添加
 */

export const xyhRoutes = [
  {
    path: '/login',
    component: () => import('@/views/xyh/Login'),
    hidden: true,
    meta: { title: '登录 - 学易换' }
  },
  {
    path: '/home',
    component: () => import('@/views/xyh/Home'),
    meta: { title: '学易换 - 校园二手交易平台' }
  },
  {
    path: '/goods/:id',
    component: () => import('@/views/xyh/GoodsDetail'),
    name: 'GoodsDetail',
    meta: { title: '商品详情 - 学易换' }
  },
  {
    path: '/publish',
    component: () => import('@/views/xyh/Publish'),
    meta: { title: '发布商品 - 学易换' }
  },
  {
    path: '/profile',
    component: () => import('@/views/xyh/Profile'),
    meta: { title: '个人中心 - 学易换' }
  },
  {
    path: '/message',
    component: () => import('@/views/xyh/Message'),
    meta: { title: '消息 - 学易换' }
  },
  {
    path: '/cart',
    component: () => import('@/views/xyh/Cart'),
    meta: { title: '购物车 - 学易换' }
  },
  {
    path: '/checkout',
    component: () => import('@/views/xyh/Cart'),
    meta: { title: '确认订单 - 学易换' }
  },
  {
    path: '/my/publish',
    component: () => import('@/views/xyh/Profile'),
    meta: { title: '我的发布 - 学易换' }
  },
  {
    path: '/my/sold',
    component: () => import('@/views/xyh/Profile'),
    meta: { title: '我卖出的 - 学易换' }
  },
  {
    path: '/my/bought',
    component: () => import('@/views/xyh/Profile'),
    meta: { title: '我买到的 - 学易换' }
  },
  {
    path: '/my/favorite',
    component: () => import('@/views/xyh/Profile'),
    meta: { title: '我的收藏 - 学易换' }
  },
  {
    path: '*',
    redirect: '/home',
    hidden: true
  }
]

const xyhRouter = new Router({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: xyhRoutes
})

export default xyhRouter
