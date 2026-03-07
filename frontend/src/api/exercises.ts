import client from './client'

export const listExercises = (params?: { muscle_group?: string; type?: string; search?: string }) =>
  client.get('/api/exercises', { params })

export const createExercise = (data: { name: string; muscle_group: string; type: string }) =>
  client.post('/api/exercises', data)

export const deleteExercise = (id: string) =>
  client.post(`/api/exercises/${id}/delete`, {})

export const getExerciseHistory = (id: string) =>
  client.get(`/api/exercises/${id}/history`)
