<template>
  <tr>
    <td>{{ set.setNumber }}</td>
    <td>
      <q-input
        v-model.number="weight"
        type="number" dense borderless
        input-style="text-align: center"
        @blur="emit('update', { weightKg: weight, reps: repsVal, isDone: set.isDone })"
      />
    </td>
    <td>
      <q-input
        v-model.number="repsVal"
        type="number" dense borderless
        input-style="text-align: center"
        @blur="emit('update', { weightKg: weight, reps: repsVal, isDone: set.isDone })"
      />
    </td>
    <td class="text-center">
      <q-checkbox
        v-model="done"
        color="primary"
        @update:model-value="emit('update', { weightKg: weight, reps: repsVal, isDone: done })"
      />
    </td>
    <td>
      <q-btn flat round dense icon="delete" color="grey-5" size="sm" @click="emit('remove')" />
    </td>
  </tr>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { WorkoutSet } from 'stores/workout'

const props = defineProps<{ set: WorkoutSet }>()
const emit  = defineEmits<{
  (e: 'update', data: { weightKg?: number; reps?: number; isDone: boolean }): void
  (e: 'remove'): void
}>()

const weight  = ref<number | null>(props.set.weightKg)
const repsVal = ref<number | null>(props.set.reps)
const done    = ref(props.set.isDone)
</script>
