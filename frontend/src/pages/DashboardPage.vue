<template>
  <q-page class="q-pa-md">
    <div class="text-h5 text-weight-bold q-mb-md">Dashboard</div>

    <!-- Weekly summary -->
    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-4">
        <q-card flat bordered class="text-center q-pa-md">
          <div class="text-h4 text-primary text-weight-bold">{{ summary?.weeklyWorkouts ?? '–' }}</div>
          <div class="text-caption text-grey">Sessions this week</div>
        </q-card>
      </div>
      <div class="col-4">
        <q-card flat bordered class="text-center q-pa-md">
          <div class="text-h4 text-primary text-weight-bold">{{ summary ? Math.round(summary.weeklyVolume) : '–' }}</div>
          <div class="text-caption text-grey">Volume (kg)</div>
        </q-card>
      </div>
      <div class="col-4">
        <q-card flat bordered class="text-center q-pa-md">
          <div class="text-h4 text-primary text-weight-bold">{{ summary?.streak ?? '–' }}</div>
          <div class="text-caption text-grey">Day streak 🔥</div>
        </q-card>
      </div>
    </div>

    <!-- Quick start -->
    <q-btn
      label="+ NEW SESSION"
      color="primary"
      class="full-width q-mb-lg"
      size="lg"
      unelevated
      to="/app/log"
    />

    <!-- Recent workouts -->
    <div class="text-h6 q-mb-sm">Recent Workouts</div>
    <q-list separator>
      <q-item v-for="w in recentWorkouts" :key="w.id" clickable v-ripple :to="`/app/history`">
        <q-item-section>
          <q-item-label>{{ w.title }}</q-item-label>
          <q-item-label caption>{{ formatDate(w.startedAt) }}</q-item-label>
        </q-item-section>
        <q-item-section side>
          <q-icon name="chevron_right" />
        </q-item-section>
      </q-item>
      <q-item v-if="!recentWorkouts.length">
        <q-item-section class="text-grey text-center">No workouts yet</q-item-section>
      </q-item>
    </q-list>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useStatsStore } from 'stores/stats'
import { useWorkoutStore } from 'stores/workout'

const statsStore   = useStatsStore()
const workoutStore = useWorkoutStore()

const summary      = computed(() => statsStore.summary)
const recentWorkouts = computed(() => workoutStore.workouts.slice(0, 5))

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
}

onMounted(async () => {
  await Promise.all([statsStore.fetchSummary(), workoutStore.fetchWorkouts()])
})
</script>
