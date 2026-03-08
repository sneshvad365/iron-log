<template>
  <q-page class="q-pa-md" style="max-width: 440px; margin: 0 auto;">
    <div class="text-h5 text-weight-bold q-mb-xs">Support Iron Log</div>
    <div class="text-caption text-grey q-mb-lg">
      Iron Log is free and open source. If you find it useful, a small donation helps keep it running.
    </div>

    <q-card flat bordered class="q-pa-lg">
      <div class="text-subtitle2 text-grey q-mb-md" style="text-transform: uppercase; letter-spacing: 0.08em;">
        Choose an amount
      </div>

      <!-- Preset amounts -->
      <div class="row q-gutter-sm q-mb-md">
        <q-btn
          v-for="preset in presets" :key="preset"
          :label="`€${preset}`"
          :outline="amount !== preset"
          :unelevated="amount === preset"
          :color="amount === preset ? 'primary' : 'grey-7'"
          @click="setPreset(preset)"
          style="flex: 1;"
        />
      </div>

      <!-- Custom amount -->
      <q-input
        v-model.number="customAmount"
        label="Custom amount (€)"
        outlined dense
        type="number"
        min="1"
        max="500"
        class="q-mb-lg"
        @focus="amount = 0"
        @update:model-value="amount = customAmount || 0"
      >
        <template #prepend>
          <span style="color:#aaa; font-size:1rem;">€</span>
        </template>
      </q-input>

      <q-btn
        label="Donate with Stripe"
        icon="favorite"
        color="primary"
        unelevated
        size="md"
        class="full-width"
        :loading="loading"
        :disable="!finalAmount || finalAmount < 1"
        @click="donate"
      />

      <div class="text-caption text-grey q-mt-md text-center">
        Secure payment via Stripe. We never see your card details.
      </div>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { createDonationSession } from 'api/donate'

const presets      = [3, 5, 10]
const amount       = ref(5)
const customAmount = ref<number | null>(null)
const loading      = ref(false)

const finalAmount = computed(() =>
  customAmount.value && amount.value === 0 ? customAmount.value : amount.value
)

function setPreset(val: number) {
  amount.value       = val
  customAmount.value = null
}

async function donate() {
  loading.value = true
  try {
    const res = await createDonationSession(finalAmount.value)
    window.location.href = res.data.data.url
  } catch (e: any) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
