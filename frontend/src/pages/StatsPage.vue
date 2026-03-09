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

    <!-- Period toggle (shared) -->
    <div class="row justify-end q-mb-md">
      <q-btn-toggle
        v-model="period"
        :options="[{ label: 'Weekly', value: 'weekly' }, { label: 'Monthly', value: 'monthly' }]"
        dense flat
        @update:model-value="onPeriodChange"
      />
    </div>

    <!-- Volume over time -->
    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="row items-center q-mb-sm">
          <div class="text-subtitle1 text-weight-bold col">Volume Over Time</div>
          <q-btn v-if="selectedLabel" flat dense size="sm" icon="close" color="grey"
            @click="clearSelection" label="Clear selection" />
        </div>
        <VolumeChart
          :data="statsStore.volume"
          :selected-label="selectedLabel"
          @bar-click="onBarClick"
        />
      </q-card-section>
    </q-card>

    <!-- Muscle breakdown -->
    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="row items-center q-mb-sm">
          <div class="text-subtitle1 text-weight-bold col">Muscle Group Breakdown</div>
          <div v-if="selectedLabel" class="text-caption text-grey">{{ breakdownRangeLabel }}</div>
        </div>
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

const statsStore    = useStatsStore()
const period        = ref<'weekly' | 'monthly'>('weekly')
const selectedLabel = ref<string | undefined>(undefined)

function dateRangeFor(label: string): { from: string; to: string } {
  if (period.value === 'monthly') {
    const start = new Date(label)
    const end   = new Date(start.getFullYear(), start.getMonth() + 1, 0)
    return { from: label, to: end.toISOString().slice(0, 10) }
  } else {
    const start = new Date(label)
    const end   = new Date(start)
    end.setDate(end.getDate() + 6)
    return { from: label, to: end.toISOString().slice(0, 10) }
  }
}

const breakdownRangeLabel = computed(() => {
  if (!selectedLabel.value) return ''
  const { from, to } = dateRangeFor(selectedLabel.value)
  return `${from} → ${to}`
})

function onBarClick(label: string) {
  selectedLabel.value = label
  const { from, to } = dateRangeFor(label)
  statsStore.fetchMuscleBreakdown(period.value, 12, from, to)
}

function clearSelection() {
  selectedLabel.value = undefined
  statsStore.fetchMuscleBreakdown(period.value)
}

function onPeriodChange() {
  selectedLabel.value = undefined
  statsStore.fetchVolume(period.value)
  statsStore.fetchMuscleBreakdown(period.value)
}

const lifetimeStats = computed(() => [
  { label: 'Total Workouts', value: statsStore.summary?.totalWorkouts  ?? '–' },
  { label: 'Volume (kg)',    value: statsStore.summary ? Math.round(statsStore.summary.weeklyVolume) : '–' },
  { label: 'Hours Trained', value: statsStore.summary ? Math.round(statsStore.summary.weeklyMinutes / 60) : '–' },
  { label: 'Streak',        value: statsStore.summary?.streak ?? '–' },
])

onMounted(async () => {
  await Promise.all([
    statsStore.fetchSummary(),
    statsStore.fetchVolume(period.value),
    statsStore.fetchPRs(),
    statsStore.fetchMuscleBreakdown(period.value),
  ])
})
</script>

