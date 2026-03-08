import client from './client'

export const listReports  = () => client.get('/api/reports')
export const getReport    = (id: string) => client.get(`/api/reports/${id}`)
export const deleteReport = (id: string) => client.post(`/api/reports/${id}/delete`, {})
