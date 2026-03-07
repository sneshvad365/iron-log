<template>
  <div class="text-h6 text-mono text-grey-7">{{ display }}</div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const props   = defineProps<{ startedAt: string }>()
const display = ref('00:00:00')
let interval: ReturnType<typeof setInterval>

function tick() {
  const elapsed = Math.floor((Date.now() - new Date(props.startedAt).getTime()) / 1000)
  const h = Math.floor(elapsed / 3600)
  const m = Math.floor((elapsed % 3600) / 60)
  const s = elapsed % 60
  display.value = [h, m, s].map(n => String(n).padStart(2, '0')).join(':')
}

onMounted(() => { tick(); interval = setInterval(tick, 1000) })
onUnmounted(() => clearInterval(interval))
</script>
