<template>
  <q-page class="q-pa-md">
    <div class="text-h5 text-weight-bold q-mb-md">AI Reports</div>

    <div v-if="!reportsStore.reports.length" class="flex flex-center column" style="min-height: 40vh;">
      <q-icon name="auto_awesome" size="48px" color="grey-7" />
      <div class="text-grey q-mt-md">No reports yet. Generate one from the Stats page.</div>
    </div>

    <q-list separator v-else>
      <q-item
        v-for="r in reportsStore.reports"
        :key="r.id"
        clickable v-ripple
        :to="`/app/reports/${r.id}`"
        style="background: #1a1a1a; border: 1px solid #2a2a2a; border-radius: 8px; margin-bottom: 8px;"
      >
        <q-item-section avatar>
          <q-icon name="auto_awesome" color="primary" />
        </q-item-section>
        <q-item-section>
          <q-item-label style="color: #eee; font-weight: 600;">{{ r.title }}</q-item-label>
          <q-item-label caption style="color: #777;">{{ formatDate(r.createdAt) }}</q-item-label>
        </q-item-section>
        <q-item-section side>
          <q-btn flat round dense icon="delete" color="negative" size="sm"
            @click.prevent="remove(r.id)" />
        </q-item-section>
      </q-item>
    </q-list>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useReportsStore } from 'stores/reports'

const $q           = useQuasar()
const reportsStore = useReportsStore()

function formatDate(dt: string) {
  return new Date(dt).toLocaleString('en-GB', {
    weekday: 'short', day: 'numeric', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

function remove(id: string) {
  $q.dialog({ title: 'Delete report?', message: 'This cannot be undone.', cancel: true, persistent: true })
    .onOk(async () => { await reportsStore.deleteReport(id) })
}

onMounted(() => reportsStore.fetchReports())
</script>
