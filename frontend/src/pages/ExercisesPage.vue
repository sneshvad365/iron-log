<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="text-h5 text-weight-bold col">Exercise Library</div>
      <q-btn label="+ CREATE" color="primary" unelevated @click="showCreate = true" />
    </div>

    <!-- Search & filters -->
    <q-input v-model="search" label="Search exercises..." dense outlined clearable class="q-mb-sm" />
    <div class="q-gutter-xs q-mb-md">
      <q-chip
        v-for="mg in muscleGroups" :key="mg"
        clickable
        :color="selectedGroup === mg ? 'primary' : 'grey-3'"
        :text-color="selectedGroup === mg ? 'white' : 'dark'"
        @click="selectedGroup = selectedGroup === mg ? '' : mg"
      >{{ mg }}</q-chip>
    </div>

    <!-- Exercise grid -->
    <div class="row q-col-gutter-md">
      <div v-for="e in filtered" :key="e.id" class="col-12 col-sm-6 col-md-4">
        <ExerciseCard :exercise="e" @delete="exerciseStore.deleteExercise(e.id)" />
      </div>
    </div>

    <!-- Create exercise dialog -->
    <q-dialog v-model="showCreate">
      <q-card style="min-width: 320px">
        <q-card-section>
          <div class="text-h6">New Exercise</div>
        </q-card-section>
        <q-card-section>
          <q-input v-model="form.name" label="Name" outlined dense class="q-mb-sm" />
          <q-select v-model="form.muscleGroup" :options="muscleGroups" label="Muscle Group" outlined dense class="q-mb-sm" />
          <q-select v-model="form.type" :options="['Strength', 'Cardio', 'Bodyweight']" label="Type" outlined dense />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Cancel" v-close-popup />
          <q-btn label="Create" color="primary" :loading="creating" @click="create" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useExerciseStore } from 'stores/exercise'
import ExerciseCard from 'components/ExerciseCard.vue'

const exerciseStore = useExerciseStore()

const search        = ref('')
const selectedGroup = ref('')
const showCreate    = ref(false)
const creating      = ref(false)

const muscleGroups = ['All', 'Chest', 'Back', 'Legs', 'Shoulders', 'Arms', 'Core', 'Cardio']

const form = reactive({ name: '', muscleGroup: '', type: 'Strength' })

const filtered = computed(() =>
  exerciseStore.exercises.filter(e => {
    const matchSearch = e.name.toLowerCase().includes(search.value.toLowerCase())
    const matchGroup  = !selectedGroup.value || selectedGroup.value === 'All' || e.muscleGroup === selectedGroup.value
    return matchSearch && matchGroup
  })
)

async function create() {
  creating.value = true
  try {
    await exerciseStore.createExercise(form.name, form.muscleGroup, form.type)
    showCreate.value = false
    form.name = ''
  } finally {
    creating.value = false
  }
}

onMounted(() => exerciseStore.fetchExercises())
</script>
