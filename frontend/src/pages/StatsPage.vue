<template>
  <q-page class="q-pa-md">
    <div class="text-h5 text-weight-bold q-mb-md">Stats</div>

    <!-- Lifetime stats -->
    <div class="row q-col-gutter-md q-mb-lg">
      <div class="col-6 col-sm-3" v-for="stat in lifetimeStats" :key="stat.label">
        <q-card flat bordered class="text-center q-pa-md">
          <div class="text-h5 text-primary text-weight-bold">{{ stat.value }}</div>
          <div class="text-caption text-grey">{{ stat.label }}</div>
        </q-card>
      </div>
    </div>

    <!-- Volume over time -->
    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="row items-center q-mb-sm">
          <div class="text-subtitle1 text-weight-bold col">Volume Over Time</div>
          <q-btn-toggle
            v-model="volumePeriod"
            :options="[{ label: 'Weekly', value: 'weekly' }, { label: 'Monthly', value: 'monthly' }]"
            dense flat
            @update:model-value="statsStore.fetchVolume(volumePeriod)"
          />
        </div>
        <VolumeChart :data="statsStore.volume" />
      </q-card-section>
    </q-card>

    <!-- Muscle breakdown -->
    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="text-subtitle1 text-weight-bold q-mb-sm">Muscle Group Breakdown</div>
        <MuscleBreakdownChart :data="statsStore.muscleBreakdown" />
      </q-card-section>
    </q-card>

    <!-- PRs table -->
    <q-card flat bordered>
      <q-card-section>
        <div class="text-subtitle1 text-weight-bold q-mb-sm">All-Time PRs</div>
        <q-markup-table flat dense>
          <thead>
            <tr><th>Exercise</th><th>Weight (kg)</th><th>Reps</th><th>Est. 1RM</th><th>Date</th></tr>
          </thead>
          <tbody>
            <tr v-for="pr in statsStore.prs" :key="pr.exerciseName">
              <td>{{ pr.exerciseName }}</td>
              <td>{{ pr.weightKg }}</td>
              <td>{{ pr.reps }}</td>
              <td>{{ pr.estimatedOneRm.toFixed(1) }}</td>
              <td>{{ pr.date }}</td>
            </tr>
            <tr v-if="!statsStore.prs.length">
              <td colspan="5" class="text-center text-grey">No data yet</td>
            </tr>
          </tbody>
        </q-markup-table>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useStatsStore } from 'stores/stats'
import VolumeChart from 'components/VolumeChart.vue'
import MuscleBreakdownChart from 'components/MuscleBreakdownChart.vue'

const statsStore  = useStatsStore()
const volumePeriod = ref<'weekly' | 'monthly'>('weekly')

const lifetimeStats = computed(() => [
  { label: 'Total Workouts',  value: statsStore.summary?.totalWorkouts  ?? '–' },
  { label: 'Volume (kg)',     value: statsStore.summary ? Math.round(statsStore.summary.weeklyVolume) : '–' },
  { label: 'Hours Trained',  value: statsStore.summary ? Math.round(statsStore.summary.weeklyMinutes / 60) : '–' },
  { label: 'Streak',         value: statsStore.summary?.streak ?? '–' },
])

onMounted(async () => {
  await Promise.all([
    statsStore.fetchSummary(),
    statsStore.fetchVolume(),
    statsStore.fetchPRs(),
    statsStore.fetchMuscleBreakdown(),
  ])
})
</script>
