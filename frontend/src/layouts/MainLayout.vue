<template>
  <q-layout view="lHh Lpr lFf">
    <q-header style="background: #0d0d0d; border-bottom: 1px solid #1e1e1e;">
      <q-toolbar>
        <q-btn flat round dense icon="menu" color="white" @click="drawerOpen = !drawerOpen" />
        <q-toolbar-title>
          <span style="font-weight: 900; font-size: 1.2rem; letter-spacing: 0.1em; color: #ff6200;">
            IRON LOG
          </span>
        </q-toolbar-title>

        <!-- User chip -->
        <div v-if="auth.user" style="display: flex; align-items: center; gap: 8px; margin-right: 8px;">
          <div style="
            width: 30px; height: 30px; border-radius: 50%;
            background: #ff6200; display: flex; align-items: center; justify-content: center;
            font-weight: 800; font-size: 0.8rem; color: #fff; flex-shrink: 0;
          ">{{ initials }}</div>
          <span style="font-size: 0.8rem; color: #aaa; font-weight: 500;">{{ auth.user.name }}</span>
        </div>

        <q-btn flat round dense icon="logout" color="grey-5" @click="logout" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="drawerOpen" show-if-above :width="220" :breakpoint="600">
      <q-scroll-area class="fit" style="background: #0d0d0d;">
        <div style="padding: 24px 20px 12px; font-weight: 900; font-size: 1.1rem; letter-spacing: 0.12em; color: #ff6200;">
          IRON LOG
        </div>
        <q-list>
          <q-item
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            clickable
            v-ripple
            exact
            style="border-radius: 0; color: #aaaaaa;"
          >
            <q-item-section avatar>
              <q-icon :name="link.icon" />
            </q-item-section>
            <q-item-section style="font-weight: 600; font-size: 0.85rem; letter-spacing: 0.04em; text-transform: uppercase;">
              {{ link.label }}
            </q-item-section>
          </q-item>
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <q-page-container class="app-bg">
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from 'stores/auth'

const drawerOpen = ref(false)
const router     = useRouter()
const auth       = useAuthStore()

const initials = computed(() => {
  if (!auth.user?.name) return '?'
  return auth.user.name.split(' ').map(p => p[0]).join('').slice(0, 2).toUpperCase()
})

const navLinks = [
  { to: '/app/dashboard', icon: 'dashboard',  label: 'Dashboard'  },
  { to: '/app/log',       icon: 'fitness_center', label: 'Log Workout' },
  { to: '/app/history',   icon: 'history',    label: 'History'    },
  { to: '/app/exercises', icon: 'list',       label: 'Exercises'  },
  { to: '/app/stats',    icon: 'bar_chart',    label: 'Stats'    },
  { to: '/app/reports',  icon: 'auto_awesome', label: 'Reports'  },
]

function logout() {
  auth.logout()
  void router.push('/login')
}
</script>
