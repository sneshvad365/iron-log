<template>
  <q-card flat bordered>
    <ExerciseGif :gif-url="exercise.gifUrl" :gif-url2="exercise.gifUrl2" :name="exercise.name" :size="160"
      style="width: 100%; height: 160px; border-radius: 8px 8px 0 0;" />
    <q-card-section>
      <div class="row items-start no-wrap">
        <div class="col">
          <div class="text-subtitle1 text-weight-bold">{{ exercise.name }}</div>
          <q-badge color="primary" class="q-mr-xs">{{ exercise.muscleGroup }}</q-badge>
          <q-badge color="grey-5" text-color="dark">{{ exercise.type }}</q-badge>
        </div>
        <q-btn
          v-if="exercise.createdBy"
          flat round dense icon="delete" color="grey-5" size="sm"
          @click="emit('delete')"
        />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import type { Exercise } from 'stores/exercise'
import ExerciseGif from 'components/ExerciseGif.vue'

defineProps<{ exercise: Exercise }>()
const emit = defineEmits<{ (e: 'delete'): void }>()
</script>
