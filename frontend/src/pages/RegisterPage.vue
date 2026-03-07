<template>
  <q-card flat class="q-pa-lg" style="background: #1a1a1a; border: 1px solid #2a2a2a; border-radius: 8px;">
    <q-card-section>
      <div style="font-weight: 800; font-size: 1.1rem; letter-spacing: 0.06em; text-transform: uppercase; color: #fff; margin-bottom: 4px;">Create Account</div>
      <div class="orange-bar"></div>
      <q-input v-model="name"     label="Name"     outlined dense dark class="q-mb-sm" />
      <q-input v-model="email"    label="Email"    type="email"    outlined dense dark class="q-mb-sm" />
      <q-input v-model="password" label="Password" type="password" outlined dense dark class="q-mb-md" />
      <q-btn label="Register" color="primary" unelevated class="full-width" style="font-weight: 800; letter-spacing: 0.08em;" :loading="loading" @click="submit" />
    </q-card-section>
    <q-card-section class="text-center q-pt-none">
      <router-link to="/login" style="color: #ff6200; font-size: 0.85rem;">Already have an account? Sign in</router-link>
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'stores/auth'

const router   = useRouter()
const $q       = useQuasar()
const auth     = useAuthStore()
const name     = ref('')
const email    = ref('')
const password = ref('')
const loading  = ref(false)

async function submit() {
  loading.value = true
  try {
    await auth.register(name.value, email.value, password.value)
    void router.push('/app/dashboard')
  } catch (e: any) {
    $q.notify({ type: 'negative', message: e.response?.data?.error ?? 'Registration failed' })
  } finally {
    loading.value = false
  }
}
</script>
