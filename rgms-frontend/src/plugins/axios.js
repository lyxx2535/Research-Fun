"use strict";

import Vue from 'vue';
import axios from "axios";

// Full config:  https://github.com/axios/axios#request-config
// axios.defaults.baseURL = process.env.baseURL || process.env.apiUrl || '';
// axios.defaults.headers.common['Authorization'] = AUTH_TOKEN;
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

// dyj服务器:
// http://119.29.53.191:7069/rgms-0.0.1

// 最终上线:
// http://42.193.37.120:9712

// 开发版
// http://119.29.53.191:8088/
let config = {
  //baseURL: "http://172.28.120.162:9720",
  //baseURL: "http://192.168.67.1:80",
  // baseURL: "http://localhost:9720",
  baseURL: "http://42.193.37.120:9712",
  headers: {
    'Content-Type': 'application/json'
  },
  // timeout: 60 * 1000, // Timeout
  // withCredentials: true // Check cross-site Access-Control
};

const _axios = axios.create(config);
_axios.interceptors.request.use(
  function(config) {
    // Do something before request is sent
    if (sessionStorage.getItem('token')) {
      config.headers.token = sessionStorage.getItem('token');
    }
    return config;
  },
  function(error) {
    // Do something with request error
    return Promise.reject(error);
  }
);

// Add a response interceptor
_axios.interceptors.response.use(
  function(response) {
    // Do something with response data
    return response;
  },
  function(error) {
    // Do something with response error
    return Promise.reject(error);
  }
);

Plugin.install = function(Vue, options) {
  Vue.axios = _axios;
  window.axios = _axios;
  Object.defineProperties(Vue.prototype, {
    axios: {
      get() {
        return _axios;
      }
    },
    $axios: {
      get() {
        return _axios;
      }
    },
  });
};

Vue.use(Plugin)

export default Plugin;
