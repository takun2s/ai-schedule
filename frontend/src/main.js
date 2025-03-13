import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from './utils/axios'

Vue.use(ElementUI)
Vue.config.productionTip = false

// 配置axios
Vue.prototype.$http = axios  // 将封装的请求方法挂载到Vue实例上

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
