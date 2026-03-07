import client from './client'

export const createSet = (workoutId: string, data: object) =>
  client.post(`/api/workouts/${workoutId}/sets`, data)

export const updateSet = (id: string, data: object) =>
  client.post(`/api/sets/${id}/update`, data)

export const deleteSet = (id: string) =>
  client.post(`/api/sets/${id}/delete`, {})
