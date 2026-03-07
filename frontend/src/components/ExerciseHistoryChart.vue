<template>
  <v-chart :option="option" style="height: 240px; width: 100%" autoresize />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent])

const props = defineProps<{
  data: { date: string; estimatedOneRm: number }[]
}>()

const option = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: props.data.map(d => d.date) },
  yAxis: { type: 'value', name: 'Est. 1RM (kg)' },
  series: [{
    type: 'line',
    data: props.data.map(d => d.estimatedOneRm),
    smooth: true,
    itemStyle: { color: '#f57c00' },
    areaStyle: { color: 'rgba(245, 124, 0, 0.15)' },
  }],
}))
</script>
