<template>
  <q-card flat bordered>
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

defineProps<{ exercise: Exercise }>()
const emit = defineEmits<{ (e: 'delete'): void }>()
</script>
