import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from 'api/auth'

interface User {
  id: string
  email: string
  name: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const user  = ref<User | null>(null)

  async function login(email: string, password: string) {
    const res = await authApi.login(email, password)
    token.value = res.data.data.token
    user.value  = res.data.data.user
  }

  async function register(name: string, email: string, password: string) {
    const res = await authApi.register(name, email, password)
    token.value = res.data.data.token
    user.value  = res.data.data.user
  }

  function logout() {
    token.value = null
    user.value  = null
  }

  return { token, user, login, register, logout }
})
