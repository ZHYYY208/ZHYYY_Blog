<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'

const photos = ref([])
const error = ref('')

onMounted(async () => {
  try {
    photos.value = await api.get('/photos')
  } catch (e) {
    error.value = '后端未连接：' + e.message
  }
})
</script>

<template>
  <div>
    <h1>相册</h1>
    <p v-if="error" class="hint">{{ error }}</p>
    <div class="grid">
      <figure v-for="p in photos" :key="p.id" class="glass photo">
        <img :src="p.url" :alt="p.title" loading="lazy" />
        <figcaption>{{ p.title }}</figcaption>
      </figure>
    </div>
    <p v-if="!photos.length && !error" class="hint">还没有照片</p>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
}
.photo {
  margin: 0;
  overflow: hidden;
  border-radius: 22px;
  transition: transform 0.25s;
}
.photo:hover { transform: translateY(-3px); }
.photo img { width: 100%; height: 200px; object-fit: cover; display: block; }
.photo figcaption { padding: 12px 16px; font-size: 13px; color: var(--text); }
.hint { color: var(--text-muted); }
</style>
