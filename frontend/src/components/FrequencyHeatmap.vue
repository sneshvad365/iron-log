<template>
  <v-chart :option="option" style="height: 180px; width: 100%" autoresize />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { HeatmapChart } from 'echarts/charts'
import { CalendarComponent, TooltipComponent, VisualMapComponent } from 'echarts/components'

use([CanvasRenderer, HeatmapChart, CalendarComponent, TooltipComponent, VisualMapComponent])

const props = defineProps<{
  data: { date: string; volume: number; workoutCount: number }[]
  year?: number
}>()

const calYear = computed(() => props.year ?? new Date().getFullYear())

const option = computed(() => ({
  tooltip: { formatter: (p: any) => `${p.data[0]}: ${p.data[1].toFixed(0)} kg` },
  visualMap: { show: false, min: 0, max: Math.max(...props.data.map(d => d.volume), 1), inRange: { color: ['#fff3e0', '#f57c00'] } },
  calendar: {
    range: String(calYear.value),
    cellSize: 14,
    itemStyle: { borderWidth: 2, borderColor: '#fff' },
  },
  series: [{
    type: 'heatmap',
    coordinateSystem: 'calendar',
    data: props.data.map(d => [d.date, d.volume]),
  }],
}))
</script>
