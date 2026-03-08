import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as statsApi from 'api/stats'

export interface Summary {
  weeklyVolume: number
  weeklyWorkouts: number
  weeklyMinutes: number
  streak: number
  totalWorkouts: number
}

export const useStatsStore = defineStore('stats', () => {
  const summary        = ref<Summary | null>(null)
  const volume         = ref<{ label: string; volume: number }[]>([])
  const prs            = ref<{ exerciseName: string; weightKg: number; reps: number; estimatedOneRm: number; date: string }[]>([])
  const progress       = ref<{ date: string; estimatedOneRm: number; bestWeightKg: number; bestReps: number }[]>([])
  const frequency      = ref<{ date: string; volume: number; workoutCount: number }[]>([])
  const muscleBreakdown = ref<{ muscleGroup: string; volumeKg: number; percentage: number }[]>([])

  async function fetchSummary() {
    const res = await statsApi.getSummary()
    summary.value = res.data.data
  }

  async function fetchVolume(period: 'weekly' | 'monthly' = 'weekly', count = 12) {
    const res = await statsApi.getVolume(period, count)
    volume.value = res.data.data
  }

  async function fetchPRs() {
    const res = await statsApi.getPRs()
    prs.value = res.data.data
  }

  async function fetchProgress(exerciseId: string) {
    const res = await statsApi.getProgress(exerciseId)
    progress.value = res.data.data
  }

  async function fetchFrequency(days = 365) {
    const res = await statsApi.getFrequency(days)
    frequency.value = res.data.data
  }

  async function fetchMuscleBreakdown(period: 'weekly' | 'monthly' = 'weekly', count = 12, from?: string, to?: string) {
    const res = await statsApi.getMuscleBreakdown(period, count, from, to)
    muscleBreakdown.value = res.data.data
  }

  return {
    summary, volume, prs, progress, frequency, muscleBreakdown,
    fetchSummary, fetchVolume, fetchPRs, fetchProgress, fetchFrequency, fetchMuscleBreakdown,
  }
})
