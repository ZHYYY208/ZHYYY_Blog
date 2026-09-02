<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'

const posts = ref([])
const error = ref('')

onMounted(async () => {
  try {
    posts.value = await api.get('/posts')
  } catch (e) {
    error.value = '后端未连接：' + e.message
  }
})
</script>

<template>
  <div>
    <h1>博客文章</h1>
    <p v-if="error" class="hint">{{ error }}</p>
    <article v-for="p in posts" :key="p.id" class="glass item">
      <router-link :to="`/post/${p.id}`" class="title">
        <h2>{{ p.title }}</h2>
      </router-link>
      <p class="meta">{{ p.createdAt }}</p>
      <p class="summary">{{ p.summary }}</p>
    </article>
    <p v-if="!posts.length && !error" class="hint">还没有文章</p>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.item { padding: 24px 28px; margin-bottom: 18px; transition: transform 0.25s, box-shadow 0.25s; }
.item:hover { transform: translateY(-2px) scale(1.005); }
.title { text-decoration: none; }
.title h2 { color: var(--text-h); margin: 0 0 6px; font-size: 22px; }
.item:hover .title h2 { color: var(--accent); }
.meta { color: var(--text-muted); font-size: 13px; margin: 0 0 10px; }
.summary { margin: 0; color: var(--text); opacity: 0.85; }
.hint { color: var(--text-muted); }
</style>
