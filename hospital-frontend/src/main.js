/*
 * main js
 *
 */
import Vue from "vue";
import App from "./App.vue";
import './assets/font/iconfont.css'
import router from "./router";
Vue.config.productionTip = false;

// 创建Vue实例
new Vue({
  router,
  render: h => h(App)
}).$mount("#app");
