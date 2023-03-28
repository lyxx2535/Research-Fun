import Vue from 'vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './plugins/axios'
import App from './App.vue'
import router from './router'
import store from './store'
import './plugins/element.js'
import qs from 'qs'

//引入全局样式表
import './assets/css/global.css'

Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.prototype.$qs = qs;

// 每次跳转页面时定位到页面顶部
router.afterEach((to,from,next) => {
    window.scrollTo(0,0);
});

new Vue({
  router,
  store: store,
  render: h => h(App)
}).$mount('#app')


