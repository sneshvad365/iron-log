import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as exercisesApi from 'api/exercises'

export interface Exercise {
  id: string
  name: string
  muscleGroup: string
  type: string
  createdBy: string | null
  createdAt: string
  gifUrl: string | null
  gifUrl2: string | null
}

export const useExerciseStore = defineStore('exercise', () => {
  const exercises = ref<Exercise[]>([])

  async function fetchExercises(params?: { muscle_group?: string; type?: string; search?: string }) {
    const res = await exercisesApi.listExercises(params)
    exercises.value = res.data.data
  }

  async function createExercise(name: string, muscleGroup: string, type: string) {
    const res = await exercisesApi.createExercise({ name, muscle_group: muscleGroup, type })
    exercises.value.push(res.data.data)
    return res.data.data as Exercise
  }

  async function deleteExercise(id: string) {
    await exercisesApi.deleteExercise(id)
    exercises.value = exercises.value.filter(e => e.id !== id)
  }

  async function getHistory(id: string) {
    const res = await exercisesApi.getExerciseHistory(id)
    return res.data.data
  }

  return { exercises, fetchExercises, createExercise, deleteExercise, getHistory }
})
