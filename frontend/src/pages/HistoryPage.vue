<template>
  <q-page class="q-pa-md">
    <div class="text-h5 text-weight-bold q-mb-md">History</div>

    <div class="row q-col-gutter-sm q-mb-md">
      <div class="col-12 col-sm-6">
        <q-input v-model="search" label="Search workouts..." dense outlined dark clearable />
      </div>
    </div>

    <q-expansion-item
      v-for="w in filtered"
      :key="w.id"
      :label="w.title"
      :caption="formatDate(w.startedAt)"
      icon="fitness_center"
      class="q-mb-sm"
      style="background: #1a1a1a; border: 1px solid #2a2a2a; border-radius: 8px;"
      expand-separator
      @show="loadDetail(w.id)"
    >
      <q-card flat style="background: #1a1a1a;">
        <q-card-section>
          <!-- Summary row -->
          <div class="row q-gutter-md q-mb-md text-caption" style="color: #aaa;">
            <span v-if="w.endedAt"><q-icon name="timer" /> {{ duration(w.startedAt, w.endedAt) }}</span>
            <span v-if="!w.endedAt" class="text-orange">In Progress</span>
          </div>

          <!-- Sets grouped by exercise -->
          <q-inner-loading :showing="loadingId === w.id" />

          <div v-if="details[w.id]">
            <div
              v-for="(sets, exName) in groupByExercise(details[w.id].sets)"
              :key="exName"
              class="q-mb-md"
            >
              <div style="font-weight: 700; font-size: 0.85rem; color: #ff6200; text-transform: uppercase; letter-spacing: 0.04em; margin-bottom: 6px;">
                {{ exName }}
              </div>
              <q-markup-table flat dense style="background: #111; border-radius: 6px;">
                <thead>
                  <tr style="color: #666; font-size: 0.75rem;">
                    <th class="text-left">Set</th>
                    <th class="text-right">Weight</th>
                    <th class="text-right">Reps</th>
                    <th class="text-right">1RM est.</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="s in sets" :key="s.id" style="color: #ccc; font-size: 0.85rem;">
                    <td>{{ s.setNumber }}</td>
                    <td class="text-right">{{ s.weightKg != null ? s.weightKg + ' kg' : '—' }}</td>
                    <td class="text-right">{{ s.reps ?? '—' }}</td>
                    <td class="text-right" style="color: #ff6200;">
                      {{ s.weightKg && s.reps ? (s.weightKg * (1 + s.reps / 30)).toFixed(1) + ' kg' : '—' }}
                    </td>
                  </tr>
                </tbody>
              </q-markup-table>
            </div>
          </div>
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
import * as workoutsApi from 'api/workouts'

const $q           = useQuasar()
const workoutStore = useWorkoutStore()
const search       = ref('')
const details      = ref<Record<string, any>>({})
const loadingId    = ref<string | null>(null)

const filtered = computed(() =>
  workoutStore.workouts
    .filter(w => w.title.toLowerCase().includes(search.value.toLowerCase()))
    .slice()
    .sort((a, b) => new Date(b.startedAt).getTime() - new Date(a.startedAt).getTime())
)

async function loadDetail(id: string) {
  if (details.value[id]) return
  loadingId.value = id
  try {
    const res = await workoutsApi.getWorkout(id)
    details.value[id] = res.data.data
  } finally {
    loadingId.value = null
  }
}

function groupByExercise(sets: any[]) {
  const groups: Record<string, any[]> = {}
  for (const s of sets) {
    const name = s.exerciseName ?? 'Unknown'
    if (!groups[name]) groups[name] = []
    groups[name].push(s)
  }
  return groups
}

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
