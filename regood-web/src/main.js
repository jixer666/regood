import Vue from 'vue'

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css'

import Element from 'element-ui'
import './styles/element-variables.scss'

import '@/styles/index.scss'
import '@/styles/xueyihuan.scss'

import App from './App'
import store from './store'
import router from './router'

import './icons'
import './permission'
import './utils/error-log'

import * as filters from './filters'
import plugins from './plugins'

import Pagination from '@/components/Pagination'
import DictData from '@/components/DictData'
import DictTag from '@/components/DictTag'

if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

Vue.use(Element, {
  size: Cookies.get('size') || 'medium'
})
Vue.use(plugins)

Vue.component('Pagination', Pagination)
Vue.component('DictTag', DictTag)

DictData.install()

Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.config.productionTip = false

import { xyhRoutes } from './router/xyh-router'

router.addRoutes(xyhRoutes)

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
