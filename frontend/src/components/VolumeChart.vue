<template>
  <v-chart :option="option" style="height: 240px; width: 100%" autoresize @click="onClick" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'

use([CanvasRenderer, BarChart, GridComponent, TooltipComponent])

const props = defineProps<{
  data: { label: string; volume: number }[]
  selectedLabel?: string
}>()

const emit = defineEmits<{ barClick: [label: string] }>()

function onClick(params: { name: string; componentType: string }) {
  if (params.componentType === 'series') emit('barClick', params.name)
}

const option = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: props.data.map(d => d.label), axisLabel: { rotate: 30 } },
  yAxis: { type: 'value', name: 'kg' },
  series: [{
    type: 'bar',
    data: props.data.map(d => ({
      value: d.volume,
      itemStyle: {
        color: d.label === props.selectedLabel ? '#ff6200' : '#f57c00',
        opacity: props.selectedLabel && d.label !== props.selectedLabel ? 0.4 : 1,
      },
    })),
  }],
}))
</script>
