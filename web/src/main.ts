import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'
import axios from 'axios'

axios.defaults.baseURL = process.env.VUE_APP_SERVER

createApp(App).use(store).use(router).use(Antd).mount('#app')

console.log('running in ', process.env.NODE_ENV)
