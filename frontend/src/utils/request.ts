import axios from 'axios';
import { Message, MessageBox } from 'element-ui';
import store from '@/store';
import router from '@/router';

const request = axios.create({
    baseURL: process.env.VUE_APP_API_URL,
    timeout: 10000
});

request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

request.interceptors.response.use(
    response => {
        const res = response.data;
        if (res.code !== 200) {
            Message({
                message: res.message || '错误',
                type: 'error',
                duration: 5 * 1000
            });

            if (res.code === 401) {
                MessageBox.confirm('登录状态已过期，请重新登录', '确定登出', {
                    confirmButtonText: '重新登录',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    store.dispatch('logout').then(() => {
                        router.push('/login');
                    });
                });
            }
            return Promise.reject(new Error(res.message || '错误'));
        }
        return res;
    },
    error => {
        Message({
            message: error.message,
            type: 'error',
            duration: 5 * 1000
        });
        return Promise.reject(error);
    }
);

export default request;
