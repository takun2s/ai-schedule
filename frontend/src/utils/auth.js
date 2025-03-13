const TOKEN_KEY = 'token'
const LAST_PATH_KEY = 'lastPath'

export default {
  // Token 相关操作
  getToken() {
    return localStorage.getItem(TOKEN_KEY)
  },
  setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
  },
  removeToken() {
    localStorage.removeItem(TOKEN_KEY)
  },

  // 路径相关操作
  setLastPath(path) {
    if (path && path !== '/login' && !path.startsWith('/login?')) {
      localStorage.setItem(LAST_PATH_KEY, path)
      console.log('Saved last path:', path) // 调试日志
    }
  },
  getLastPath() {
    return localStorage.getItem(LAST_PATH_KEY)
  },
  clearLastPath() {
    localStorage.removeItem(LAST_PATH_KEY)
  },
  
  // 登出操作
  logout() {
    this.removeToken()
    this.clearLastPath()
  },

  // 认证相关操作
  isAuthenticated() {
    return !!this.getToken()
  },

  clearAuth() {
    this.removeToken()
    this.clearLastPath()
  }
}
