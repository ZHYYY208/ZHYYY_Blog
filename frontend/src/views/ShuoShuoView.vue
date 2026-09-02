<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'

const items = ref([])
const error = ref('')

onMounted(async () => {
  try {
    items.value = await api.get('/shuoshuo')
  } catch (e) {
    error.value = '加载失败：' + e.message
  }
})
</script>

<template>
  <div>
    <h1>说说</h1>
    <p v-if="error" class="hint">{{ error }}</p>

    <div v-for="it in items" :key="it.id" class="glass shuo">
      <p>{{ it.content }}</p>
      <span class="meta">{{ it.createdAt }}</span>
    </div>
    <p v-if="!items.length && !error" class="hint">还没有说说</p>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.shuo { padding: 20px 24px; margin-bottom: 14px; }
.meta { color: var(--text-muted); font-size: 12px; }
.shuo p { margin: 0 0 8px; white-space: pre-wrap; color: var(--text-h); }
.hint { color: var(--text-muted); }
</style>
