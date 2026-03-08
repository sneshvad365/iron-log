import { route } from 'quasar/wrappers'
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('layouts/AuthLayout.vue'),
    children: [
      { path: '',         redirect: '/login' },
      { path: 'login',    component: () => import('pages/LoginPage.vue') },
      { path: 'register', component: () => import('pages/RegisterPage.vue') },
    ],
  },
  {
    path: '/app',
    component: () => import('layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '',          redirect: '/app/dashboard' },
      { path: 'dashboard', component: () => import('pages/DashboardPage.vue') },
      { path: 'log',       component: () => import('pages/LogWorkoutPage.vue') },
      { path: 'history',   component: () => import('pages/HistoryPage.vue') },
      { path: 'exercises', component: () => import('pages/ExercisesPage.vue') },
      { path: 'stats',     component: () => import('pages/StatsPage.vue') },
      { path: 'reports',   component: () => import('pages/ReportsPage.vue') },
      { path: 'reports/:id', component: () => import('pages/ReportDetailPage.vue') },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/login' },
]

export default route(function () {
  const Router = createRouter({
    history: createWebHistory(),
    routes,
  })

  // Auth guard — import store lazily so Pinia is ready (boot runs before routing)
  Router.beforeEach(async (to) => {
    const { useAuthStore } = await import('stores/auth')
    const auth = useAuthStore()
    if (to.meta.requiresAuth && !auth.token) return { path: '/login' }
    if ((to.path === '/login' || to.path === '/register') && auth.token) return { path: '/app/dashboard' }
  })

  return Router
})
