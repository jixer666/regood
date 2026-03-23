import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false })

function isWhitePath(path, whiteList) {
  if (whiteList.indexOf(path) !== -1) {
    return true
  }
  for (let i = 0; i < whiteList.length; i++) {
    const whitePath = whiteList[i]
    if (whitePath.endsWith('/*') || whitePath.endsWith('/:id')) {
      const basePath = whitePath.replace(/\/\*$/, '').replace(/\/:id$/, '')
      if (path.startsWith(basePath + '/')) {
        return true
      }
    }
    if (path.startsWith(whitePath + '/')) {
      return true
    }
  }
  return false
}


router.beforeEach(async (to, from, next) => {
  // start progress bar
  NProgress.start()

  // set page title
  document.title = getPageTitle(to.meta.title)

  // determine whether the user has logged in
  const hasToken = getToken()
  console.log(to.path)

  if (hasToken) {
    if (to.path === '/admin/login') {
      // if is logged in, redirect to the home page
      next({ path: '/admin/dashboard' })
      NProgress.done() // hack: https://github.com/PanJiaChen/vue-element-admin/pull/2939
    } else {
      // determine whether the user has obtained his permission roles through getInfo
      const hasRoles = store.getters.roles && store.getters.roles.length > 0
      if (hasRoles) {
        next()
      } else {
        try {
          // get user info
          // note: roles must be a object array! such as: ['admin'] or ,['developer','editor']
          const { roles } = await store.dispatch('user/getInfo')

          // generate accessible routes map based on roles
          const accessRoutes = await store.dispatch('permission/generateRoutes', roles)

          // dynamically add accessible routes
          router.addRoutes(accessRoutes)

          // hack method to ensure that addRoutes is complete
          // set the replace: true, so the navigation will not leave a history record
          if (to.path === '/') {
            next('/admin/dashboard')
          } else {
            next({ ...to, replace: true })
          }
        } catch (error) {
          // remove token and go to login page to re-login
          // await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next(`/home`)
          NProgress.done()
        }
      }
    }
  } else {
    if (store.getters.whiteList && store.getters.whiteList.length > 0) {
      next();
    } else {
      const whiteList = await store.dispatch('settings/getWhiteList')
      if (isWhitePath(to.path, whiteList)) {
        next({ ...to, replace: true })
      } else {
        next(`/home`)
        NProgress.done()
      }
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
