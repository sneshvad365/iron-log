import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from 'api/auth'

interface User {
  id: string
  email: string
  name: string
}

const TOKEN_KEY = 'iron_log_token'
const USER_KEY  = 'iron_log_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const user  = ref<User | null>(JSON.parse(localStorage.getItem(USER_KEY) ?? 'null'))

  async function login(email: string, password: string) {
    const res = await authApi.login(email, password)
    token.value = res.data.data.token
    user.value  = res.data.data.user
    localStorage.setItem(TOKEN_KEY, token.value)
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
  }

  async function register(name: string, email: string, password: string) {
    const res = await authApi.register(name, email, password)
    token.value = res.data.data.token
    user.value  = res.data.data.user
    localStorage.setItem(TOKEN_KEY, token.value)
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
  }

  function logout() {
    token.value = null
    user.value  = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, user, login, register, logout }
})
