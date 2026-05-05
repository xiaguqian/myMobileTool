import axios from 'axios'
import { showToast } from 'vant'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    console.log('请求错误:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200 && res.code !== 201) {
      showToast(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.log('响应错误:', error)
    showToast(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
