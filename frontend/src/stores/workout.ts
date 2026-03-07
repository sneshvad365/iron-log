import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as workoutsApi from 'api/workouts'
import * as setsApi from 'api/sets'

export interface WorkoutSet {
  id: string
  workoutId: string
  exerciseId: string
  setNumber: number
  weightKg: number | null
  reps: number | null
  durationS: number | null
  isDone: boolean
  notes: string | null
  loggedAt: string
}

export interface Workout {
  id: string
  userId: string
  title: string
  notes: string | null
  startedAt: string
  endedAt: string | null
}

export const useWorkoutStore = defineStore('workout', () => {
  const workouts       = ref<Workout[]>([])
  const activeWorkout  = ref<Workout | null>(null)
  const activeSets     = ref<WorkoutSet[]>([])

  async function fetchWorkouts() {
    const res = await workoutsApi.listWorkouts()
    workouts.value = res.data.data
  }

  async function fetchActive() {
    const res = await workoutsApi.getActiveWorkout()
    activeWorkout.value = res.data.data
  }

  async function startWorkout(title: string) {
    const res = await workoutsApi.createWorkout(title)
    activeWorkout.value = res.data.data
    activeSets.value = []
    return res.data.data as Workout
  }

  async function finishWorkout() {
    if (!activeWorkout.value) return
    await workoutsApi.finishWorkout(activeWorkout.value.id)
    activeWorkout.value = null
    activeSets.value = []
  }

  async function updateWorkoutTitle(title: string) {
    if (!activeWorkout.value) return
    const res = await workoutsApi.updateWorkout(activeWorkout.value.id, { title })
    activeWorkout.value = res.data.data
  }

  async function addSet(exerciseId: string, setNumber: number, weightKg?: number, reps?: number) {
    if (!activeWorkout.value) return
    const res = await setsApi.createSet(activeWorkout.value.id, {
      exercise_id: exerciseId,
      set_number: setNumber,
      weight_kg: weightKg,
      reps,
    })
    activeSets.value.push(res.data.data)
    return res.data.data as WorkoutSet
  }

  async function updateSet(id: string, data: Partial<Pick<WorkoutSet, 'weightKg' | 'reps' | 'isDone' | 'notes'>>) {
    const res = await setsApi.updateSet(id, {
      weight_kg: data.weightKg,
      reps:      data.reps,
      is_done:   data.isDone,
      notes:     data.notes,
    })
    const idx = activeSets.value.findIndex(s => s.id === id)
    if (idx >= 0) activeSets.value[idx] = res.data.data
  }

  async function removeSet(id: string) {
    await setsApi.deleteSet(id)
    activeSets.value = activeSets.value.filter(s => s.id !== id)
  }

  async function deleteWorkout(id: string) {
    await workoutsApi.deleteWorkout(id)
    workouts.value = workouts.value.filter(w => w.id !== id)
  }

  return {
    workouts, activeWorkout, activeSets,
    fetchWorkouts, fetchActive, startWorkout, finishWorkout,
    updateWorkoutTitle, addSet, updateSet, removeSet, deleteWorkout,
  }
})
