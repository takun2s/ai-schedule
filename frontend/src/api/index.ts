import request from '@/utils/request';

export const taskApi = {
  list: () => request.get('/api/tasks'),
  create: (data: any) => request.post('/api/tasks', data),
  update: (id: number, data: any) => request.put(`/api/tasks/${id}`, data),
  delete: (id: number) => request.delete(`/api/tasks/${id}`),
  execute: (id: number) => request.post(`/api/tasks/${id}/execute`),
  getLogs: (id: number) => request.get(`/api/tasks/${id}/logs`),
};

export const dagApi = {
  list: () => request.get('/api/dags'),
  create: (data: any) => request.post('/api/dags', data),
  update: (id: number, data: any) => request.put(`/api/dags/${id}`, data),
  delete: (id: number) => request.delete(`/api/dags/${id}`),
  execute: (id: number) => request.post(`/api/dags/${id}/execute`),
  getStatus: (id: number) => request.get(`/api/dags/${id}/status`),
};

export const alertApi = {
  list: () => request.get('/api/alerts'),
  create: (data: any) => request.post('/api/alerts', data),
  update: (id: number, data: any) => request.put(`/api/alerts/${id}`, data),
  delete: (id: number) => request.delete(`/api/alerts/${id}`),
};

export const authApi = {
  login: (data: any) => request.post('/api/auth/login', data),
  logout: () => request.post('/api/auth/logout'),
  getCurrentUser: () => request.get('/api/auth/user'),
};
