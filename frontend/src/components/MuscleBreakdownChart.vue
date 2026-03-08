<template>
  <v-chart :option="option" style="height: 280px; width: 100%" autoresize />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'

use([CanvasRenderer, PieChart, TooltipComponent, LegendComponent])

const props = defineProps<{ data: { muscleGroup: string; volumeKg: number; percentage: number }[] }>()

const option = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {d}%' },
  legend: { orient: 'vertical', left: 'left', textStyle: { color: '#ccc' } },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    data: props.data.map(d => ({ name: d.muscleGroup, value: d.volumeKg })),
    label: { show: false },
    emphasis: { label: { show: true } },
  }],
}))
</script>
