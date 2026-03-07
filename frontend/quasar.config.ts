import { defineConfig } from '#q-app/wrappers'
import { fileURLToPath } from 'node:url'
import path from 'node:path'

const src = fileURLToPath(new URL('./src', import.meta.url))

export default defineConfig((_ctx) => {
  return {
    boot: ['pinia', 'axios'],

    css: ['app.scss'],

    extras: ['roboto-font', 'material-icons'],

    build: {
      target: { browser: ['es2022'] },
      vueRouterMode: 'history',
      typescript: { strict: true },
      alias: {
        stores:     path.join(src, 'stores'),
        pages:      path.join(src, 'pages'),
        layouts:    path.join(src, 'layouts'),
        components: path.join(src, 'components'),
        api:        path.join(src, 'api'),
        assets:     path.join(src, 'assets'),
        boot:       path.join(src, 'boot'),
      },
    },

    devServer: {
      port: 9000,
      allowedHosts: ['tubbier-nonbearded-sloan.ngrok-free.dev'],
      proxy: {
        '/api': {
          target: process.env.VITE_API_BASE_URL || 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },

    framework: {
      config: { dark: true },
      plugins: ['Notify', 'Dialog', 'Loading'],
    },

    animations: [],

    ssr: { pwa: false },
    pwa: {},
    cordova: {},
    capacitor: { hideSplashscreen: true },
    electron: {
      inspectPort: 5858,
      bundler: 'packager',
    },
    bex: { extraScripts: [] },
  }
})
