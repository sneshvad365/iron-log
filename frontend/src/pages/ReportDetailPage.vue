<template>
  <q-page class="q-pa-md">
    <div v-if="loading" class="flex flex-center" style="min-height: 60vh;">
      <q-spinner color="primary" size="48px" />
    </div>

    <div v-else-if="report">
      <div class="row items-center q-mb-lg">
        <q-btn flat round dense icon="arrow_back" color="grey" :to="'/app/reports'" class="q-mr-sm" />
        <div class="col">
          <div class="text-h6 text-weight-bold" style="color: #eee;">{{ report.title }}</div>
          <div class="text-caption text-grey">{{ formatDate(report.createdAt) }}</div>
        </div>
      </div>

      <q-card flat style="background: #1a1a1a; border: 1px solid #2a2a2a; max-width: 720px; margin: 0 auto;">
        <q-card-section class="q-pa-lg">
          <div class="report-body" v-html="reportHtml" />
        </q-card-section>
      </q-card>
    </div>

    <div v-else class="text-grey text-center q-mt-xl">Report not found.</div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useReportsStore } from 'stores/reports'
import type { ReportDetail } from 'stores/reports'

const route        = useRoute()
const reportsStore = useReportsStore()
const report       = ref<ReportDetail | null>(null)
const loading      = ref(true)

function formatDate(dt: string) {
  return new Date(dt).toLocaleString('en-GB', {
    weekday: 'short', day: 'numeric', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

function markdownToHtml(md: string): string {
  return md
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/^[-•] (.+)$/gm, '<li>$1</li>')
    .replace(/(<li>.*<\/li>)/gs, '<ul>$1</ul>')
    .replace(/\n\n/g, '</p><p>')
    .replace(/^(?!<[hup])/gm, '')
    .replace(/^(.+)$/gm, (line) => line.startsWith('<') ? line : `<p>${line}</p>`)
}

const reportHtml = computed(() => report.value ? markdownToHtml(report.value.content) : '')

onMounted(async () => {
  try {
    report.value = await reportsStore.fetchReport(route.params.id as string)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.report-body { color: #ddd; line-height: 1.8; font-size: 0.95rem; }
.report-body :deep(h1),
.report-body :deep(h2),
.report-body :deep(h3) { color: #ff6200; margin-top: 1.4em; margin-bottom: 0.4em; }
.report-body :deep(h1) { font-size: 1.4rem; }
.report-body :deep(h2) { font-size: 1.2rem; }
.report-body :deep(h3) { font-size: 1rem; text-transform: uppercase; letter-spacing: 0.05em; }
.report-body :deep(ul) { padding-left: 1.4em; margin: 0.5em 0; }
.report-body :deep(li) { margin-bottom: 0.3em; }
.report-body :deep(strong) { color: #fff; }
.report-body :deep(p) { margin: 0.6em 0; }
</style>
