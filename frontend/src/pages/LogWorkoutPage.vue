<template>
  <q-page class="q-pa-md">
    <!-- No active session -->
    <div v-if="!workout" class="flex flex-center column" style="min-height: 60vh">
      <q-icon name="fitness_center" size="64px" color="grey-4" />
      <div class="text-h6 text-grey q-mt-md q-mb-lg">No active session</div>
      <q-input v-model="newTitle" label="Session title" outlined dense class="q-mb-md" style="width: 320px" />
      <q-btn label="Start Session" color="primary" size="lg" unelevated :loading="starting" @click="startSession" />
    </div>

    <!-- Active session -->
    <div v-else>
      <div class="row items-center q-mb-md">
        <q-input v-model="editTitle" dense borderless class="text-h6 text-weight-bold col" @blur="saveTitle" />
        <ActiveSessionTimer :started-at="workout.startedAt" />
        <q-btn label="Finish" color="primary" unelevated class="q-ml-md" @click="finish" />
      </div>

      <div class="text-caption text-grey q-mb-sm">Exercises</div>
      <q-card v-for="(group, exerciseId) in groupedSets" :key="exerciseId" flat bordered class="q-mb-md">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold q-mb-sm">{{ exerciseNames[exerciseId] }}</div>
          <q-markup-table flat dense>
            <thead>
              <tr>
                <th>SET</th><th>WEIGHT (KG)</th><th>REPS</th><th>DONE</th><th></th>
              </tr>
            </thead>
            <tbody>
              <SetRow
                v-for="s in group"
                :key="s.id"
                :set="s"
                @update="(data) => workoutStore.updateSet(s.id, data)"
                @remove="() => workoutStore.removeSet(s.id)"
              />
            </tbody>
          </q-markup-table>
          <q-btn
            flat dense label="+ ADD SET" color="primary" class="q-mt-sm"
            @click="addSet(exerciseId, group)"
          />
        </q-card-section>
      </q-card>

      <q-btn
        label="+ ADD EXERCISE"
        color="primary" outline class="full-width q-mt-sm"
        @click="showExercisePicker = true"
      />
    </div>

    <!-- Exercise picker dialog -->
    <q-dialog v-model="showExercisePicker">
      <q-card style="min-width: 340px">
        <q-card-section>
          <div class="text-h6">Pick an Exercise</div>
          <q-input v-model="search" placeholder="Search..." dense outlined class="q-mt-sm" />
        </q-card-section>
        <q-list separator style="max-height: 360px; overflow-y: auto">
          <q-item
            v-for="e in filteredExercises" :key="e.id"
            clickable v-ripple
            @click="addExercise(e)"
          >
            <q-item-section>
              <q-item-label>{{ e.name }}</q-item-label>
              <q-item-label caption>{{ e.muscleGroup }}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
        <q-card-actions align="right">
          <q-btn flat label="Close" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useWorkoutStore } from 'stores/workout'
import { useExerciseStore } from 'stores/exercise'
import type { Exercise } from 'stores/exercise'
import type { WorkoutSet } from 'stores/workout'
import SetRow from 'components/SetRow.vue'
import ActiveSessionTimer from 'components/ActiveSessionTimer.vue'

const router        = useRouter()
const workoutStore  = useWorkoutStore()
const exerciseStore = useExerciseStore()

const workout            = computed(() => workoutStore.activeWorkout)
const newTitle           = ref('Morning Workout')
const editTitle          = ref('')
const starting           = ref(false)
const showExercisePicker = ref(false)
const search             = ref('')

const exerciseNames = computed(() => {
  const map: Record<string, string> = {}
  exerciseStore.exercises.forEach(e => { map[e.id] = e.name })
  return map
})

const groupedSets = computed(() => {
  const groups: Record<string, WorkoutSet[]> = {}
  workoutStore.activeSets.forEach(s => {
    if (!groups[s.exerciseId]) groups[s.exerciseId] = []
    groups[s.exerciseId]!.push(s)
  })
  return groups
})

const filteredExercises = computed(() =>
  exerciseStore.exercises.filter(e =>
    e.name.toLowerCase().includes(search.value.toLowerCase())
  )
)

async function startSession() {
  starting.value = true
  try {
    await workoutStore.startWorkout(newTitle.value || 'My Workout')
    editTitle.value = newTitle.value
  } finally {
    starting.value = false
  }
}

async function saveTitle() {
  if (editTitle.value && workout.value) {
    await workoutStore.updateWorkoutTitle(editTitle.value)
  }
}

async function finish() {
  await workoutStore.finishWorkout()
  void router.push('/app/history')
}

async function addSet(exerciseId: string, group: WorkoutSet[]) {
  const last     = group[group.length - 1]
  const setNumber = group.length + 1
  await workoutStore.addSet(exerciseId, setNumber, last?.weightKg ?? undefined, last?.reps ?? undefined)
}

async function addExercise(e: Exercise) {
  showExercisePicker.value = false
  await workoutStore.addSet(e.id, 1)
}

onMounted(async () => {
  await Promise.all([workoutStore.fetchActive(), exerciseStore.fetchExercises()])
  if (workout.value) editTitle.value = workout.value.title
})
</script>
