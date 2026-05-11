import axios from 'axios'
import {createDiscreteApi} from 'naive-ui'
import router from "@/app/router/router.ts";

const {message} = createDiscreteApi(['message'])
let redirectingLogin = false

// baseURL = import.meta.env.VITE_APP_BASE_API_URL
const request = axios.create({
  baseURL: '',
  timeout: 30000,
  withCredentials: true,
  headers: {
    'X-Requested-With': 'XMLHttpRequest',
  }
})

// 全局请求拦截器
request.interceptors.request.use(config => {
  return config
}, function (error) {
  return Promise.reject(error)
},)

// 全局响应拦截器
request.interceptors.response.use(response => {
  if (typeof response.data !== 'object') {
    message.error('服务端异常！')
    return Promise.reject(response)
  }
  //data 业务数据
  const {data} = response
  //不是0和40400就进行拦截
  if (data.code !== 0 && data.code !== 40400) {
    if (data.message) {
      message.warning(data.message)
    }
    //未登录
    if (data.code === 40100) {
      if (!response.request.responseURL.includes('login') && !window.location.pathname.includes('/login')) {
        if (!redirectingLogin) {
          redirectingLogin = true
          router.push(`/login?from=${window.location.pathname}`)
        }
      }
    }
    return Promise.reject(response)
  }
  return data
}, (error) => {
  if (!error.response) {
    message.error('网络异常，请检查连接')
    return Promise.reject(error)
  }
  if (error.response.status === 404) {
    message.error('资源不存在')
  }
  if (error.response.status === 500) {
    message.error('网络连接异常')
  }
  console.error(error)
  return Promise.reject(error)
})

export default request
