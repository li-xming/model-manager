/**
 * HTTP请求工具
 */
import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import type { ResponseVO } from '../types/api';

// 创建axios实例
const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  (response: AxiosResponse<ResponseVO>) => {
    const { data } = response;
    
    // 如果响应成功
    if (data.code === 200 || data.code === 0) {
      return data;
    }
    
    // 如果业务逻辑错误，仅向上抛出错误，由调用方自行决定如何提示
    return Promise.reject(new Error(data.message || '请求失败'));
  },
  (error) => {
    // HTTP错误处理
    let errorMessage = '请求失败';
    
    if (error.response) {
      const { status, data } = error.response;
      switch (status) {
        case 400:
          errorMessage = data?.message || '请求参数错误';
          break;
        case 401:
          errorMessage = '未授权，请重新登录';
          // 可以在这里跳转到登录页
          break;
        case 403:
          errorMessage = '拒绝访问';
          break;
        case 404:
          errorMessage = '请求的资源不存在';
          break;
        case 500:
          errorMessage = data?.message || '服务器内部错误';
          break;
        default:
          errorMessage = data?.message || `请求失败 (${status})`;
      }
    } else if (error.request) {
      errorMessage = '网络错误，请检查网络连接';
    } else {
      errorMessage = error.message || '请求失败';
    }
    
    // 将解析后的错误信息挂到 error 对象上，方便调用方使用
    (error as any).friendlyMessage = errorMessage;
    return Promise.reject(error);
  }
);

export default instance;

