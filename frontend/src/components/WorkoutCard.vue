<template>
  <q-card flat bordered class="q-mb-sm">
    <q-card-section>
      <div class="row items-center">
        <div class="col">
          <div class="text-subtitle1 text-weight-bold">{{ workout.title }}</div>
          <div class="text-caption text-grey">{{ formatDate(workout.startedAt) }}</div>
        </div>
        <q-icon name="chevron_right" color="grey" />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import type { Workout } from 'stores/workout'

defineProps<{ workout: Workout }>()

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' })
}
</script>
