import client from './client'

export const listWorkouts   = ()                       => client.get('/api/workouts')
export const createWorkout  = (title: string)          => client.post('/api/workouts', { title })
export const getActiveWorkout = ()                     => client.get('/api/workouts/active')
export const getWorkout     = (id: string)             => client.get(`/api/workouts/${id}`)
export const updateWorkout  = (id: string, data: object) => client.post(`/api/workouts/${id}/update`, data)
export const deleteWorkout  = (id: string)             => client.post(`/api/workouts/${id}/delete`, {})
export const finishWorkout  = (id: string)             => client.post(`/api/workouts/${id}/update`, { ended_at: new Date().toISOString() })
