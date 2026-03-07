<template>
  <q-page class="q-pa-md">
    <div class="text-h5 text-weight-bold q-mb-md">History</div>

    <!-- Filter bar -->
    <div class="row q-col-gutter-sm q-mb-md">
      <div class="col-12 col-sm-6">
        <q-input v-model="search" label="Search workouts..." dense outlined clearable @update:model-value="applyFilters" />
      </div>
    </div>

    <!-- Workout list -->
    <q-expansion-item
      v-for="w in filtered"
      :key="w.id"
      :label="w.title"
      :caption="formatDate(w.startedAt)"
      icon="fitness_center"
      class="q-mb-sm"
      bordered
      expand-separator
    >
      <q-card flat>
        <q-card-section>
          <div class="row q-col-gutter-sm q-mb-sm text-caption text-grey">
            <span v-if="w.endedAt">Duration: {{ duration(w.startedAt, w.endedAt) }}</span>
            <span v-if="!w.endedAt" class="text-orange">In Progress</span>
          </div>
          <div class="text-caption text-grey q-mb-xs">(Expand to see sets — full detail loads here)</div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat color="negative" label="Delete" @click="remove(w.id)" />
        </q-card-actions>
      </q-card>
    </q-expansion-item>

    <div v-if="!filtered.length" class="text-grey text-center q-mt-xl">No workouts found.</div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useWorkoutStore } from 'stores/workout'

const $q           = useQuasar()
const workoutStore = useWorkoutStore()
const search       = ref('')

const filtered = computed(() =>
  workoutStore.workouts.filter(w =>
    w.title.toLowerCase().includes(search.value.toLowerCase())
  )
)

function applyFilters() { /* reactive via computed */ }

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })
}

function duration(start: string, end: string) {
  const mins = Math.round((new Date(end).getTime() - new Date(start).getTime()) / 60000)
  return `${mins} min`
}

async function remove(id: string) {
  $q.dialog({ title: 'Delete workout?', message: 'This cannot be undone.', cancel: true, persistent: true })
    .onOk(async () => {
      await workoutStore.deleteWorkout(id)
    })
}

onMounted(() => workoutStore.fetchWorkouts())
</script>
