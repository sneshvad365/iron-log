<template>
  <div class="exercise-gif" :style="{ width: size + 'px', height: size + 'px' }">
    <template v-if="gifUrl && !hasError">
      <img :src="gifUrl" :alt="name" class="exercise-gif__frame" :class="{ active: !showSecond }" @error="onError" />
      <img v-if="gifUrl2" :src="gifUrl2" :alt="name" class="exercise-gif__frame" :class="{ active: showSecond }" />
    </template>
    <div v-else class="exercise-gif__placeholder">
      <q-icon name="fitness_center" color="grey-7" :size="iconSize" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'

const props = withDefaults(defineProps<{
  gifUrl?: string | null
  gifUrl2?: string | null
  name?: string
  size?: number
}>(), {
  size: 64,
  name: '',
})

const showSecond = ref(false)
const hasError   = ref(false)
let interval: ReturnType<typeof setInterval> | null = null

const iconSize = computed(() => Math.round(props.size * 0.4) + 'px')

function onError() { hasError.value = true }

onMounted(() => {
  if (props.gifUrl && props.gifUrl2) {
    interval = setInterval(() => { showSecond.value = !showSecond.value }, 1000)
  }
})

onUnmounted(() => { if (interval) clearInterval(interval) })
</script>

<style scoped>
.exercise-gif {
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #1a1a1a;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.exercise-gif__frame {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: none;
}
.exercise-gif__frame.active {
  opacity: 1;
}
.exercise-gif__placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}
</style>
