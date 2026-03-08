import client from './client'

export const getSummary = () =>
  client.get('/api/stats/summary')

export const getVolume = (period: 'weekly' | 'monthly' = 'weekly', count = 12) =>
  client.get('/api/stats/volume', { params: { period, count } })

export const getPRs = () =>
  client.get('/api/stats/prs')

export const getProgress = (exerciseId: string) =>
  client.get(`/api/stats/progress/${exerciseId}`)

export const getFrequency = (days = 365) =>
  client.get('/api/stats/frequency', { params: { days } })

export const getMuscleBreakdown = (period: 'weekly' | 'monthly' = 'weekly', count = 12, from?: string, to?: string) =>
  client.get('/api/stats/muscle-breakdown', { params: { period, count, ...(from && to ? { from, to } : {}) } })

export const analyseWorkouts = () =>
  client.post('/api/stats/analyse', {})
