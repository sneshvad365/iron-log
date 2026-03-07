<template>
  <q-card flat bordered class="text-center q-pa-md">
    <div class="text-caption text-grey q-mb-xs">{{ pr.exerciseName }}</div>
    <div class="text-h6 text-primary text-weight-bold">{{ pr.weightKg }} kg × {{ pr.reps }}</div>
    <div class="text-caption text-grey">{{ pr.date }}</div>
  </q-card>
</template>

<script setup lang="ts">
defineProps<{
  pr: { exerciseName: string; weightKg: number; reps: number; estimatedOneRm: number; date: string }
}>()
</script>
