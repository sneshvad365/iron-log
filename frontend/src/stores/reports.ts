import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as reportsApi from 'api/reports'

export interface ReportSummary {
  id: string
  title: string
  createdAt: string
}

export interface ReportDetail extends ReportSummary {
  content: string
}

export const useReportsStore = defineStore('reports', () => {
  const reports = ref<ReportSummary[]>([])

  async function fetchReports() {
    const res = await reportsApi.listReports()
    reports.value = res.data.data
  }

  async function fetchReport(id: string): Promise<ReportDetail> {
    const res = await reportsApi.getReport(id)
    return res.data.data
  }

  async function deleteReport(id: string) {
    await reportsApi.deleteReport(id)
    reports.value = reports.value.filter(r => r.id !== id)
  }

  function addReport(report: ReportSummary) {
    reports.value.unshift(report)
  }

  return { reports, fetchReports, fetchReport, deleteReport, addReport }
})
