/*
 * main js
 *
 */
import Vue from "vue";
import App from "./App.vue";
import './assets/font/iconfont.css'
import router from "./router";
Vue.config.productionTip = false;
Vue.prototype.$baseUrl=process.env.VUE_APP_BASEURL

// 创建Vue实例
new Vue({
  router,
  render: h => h(App)
}).$mount("#app");
